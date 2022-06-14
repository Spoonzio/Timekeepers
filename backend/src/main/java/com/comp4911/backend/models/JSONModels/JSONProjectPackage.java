package com.comp4911.backend.models.JSONModels;

import com.comp4911.backend.access.PaygradeManager;
import com.comp4911.backend.lib.ApproveStatus;
import com.comp4911.backend.lib.Util;
import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.ProjectPackageEntity;
import com.comp4911.backend.models.TimesheetrowEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JSONProjectPackage {
    private String parentWorkPackageId;
    private String projectId;
    private String workPackageId;
    private Boolean isOpen;
    private String managerId;
    private String manager;
    private String assistantId;
    private String assistant;
    private BigDecimal hourSpent;
    private BigDecimal costEstimate;
    private Map<String, String> personDays;
    private List<JSONEmployee> employeeEntities;
    private List<JSONProjectPackage> childWorkPackages;

    public JSONProjectPackage() {}

    public JSONProjectPackage(ProjectPackageEntity projectPackageEntity, PaygradeManager paygradeManager) {
        if (projectPackageEntity == null) {
            return;
        }
        this.projectId = projectPackageEntity.getProjectId();
        this.workPackageId = projectPackageEntity.getWorkPackageId();
        this.isOpen = projectPackageEntity.getIsOpen();
        EmployeeEntity managerEntity = projectPackageEntity.getManagerEntity();
        this.manager = managerEntity != null ? (managerEntity.getFirstName() + " " + managerEntity.getLastName()) : null;
        this.managerId = managerEntity != null ? managerEntity.getEmployeeId() : null;
        EmployeeEntity assistantEntity = projectPackageEntity.getAssistantEntity();
        this.assistant = assistantEntity != null ? (assistantEntity.getFirstName() + " " + assistantEntity.getLastName()) : null;
        this.assistantId = assistantEntity != null ? assistantEntity.getEmployeeId() : null;
        this.employeeEntities = new ArrayList<>();
        if (projectPackageEntity.getEmployeeEntities() != null) {
            for (EmployeeEntity employee : projectPackageEntity.getEmployeeEntities()) {
                this.employeeEntities.add(new JSONEmployee(employee));
            }
        }
        this.childWorkPackages = new ArrayList<>();
        if (projectPackageEntity.getChildWorkPackages() != null) {
            for (ProjectPackageEntity childWorkPackage : projectPackageEntity.getChildWorkPackages()) {
                this.childWorkPackages.add(new JSONProjectPackage(childWorkPackage, paygradeManager));
            }
        }
        ProjectPackageEntity parentWorkPackage = projectPackageEntity.getParentProjectPackageEntity();
        this.parentWorkPackageId = parentWorkPackage != null ? parentWorkPackage.getWorkPackageId() : null;
        this.personDays = projectPackageEntity.getPersonDays();

        BigDecimal totalHours = BigDecimal.ZERO;
        Set<TimesheetrowEntity> rows = Util.travelTree(projectPackageEntity);
        if (rows != null) {
            for (TimesheetrowEntity row : rows) {
                if (row.getTimesheetEntity().getIsApproved().equals(ApproveStatus.APPROVED.getValue())) {
                    totalHours = totalHours.add(new JSONTimesheetrow(row).getTotal());
                }
            }
        }
        this.hourSpent = totalHours;

        BigDecimal totalCost = BigDecimal.ZERO;
        for (Map.Entry<String,String> personDay : getPersonDays().entrySet()) {
            BigDecimal rate = paygradeManager.find(personDay.getKey()).get(0).getRate();
            BigDecimal day = new BigDecimal(personDay.getValue());
            totalCost = totalCost.add(day.multiply(BigDecimal.valueOf(24)).multiply(rate));
        }
        this.costEstimate = totalCost;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getWorkPackageId() {
        return workPackageId;
    }

    public void setWorkPackageId(String workPackageId) {
        this.workPackageId = workPackageId;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public List<JSONEmployee> getEmployeeEntities() {
        return employeeEntities;
    }

    public void setEmployeeEntities(List<JSONEmployee> employeeEntities) {
        this.employeeEntities = employeeEntities;
    }

    public List<JSONProjectPackage> getChildWorkPackages() {
        return childWorkPackages;
    }

    public void setChildWorkPackages(List<JSONProjectPackage> childWorkPackages) {
        this.childWorkPackages = childWorkPackages;
    }

    public String getParentWorkPackageId() {
        return parentWorkPackageId;
    }

    public void setParentWorkPackageId(String parentWorkPackageId) {
        this.parentWorkPackageId = parentWorkPackageId;
    }

    public String getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public String getAssistant() {
        return assistant;
    }

    public void setAssistant(String assistant) {
        this.assistant = assistant;
    }

    public Map<String, String> getPersonDays() {
        return personDays;
    }

    public void setPersonDays(Map<String, String> personDays) {
        this.personDays = personDays;
    }

    public BigDecimal getHourSpent() {
        return hourSpent;
    }

    public void setHourSpent(BigDecimal hourSpent) {
        this.hourSpent = hourSpent;
    }

    public BigDecimal getCostEstimate() {
        return costEstimate;
    }

    public void setCostEstimate(BigDecimal costEstimate) {
        this.costEstimate = costEstimate;
    }
}
