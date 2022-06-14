package com.comp4911.backend.api;

import com.comp4911.backend.access.*;
import com.comp4911.backend.api.filters.annotations.AuthAdmin;
import com.comp4911.backend.api.filters.annotations.AuthEMP;
import com.comp4911.backend.api.filters.annotations.AuthHR;
import com.comp4911.backend.lib.ApproveStatus;
import com.comp4911.backend.lib.ResponseFormat;
import com.comp4911.backend.lib.Role;
import com.comp4911.backend.models.*;
import com.comp4911.backend.models.JSONModels.JSONTimesheetrow;
import com.comp4911.backend.models.JSONModels.JSONWeeklyReport;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Path("/report")
public class ReportResource {

    @Inject
    private ProjectPackageManager projectPackageManager;

    @Inject
    private TimesheetRowManager timesheetRowManager;

    @Inject
    private WeeklyReportManager weeklyReportManager;

    @Inject
    private PaygradeManager paygradeManager;

    @Context
    HttpServletRequest httpServletRequest;

    @Path("/RE/{projectId}/{workPackageId}")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getREReport(@QueryParam("date") String date, @PathParam("projectId") String projectId, @PathParam("workPackageId") String workPackageId) {
        //convert date to sql date
        Date sqlDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            try {
                LocalDate localDate = formatter.parse(date).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                sqlDate = Date.valueOf(localDate);
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("Invalid date format");
            }
        } else {
            return ResponseFormat.buildErrorResult("Date is required");
        }
        WeeklyReportEntity weeklyReportEntity = weeklyReportManager.find(projectId, workPackageId, sqlDate);
        if (weeklyReportEntity == null) {
            return ResponseFormat.buildErrorResult("No RE report found");
        }
        return ResponseFormat.buildSuccessResult(new JSONWeeklyReport(weeklyReportEntity));
    }

    @Path("/RE/{projectId}/{workPackageId}")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> updateREReport(
            @QueryParam("date") String date,
            @PathParam("projectId") String projectId,
            @PathParam("workPackageId") String workPackageId,
            HashMap<String, Object> body) {
        //convert date to sql date
        Date sqlDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            try {
                LocalDate localDate = formatter.parse(date).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                sqlDate = Date.valueOf(localDate);
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("Invalid date format");
            }
        } else {
            return ResponseFormat.buildErrorResult("Date is required");
        }
        WeeklyReportEntity weeklyReportEntity = weeklyReportManager.find(projectId, workPackageId, sqlDate);
        if (weeklyReportEntity == null) {
            return ResponseFormat.buildErrorResult("No RE report found");
        }

        EmployeeEntity requester = (EmployeeEntity) httpServletRequest.getAttribute("requester");
        ProjectPackageEntity project = projectPackageManager.find(weeklyReportEntity.getProjectPackageEntity().getProjectId());
        boolean isAdmin = requester.getRole().equals(Role.ADMIN.getValue());
        boolean isManager = project.getManagerEntity().getEmployeeNum().equals(requester.getEmployeeNum());
        boolean isAssistant = project.getAssistantEntity() == null ? false : project.getAssistantEntity().getEmployeeNum().equals(requester.getEmployeeNum());
        boolean isCreator = weeklyReportEntity.getCreatedBy().equals(requester.getEmployeeNum());
        if (!isAdmin && !isManager && !isAssistant && !isCreator) {
            return ResponseFormat.buildErrorResult("Only admin, PM/Assistant for this project, or creator of this report can update");
        }

        //getting all attributes
        String comments;
        String workAccomplished;
        String workPlanned;
        String problemsFaced;
        String problemsAnticipated;
        Map<String, String> etc;
        Map<String, String> engineerPlanned;
        try {
            comments = (String) body.get("comments");
            workAccomplished = (String) body.get("workAccomplished");
            workPlanned = (String) body.get("workPlanned");
            problemsFaced = (String) body.get("problemsFaced");
            problemsAnticipated = (String) body.get("problemsAnticipated");
            engineerPlanned = body.get("engineerPlanned") == null ? null : (Map<String, String>) body.get("engineerPlanned");
            etc = body.get("etc") == null ? null : (Map<String, String>) body.get("etc");
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult(e.getMessage());
        }

        weeklyReportEntity.setComment(comments);
        weeklyReportEntity.setWorkAccomplished(workAccomplished);
        weeklyReportEntity.setWorkPlanned(workPlanned);
        weeklyReportEntity.setProblemFaced(problemsFaced);
        weeklyReportEntity.setProblemAnticipated(problemsAnticipated);
        weeklyReportEntity.setEngineerPlanned(engineerPlanned);
        weeklyReportEntity.setEtc(etc);
        weeklyReportManager.merge(weeklyReportEntity);

        return ResponseFormat.buildSuccessResult("RE report updated");
    }

    @Path("/RE")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getREReports() {
        EmployeeEntity requester = (EmployeeEntity) httpServletRequest.getAttribute("requester");
        boolean isAdmin = requester.getRole().equals(Role.ADMIN.getValue());
        List<WeeklyReportEntity> weeklyReportEntities = new ArrayList<>();
        if (isAdmin) {
            weeklyReportEntities = weeklyReportManager.findAll();
        } else {
            //if not admin, get all reports created by requester + all reports for projects that requester is managing/assisting
            Set<WeeklyReportEntity> availableReports = new HashSet<>();
            availableReports.addAll(weeklyReportManager.findByCreator(requester.getEmployeeId()));
            List<ProjectPackageEntity> managing = projectPackageManager.findByManagerId(requester.getEmployeeId());
            List<ProjectPackageEntity> assisting = projectPackageManager.findByAssistantId(requester.getEmployeeId());
            availableReports.addAll(weeklyReportManager.findByProjectIds(managing.stream().map(ProjectPackageEntity::getProjectId).collect(Collectors.toList())));
            availableReports.addAll(weeklyReportManager.findByProjectIds(assisting.stream().map(ProjectPackageEntity::getProjectId).collect(Collectors.toList())));
            weeklyReportEntities.addAll(availableReports);
        }
        ArrayList<Map<String, String>> result = new ArrayList<>();
        for (WeeklyReportEntity weeklyReportEntity : weeklyReportEntities) {
            Map<String, String> map = new HashMap<>();
            map.put("projectId", weeklyReportEntity.getProjectPackageEntity().getProjectId());
            map.put("workPackageId", weeklyReportEntity.getProjectPackageEntity().getWorkPackageId());
            map.put("date", weeklyReportEntity.getReportDate().toString());
            result.add(map);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("reports", result);
        return ResponseFormat.buildSuccessResult(resultMap);
    }

    @Path("/RE")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> createMonthlyReport(HashMap<String, Object> body) {
        //getting all attributes
        String projectId;
        String workPackageId;
        String date;
        String comments;
        String workAccomplished;
        String workPlanned;
        String problemsFaced;
        String problemsAnticipated;
        Map<String, String> etc;
        Map<String, String> engineerPlanned;
        try {
            projectId = (String) body.get("projectId");
            workPackageId = (String) body.get("workPackageId");
            date = (String) body.get("reportPeriodFrom");
            comments = (String) body.get("comments");
            workAccomplished = (String) body.get("workAccomplished");
            workPlanned = (String) body.get("workPlanned");
            problemsFaced = (String) body.get("problemsFaced");
            problemsAnticipated = (String) body.get("problemsAnticipated");
            engineerPlanned = body.get("engineerPlanned") == null ? null : (Map<String, String>) body.get("engineerPlanned");
            etc = body.get("etc") == null ? null : (Map<String, String>) body.get("etc");
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult(e.getMessage());
        }

        //validating projectId + workPackageId
        ProjectPackageEntity projectPackageEntity = projectPackageManager.find(projectId, workPackageId);
        if (projectPackageEntity == null) {
            return ResponseFormat.buildErrorResult("projectPackage not found");
        }
        if (!projectPackageEntity.getIsOpen()) {
            return ResponseFormat.buildErrorResult("projectPackage is closed");
        }

        //validating date
        if (date == null || date.isEmpty()) {
            return ResponseFormat.buildErrorResult("reportPeriodFrom is required");
        }

        //validating enginnerPlanned (Looking at sample document, this field looks like it could be null)
        for (String payGrade : engineerPlanned.keySet()) {
            if (paygradeManager.find(payGrade).size() == 0) {
                return ResponseFormat.buildFailedResult("paygrade " + payGrade + " does not exist");
            }
        }

        //validating etc
        for (String payGrade : etc.keySet()) {
            if (paygradeManager.find(payGrade).size() == 0) {
                return ResponseFormat.buildFailedResult("paygrade " + payGrade + " does not exist");
            }
        }

        //converting date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sqlDate = null;
        try {
            LocalDate localDate = sdf.parse(date).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            sqlDate = Date.valueOf(localDate);
        } catch (ParseException e) {
            return ResponseFormat.buildErrorResult("Date is not in yyyy-MM-dd format.");
        }

        //finding if report exists
        if (weeklyReportManager.find(projectId, workPackageId, sqlDate) != null) {
            return ResponseFormat.buildErrorResult("Report already exists for this workpackage for this week.");
        }

        //creating report
        WeeklyReportEntity report = new WeeklyReportEntity();
        report.setProjectPackageNum(projectPackageEntity.getProjectPackageNum());
        report.setReportDate(sqlDate);
        report.setComment(comments);
        report.setWorkAccomplished(workAccomplished);
        report.setWorkPlanned(workPlanned);
        report.setProblemFaced(problemsFaced);
        report.setProblemAnticipated(problemsAnticipated);
        report.setCreatedBy(((EmployeeEntity)httpServletRequest.getAttribute("requester")).getEmployeeNum());
        report.setCreatedOn(Date.valueOf(LocalDate.now()));
        report.setEngineerPlanned(engineerPlanned);
        report.setEtc(etc);

        weeklyReportManager.persist(report);
        return ResponseFormat.buildSuccessResult("Report created successfully");
    }

    //get weekly reports
    @Path("/weekly")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getWeeklyReport(@QueryParam("date") String date, @QueryParam("projectId") String projectId) {
        // since weeklyreports are a list within a project, projectId is required
        if (projectId == null) {
            return ResponseFormat.buildErrorResult("ProjectId is required");
        }

        //Validate projectId
        ProjectPackageEntity projectPackageEntity = projectPackageManager.find(projectId);
        if (projectPackageEntity == null) {
            return ResponseFormat.buildErrorResult("Invalid project id");
        }

        //convert date to sql date
        Date sqlDate = null;
        Date reportStartDate = null;
        Date sqlDatePlusOne = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            try {
                LocalDate localDate = formatter.parse(date).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                sqlDatePlusOne = Date.valueOf(localDate.plusDays(1));
                sqlDate = Date.valueOf(localDate);
                localDate = localDate.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                reportStartDate = Date.valueOf(localDate);
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("Invalid date format");
            }
        }

        //get all timesheet rows for this projectId (sqlDate plus one day because this find function is exclusive on the end date)
        List<TimesheetrowEntity> timesheetRows = timesheetRowManager.find(null, sqlDatePlusOne, null, projectId, null);

        //filter timesheet rows so only approved ones are left
        timesheetRows = timesheetRows.stream().filter(row -> {
            Byte approveStatus = row.getTimesheetEntity().getIsApproved();
            return approveStatus != null && approveStatus.equals(ApproveStatus.APPROVED.getValue()) && row.getProjectPackageEntity().getIsOpen();
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("projectId", projectId);
        if (sqlDate != null) {
            result.put("reportDate", new Date(System.currentTimeMillis()).toString());
            result.put("reportPeriod", reportStartDate.toString()+" - "+sqlDate.toString());
            Map<String, ArrayList<ArrayList<Object>>> reports = new HashMap<>();

            // reportInfo contains easier to access information about the report
            Map<String, Map<String, Set<String>>> reportInfo = new HashMap<>();
            for (TimesheetrowEntity row : timesheetRows) {
                String workPackageId = row.getProjectPackageEntity().getWorkPackageId();
                String weekEnding = row.getTimesheetEntity().getWeekEnding().toString();
                String employeeId = row.getEmployeeEntity().getCredentialEntity().getUserName();

                if (!reportInfo.containsKey(workPackageId)) {
                    reportInfo.put(workPackageId, new HashMap<>());
                }
                if (!reportInfo.get(workPackageId).containsKey("weeks")) {
                    reportInfo.get(workPackageId).put("weeks", new HashSet<>());
                }
                reportInfo.get(workPackageId).get("weeks").add(weekEnding);
                if (!reportInfo.get(workPackageId).containsKey("employees")) {
                    reportInfo.get(workPackageId).put("employees", new HashSet<>());
                }
                reportInfo.get(workPackageId).get("employees").add(employeeId);
            }

            //make frame for report
            for (String workPackageId : reportInfo.keySet()) {
                reports.put(workPackageId, new ArrayList<>());
                ArrayList<Object> row = new ArrayList<>();
                row.add("Employee Id");
                for (String weekEnding : reportInfo.get(workPackageId).get("weeks")) {
                    row.add(weekEnding);
                }
                row.add("Total");
                row.add("Planned Hours");

                reports.get(workPackageId).add(row);

                for (String employeeId : reportInfo.get(workPackageId).get("employees")) {
                    ArrayList<Object> empRow = new ArrayList<>();
                    for (int i = 0; i < row.size(); i++) {
                        empRow.add(BigDecimal.ZERO);
                    }
                    empRow.set(0, employeeId);
                    reports.get(workPackageId).add(empRow);
                }
            }

            //fill report
            for (TimesheetrowEntity row : timesheetRows) {
                String workPackageId = row.getProjectPackageEntity().getWorkPackageId();
                String weekEnding = row.getTimesheetEntity().getWeekEnding().toString();
                String employeeId = row.getEmployeeEntity().getCredentialEntity().getUserName();

                int columnIndex = reports.get(workPackageId).get(0).indexOf(weekEnding);
                int rowIndex = reports.get(workPackageId).indexOf(reports.get(workPackageId).stream().filter(r -> r.get(0).equals(employeeId)).collect(Collectors.toList()).get(0));

                reports.get(workPackageId).get(rowIndex).set(columnIndex, new JSONTimesheetrow(row).getTotal());
            }

            //fill total
            for (String workPackageId : reportInfo.keySet()) {
                for (String employeeId : reportInfo.get(workPackageId).get("employees")) {
                    int rowIndex = reports.get(workPackageId).indexOf(reports.get(workPackageId).stream().filter(r -> r.get(0).equals(employeeId)).collect(Collectors.toList()).get(0));
                    int columnIndex = reports.get(workPackageId).get(0).indexOf("Total");

                    BigDecimal total = BigDecimal.ZERO;
                    for (int i = 1; i < reports.get(workPackageId).get(0).size()-2; i++) {
                        total = total.add((BigDecimal) reports.get(workPackageId).get(rowIndex).get(i));
                    }
                    reports.get(workPackageId).get(rowIndex).set(columnIndex, total);
                }
            }

            //fill planned hours
            for (String workPackageId : reportInfo.keySet()) {
                int columnIndex = reports.get(workPackageId).get(0).indexOf("Planned Hours");
                for (int i = 1; i < reports.get(workPackageId).size(); i++) {
                    reports.get(workPackageId).get(i).set(columnIndex, "");
                }
            }

            //create total row at the end of each work package report
            for (String workPackageId : reportInfo.keySet()) {
                ArrayList<Object> totalRow = new ArrayList<>();
                int titleColumn = reports.get(workPackageId).get(0).indexOf("Employee Id");
                int totalColumn = reports.get(workPackageId).get(0).indexOf("Total");
                int plannedHoursColumn = reports.get(workPackageId).get(0).indexOf("Planned Hours");
                for (int i = 0; i < reports.get(workPackageId).get(0).size(); i++) {
                    totalRow.add("");
                }

                //adding all value in total column to get total for entire work package
                BigDecimal total = BigDecimal.ZERO;
                for (int i = 1; i < reports.get(workPackageId).size(); i++) {
                    total = total.add((BigDecimal) reports.get(workPackageId).get(i).get(totalColumn));
                }

                //planned hour = planned person days * 24 hours
                ProjectPackageEntity ppe = projectPackageManager.find(projectId, workPackageId);
                Map<String, String> plannedHours = ppe.getPersonDays();
                BigDecimal plannedHoursTotal = BigDecimal.ZERO;
                for (String plannedHour : plannedHours.values()) {
                    BigDecimal plannedHourValue = new BigDecimal(plannedHour);
                    plannedHoursTotal = plannedHoursTotal.add(plannedHourValue);
                }
                plannedHoursTotal = plannedHoursTotal.multiply(new BigDecimal(24));

                totalRow.set(titleColumn, "Total");
                totalRow.set(totalColumn, total);
                totalRow.set(plannedHoursColumn, plannedHoursTotal);
                reports.get(workPackageId).add(totalRow);
            }

            result.put("reports", reports);

            return ResponseFormat.buildSuccessResult(result);
        } else {
            // if sqlDate is null, user is not trying to find a specific report. Instead, they are trying to find all available reports
            HashSet<String> reports = new HashSet<>();
            for (TimesheetrowEntity row : timesheetRows) {
                String workPackageId = row.getProjectPackageEntity().getWorkPackageId();
                String weekEnding = row.getTimesheetEntity().getWeekEnding().toString();

                if (!reports.contains(weekEnding)) {
                    reports.add(weekEnding);
                }
            }

            result.put("reports", reports);
            return ResponseFormat.buildSuccessResult(result);
        }
    }

    @Path("/monthly")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> getMonthlyReport(@QueryParam("projectId") String projectId, @QueryParam("date") String date) {
        // if date is not provided, return the list of available reports (based on logged in user. filter by projectId)
        if (date == null || date.isEmpty()) {

            List<ProjectPackageEntity> lowestLevelProjectPackages = projectPackageManager.findAllLowestLevel();
            //filter wp based on logged in user
            EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");
            boolean isAdmin = requester.getRole().equals(Role.ADMIN.getValue());
            lowestLevelProjectPackages = lowestLevelProjectPackages.stream().filter(p -> {
                String projectManagerId = projectPackageManager.find(p.getProjectId()).getManagerEntity().getEmployeeId();
                //open && (admin || project manager || responsible engineer)
                boolean isAuthorized = p.getIsOpen() && (isAdmin || requester.getEmployeeId().equals(projectManagerId) || requester.getEmployeeId().equals(p.getManagerEntity().getEmployeeId()));
                return isAuthorized && (projectId == null ? true : p.getProjectId().equals(projectId));
            }).collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("reports", new HashSet<Map<String, String>>());

            //monthly report for a wp is available from the creation date.
            for (ProjectPackageEntity projectPackageEntity : lowestLevelProjectPackages) {
                LocalDate firstMonth = projectPackageEntity.getCreatedOn().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
                LocalDate thisMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                while (true) {
                    Map<String, String> map = new HashMap<>();
                    map.put("date", firstMonth.toString());
                    map.put("projectId", projectPackageEntity.getProjectId());
                    map.put("workPackageId", projectPackageEntity.getWorkPackageId());
                    ((HashSet<Map<String, String>>)result.get("reports")).add(map);
                    if (firstMonth.equals(thisMonth)) {
                        break;
                    }
                    firstMonth = firstMonth.plusMonths(1);
                }
            }
            return ResponseFormat.buildSuccessResult(result);
        } else {
            // if date is provided, return the report for that month

            //converting date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date sqlDate = null;
            Date nextMonthStartingDate = null;
            Date thisMonthEndingDate = null;
            try {
                LocalDate localDate = sdf.parse(date).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                localDate = localDate.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate localDate2 = localDate.plusMonths(1);
                nextMonthStartingDate = Date.valueOf(localDate2);
                sqlDate = Date.valueOf(localDate);
                thisMonthEndingDate = Date.valueOf(localDate.plusMonths(1).minusDays(1));
            } catch (ParseException e) {
                return ResponseFormat.buildErrorResult("Date is not in yyyy-MM-dd format.");
            }

            // validating projectId
            List<ProjectPackageEntity> projectPackageEntities = projectPackageManager.findAll(projectId);
            if (projectPackageEntities == null || projectPackageEntities.size() == 0) {
                return ResponseFormat.buildErrorResult("You provided date. This means that you want to see a specific report. But the projectId you provided does not exist.");
            }

            //get all lowest level work packages (because only lowest work packages have monthly reports and timesheet)
            List<ProjectPackageEntity> lowestLevelWorkPackages = projectPackageEntities.stream().filter(p -> p.getChildWorkPackages().size() == 0).collect(Collectors.toList());

            //filtering packages based on logged in user (this wp report is available only to Admin, PM, and responsible engineer)
            EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");
            boolean isAdmin = requester.getRole().equals(Role.ADMIN.getValue());
            String projectManagerNum = projectPackageManager.find(projectId).getManagerEntity().getEmployeeId();
            lowestLevelWorkPackages = lowestLevelWorkPackages.stream().filter(p -> {
                //open && (admin || project manager || responsible engineer)
                return p.getIsOpen() && (isAdmin || requester.getEmployeeId().equals(projectManagerNum) || requester.getEmployeeId().equals(p.getManagerEntity().getEmployeeId()));
            }).collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("projectId", projectPackageManager.find(projectId).getProjectPackageNum());
            result.put("projectName", projectId);
            result.put("reportDate", LocalDate.now().toString());
            result.put("reportPeriod", sqlDate.toString() + " - " + thisMonthEndingDate.toString());
            result.put("reports", new ArrayList<Map<String, Object>>());

            for (ProjectPackageEntity projectPackageEntity : lowestLevelWorkPackages) {
                String workPackageId = projectPackageEntity.getWorkPackageId();

                //get timesheet rows for this work package
                List<TimesheetrowEntity> timesheetRows = timesheetRowManager.find(null, nextMonthStartingDate, null, projectId, workPackageId);

                //calculating BCWS for this work package
                double BCWS_MD = 0;
                double BCWS_LD = 0;
                for(Map.Entry<String, String> pd : projectPackageEntity.getPersonDays().entrySet()) {
                    BCWS_MD += Double.parseDouble(pd.getValue());
                    BCWS_LD += (paygradeManager.find(pd.getKey()).get(0).getRate().doubleValue() * Double.parseDouble(pd.getValue()) * 24);
                }

                //get the last engineer planned for this work package for this time period (get ETC at the same time. cuz they are on the same table)
                double ENGINEER_PLANNED_MD = 0;
                double ENGINEER_PLANNED_LD = 0;
                double ETC_MD = 0;
                double ETC_LD = 0;
                String comments = "";
                List<WeeklyReportEntity> weeklyReportsForThisMonth = weeklyReportManager.find(sqlDate, nextMonthStartingDate, projectId, workPackageId);
                if (weeklyReportsForThisMonth != null && weeklyReportsForThisMonth.size() > 0) {
                    for(Map.Entry<String, String> pd : weeklyReportsForThisMonth.get(0).getEngineerPlanned().entrySet()) {
                        double payrate = paygradeManager.find(pd.getKey()).get(0).getRate().doubleValue();
                        ENGINEER_PLANNED_MD += Double.parseDouble(pd.getValue());
                        ENGINEER_PLANNED_LD += (payrate * Double.parseDouble(pd.getValue()) * 24);

                        ETC_MD += Double.parseDouble(weeklyReportsForThisMonth.get(0).getEtc().get(pd.getKey()));
                        ETC_LD += (payrate * Double.parseDouble(weeklyReportsForThisMonth.get(0).getEtc().get(pd.getKey())) * 24);
                    }
                    for (WeeklyReportEntity weeklyReportEntity : weeklyReportsForThisMonth) {
                        String comment = weeklyReportEntity.getComment();
                        String workAccomplished = weeklyReportEntity.getWorkAccomplished();
                        String workPlanned = weeklyReportEntity.getWorkPlanned();
                        String problemFaced = weeklyReportEntity.getProblemFaced();
                        String problemAnticipated = weeklyReportEntity.getProblemAnticipated();
                        if (comment != null && !comment.isEmpty()) {
                            comments += comment + "<br/>";
                        }
                        if (workAccomplished != null && !workAccomplished.isEmpty()) {
                            comments += workAccomplished + "<br/>";
                        }
                        if (workPlanned != null && !workPlanned.isEmpty()) {
                            comments += workPlanned + "<br/>";
                        }
                        if (problemFaced != null && !problemFaced.isEmpty()) {
                            comments += problemFaced + "<br/>";
                        }
                        if (problemAnticipated != null && !problemAnticipated.isEmpty()) {
                            comments += problemAnticipated + "<br/>";
                        }
                    }
                }

                //calculating ACWP for this work package
                double ACWP_MD = 0;
                double ACWP_LD = 0;
                for(TimesheetrowEntity timesheetrowEntity : timesheetRows) {
                    ACWP_MD += new JSONTimesheetrow(timesheetrowEntity).getTotal().doubleValue();
                    ACWP_LD += (timesheetrowEntity.getEmployeeEntity().getPaygradeEntity().getRate().doubleValue() * new JSONTimesheetrow(timesheetrowEntity).getTotal().doubleValue());
                }
                ACWP_MD = ACWP_MD / 24;

                //calculating EAC
                double EAC_MD = ACWP_MD + ETC_MD;
                double EAC_LD = ACWP_LD + ETC_LD;

                //calculating Variance
                double VARIANCE_MD = 0;
                double VARIANCE_LD = 0;
                try {
                    VARIANCE_MD = ((EAC_MD / BCWS_MD) - 1) * 100;
                    VARIANCE_LD = ((EAC_LD / BCWS_LD) - 1) * 100;
                } catch (Exception ignored) {}

                //calculating Completion
                double COMPLETION = 0;
                try {
                    COMPLETION = (ACWP_MD / EAC_MD) * 100;
                } catch (Exception ignored) {}

                List<Map<String, Object>> reportRows = new ArrayList<>();
                double finalVARIANCE_MD = VARIANCE_MD;
                double finalENGINEER_PLANNED_MD = ENGINEER_PLANNED_MD;
                double finalBCWS_MD = BCWS_MD;
                double finalACWP_MD = ACWP_MD;
                reportRows.add(new HashMap<String, Object>() {{
                    put("Name", "MD");
                    put("BCWS", finalBCWS_MD);
                    put("EngPlanned", finalENGINEER_PLANNED_MD);
                    put("ACWP", finalACWP_MD);
                    put("EAC", EAC_MD);
                    put("Variance", finalVARIANCE_MD);
                }});

                double finalVARIANCE_LD = VARIANCE_LD;
                double finalACWP_LD = ACWP_LD;
                double finalENGINEER_PLANNED_LD = ENGINEER_PLANNED_LD;
                double finalBCWS_LD = BCWS_LD;
                reportRows.add(new HashMap<String, Object>() {{
                    put("Name", "LD");
                    put("BCWS", finalBCWS_LD);
                    put("EngPlanned", finalENGINEER_PLANNED_LD);
                    put("ACWP", finalACWP_LD);
                    put("EAC", EAC_LD);
                    put("Variance", finalVARIANCE_LD);
                }});

                List<Map<String, Object>> reports = (List<Map<String, Object>>)result.get("reports");
                double finalCOMPLETION = COMPLETION;
                String finalComments = comments;
                reports.add(new HashMap<String, Object>() {{
                    put("WorkPackageName", workPackageId);
                    put("Completion", (Double.isNaN(finalCOMPLETION) ? 0 : finalCOMPLETION) + "%");
                    put("Comments", finalComments);
                    put("Rows", reportRows);
                }});
            }

            return ResponseFormat.buildSuccessResult(result);
        }
    }
}
