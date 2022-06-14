package com.comp4911.backend.models.JSONModels;

import com.comp4911.backend.models.WeeklyReportEntity;

import java.util.HashMap;
import java.util.Map;

public class JSONWeeklyReport {
    private String projectId;
    private String workPackageId;
    private String reportDate;
    private String comment;
    private String workAccomplished;
    private String workPlanned;
    private String problemFaced;
    private String problemAnticipated;
    private String createdOn;
    private String createdBy;
    private Map<String, String> engineerPlanned;
    private Map<String, String> etc;

    public JSONWeeklyReport(WeeklyReportEntity weeklyReport) {
        this.projectId = weeklyReport.getProjectPackageEntity().getProjectId();
        this.workPackageId = weeklyReport.getProjectPackageEntity().getWorkPackageId();
        this.reportDate = weeklyReport.getReportDate().toString();
        this.comment = weeklyReport.getComment();
        this.workAccomplished = weeklyReport.getWorkAccomplished();
        this.workPlanned = weeklyReport.getWorkPlanned();
        this.problemFaced = weeklyReport.getProblemFaced();
        this.problemAnticipated = weeklyReport.getProblemAnticipated();
        this.createdOn = weeklyReport.getCreatedOn().toString();
        this.createdBy = weeklyReport.getEmployeeEntity().getFirstName() + " " + weeklyReport.getEmployeeEntity().getLastName();
        this.engineerPlanned = weeklyReport.getEngineerPlanned();
        this.etc = weeklyReport.getEtc();
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

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
}
