package com.comp4911.backend.models.JSONModels;

import com.comp4911.backend.models.TimesheetrowEntity;

import java.math.BigDecimal;

public class JSONTimesheetrow {
    private String employeeFullName;
    private String employeeId;
    private String projectId;
    private String workPackageId;
    private BigDecimal mon;
    private BigDecimal tue;
    private BigDecimal wed;
    private BigDecimal thu;
    private BigDecimal fri;
    private BigDecimal sat;
    private BigDecimal sun;
    private BigDecimal total;

    public JSONTimesheetrow(TimesheetrowEntity timesheetrow) {
        this.projectId = timesheetrow.getProjectPackageEntity().getProjectId();
        this.workPackageId = timesheetrow.getProjectPackageEntity().getWorkPackageId();
        this.mon = timesheetrow.getMon();
        this.tue = timesheetrow.getTue();
        this.wed = timesheetrow.getWed();
        this.thu = timesheetrow.getThu();
        this.fri = timesheetrow.getFri();
        this.sat = timesheetrow.getSat();
        this.sun = timesheetrow.getSun();
        this.total = mon.add(tue).add(wed).add(thu).add(fri).add(sat).add(sun);
        this.employeeFullName = timesheetrow.getEmployeeEntity().getFirstName() + " " + timesheetrow.getEmployeeEntity().getLastName();
        this.employeeId = timesheetrow.getEmployeeEntity().getEmployeeId();
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

    public BigDecimal getMon() {
        return mon;
    }

    public void setMon(BigDecimal mon) {
        this.mon = mon;
    }

    public BigDecimal getTue() {
        return tue;
    }

    public void setTue(BigDecimal tue) {
        this.tue = tue;
    }

    public BigDecimal getWed() {
        return wed;
    }

    public void setWed(BigDecimal wed) {
        this.wed = wed;
    }

    public BigDecimal getThu() {
        return thu;
    }

    public void setThu(BigDecimal thu) {
        this.thu = thu;
    }

    public BigDecimal getFri() {
        return fri;
    }

    public void setFri(BigDecimal fri) {
        this.fri = fri;
    }

    public BigDecimal getSat() {
        return sat;
    }

    public void setSat(BigDecimal sat) {
        this.sat = sat;
    }

    public BigDecimal getSun() {
        return sun;
    }

    public void setSun(BigDecimal sun) {
        this.sun = sun;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public void setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
