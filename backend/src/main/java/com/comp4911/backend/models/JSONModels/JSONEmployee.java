package com.comp4911.backend.models.JSONModels;

import com.comp4911.backend.models.CredentialEntity;
import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.PaygradeEntity;

public class JSONEmployee {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean isDisabled;
    private JSONPaygrade paygradeEntity;
    private JSONCredential credentialEntity;
    private JSONEmployee supervisor;
    private JSONEmployee approver;

    public JSONEmployee() {}

    public JSONEmployee(EmployeeEntity employeeEntity) {
        employeeId = employeeEntity.getEmployeeId();
        firstName = employeeEntity.getFirstName();
        lastName = employeeEntity.getLastName();
        role = employeeEntity.getRole();
        isDisabled = employeeEntity.getIsDisabled() != 0;
        credentialEntity = new JSONCredential(employeeEntity.getCredentialEntity());
        supervisor = employeeEntity.getSupervisor()==null?null:new JSONEmployee(employeeEntity.getSupervisor());
        approver = employeeEntity.getTimesheetApprover()==null?null:new JSONEmployee(employeeEntity.getTimesheetApprover());
        paygradeEntity =  employeeEntity.getPaygradeEntity()==null?null:new JSONPaygrade(employeeEntity.getPaygradeEntity());
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public JSONCredential getCredentialEntity() {
        return credentialEntity;
    }

    public void setCredentialEntity(JSONCredential credentialEntity) {
        this.credentialEntity = credentialEntity;
    }

    public JSONEmployee getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(JSONEmployee supervisor) {
        this.supervisor = supervisor;
    }

    public JSONEmployee getApprover() {
        return approver;
    }

    public void setApprover(JSONEmployee approver) {
        this.approver = approver;
    }

    public JSONPaygrade getPaygradeEntity() {
        return paygradeEntity;
    }

    public void setPaygradeEntity(JSONPaygrade paygradeEntity) {
        this.paygradeEntity = paygradeEntity;
    }
}
