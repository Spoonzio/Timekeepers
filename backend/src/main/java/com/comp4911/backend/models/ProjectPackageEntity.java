package com.comp4911.backend.models;

import com.comp4911.backend.lib.JSONAttributeConverter;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "projectpackage", schema = "timesheet")
public class ProjectPackageEntity {
    private Integer projectPackageNum;
    private String projectId;
    private String workPackageId;
    private Boolean isOpen;
    private EmployeeEntity managerEntity;
    private EmployeeEntity assistantEntity;
    private ProjectPackageEntity parentProjectPackageEntity;
    private Collection<WeeklyReportEntity> montlyReportEntities;
    private Set<EmployeeEntity> employeeEntities;
    private Set<ProjectPackageEntity> childWorkPackages;
    private Set<TimesheetrowEntity> timesheetrowEntities;
    private Map<String, String> personDays;
    private java.sql.Date createdOn;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projectPackageNum")
    public Integer getProjectPackageNum() {
        return projectPackageNum;
    }
    public void setProjectPackageNum(Integer projectPackageNum) {
        this.projectPackageNum = projectPackageNum;
    }

    @Column(name = "projectId")
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Column(name = "workPackageId")
    public String getWorkPackageId() {
        return workPackageId;
    }
    public void setWorkPackageId(String workPackageId) {
        this.workPackageId = workPackageId;
    }

    @Column(name = "isOpen", columnDefinition = "TINYINT(1)")
    public Boolean getIsOpen() {
        return isOpen;
    }
    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    @Column(name = "personDays")
    @Convert(converter = JSONAttributeConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    public Map<String, String> getPersonDays() {
        return personDays;
    }

    public void setPersonDays(Map<String, String> personDays) {
        this.personDays = personDays;
    }

    @Column(name = "createdOn")
    public java.sql.Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(java.sql.Date createdOn) {
        this.createdOn = createdOn;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "managerNum", referencedColumnName = "employeeNum")
    public EmployeeEntity getManagerEntity() {
        return managerEntity;
    }
    public void setManagerEntity(EmployeeEntity managerEntity) {
        this.managerEntity = managerEntity;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parentProjectPackageNum", referencedColumnName = "projectPackageNum")
    public ProjectPackageEntity getParentProjectPackageEntity() {
        return parentProjectPackageEntity;
    }
    public void setParentProjectPackageEntity(ProjectPackageEntity parentProjectPackageEntity) {
        this.parentProjectPackageEntity = parentProjectPackageEntity;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "employeeprojectpackage", schema = "timesheet",
            joinColumns = @JoinColumn(name = "projectPackageNum", referencedColumnName = "projectPackageNum", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "employeeNum", referencedColumnName = "employeeNum", nullable = false))
    public Set<EmployeeEntity> getEmployeeEntities() {
        return employeeEntities;
    }
    public void setEmployeeEntities(Set<EmployeeEntity> employeeEntities) {
        this.employeeEntities = employeeEntities;
    }

    @OneToMany(mappedBy = "parentProjectPackageEntity", fetch = FetchType.EAGER)
    public Set<ProjectPackageEntity> getChildWorkPackages() {
        return childWorkPackages;
    }
    public void setChildWorkPackages(Set<ProjectPackageEntity> childWorkPackages) {
        this.childWorkPackages = childWorkPackages;
    }

    @OneToMany(mappedBy = "projectPackageEntity", fetch = FetchType.EAGER)
    public Set<TimesheetrowEntity> getTimesheetrowEntities() {
        return timesheetrowEntities;
    }
    public void setTimesheetrowEntities(Set<TimesheetrowEntity> timesheetrowEntities) {
        this.timesheetrowEntities = timesheetrowEntities;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assistantNum", referencedColumnName = "employeeNum")
    public EmployeeEntity getAssistantEntity() {
        return assistantEntity;
    }

    public void setAssistantEntity(EmployeeEntity assistantEntity) {
        this.assistantEntity = assistantEntity;
    }

    @OneToMany(mappedBy = "projectPackageEntity")
    public Collection<WeeklyReportEntity> getMontlyReportEntities() {
        return montlyReportEntities;
    }

    public void setMontlyReportEntities(Collection<WeeklyReportEntity> montlyReportEntities) {
        this.montlyReportEntities = montlyReportEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPackageEntity that = (ProjectPackageEntity) o;
        return Objects.equals(getProjectPackageNum(), that.getProjectPackageNum()) && Objects.equals(getProjectId(), that.getProjectId()) && Objects.equals(getWorkPackageId(), that.getWorkPackageId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectPackageNum(), getProjectId(), getWorkPackageId());
    }
}
