package com.comp4911.backend.models.JSONModels;

import com.comp4911.backend.lib.ApproveStatus;
import com.comp4911.backend.lib.Constants;
import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.TimesheetEntity;
import com.comp4911.backend.models.TimesheetrowEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JSONTimesheet {
    private String employeeId;
    private String employeeFullName;
    private String weekEnding;
    private Byte isApproved;
    private BigDecimal availableFlexTime;
    private BigDecimal flexTimeUsed;
    private BigDecimal totalHours;
    private BigDecimal overtime;
    private String approverId;
    private String approverFullName;
    private String supervisorId;
    private String superviroFullName;
    private List<JSONTimesheetrow> timesheetrows;

    public JSONTimesheet(TimesheetEntity timesheetEntity) {
        this.employeeId = timesheetEntity.getEmployeeEntity().getEmployeeId();
        this.weekEnding = timesheetEntity.getWeekEnding().toString();
        this.isApproved = timesheetEntity.getIsApproved();
        this.timesheetrows = new ArrayList<>();
        totalHours = new BigDecimal(0);
        for (TimesheetrowEntity row : timesheetEntity.getTimesheetRowEntities()) {
            this.timesheetrows.add(new JSONTimesheetrow(row));
            totalHours = totalHours.add(row.getMon()).add(row.getTue()).add(row.getWed()).add(row.getThu()).add(row.getFri()).add(row.getSat()).add(row.getSun());
        }
        this.employeeFullName = timesheetEntity.getEmployeeEntity().getFirstName() + " " + timesheetEntity.getEmployeeEntity().getLastName();

        this.availableFlexTime = timesheetEntity.getEmployeeEntity().getFlexTime();

        flexTimeUsed = timesheetEntity.getFlexTime() == null ? new BigDecimal(0) : timesheetEntity.getFlexTime();
        totalHours = totalHours.add(flexTimeUsed == null ? new BigDecimal(0) : flexTimeUsed);
        overtime = totalHours.subtract(Constants.MINIMUM_WORKING_HOURS);

        EmployeeEntity approver = timesheetEntity.getEmployeeEntity().getTimesheetApprover();
        approverId = (approver == null) ? null : approver.getEmployeeId();
        approverFullName = (approver == null) ? null : approver.getFirstName() + " " + approver.getLastName();

        EmployeeEntity supervisor = timesheetEntity.getEmployeeEntity().getSupervisor();
        supervisorId = (supervisor == null) ? null : supervisor.getEmployeeId();
        superviroFullName = (supervisor == null) ? null : supervisor.getFirstName() + " " + supervisor.getLastName();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getWeekEnding() {
        return weekEnding;
    }

    public void setWeekEnding(String weekEnding) {
        this.weekEnding = weekEnding;
    }

    public String getIsApproved() {
        return ApproveStatus.getStatus(isApproved).getKey();
    }

    public void setIsApproved(Byte isApproved) {
        this.isApproved = isApproved;
    }

    public List<JSONTimesheetrow> getTimesheetrows() {
        return timesheetrows;
    }

    public void setTimesheetrows(List<JSONTimesheetrow> timesheetrows) {
        this.timesheetrows = timesheetrows;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public void setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
    }

    public BigDecimal getAvailableFlexTime() {
        return availableFlexTime;
    }

    public void setAvailableFlexTime(BigDecimal availableFlexTime) {
        this.availableFlexTime = availableFlexTime;
    }

    public BigDecimal getFlexTimeUsed() {
        return flexTimeUsed;
    }

    public void setFlexTimeUsed(BigDecimal flexTimeUsed) {
        this.flexTimeUsed = flexTimeUsed;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public BigDecimal getOvertime() {
        return overtime;
    }

    public void setOvertime(BigDecimal overtime) {
        this.overtime = overtime;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getApproverFullName() {
        return approverFullName;
    }

    public void setApproverFullName(String approverFullName) {
        this.approverFullName = approverFullName;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getSuperviroFullName() {
        return superviroFullName;
    }

    public void setSuperviroFullName(String superviroFullName) {
        this.superviroFullName = superviroFullName;
    }
}
