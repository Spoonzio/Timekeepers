package com.comp4911.backend.models;
import com.comp4911.backend.lib.JSONAttributeConverter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Map;

@Entity
@Table(name = "weeklyreport", schema = "timesheet")
public class WeeklyReportEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "reportId")
    private Integer reportId;
    @Basic
    @Column(name = "projectPackageNum")
    private Integer projectPackageNum;
    @Basic
    @Column(name = "createdOn")
    private Date createdOn;
    @Basic
    @Column(name = "createdBy")
    private Integer createdBy;
    @Basic
    @Column(name = "reportDate")
    private Date reportDate;
    @Basic
    @Column(name = "comment")
    private String comment;
    @Basic
    @Column(name = "workAccomplished")
    private String workAccomplished;
    @Basic
    @Column(name = "workPlanned")
    private String workPlanned;
    @Basic
    @Column(name = "problemFaced")
    private String problemFaced;
    @Basic
    @Column(name = "problemAnticipated")
    private String problemAnticipated;
    @ManyToOne
    @JoinColumn(name = "createdBy", referencedColumnName = "employeeNum", nullable = false, insertable = false, updatable = false)
    private EmployeeEntity employeeEntity;
    @ManyToOne
    @JoinColumn(name = "projectPackageNum", referencedColumnName = "projectPackageNum", nullable = false, insertable = false, updatable = false)
    private ProjectPackageEntity projectPackageEntity;

    @Column(name = "engineerPlanned")
    @Convert(converter = JSONAttributeConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    private Map<String, String> engineerPlanned;

    @Column(name = "etc")
    @Convert(converter = JSONAttributeConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    private Map<String, String> etc;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getProjectPackageNum() {
        return projectPackageNum;
    }

    public void setProjectPackageNum(Integer projectPackageNum) {
        this.projectPackageNum = projectPackageNum;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getWorkAccomplished() {
        return workAccomplished;
    }

    public void setWorkAccomplished(String workAccomplished) {
        this.workAccomplished = workAccomplished;
    }

    public String getWorkPlanned() {
        return workPlanned;
    }

    public void setWorkPlanned(String workPlanned) {
        this.workPlanned = workPlanned;
    }

    public String getProblemFaced() {
        return problemFaced;
    }

    public void setProblemFaced(String problemFaced) {
        this.problemFaced = problemFaced;
    }

    public String getProblemAnticipated() {
        return problemAnticipated;
    }

    public void setProblemAnticipated(String problemAnticipated) {
        this.problemAnticipated = problemAnticipated;
    }

    public Map<String, String> getEngineerPlanned() {
        return engineerPlanned;
    }

    public void setEngineerPlanned(Map<String, String> engineerPlanned) {
        this.engineerPlanned = engineerPlanned;
    }

    public Map<String, String> getEtc() {
        return etc;
    }

    public void setEtc(Map<String, String> etc) {
        this.etc = etc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeeklyReportEntity that = (WeeklyReportEntity) o;

        if (reportId != null ? !reportId.equals(that.reportId) : that.reportId != null) return false;
        if (projectPackageNum != null ? !projectPackageNum.equals(that.projectPackageNum) : that.projectPackageNum != null)
            return false;
        if (createdOn != null ? !createdOn.equals(that.createdOn) : that.createdOn != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (reportDate != null ? !reportDate.equals(that.reportDate) : that.reportDate != null) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (workAccomplished != null ? !workAccomplished.equals(that.workAccomplished) : that.workAccomplished != null)
            return false;
        if (workPlanned != null ? !workPlanned.equals(that.workPlanned) : that.workPlanned != null) return false;
        if (problemFaced != null ? !problemFaced.equals(that.problemFaced) : that.problemFaced != null) return false;
        if (problemAnticipated != null ? !problemAnticipated.equals(that.problemAnticipated) : that.problemAnticipated != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = reportId != null ? reportId.hashCode() : 0;
        result = 31 * result + (projectPackageNum != null ? projectPackageNum.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (reportDate != null ? reportDate.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (workAccomplished != null ? workAccomplished.hashCode() : 0);
        result = 31 * result + (workPlanned != null ? workPlanned.hashCode() : 0);
        result = 31 * result + (problemFaced != null ? problemFaced.hashCode() : 0);
        result = 31 * result + (problemAnticipated != null ? problemAnticipated.hashCode() : 0);
        return result;
    }

    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public void setEmployeeEntity(EmployeeEntity employeeEntity) {
        this.employeeEntity = employeeEntity;
    }

    public ProjectPackageEntity getProjectPackageEntity() {
        return projectPackageEntity;
    }

    public void setProjectPackageEntity(ProjectPackageEntity projectPackageEntity) {
        this.projectPackageEntity = projectPackageEntity;
    }
}
