package com.comp4911.backend.api;

import com.comp4911.backend.access.CredentialManager;
import com.comp4911.backend.access.EmployeeManager;
import com.comp4911.backend.access.PaygradeManager;
import com.comp4911.backend.access.ProjectPackageManager;
import com.comp4911.backend.api.filters.annotations.AuthAdmin;
import com.comp4911.backend.api.filters.annotations.AuthEMP;
import com.comp4911.backend.api.filters.annotations.AuthHR;
import com.comp4911.backend.lib.Constants;
import com.comp4911.backend.lib.ResponseFormat;
import com.comp4911.backend.lib.Role;
import com.comp4911.backend.lib.Validation;
import com.comp4911.backend.models.CredentialEntity;
import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.JSONModels.JSONEmployee;
import com.comp4911.backend.models.PaygradeEntity;
import com.comp4911.backend.models.ProjectPackageEntity;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Path("/user")
public class UserResource {

    @Inject
    private CredentialManager credentialManager;
    @Inject
    private EmployeeManager employeeManager;
    @Inject
    private PaygradeManager paygradeManager;
    @Inject
    private ProjectPackageManager projectPackageManager;

    @Context
    HttpServletRequest httpServletRequest;

    /**
     * Create Employee in database using API
     * @param userInfo user info in JSON
     * @return Response Code
     */
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR
    public Map<String, Object> createUser(HashMap<String, String> userInfo) {
        //you can get requester's employee object like this
        //EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");

        String username = userInfo.get("userName");
        String password = userInfo.get("password");
        String employeeId = userInfo.get("employeeId");
        String firstName = userInfo.get("firstName");
        String lastName = userInfo.get("lastName");
        String role = userInfo.get("role");
        String paygrade = userInfo.get("paygrade");
        String supervisor = userInfo.get("supervisor");
        String approver = userInfo.get("approver");
        String disabled = userInfo.get("disabled");

        //check if employeeId is valid
        if (employeeManager.find(employeeId) != null) {
            return ResponseFormat.buildFailedResult("employeeId already exists.");
        }

        Boolean disabledInt;
        try{
            disabledInt = disabled==null ? null : Boolean.parseBoolean(disabled);
        } catch (NumberFormatException e){
            return ResponseFormat.buildErrorResult("disabled value is not valid.");
        }

        if (employeeId == null || username == null || password == null || firstName == null || lastName == null || role == null || paygrade == null) {
            return ResponseFormat.buildFailedResult("missing required fields.");
        }

        CredentialEntity ce = credentialManager.find(username);
        if (ce != null) {
            return ResponseFormat.buildFailedResult("username already exists.");
        }

        //creating a hashed password
//        String hashedPassword = null;
//        try {
//            hashedPassword = PBKDF2.hashPassword(password, username);
//        } catch (Exception e) {
//            return ResponseFormat.buildErrorResult("failed to hash password.");
//        }

        if(username.trim().isEmpty() || password.trim().isEmpty()){
            return ResponseFormat.buildErrorResult("username or password cannot be blank");
        }

        //checking if supervisor is valid & EM or ADMIN
        EmployeeEntity supervisorEntity = null;
        if (supervisor != null) {
            supervisorEntity = employeeManager.find(supervisor);
            if (supervisorEntity == null) {
                return ResponseFormat.buildFailedResult("invalid supervisor id.");
            }
        }

        //checking if approver is valid
        EmployeeEntity approverEntity = null;
        if (approver != null) {
            approverEntity = employeeManager.find(approver);
            if (approverEntity == null) {
                return ResponseFormat.buildFailedResult("invalid approver id.");
            }
        }

        //checking if paygrade is valid
        // if year is not specified, update the current year
        String year = "" + Calendar.getInstance().get(Calendar.YEAR);
        PaygradeEntity paygradeEntity = paygradeManager.find(paygrade, year);
        if (paygradeEntity == null) {
            return ResponseFormat.buildFailedResult("no such paygrade. (year: " + year + ")");
        }

        //checking if role is valid
        try {
            Role.fromString(role);
        } catch (Exception e) {
            return ResponseFormat.buildFailedResult("no such role.");
        }


        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeId(employeeId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPaygradeEntity(paygradeEntity);
        employee.setSupervisor(supervisorEntity);
        employee.setTimesheetApprover(approverEntity);
        employee.setIsDisabled(disabledInt ? (byte)1 : (byte)0);
        employee.setRole(Role.fromString(role).getValue());

        employee.setFlexTime(Constants.DEFAULT_FLEX_TIME);

        CredentialEntity credential = new CredentialEntity();
        credential.setUserName(username);
        credential.setPassword(password);
        credential.setEmployeeEntity(employee);

        employeeManager.persist(employee);
        employeeManager.flush();
        credential.setEmployeeEntity(employee);
        credentialManager.persist(credential);

        //default join in sick and vacation leave
        ProjectPackageEntity sickPackage = projectPackageManager.find(Constants.SICK_LEAVE_CODE);
        ProjectPackageEntity vacationPackage = projectPackageManager.find(Constants.VACATION_LEAVE_CODE);
        sickPackage.getEmployeeEntities().add(employee);
        vacationPackage.getEmployeeEntities().add(employee);
        projectPackageManager.merge(sickPackage);
        projectPackageManager.merge(vacationPackage);

        return ResponseFormat.buildSuccessResult(new JSONEmployee(employeeManager.find(employee.getEmployeeId())));
    }


    /**
     * get all employees by role
     * @param role role of the employee
     * @return  list of employees
     */
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/role/{role}")
    @AuthAdmin @AuthHR
    public Map<String, Object> readUsersByRole(@PathParam("role") String role){
        //checking if role is valid
        try {
            Role.fromString(role);
        } catch (Exception e) {
            return ResponseFormat.buildFailedResult("no such role.");
        }

        List<JSONEmployee> eeJsonList = new ArrayList<>();
        for (EmployeeEntity ee : employeeManager.findAllByRole(Role.fromString(role).getValue())) {
            eeJsonList.add(new JSONEmployee(ee));
        }

        // Return all user object []
        return ResponseFormat.buildSuccessResult(eeJsonList);
    }

    /**
     * get all roles
     * @return  list of roles
     */
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/role")
    @AuthAdmin @AuthHR
    public Map<String, Object> readRoles(){
        List<String> roles = Arrays.stream(Role.values()).map(Role::getValue).collect(Collectors.toList());
        return ResponseFormat.buildSuccessResult(roles);
    }

    /**
     * Read Employee in database using API
     * @param employeeId employee id as Path param
     * @return Employee object with that empID
     */
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Path("{id}")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> readUser(@PathParam("id") String employeeId){

        EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");

        // Search for employee in DB
        EmployeeEntity ee = employeeManager.find(employeeId);
        if (ee == null) {
            return ResponseFormat.buildFailedResult("no such employee.");
        }
        if (!requester.getEmployeeNum().equals(ee.getEmployeeNum()) && requester.getRole().equals(Role.EMP.getValue()) && (!requester.getSupervising().contains(ee) && !requester.getTimesheetApproving().contains(ee))) {
            return ResponseFormat.buildFailedResult("You are not authorized to view this employee.");
        }

        return ResponseFormat.buildSuccessResult(new JSONEmployee(ee));
    }

    /**
     * Read Employee list in database using API
     * @return Array of employee objects
     */
    @GET
    @Produces("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> readUsers(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("supervisorId") String supervisorId,
            @QueryParam("role") String role,
            @QueryParam("approverId") String approverId
            ){
        EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");
        if (page != null && page < 0 || size != null && size < 0) {
            return ResponseFormat.buildFailedResult("page or size is negative.");
        }
        if ((page == null && size != null) || (page != null && size == null)) {
            return ResponseFormat.buildFailedResult("page and size must be both set or both unset.");
        }

        List<EmployeeEntity> eeList = employeeManager.findAll(supervisorId, approverId, role, page, size);

        // Get users
        List<JSONEmployee> eeJsonList = new ArrayList<>();
        for (EmployeeEntity ee : eeList) {
            eeJsonList.add(new JSONEmployee(ee));
        }

        HashMap<String, Object> response = new HashMap<>();
        response.put("list", eeJsonList);
        response.put("total", eeList.size());

        // Return all user object []
        return ResponseFormat.buildSuccessResult(response);
    }

    /**
     * Update Employee in database using API
     * @param userInfo Employee object with new field
     * @return Response Code
     */
    @PUT
    @Path("{id}")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> updateUser(@PathParam("id") String empId, HashMap<String, String> userInfo){

        EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");

        // Find employee to be updated====
        EmployeeEntity employee = employeeManager.find(empId);
        if (employee == null) {
            return ResponseFormat.buildErrorResult("employee not found.");
        }

        if (!requester.getEmployeeNum().equals(employee.getEmployeeNum()) && requester.getRole().equals(Role.EMP.getValue()) && (!requester.getSupervising().contains(employee) && !requester.getTimesheetApproving().contains(employee))) {
            return ResponseFormat.buildFailedResult("You are not authorized to view this employee.");
        }

        // Validate new info====
        String newEmployeeId = userInfo.get("employeeId");
        String newUsername = userInfo.get("userName");
        String newPassword = userInfo.get("password");
        String newFirstName = userInfo.get("firstName");
        String newLastName = userInfo.get("lastName");
        String newRole = userInfo.get("role");
        String newDisabled = userInfo.get("disabled");
        String newSupervisor = userInfo.get("supervisor");
        String newApprover = userInfo.get("approver");
        String newPaygrade = userInfo.get("paygrade");

        employee.setFirstName(newFirstName);
        employee.setLastName(newLastName);

        EmployeeEntity ee = employeeManager.find(newEmployeeId);
        if (ee != null && !ee.getEmployeeId().equals(empId)) {
            return ResponseFormat.buildFailedResult("employeeId already exists.");
        } else if (ee == null){
            employee.setEmployeeId(newEmployeeId);
        }

        EmployeeEntity newSE = employeeManager.find(newSupervisor);
        if(newSupervisor != null && newSE == null){
            return ResponseFormat.buildErrorResult("invalid supervisor id.");
        }else{
            if (Validation.canBeSupervisor(newSE, employee)) {
                employee.setSupervisor(newSE);
            } else {
                return ResponseFormat.buildErrorResult("this operation destroys the supervisor hierarchy.");
            }
        }

        EmployeeEntity newAE = employeeManager.find(newApprover);
        if(newApprover != null && newAE == null){
            return ResponseFormat.buildErrorResult("invalid approver id.");
        }else{
            if (Validation.canBeTimesheetApprover(newAE, employee)) {
                employee.setTimesheetApprover(newAE);
            } else {
                return ResponseFormat.buildErrorResult("this operation destroys the timesheet approver hierarchy.");
            }
        }

        //checking if role is valid
        try {
            Role.fromString(newRole);
        } catch (Exception e) {
            return ResponseFormat.buildFailedResult("no such role.");
        }
        employee.setRole(Role.fromString(newRole).getValue());

        employee.setIsDisabled((Boolean.parseBoolean(newDisabled)) ? (byte)1 : (byte)0);

        //checking if paygrade is valid
        String year = "" + Calendar.getInstance().get(Calendar.YEAR);
        PaygradeEntity newPG = paygradeManager.find(newPaygrade.toUpperCase(), year);
        if(newPG == null){
            return ResponseFormat.buildErrorResult("invalid paygrade. (year: " + year + ")");
        }else{
            employee.setPaygradeEntity(newPG);
        }

        // Credential===
        CredentialEntity newCredential = credentialManager.findByEmpID(empId);
        String oldUsername = newCredential.getUserName();

        if (credentialManager.find(newUsername) != null && !newUsername.equals(oldUsername)) {
            return ResponseFormat.buildFailedResult("username already exists.");
        }
        newCredential.setUserName(newUsername.trim());

        if(newPassword!=null && newPassword.trim().length()>0){
            // Valid password
            newCredential.setPassword(newPassword.trim());
        }

        credentialManager.remove(credentialManager.find(oldUsername));

        employeeManager.merge(employee);
        credentialManager.persist(newCredential);

        return ResponseFormat.buildSuccessResult(new JSONEmployee(employeeManager.find(employee.getEmployeeId())));
    }

    /**
     * Update user's password
     * @param empId employee ID
     * @param pwInfo JSON request with password
     * @return Response Code
     */
    @PUT
    @Path("password/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> updatePassword(@PathParam("id") String empId, HashMap<String, String> pwInfo) {

        EmployeeEntity requester = (EmployeeEntity) httpServletRequest.getAttribute("requester");

        // Find employee to be updated====
        EmployeeEntity employee = employeeManager.find(empId);
        if (employee == null) {
            return ResponseFormat.buildErrorResult("employee not found.");
        }

        if (!requester.getEmployeeNum().equals(employee.getEmployeeNum()) && requester.getRole().equals(Role.EMP.getValue()) && (!requester.getSupervising().contains(employee) && !requester.getTimesheetApproving().contains(employee))) {
            return ResponseFormat.buildFailedResult("You are not authorized to view this employee.");
        }

        // Validate new info====
        String oldPassword = pwInfo.get("oldPassword");
        String newPassword = pwInfo.get("newPassword");
        if(oldPassword==null || oldPassword.trim().isEmpty()){
            return ResponseFormat.buildErrorResult("current password is required.");
        }

        if(newPassword==null || newPassword.trim().isEmpty()){
            return ResponseFormat.buildErrorResult("must provide a new valid password.");
        }

        // Credential===
        CredentialEntity newCredential = credentialManager.findByEmpID(empId);
        if(!newCredential.getPassword().equals(oldPassword)){
            // Current password do not match with given pw
            return ResponseFormat.buildErrorResult("current password is incorrect.");
        }

        newCredential.setPassword(newPassword.trim());
        credentialManager.remove(newCredential);
        credentialManager.persist(newCredential);

        return ResponseFormat.buildSuccessResult(new JSONEmployee(employeeManager.find(employee.getEmployeeId())));
    }

    /**
     * Delete Employee in database using API
     * @param employeeId employee ID
     * @return Response Code
     */
    @DELETE
    @Produces("application/json")
    @Consumes("application/json")
    @Path("{id}")
    @AuthAdmin @AuthHR
    public Map<String, Object> deleteUser(@PathParam("id") String employeeId){
        // Check employee ID
        if (employeeId == null) {
            return ResponseFormat.buildFailedResult("invalid employee id.");
        }

        // Find reference in DB
        EmployeeEntity ee = employeeManager.find(employeeId);
        if(ee == null) return ResponseFormat.buildFailedResult("invalid employee id.");

//        CredentialEntity ce = credentialManager.findByEmpID(ee.getEmployeeId().toString());
//        credentialManager.remove(ce);

        // Set employee as inactive
        ee.setIsDisabled((byte)1);
        employeeManager.merge(ee);

        // Return employee deletion success
        EmployeeEntity lookupEmp = employeeManager.find(employeeId);
        if (lookupEmp != null && lookupEmp.getIsDisabled() == 1) {
            // Deletion complete
            // Extra code goes here
            return ResponseFormat.buildSuccessResult(new JSONEmployee(lookupEmp));
        }else{
            // Fail to delete
            return ResponseFormat.buildFailedResult("fail to delete employee.");
        }
    }
}
