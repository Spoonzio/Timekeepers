package com.comp4911.backend.api;

import com.comp4911.backend.access.CredentialManager;
import com.comp4911.backend.access.ProjectPackageManager;
import com.comp4911.backend.lib.Constants;
import com.comp4911.backend.lib.ResponseFormat;
import com.comp4911.backend.lib.Token;
import com.comp4911.backend.models.CredentialEntity;
import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.ProjectPackageEntity;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Path("/auth")
public class AuthenticationResource {

    @Inject
    private CredentialManager credentialManager;

    @Inject
    private ProjectPackageManager projectPackageManager;

    @POST
    @Path("/login")
    @Produces("application/json")
    @Consumes("application/json")
    public Map<String, Object> login(HashMap<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseFormat.buildFailedResult("username and password are required");
        }
        CredentialEntity ce = credentialManager.find(username);
        if (ce == null) {
            return ResponseFormat.buildFailedResult("user does not exist");
        }

        boolean isValid = false;
        try {
            //Hash pw using username as salt
            //isValid = PBKDF2.verifyPassword(password, username, ce.getPassword());

            // Compare pw without HASHing
            isValid = password.equals(ce.getPassword());
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult(e.getMessage());
        }

        if (!isValid) {
            return ResponseFormat.buildFailedResult("invalid password");
        }

        // Update token if expired
        if (ce.getToken() == null || ce.getTimestamp() == null || ce.getTimestamp().before(new Timestamp(System.currentTimeMillis()))) {
            String token = Token.generateToken();
            ce.setToken(token);
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        now.setTime(now.getTime() + TimeUnit.MINUTES.toMillis(Constants.LOGIN_TIMEOUT_IN_MINUTES));
        ce.setTimestamp(now);
        credentialManager.merge(ce);

        HashMap<String, Object> response = new HashMap<>();
        response.put("token", ce.getToken());
        response.put("username", username);
        response.put("employeeId", ce.getEmployeeEntity().getEmployeeId());

        //determine the role of the user
        EmployeeEntity employeeEntity = ce.getEmployeeEntity();
        ArrayList<String> roles = new ArrayList<>();
        roles.add(employeeEntity.getRole());
        //if the user is a manager of any projects, add the Project Manager role
        if (projectPackageManager.findByManagerId(employeeEntity.getEmployeeId()).size() > 0) {
            roles.add("Project Manager");
        }
        //if the user is timesheet approver, add the Timesheet Approver role
        if (employeeEntity.getTimesheetApproving().size() > 0) {
            roles.add("Timesheet Approver");
        }
        //if the user is responsible engineer, add the Responsible Engineer role
        if (projectPackageManager.findByResponsibleEngineer(employeeEntity.getEmployeeId()).size() > 0) {
            roles.add("Responsible Engineer");
        }

        //if the user is a supervisor, add the Supervisor role
        if (employeeEntity.getSupervising().size() > 0) {
            roles.add("Supervisor");
        }

        //if the user is a Project Assistant, add the Project Assistant role
        if (projectPackageManager.findByAssistantId(employeeEntity.getEmployeeId()).size() > 0) {
            roles.add("Project Assistant");
        }

        response.put("roles", roles);
        response.put("role", employeeEntity.getRole());

        return ResponseFormat.buildSuccessResult(response, "login successful");
    }
}