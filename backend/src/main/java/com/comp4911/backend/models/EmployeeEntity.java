package com.comp4911.backend.models;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "employee", schema = "timesheet")
public class EmployeeEntity {
    private Integer employeeNum;
    private String employeeId;
    private String firstName;
    private String lastName;
    private Byte isDisabled;
    private String role;
    private BigDecimal flexTime;
    private BigDecimal vacationTime;
    private CredentialEntity credentialEntity;
    private PaygradeEntity paygradeEntity;
    private EmployeeEntity supervisor;
    private Set<EmployeeEntity> supervising;
    private EmployeeEntity timesheetApprover;
    private Collection<WeeklyReportEntity> monthlyReportEntities;
    private Set<EmployeeEntity> timesheetApproving;
    private Collection<TimesheetEntity> timesheetEntities;
    private Collection<TimesheetrowEntity> timesheetRowEntities;
    private Collection<ProjectPackageEntity> participatingProjectPackageEntities;

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "employeeNum")
    public Integer getEmployeeNum() {
        return employeeNum;
    }

    public void setEmployeeNum(Integer employeeNum) {
        this.employeeNum = employeeNum;
    }

    @Basic
    @Column(name = "employeeId", unique = true)
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "firstName")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "lastName")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Column(name = "isDisabled")
    public Byte getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Byte isDisabled) {
        this.isDisabled = isDisabled;
    }

    @Basic
    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Basic
    @Column(name = "flexTime")
    public BigDecimal getFlexTime() {
        return flexTime;
    }

    public void setFlexTime(BigDecimal flexTime) {
        this.flexTime = flexTime;
    }

    @Basic
    @Column(name = "vacationTime")
    public BigDecimal getVacationTime() {
        return vacationTime;
    }

    public void setVacationTime(BigDecimal vacationTime) {
        this.vacationTime = vacationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeEntity employee = (EmployeeEntity) o;

        if (employeeNum != null ? !employeeNum.equals(employee.employeeNum) : employee.employeeNum != null)
            return false;
        if (employeeId != null ? !employeeId.equals(employee.employeeId) : employee.employeeId != null) return false;
        if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
        if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;
        if (isDisabled != null ? !isDisabled.equals(employee.isDisabled) : employee.isDisabled != null) return false;
        if (role != null ? !role.equals(employee.role) : employee.role != null) return false;
        if (flexTime != null ? !flexTime.equals(employee.flexTime) : employee.flexTime != null) return false;
        if (vacationTime != null ? !vacationTime.equals(employee.vacationTime) : employee.vacationTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = employeeNum != null ? employeeNum.hashCode() : 0;
        result = 31 * result + (employeeId != null ? employeeId.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (isDisabled != null ? isDisabled.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (flexTime != null ? flexTime.hashCode() : 0);
        result = 31 * result + (vacationTime != null ? vacationTime.hashCode() : 0);
        return result;
    }

    @OneToOne(mappedBy = "employeeEntity")
    public CredentialEntity getCredentialEntity() {
        return credentialEntity;
    }

    public void setCredentialEntity(CredentialEntity credentialEntity) {
        this.credentialEntity = credentialEntity;
    }

    @ManyToOne
    @JoinColumn(name = "paygradeId", referencedColumnName = "paygradeId")
    public PaygradeEntity getPaygradeEntity() {
        return paygradeEntity;
    }

    public void setPaygradeEntity(PaygradeEntity paygradeEntity) {
        this.paygradeEntity = paygradeEntity;
    }

    @ManyToOne
    @JoinColumn(name = "supervisorId", referencedColumnName = "employeeNum")
    public EmployeeEntity getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(EmployeeEntity supervisor) {
        this.supervisor = supervisor;
    }

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.EAGER)
    public Set<EmployeeEntity> getSupervising() {
        return supervising;
    }

    public void setSupervising(Set<EmployeeEntity> supervising) {
        this.supervising = supervising;
    }

    @ManyToOne
    @JoinColumn(name = "timesheetApproverId", referencedColumnName = "employeeNum")
    public EmployeeEntity getTimesheetApprover() {
        return timesheetApprover;
    }

    public void setTimesheetApprover(EmployeeEntity timesheetApprover) {
        this.timesheetApprover = timesheetApprover;
    }

    @OneToMany(mappedBy = "timesheetApprover", fetch = FetchType.EAGER)
    public Set<EmployeeEntity> getTimesheetApproving() {
        return timesheetApproving;
    }

    public void setTimesheetApproving(Set<EmployeeEntity> timesheetApproving) {
        this.timesheetApproving = timesheetApproving;
    }

    @OneToMany(mappedBy = "employeeEntity")
    public Collection<TimesheetEntity> getTimesheetEntities() {
        return timesheetEntities;
    }

    public void setTimesheetEntities(Collection<TimesheetEntity> timesheetEntities) {
        this.timesheetEntities = timesheetEntities;
    }

    @OneToMany(mappedBy = "employeeEntity")
    public Collection<TimesheetrowEntity> getTimesheetRowEntities() {
        return timesheetRowEntities;
    }

    public void setTimesheetRowEntities(Collection<TimesheetrowEntity> timesheetRowEntities) {
        this.timesheetRowEntities = timesheetRowEntities;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "employeeEntities")
    public Collection<ProjectPackageEntity> getParticipatingProjectPackageEntities() {
        return participatingProjectPackageEntities;
    }

    public void setParticipatingProjectPackageEntities(Collection<ProjectPackageEntity> participatingProjectPackageEntities) {
        this.participatingProjectPackageEntities = participatingProjectPackageEntities;
    }

    @OneToMany(mappedBy = "employeeEntity")
    public Collection<WeeklyReportEntity> getMonthlyReportEntities() {
        return monthlyReportEntities;
    }

    public void setMonthlyReportEntities(Collection<WeeklyReportEntity> monthlyReportEntities) {
        this.monthlyReportEntities = monthlyReportEntities;
    }
}
