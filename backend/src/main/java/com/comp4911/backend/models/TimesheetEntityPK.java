package com.comp4911.backend.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Date;

public class TimesheetEntityPK implements Serializable {
    private Integer employeeNum;
    private Date weekEnding;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimesheetEntityPK that = (TimesheetEntityPK) o;

        if (employeeNum != null ? !employeeNum.equals(that.employeeNum) : that.employeeNum != null) return false;
        if (weekEnding != null ? !weekEnding.equals(that.weekEnding) : that.weekEnding != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = employeeNum != null ? employeeNum.hashCode() : 0;
        result = 31 * result + (weekEnding != null ? weekEnding.hashCode() : 0);
        return result;
    }
}
