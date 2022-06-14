package com.comp4911.backend.api;

import com.comp4911.backend.access.*;
import com.comp4911.backend.api.filters.annotations.AuthAdmin;
import com.comp4911.backend.api.filters.annotations.AuthEMP;
import com.comp4911.backend.api.filters.annotations.AuthHR;
import com.comp4911.backend.lib.ApproveStatus;
import com.comp4911.backend.lib.ResponseFormat;
import com.comp4911.backend.lib.Role;
import com.comp4911.backend.lib.Util;
import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.JSONModels.JSONEmployee;
import com.comp4911.backend.models.JSONModels.JSONProjectPackage;
import com.comp4911.backend.models.ProjectPackageEntity;
import com.comp4911.backend.models.TimesheetrowEntity;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


@Path("/projectpackage")
public class ProjectPackageResource {
    @Inject
    private ProjectPackageManager projectPackageManager;

    @Inject
    private EmployeeManager employeeManager;

    @Inject
    private TimesheetRowManager timesheetRowManager;

    @Inject
    private PaygradeManager paygradeManager;

    @Context
    HttpServletRequest httpServletRequest;

    @Path("/")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> createProjectPackage(Map<String, Object> projectPackage) {
        String projectId;
        String workPackageId;
        String managerId;
        String assistantId;
        Boolean isOpen;
        ArrayList<String> employees;
        String parentWorkPackageId;
        Map<String, String> personDays;
        try {
            projectId = (String) projectPackage.get("projectId");
            workPackageId = (String) projectPackage.get("workPackageId");
            managerId = (String) projectPackage.get("managerId");
            assistantId = (String) projectPackage.get("assistantId");
            isOpen = (Boolean) projectPackage.get("isOpen");
            employees = projectPackage.get("employees") == null ? null : new ArrayList<String>((Collection<String>)projectPackage.get("employees"));
            parentWorkPackageId = (String) projectPackage.get("parentWorkPackageId");
            personDays = projectPackage.get("personDays") == null ? null : (Map<String, String>) projectPackage.get("personDays");
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult("Something went wrong. Please check your input.");
        }

        //required fields
        if (projectId == null || managerId == null || isOpen == null || employees == null || workPackageId == null) {
            return ResponseFormat.buildFailedResult("projectId, managerId, isOpen, employees, workPackageId required");
        }

        //parentWorkPackageId null -> this is top level
        if (parentWorkPackageId == null) {
            if (!projectId.equals(workPackageId)) {
                return ResponseFormat.buildFailedResult("projectId and workPackageId must be the same for top level project package");
            }
            ProjectPackageEntity found = projectPackageManager.find(projectId);
            if (found != null) {
                return ResponseFormat.buildFailedResult("top level project package already exists with the same projectId");
            }
        } else {
            //this is sub level
            ProjectPackageEntity project = projectPackageManager.find(projectId);
            if (project == null) {
                return ResponseFormat.buildFailedResult("projectId does not exist but you are trying to make sublevel wp");
            }
            ProjectPackageEntity found = projectPackageManager.find(projectId, workPackageId);
            if (found != null) {
                return ResponseFormat.buildFailedResult("workPackageId already exists under this project, try another workPackageId");
            }
        }

        //validating parentWorkPackage
        ProjectPackageEntity parentProjectPackage = null;
        if (parentWorkPackageId != null) {
            parentProjectPackage = projectPackageManager.find(projectId, parentWorkPackageId);
            if (parentProjectPackage == null) {
                return ResponseFormat.buildFailedResult("parentWorkPackageId does not exist under this project, try another parentWorkPackageId");
            } else {
                //cannot be divided if parent has timesheet
                if (parentProjectPackage.getTimesheetrowEntities().size() > 0) {
                    return ResponseFormat.buildFailedResult("this parentWorkPackage cannot be divided because it already has timesheet submitted");
                }
            }
        }

        //validating manager
        EmployeeEntity manager = employeeManager.find(managerId);
        if (manager == null) {
            return ResponseFormat.buildFailedResult("managerId does not exist, try another managerId");
        }

        //validating assistant
        EmployeeEntity assistant = null;
        if (assistantId != null) {
            assistant = employeeManager.find(assistantId);
            if (assistant == null) {
                return ResponseFormat.buildFailedResult("assistantId does not exist, try another assistantId");
            }
        }

        //validating employees
        List<EmployeeEntity> employeeList = new ArrayList<EmployeeEntity>();
        List<EmployeeEntity> availableEmp = new ArrayList<>();

        if (parentWorkPackageId != null) {
            // Sub WP, only parent WP emp is allowed
            parentProjectPackage = projectPackageManager.find(projectId, parentWorkPackageId);
            Set<EmployeeEntity> parentWPEmp = parentProjectPackage.getEmployeeEntities();

            for(EmployeeEntity emp : parentWPEmp){
                availableEmp.add(emp);
            }

        }else{
            // Root parent WP, all emp is allowed
            availableEmp = employeeManager.findAll();
        }

        for (String employeeId : employees) {
            EmployeeEntity employee = employeeManager.find(employeeId);
            if (employee == null) {
                return ResponseFormat.buildFailedResult("one of employees does not exist, try another employeeIds");
            }

            if(!availableEmp.contains(employee)){
                return ResponseFormat.buildFailedResult("one of employees is not from parent work package employee list");
            }
            employeeList.add(employee);
        }

        // all paygrade in personDays must be valid
        if (personDays != null) {
            for (String payGrade : personDays.keySet()) {
                if (paygradeManager.find(payGrade).size() == 0) {
                    return ResponseFormat.buildFailedResult("paygrade " + payGrade + " does not exist, try another paygrade");
                }
            }
        }

        //create projectPackage
        ProjectPackageEntity projectPackageEntity = new ProjectPackageEntity();
        projectPackageEntity.setProjectId(projectId);
        projectPackageEntity.setWorkPackageId(workPackageId);
        projectPackageEntity.setManagerEntity(manager);
        projectPackageEntity.setAssistantEntity(assistant);
        projectPackageEntity.setIsOpen(isOpen);
        projectPackageEntity.setEmployeeEntities(new HashSet<>(employeeList));
        projectPackageEntity.setParentProjectPackageEntity(parentProjectPackage);
        projectPackageEntity.setPersonDays(personDays);
        projectPackageEntity.setCreatedOn(Date.valueOf(LocalDate.now()));
        projectPackageManager.persist(projectPackageEntity);

        return ResponseFormat.buildSuccessResult("created projectPackage successfully");
    }

    @Path("/")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getAllProjectPackages(@QueryParam("page") Integer page, @QueryParam("size") Integer size, @QueryParam("employeeId") String employeeId) {

        // if employeeId is not specified, current logged inuser is assumed
        EmployeeEntity perspectiveEmployee = null;
        if (employeeId != null) {
            perspectiveEmployee = employeeManager.find(employeeId);
            if (perspectiveEmployee == null) {
                return ResponseFormat.buildFailedResult("employeeId does not exist, try another employeeId");
            }
        }   else {
            perspectiveEmployee = (EmployeeEntity)httpServletRequest.getAttribute("requester");
        }

        //if user is employee, only return his/her project packages
        //if user is project manager, return all project he/she is managing
        //if user is project assistant, return all project he/she is assisting
        //if user is admin, return all project packages
        boolean isEmployee = perspectiveEmployee.getRole().equals(Role.EMP.getValue());
        boolean isAdmin = perspectiveEmployee.getRole().equals(Role.ADMIN.getValue());

        List<ProjectPackageEntity> projectPackageEntities = new ArrayList<ProjectPackageEntity>();

        if (isAdmin){
            projectPackageEntities.addAll(projectPackageManager.findAllRoot());
        }

        if (isEmployee){
            List<ProjectPackageEntity> managing = projectPackageManager.findByManagerId(perspectiveEmployee.getEmployeeId());
            List<ProjectPackageEntity> assisting = projectPackageManager.findByAssistantId(perspectiveEmployee.getEmployeeId());
            List<ProjectPackageEntity> participating = projectPackageManager.findByParticipantId(perspectiveEmployee.getEmployeeId());

            //merge all lists
            Set<ProjectPackageEntity> uniqueSet = new HashSet<ProjectPackageEntity>();
            uniqueSet.addAll(managing);
            uniqueSet.addAll(assisting);
            uniqueSet.addAll(participating);

            projectPackageEntities.addAll(uniqueSet);
        }

        List<JSONProjectPackage> jsonProjectPackages = new ArrayList<JSONProjectPackage>();
        for (ProjectPackageEntity projectPackageEntity : projectPackageEntities) {
            JSONProjectPackage jsonProjectPackage = new JSONProjectPackage(projectPackageEntity, paygradeManager);
            BigDecimal totalCost = BigDecimal.ZERO;
            for (Map.Entry<String,String> personDay : projectPackageEntity.getPersonDays().entrySet()) {
                BigDecimal rate = paygradeManager.find(personDay.getKey()).get(0).getRate();
                BigDecimal day = new BigDecimal(personDay.getValue());
                totalCost = totalCost.add(day.multiply(BigDecimal.valueOf(24)).multiply(rate));
            }
            jsonProjectPackage.setCostEstimate(totalCost);
            jsonProjectPackages.add(jsonProjectPackage);
        }
        if (page == null || size == null) {
            return ResponseFormat.buildSuccessResult(jsonProjectPackages);
        }
        return ResponseFormat.buildSuccessResult(Util.getPage(jsonProjectPackages, page, size));
    }

    @Path("/{projectId}/{workPackageId}")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getASpecificWpByProjectIdAndWpId(@PathParam("projectId") String projectId, @PathParam("workPackageId") String workPackageId) {
        if (projectId == null || workPackageId == null) {
            return ResponseFormat.buildErrorResult("projectId or workPackageId was not provided.");
        }

        ProjectPackageEntity found = projectPackageManager.find(projectId, workPackageId);
        if (found == null) {
            return ResponseFormat.buildErrorResult("No such workpackage");
        }

        return ResponseFormat.buildSuccessResult(new JSONProjectPackage(found, paygradeManager));
    }

    @Path("/{projectId}/{wpId}/spent")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getHourSpentByProjectIdAndWpId(@PathParam("projectId") String projectId, @PathParam("wpId") String workPackageId) {
        if (projectId == null || workPackageId == null) {
            return ResponseFormat.buildErrorResult("projectId or workPackageId was not provided.");
        }

        ProjectPackageEntity found = projectPackageManager.find(projectId, workPackageId);
        if (found == null) {
            return ResponseFormat.buildErrorResult("No such workpackage");
        }

        float hoursSpent = getHoursSpent(found);

        return ResponseFormat.buildSuccessResult(hoursSpent);
    }

    public float getHoursSpent(ProjectPackageEntity projPackage){
        float hours = 0;

        if(projPackage == null){
            return hours;
        }

        // Get timesheet hours of children wp (RECURSION)
        Set<ProjectPackageEntity> childrenWP = projPackage.getChildWorkPackages();
        float childrenHours = 0;
        for(ProjectPackageEntity p : childrenWP){
            childrenHours += getHoursSpent(p);
        }

        // Get timesheet hours of current wp
        float thisProjHours = 0;
        Integer thisProjNum = projPackage.getProjectPackageNum();
        List<TimesheetrowEntity> timesheetRowList = timesheetRowManager.find(thisProjNum.toString());

        for(TimesheetrowEntity tsr : timesheetRowList){
            Byte tsStatusByte = tsr.getTimesheetEntity().getIsApproved();
            ApproveStatus tsStatus = ApproveStatus.getStatus(tsStatusByte);
            if(tsStatus != null && tsStatus.equals(ApproveStatus.APPROVED)){
                // Add hours if TS is approved
                BigDecimal sum = new BigDecimal(0);
                sum = sum.add(tsr.getMon());
                sum = sum.add(tsr.getTue());
                sum = sum.add(tsr.getWed());
                sum = sum.add(tsr.getThu());
                sum = sum.add(tsr.getFri());
                sum = sum.add(tsr.getSat());
                sum = sum.add(tsr.getSun());

                thisProjHours += sum.floatValue();
            }
        }

        hours += thisProjHours;
        hours += childrenHours;

        return hours;
    }

    @Path("/{projectId}/{workPackageId}/parentEmployees")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getParentPackageEmployees(@PathParam("projectId") String projectId, @PathParam("workPackageId") String workPackageId) {
        if (projectId == null || workPackageId == null) {
            return ResponseFormat.buildErrorResult("projectId or workPackageId was not provided.");
        }

        ProjectPackageEntity foundPackage = projectPackageManager.find(projectId, workPackageId);
        if (foundPackage == null) {
            return ResponseFormat.buildErrorResult("No such workpackage");
        }

        ArrayList<JSONEmployee> availableEmp = new ArrayList<>();
        if(foundPackage.getParentProjectPackageEntity() != null){
            // Has parent wp
            Set<EmployeeEntity> parentEmps = foundPackage.getParentProjectPackageEntity().getEmployeeEntities();
            if (parentEmps != null) {
                for (EmployeeEntity employee : parentEmps) {
                    availableEmp.add(new JSONEmployee(employee));
                }
            }
        }else{
            // Root parent wp

            List<EmployeeEntity> allEmp = employeeManager.findAll();
            if (allEmp != null) {
                for (EmployeeEntity employee : allEmp) {
                    availableEmp.add(new JSONEmployee(employee));
                }
            }

        }
        return ResponseFormat.buildSuccessResult(availableEmp);
    }

    @Path("/{projectId}/{workPackageId}")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> updatePackages(Map<String, Object> projectPackage, @PathParam("projectId") String projectId, @PathParam("workPackageId") String workPackageId) {
        String newProjectId;
        String newWorkPackageId;
        String newManagerId;
        String newAssistantId;
        Boolean newIsOpen;
        ArrayList<String> newEmployees;
        String newParentWorkPackageId;
        Map<String, String> newPersonDays;
        try {
            newProjectId = (String) projectPackage.get("projectId");
            newWorkPackageId = (String) projectPackage.get("workPackageId");
            newManagerId = (String) projectPackage.get("managerId");
            newAssistantId = (String) projectPackage.get("assistantId");
            newIsOpen = (Boolean) projectPackage.get("isOpen");
            newEmployees = projectPackage.get("employees") == null ? null : new ArrayList<String>((Collection<String>)projectPackage.get("employees"));
            newParentWorkPackageId = (String) projectPackage.get("parentWorkPackageId");
            newPersonDays = projectPackage.get("personDays") == null ? null : (Map<String, String>) projectPackage.get("personDays");
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult("Something went wrong. Please check your input.");
        }

        ProjectPackageEntity oldProjectPackage = projectPackageManager.find(projectId, workPackageId);
        if (oldProjectPackage == null) {
            return ResponseFormat.buildFailedResult("projectPackage not found");
        }

        if (projectPackageManager.find(newProjectId) == null) {
            return ResponseFormat.buildFailedResult("projectId does not exist, try another projectId");
        }
        oldProjectPackage.setProjectId(newProjectId);

        String projId = newProjectId == null ? projectId : newProjectId;
        if (!newWorkPackageId.equals(oldProjectPackage.getWorkPackageId()) && projectPackageManager.find(projId, newWorkPackageId) != null) {
            return ResponseFormat.buildFailedResult("workPackageId already exists, try another workPackageId");
        }
        oldProjectPackage.setWorkPackageId(newWorkPackageId);

        EmployeeEntity newManager = employeeManager.find(newManagerId);
        if (newManager == null) {
            return ResponseFormat.buildFailedResult("managerId does not exist, try another managerId");
        }
        oldProjectPackage.setManagerEntity(newManager);

        EmployeeEntity newAssistant = null;
        if (newAssistantId != null) {
            newAssistant = employeeManager.find(newAssistantId);
            if (newAssistant == null) {
                return ResponseFormat.buildFailedResult("assistantId does not exist, try another assistantId");
            }
        }
        oldProjectPackage.setAssistantEntity(newAssistant);

        // all paygrade in personDays must be valid
        if (newPersonDays != null) {
            for (String payGrade : newPersonDays.keySet()) {
                if (paygradeManager.find(payGrade).size() == 0) {
                    return ResponseFormat.buildFailedResult("paygrade " + payGrade + " does not exist, try another paygrade");
                }
            }
        }
        oldProjectPackage.setPersonDays(newPersonDays);

        Util.recursivelySetStatus(oldProjectPackage, newIsOpen, projectPackageManager);

        if (newEmployees != null) {
            List<EmployeeEntity> employeeEntities = new ArrayList<EmployeeEntity>();

            //Get parent emp list
            ProjectPackageEntity parentProjectPackage = oldProjectPackage.getParentProjectPackageEntity();
            Set<EmployeeEntity> parentEmpSet = parentProjectPackage == null ? null : parentProjectPackage.getEmployeeEntities();

            for (String employeeId : newEmployees) {
                EmployeeEntity employeeEntity = employeeManager.find(employeeId);
                if (employeeEntity == null) {
                    return ResponseFormat.buildFailedResult("one of employees does not exist, try another employeeIds");
                }

                if(parentProjectPackage != null && !parentEmpSet.contains(employeeEntity)){
                    // Emp not in parent WP
                    return ResponseFormat.buildFailedResult("one of employees does not belong to parent work package, try another employeeIds");
                }
                employeeEntities.add(employeeEntity);
            }
            oldProjectPackage.setEmployeeEntities(new HashSet<>(employeeEntities));
        }

        projId = newProjectId == null ? projectId : newProjectId;
        ProjectPackageEntity parentProjectPackage = projectPackageManager.find(projId, newParentWorkPackageId);
        if (newParentWorkPackageId != null && parentProjectPackage == null) {
            return ResponseFormat.buildFailedResult("parentWorkPackageId does not exist, try another parentWorkPackageId");
        }

        oldProjectPackage.setParentProjectPackageEntity(newParentWorkPackageId == null ? null : parentProjectPackage);

        projectPackageManager.merge(oldProjectPackage);
        return ResponseFormat.buildSuccessResult("update projectPackage successfully");
    }

    //get a project package by projectId
    @Path("/{projectId}")
    @GET
    @Produces("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getProjectPackageByProjectId(@PathParam("projectId") String projectId) {
        //if employee, only return his/her project packages
        EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");
        boolean isEmployee = requester.getRole().equals(Role.EMP.getValue());

        ProjectPackageEntity projectPackages = projectPackageManager.find(projectId);
        if (isEmployee && !projectPackages.getEmployeeEntities().contains(requester)) {
            return ResponseFormat.buildFailedResult("you are not a member of this project");
        }
        if (projectPackages == null) {
            return ResponseFormat.buildFailedResult("projectPackage not found");
        }

        JSONProjectPackage jsonProjectPackage = new JSONProjectPackage(projectPackages, paygradeManager);
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Map.Entry<String,String> personDay : projectPackages.getPersonDays().entrySet()) {
            BigDecimal rate = paygradeManager.find(personDay.getKey()).get(0).getRate();
            BigDecimal day = new BigDecimal(personDay.getValue());
            totalCost = totalCost.add(day.multiply(BigDecimal.valueOf(24)).multiply(rate));
        }
        jsonProjectPackage.setCostEstimate(totalCost);
        return ResponseFormat.buildSuccessResult(jsonProjectPackage);
    }



    @PUT
    @Path("/{projectId}/{wpId}/add")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> addEmployeeToProject(@PathParam("projectId") String projectId, @PathParam("wpId") String wpId, HashMap<String, Collection<String>>projInfo) {

        // Check project
        ProjectPackageEntity projectEntity = projectPackageManager.find(projectId, wpId);
        if (projectEntity == null) {
            return ResponseFormat.buildFailedResult("No such project package");
        }

        // Check new project employees
        Collection<String> newProjWorkerIds = projInfo.get("employees");
        Collection<EmployeeEntity> newEmpList = new ArrayList<>();

        for(String newEmpId : newProjWorkerIds){
            EmployeeEntity employee = employeeManager.find(newEmpId);
            if (employee == null) {
                return ResponseFormat.buildErrorResult("employee not found.");
            }else{
                newEmpList.add(employee);
            }
        }

        // Assign new emp to proj
        Collection<EmployeeEntity> currProjEmployees = projectEntity.getEmployeeEntities();

        for(EmployeeEntity newEmp : newEmpList){
            // Add if not exist
            if(!currProjEmployees.contains(newEmp)){
                currProjEmployees.add(newEmp);
            }
        }

        projectEntity.setEmployeeEntities(new HashSet<>(currProjEmployees));

        try {
            projectPackageManager.merge(projectEntity);
        } catch (Exception e) {
            return ResponseFormat.buildFailedResult(e.getMessage());
        }

        return ResponseFormat.buildSuccessResult("Add employee to project successfully");
    }

    @PUT
    @Path("/{projectId}/{wpId}/swap")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> swapEmployeeList(@PathParam("projectId") String projectId, @PathParam("wpId") String wpId, HashMap<String, Collection<String>>projInfo) {

        // Check project
        ProjectPackageEntity projectEntity = projectPackageManager.find(projectId, wpId);
        if (projectEntity == null) {
            return ResponseFormat.buildFailedResult("No such project package");
        }

        // Check new project employees
        Collection<String> newProjWorkerIds = projInfo.get("employees");
        Collection<EmployeeEntity> newEmpList = new ArrayList<>();

        for(String newEmpId : newProjWorkerIds){
            EmployeeEntity employee = employeeManager.find(newEmpId);
            if (employee == null) {
                return ResponseFormat.buildErrorResult("employee not found.");
            }else{
                newEmpList.add(employee);
            }
        }

        projectEntity.setEmployeeEntities(new HashSet<EmployeeEntity>(newEmpList));

        try {
            projectPackageManager.merge(projectEntity);
        } catch (Exception e) {
            return ResponseFormat.buildFailedResult(e.getMessage());
        }

        return ResponseFormat.buildSuccessResult("Swap employee list successfully");
    }

    @PUT
    @Path("/{projectId}/{wpId}/remove")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> removeEmployeeToProject(@PathParam("projectId") String projectId, @PathParam("wpId") String wpId, HashMap<String, Collection<String>>projInfo) {

        // Check project
        ProjectPackageEntity projectEntity = projectPackageManager.find(projectId, wpId);
        if (projectEntity == null) {
            return ResponseFormat.buildFailedResult("No such project package");
        }

        // Check new project employees
        Collection<String> newProjWorkerIds = projInfo.get("employees");
        Collection<EmployeeEntity> newEmpList = new ArrayList<>();

        for(String newEmpId : newProjWorkerIds){
            EmployeeEntity employee = employeeManager.find(newEmpId);
            if (employee == null) {
                return ResponseFormat.buildErrorResult("employee not found.");
            }else{
                newEmpList.add(employee);
            }
        }

        // Remove given emp from project
        Collection<EmployeeEntity> currProjEmployees = projectEntity.getEmployeeEntities();

        for(EmployeeEntity delEmp : newEmpList){
            // Remove if exist
            currProjEmployees.remove(delEmp);
        }

        projectEntity.setEmployeeEntities(new HashSet<>(currProjEmployees));

        try {
            projectPackageManager.merge(projectEntity);
        } catch (Exception e) {
            return ResponseFormat.buildFailedResult(e.getMessage());
        }

        return ResponseFormat.buildSuccessResult("Remove employee to project successfully");
    }
}
