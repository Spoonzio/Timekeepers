package com.comp4911.backend.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Date;

public class TimesheetrowEntityPK implements Serializable {
    private Integer employeeNum;
    private Date weekEnding;
    private Integer projectPackageNum;

    public TimesheetrowEntityPK() {}

    public TimesheetrowEntityPK(Integer employeeNum, Date weekEnding, Integer projectPackageNum) {
        this.employeeNum = employeeNum;
        this.weekEnding = weekEnding;
        this.projectPackageNum = projectPackageNum;
    }

    @Column(name = "employeeNum")
    @Id
    public Integer getEmployeeNum() {
        return employeeNum;
    }

    public void setEmployeeNum(Integer employeeNum) {
        this.employeeNum = employeeNum;
    }

    @Column(name = "weekEnding")
    @Id
    public Date getWeekEnding() {
        return weekEnding;
    }

    public void setWeekEnding(Date weekEnding) {
        this.weekEnding = weekEnding;
    }

    @Column(name = "projectPackageNum")
    @Id
    public Integer getProjectPackageNum() {
        return projectPackageNum;
    }

    public void setProjectPackageNum(Integer projectPackageNum) {
        this.projectPackageNum = projectPackageNum;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimesheetrowEntityPK that = (TimesheetrowEntityPK) o;

        if (employeeNum != null ? !employeeNum.equals(that.employeeNum) : that.employeeNum != null) return false;
        if (weekEnding != null ? !weekEnding.equals(that.weekEnding) : that.weekEnding != null) return false;
        if (projectPackageNum != null ? !projectPackageNum.equals(that.projectPackageNum) : that.projectPackageNum != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = employeeNum != null ? employeeNum.hashCode() : 0;
        result = 31 * result + (weekEnding != null ? weekEnding.hashCode() : 0);
        result = 31 * result + (projectPackageNum != null ? projectPackageNum.hashCode() : 0);
        return result;
    }
}
