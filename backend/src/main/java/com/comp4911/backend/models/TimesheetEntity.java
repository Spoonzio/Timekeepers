package com.comp4911.backend.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "timesheet", schema = "timesheet")
@IdClass(TimesheetEntityPK.class)
public class TimesheetEntity {
    private Integer employeeNum;
    private Date weekEnding;
    private Byte signature;
    private Byte isApproved;
    private EmployeeEntity employeeEntity;
    private BigDecimal flexTime;
    private List<TimesheetrowEntity> timesheetRowEntities;

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

    @Basic
    @Column(name = "signature")
    public Byte getSignature() {
        return signature;
    }

    public void setSignature(Byte signature) {
        this.signature = signature;
    }

    @Basic
    @Column(name = "isApproved")
    public Byte getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Byte isApproved) {
        this.isApproved = isApproved;
    }

    @Basic
    @Column(name = "flexTime")
    public BigDecimal getFlexTime() {
        return flexTime;
    }

    public void setFlexTime(BigDecimal flexTime) {
        this.flexTime = flexTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimesheetEntity that = (TimesheetEntity) o;

        if (employeeNum != null ? !employeeNum.equals(that.employeeNum) : that.employeeNum != null) return false;
        if (weekEnding != null ? !weekEnding.equals(that.weekEnding) : that.weekEnding != null) return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;
        if (isApproved != null ? !isApproved.equals(that.isApproved) : that.isApproved != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = employeeNum != null ? employeeNum.hashCode() : 0;
        result = 31 * result + (weekEnding != null ? weekEnding.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (isApproved != null ? isApproved.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "employeeNum", referencedColumnName = "employeeNum", nullable = false, insertable = false, updatable = false)
    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public void setEmployeeEntity(EmployeeEntity employeeEntity) {
        this.employeeEntity = employeeEntity;
    }

    @OneToMany(mappedBy = "timesheetEntity", fetch = FetchType.EAGER)
    public List<TimesheetrowEntity> getTimesheetRowEntities() {
        return timesheetRowEntities;
    }

    public void setTimesheetRowEntities(List<TimesheetrowEntity> timesheetRowEntities) {
        this.timesheetRowEntities = timesheetRowEntities;
    }
}
