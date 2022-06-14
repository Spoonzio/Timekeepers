package com.comp4911.backend.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "timesheetrow", schema = "timesheet")
@IdClass(TimesheetrowEntityPK.class)
public class TimesheetrowEntity {
    private Integer employeeNum;
    private Date weekEnding;
    private Integer projectPackageNum;
    private BigDecimal mon;
    private BigDecimal tue;
    private BigDecimal wed;
    private BigDecimal thu;
    private BigDecimal fri;
    private BigDecimal sat;
    private BigDecimal sun;
    private EmployeeEntity employeeEntity;
    private ProjectPackageEntity projectPackageEntity;
    private TimesheetEntity timesheetEntity;

    @Id
    @Column(name = "employeeNum")
    public Integer getEmployeeNum() {
        return employeeNum;
    }

    public void setEmployeeNum(Integer employeeNum) {
        this.employeeNum = employeeNum;
    }

    @Id
    @Column(name = "weekEnding")
    public Date getWeekEnding() {
        return weekEnding;
    }

    public void setWeekEnding(Date weekEnding) {
        this.weekEnding = weekEnding;
    }

    @Id
    @Column(name = "projectPackageNum")
    public Integer getProjectPackageNum() {
        return projectPackageNum;
    }

    public void setProjectPackageNum(Integer projectPackageNum) {
        this.projectPackageNum = projectPackageNum;
    }

    @Basic
    @Column(name = "mon")
    public BigDecimal getMon() {
        return mon;
    }

    public void setMon(BigDecimal man) {
        this.mon = man;
    }

    @Basic
    @Column(name = "tue")
    public BigDecimal getTue() {
        return tue;
    }

    public void setTue(BigDecimal tue) {
        this.tue = tue;
    }

    @Basic
    @Column(name = "wed")
    public BigDecimal getWed() {
        return wed;
    }

    public void setWed(BigDecimal wed) {
        this.wed = wed;
    }

    @Basic
    @Column(name = "thu")
    public BigDecimal getThu() {
        return thu;
    }

    public void setThu(BigDecimal thu) {
        this.thu = thu;
    }

    @Basic
    @Column(name = "fri")
    public BigDecimal getFri() {
        return fri;
    }

    public void setFri(BigDecimal fri) {
        this.fri = fri;
    }

    @Basic
    @Column(name = "sat")
    public BigDecimal getSat() {
        return sat;
    }

    public void setSat(BigDecimal sat) {
        this.sat = sat;
    }

    @Basic
    @Column(name = "sun")
    public BigDecimal getSun() {
        return sun;
    }

    public void setSun(BigDecimal sun) {
        this.sun = sun;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimesheetrowEntity that = (TimesheetrowEntity) o;

        if (employeeNum != null ? !employeeNum.equals(that.employeeNum) : that.employeeNum != null) return false;
        if (weekEnding != null ? !weekEnding.equals(that.weekEnding) : that.weekEnding != null) return false;
        if (projectPackageNum != null ? !projectPackageNum.equals(that.projectPackageNum) : that.projectPackageNum != null) return false;
        if (mon != null ? !mon.equals(that.mon) : that.mon != null) return false;
        if (tue != null ? !tue.equals(that.tue) : that.tue != null) return false;
        if (wed != null ? !wed.equals(that.wed) : that.wed != null) return false;
        if (thu != null ? !thu.equals(that.thu) : that.thu != null) return false;
        if (fri != null ? !fri.equals(that.fri) : that.fri != null) return false;
        if (sat != null ? !sat.equals(that.sat) : that.sat != null) return false;
        if (sun != null ? !sun.equals(that.sun) : that.sun != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = employeeNum != null ? employeeNum.hashCode() : 0;
        result = 31 * result + (weekEnding != null ? weekEnding.hashCode() : 0);
        result = 31 * result + (projectPackageNum != null ? projectPackageNum.hashCode() : 0);
        result = 31 * result + (mon != null ? mon.hashCode() : 0);
        result = 31 * result + (tue != null ? tue.hashCode() : 0);
        result = 31 * result + (wed != null ? wed.hashCode() : 0);
        result = 31 * result + (thu != null ? thu.hashCode() : 0);
        result = 31 * result + (fri != null ? fri.hashCode() : 0);
        result = 31 * result + (sat != null ? sat.hashCode() : 0);
        result = 31 * result + (sun != null ? sun.hashCode() : 0);
        return result;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employeeNum", referencedColumnName = "employeeNum", nullable = false, insertable = false, updatable = false)
    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public void setEmployeeEntity(EmployeeEntity employeeEntity) {
        this.employeeEntity = employeeEntity;
    }

    @ManyToOne
    @JoinColumn(name = "projectPackageNum", referencedColumnName = "projectPackageNum", nullable = false, insertable = false, updatable = false)
    public ProjectPackageEntity getProjectPackageEntity() {
        return projectPackageEntity;
    }

    public void setProjectPackageEntity(ProjectPackageEntity projectPackageEntity) {
        this.projectPackageEntity = projectPackageEntity;
    }

    @ManyToOne
    @JoinColumns({@JoinColumn(name = "employeeNum", referencedColumnName = "employeeNum", nullable = false, insertable = false, updatable = false),@JoinColumn(name = "weekEnding", referencedColumnName = "weekEnding", nullable = false, insertable = false, updatable = false)})
    public TimesheetEntity getTimesheetEntity() {
        return timesheetEntity;
    }

    public void setTimesheetEntity(TimesheetEntity timesheetEntity) {
        this.timesheetEntity = timesheetEntity;
    }
}
