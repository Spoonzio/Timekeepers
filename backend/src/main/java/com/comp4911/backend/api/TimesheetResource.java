package com.comp4911.backend.api;

import com.comp4911.backend.access.EmployeeManager;
import com.comp4911.backend.access.ProjectPackageManager;
import com.comp4911.backend.access.TimesheetManager;
import com.comp4911.backend.access.TimesheetRowManager;
import com.comp4911.backend.api.filters.annotations.AuthAdmin;
import com.comp4911.backend.api.filters.annotations.AuthEMP;
import com.comp4911.backend.api.filters.annotations.AuthHR;
import com.comp4911.backend.lib.ApproveStatus;
import com.comp4911.backend.lib.ResponseFormat;
import com.comp4911.backend.lib.Role;
import com.comp4911.backend.lib.Util;
import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.JSONModels.JSONTimesheet;
import com.comp4911.backend.models.JSONModels.JSONTimesheetrow;
import com.comp4911.backend.models.ProjectPackageEntity;
import com.comp4911.backend.models.TimesheetEntity;
import com.comp4911.backend.models.TimesheetrowEntity;

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

@Path("/timesheet")
public class TimesheetResource {

    @Inject
    private TimesheetManager timesheetManager;
    @Inject
    private EmployeeManager employeeManager;
    @Inject
    private TimesheetRowManager timesheetRowManager;
    @Inject
    private ProjectPackageManager projectPackageManager;

    @Context
    HttpServletRequest httpServletRequest;

    @POST
    @Path("/row")
    @Consumes("application/json")
    @Produces("application/json")
    @AuthEMP @AuthHR @AuthAdmin
    public Map<String, Object> addTimesheetRow(HashMap<String, String> json) {
        EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");

        String projectId = json.get("projectId");
        String workPackageId = json.get("workPackageId");
        String weekEnding = json.get("weekEnding");
        BigDecimal mon, tue, wed, thu, fri, sat, sun;
        try {
            mon = new BigDecimal(json.get("mon"));
            tue = new BigDecimal(json.get("tue"));
            wed = new BigDecimal(json.get("wed"));
            thu = new BigDecimal(json.get("thu"));
            fri = new BigDecimal(json.get("fri"));
            sat = new BigDecimal(json.get("sat"));
            sun = new BigDecimal(json.get("sun"));
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult("error when parsing timesheet hours. make sure all hours for mon-sun exists and they are numbers");
        }


        ProjectPackageEntity projectPackageEntity = projectPackageManager.find(projectId, workPackageId);
        if (projectPackageEntity == null) {
            return ResponseFormat.buildErrorResult("Project Package not found");
        }

        // if requester doesnt belong to the projectPackage, return error
        if (!projectPackageEntity.getEmployeeEntities().contains(requester)) {
            return ResponseFormat.buildErrorResult("You are not authorized to add timesheet row for this project package. (participants only)");
        }

        //only lowest level project can be assigned a timesheet
        if (!Util.isProjectLowestLevel(projectPackageEntity)) {
            return ResponseFormat.buildErrorResult("Only lowest level project can be assigned a timesheet");
        }

        Date weekEndingDate = null;
        SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");
        try {
            LocalDate localDate = formatter1.parse(weekEnding).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            weekEndingDate = Date.valueOf(localDate);
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult("Invalid date format");
        }

        if (projectId == null || workPackageId == null || weekEnding == null) {
            return ResponseFormat.buildErrorResult("Missing required parameters");
        }

        //create timesheet if not exist
        TimesheetEntity timesheetEntity = timesheetManager.find(requester.getEmployeeId(), weekEndingDate);
        if (timesheetEntity == null) {
            timesheetEntity = new TimesheetEntity();
            timesheetEntity.setEmployeeNum(requester.getEmployeeNum());
            timesheetEntity.setWeekEnding(weekEndingDate);
            timesheetEntity.setIsApproved((byte)0);
            timesheetManager.create(timesheetEntity);
            timesheetEntity = timesheetManager.find(requester.getEmployeeId(), weekEndingDate);
        } else {
            //check if timesheet is already approved or submitted
            if (timesheetEntity.getIsApproved() == ApproveStatus.SUBMITTED.getValue()) return ResponseFormat.buildErrorResult("timesheet is already submitted. cannot be changed.");
            if (timesheetEntity.getIsApproved() == ApproveStatus.APPROVED.getValue()) return ResponseFormat.buildErrorResult("timesheet is already approved. cannot be changed.");

            //if timesheet exists, check if timesheet row exists
            List<TimesheetrowEntity> exist = timesheetRowManager.find(weekEndingDate, projectId, workPackageId, requester.getEmployeeId());
            if (exist.size() > 0) {
                return ResponseFormat.buildErrorResult("You have a row with this work package already. Please update it instead of making a new row.");
            }
        }

        //create timesheet row
        TimesheetrowEntity timesheetrowEntity = new TimesheetrowEntity();
        timesheetrowEntity.setEmployeeNum(requester.getEmployeeNum());
        timesheetrowEntity.setMon(mon);
        timesheetrowEntity.setTue(tue);
        timesheetrowEntity.setWed(wed);
        timesheetrowEntity.setThu(thu);
        timesheetrowEntity.setFri(fri);
        timesheetrowEntity.setSat(sat);
        timesheetrowEntity.setSun(sun);
        timesheetrowEntity.setWeekEnding(weekEndingDate);
        timesheetrowEntity.setProjectPackageNum(projectPackageEntity.getProjectPackageNum());
        timesheetRowManager.persist(timesheetrowEntity);

        List<TimesheetrowEntity> rows = timesheetEntity.getTimesheetRowEntities();
        if (rows == null) {
            rows = new ArrayList<>();
        }
        rows.add(timesheetrowEntity);
        timesheetEntity.setTimesheetRowEntities(rows);

        timesheetManager.merge(timesheetEntity);

        timesheetEntity = timesheetManager.find(requester.getEmployeeId(), weekEndingDate);

        return ResponseFormat.buildSuccessResult(new JSONTimesheet(timesheetEntity));
    }

    /**
     * put route for editing specific timesheetRow
     */
    @PUT
    @Path("/row/{employeeId}/{weekEnding}/{projectId}/{workPackageId}")
    @Consumes("application/json")
    @Produces("application/json")
    @AuthEMP @AuthHR @AuthAdmin
    public Map<String, Object> editTimesheetRow(
            @PathParam("employeeId") String employeeId,
            @PathParam("weekEnding") String weekEnding,
            @PathParam("projectId") String projectId,
            @PathParam("workPackageId") String workPackageId,
            HashMap<String, String> json) {

        EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");

        // Check employee ID ======================
        if (employeeId == null) return ResponseFormat.buildFailedResult("invalid employee id.");
        EmployeeEntity ee = employeeManager.find(employeeId);
        if(ee == null) return ResponseFormat.buildFailedResult("employee not found.");

        // Check project & package ID =============
        if (projectId == null) return ResponseFormat.buildFailedResult("invalid project id.");
        if (workPackageId == null) return ResponseFormat.buildFailedResult("invalid package id.");
        ProjectPackageEntity projPackage = projectPackageManager.find(projectId, workPackageId);
        if(projPackage == null) return ResponseFormat.buildFailedResult("work package not found.");

        // Check Date validity (yyyy-MM-dd)========
        if (weekEnding == null) return ResponseFormat.buildFailedResult("invalid date.");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date weekEndingDate = null;
        try {
            LocalDate localDate = sdf.parse(weekEnding).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            weekEndingDate = Date.valueOf(localDate);
        } catch (ParseException e) {
            return ResponseFormat.buildErrorResult("Date is not in yyyy-MM-dd format.");
        }

        // Find for timesheetRow in DB
        List<TimesheetrowEntity> ts = timesheetRowManager.find(weekEndingDate, projectId, workPackageId, employeeId);
        if (ts == null || ts.size() <= 0) return ResponseFormat.buildErrorResult("timesheet row not found with given parameter.");


        // Check if new project package is valid
        String newProjectId = json.get("projectId");
        String newWorkPackageId = json.get("workPackageId");
        if (newProjectId == null || newWorkPackageId == null) {
            return ResponseFormat.buildFailedResult("project id or work package id is empty.");
        }

        ProjectPackageEntity newProjPackage = projectPackageManager.find(newProjectId, newWorkPackageId);

        if (newProjPackage == null) {
            return ResponseFormat.buildFailedResult("new project package is not found.");
        }

        if (!newProjPackage.getEmployeeEntities().contains(requester)) {
            return ResponseFormat.buildErrorResult("You are not authorized to add timesheet row for this project package. (participants only)");
        }

        //only lowest level package can be assigned a timesheet row
        if (!Util.isProjectLowestLevel(newProjPackage)) {
            return ResponseFormat.buildFailedResult("Only lowest level package can be assigned a timesheet row.");
        }

        TimesheetrowEntity timesheetrowToChange = ts.get(0);
        // changing hours on timesheet
        BigDecimal mon, tue, wed, thu, fri, sat, sun;
        try {
            mon = new BigDecimal(json.get("mon"));
            tue = new BigDecimal(json.get("tue"));
            wed = new BigDecimal(json.get("wed"));
            thu = new BigDecimal(json.get("thu"));
            fri = new BigDecimal(json.get("fri"));
            sat = new BigDecimal(json.get("sat"));
            sun = new BigDecimal(json.get("sun"));
        } catch (Exception e) {
            return ResponseFormat.buildErrorResult("error when parsing timesheet hours. make sure all hours for mon-sun exists and they are numbers");
        }

        timesheetrowToChange.setMon(mon);
        timesheetrowToChange.setTue(tue);
        timesheetrowToChange.setWed(wed);
        timesheetrowToChange.setThu(thu);
        timesheetrowToChange.setFri(fri);
        timesheetrowToChange.setSat(sat);
        timesheetrowToChange.setSun(sun);

        // changing wp and project
        timesheetrowToChange.setProjectPackageNum(newProjPackage.getProjectPackageNum());
        timesheetRowManager.merge(timesheetrowToChange);
        return ResponseFormat.buildSuccessResult(new JSONTimesheetrow(timesheetrowToChange));
    }

    /**
     * get route for timesheetRow
     * ?projectId=1&workPackageId=1
     * ?employeeId=1
     * ?startDate=2019-01-01&endDate=2019-01-31
     */
    @GET
    @Path("/row")
    @Produces("application/json")
    @Consumes("application/json")
    @AuthEMP @AuthAdmin @AuthHR
    public Map<String, Object> getTimesheetRow(
            @QueryParam("projectId") String projectId,
            @QueryParam("workPackageId") String workPackageId,
            @QueryParam("employeeId") String employeeId,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("size") Integer size,
            @QueryParam("page") Integer page) {
        Date start = null, end = null;
        SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            try {
                start = new java.sql.Date(formatter1.parse(startDate).getTime());
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("startDate parse error");
            }
        }
        if (endDate != null) {
            try {
                end = new java.sql.Date(formatter1.parse(endDate).getTime());
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("endDate parse error");
            }
        }
        List<TimesheetrowEntity> rows = timesheetRowManager.find(start, end, employeeId, projectId, workPackageId);
        List<JSONTimesheetrow> jsonRows = new ArrayList<JSONTimesheetrow>();
        for (TimesheetrowEntity row : rows) {
            jsonRows.add(new JSONTimesheetrow(row));
        }

        if (size != null && page != null) {
            return ResponseFormat.buildSuccessResult(Util.getPage(jsonRows, page, size));
        }
        return ResponseFormat.buildSuccessResult(jsonRows);
    }


    /**
     * get route for timesheet
     * ?isApproved=true or false or submitted
     * ?employeeId=1
     * ?startDate=2019-01-01
     * ?endDate=2019-01-31
     * ?weekEnding=2019-01-31 (Sun)
     * all query params are optional. just use ones you need.
     * ie) if you want a specific employee's timesheet, just use ?employeeId=1.
     */
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @AuthEMP @AuthAdmin @AuthHR
    public Map<String, Object> getTimesheet(
            @QueryParam("isApproved") String approved,
            @QueryParam("employeeId") String employeeId,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("weekEnding") String weekEnding,
            @QueryParam("approverId") String approverId,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size
    ) {
        Date start = null, end = null;
        SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            try {
                start = new java.sql.Date(formatter1.parse(startDate).getTime());
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("startDate parse error");
            }
        }
        if (endDate != null) {
            try {
                end = new java.sql.Date(formatter1.parse(endDate).getTime());
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("endDate parse error");
            }
        }
        if (weekEnding != null) {
            try {
                LocalDate localDate = formatter1.parse(weekEnding).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate sunLocalDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                start = new java.sql.Date(formatter1.parse(sunLocalDate.toString()).getTime());
                end = new java.sql.Date(formatter1.parse(sunLocalDate.toString()).getTime());
            } catch (Exception e) {
                return ResponseFormat.buildErrorResult("weekEnding parse error");
            }
        }

        ApproveStatus approveStatus = ApproveStatus.getStatus(approved);
        if (approveStatus == null && approved != null) return ResponseFormat.buildErrorResult("isApproved can only be approved / unapproved / submitted");
        Byte approvedByte = approveStatus == null?null:approveStatus.getValue();


        List<TimesheetEntity> resultTimesheets = timesheetManager.find(start, end, employeeId, approvedByte, approverId);

        List<JSONTimesheet> jsonTs = new ArrayList<JSONTimesheet>();

        for (TimesheetEntity Ts : resultTimesheets) {
            jsonTs.add(new JSONTimesheet(Ts));
        }

        if (page != null && size != null) {
            return ResponseFormat.buildSuccessResult(Util.getPage(jsonTs, page, size));
        }

        return ResponseFormat.buildSuccessResult(jsonTs);
    }

    /**
     * delete specific timesheetRow
     */
    @DELETE
    @Produces("application/json")
    @Path("/row")
    @AuthEMP @AuthAdmin @AuthHR
    public Map<String, Object> deleteTimesheetRow(HashMap<String, String> userInfo) {

        String employeeId = userInfo.get("employeeId");
        String projectId = userInfo.get("projectId");
        String workPackageId = userInfo.get("workPackageId");
        String weekEnding = userInfo.get("weekEnding");

        // Check employee ID ======================
        if (employeeId == null) return ResponseFormat.buildFailedResult("invalid employee id.");
        EmployeeEntity ee = employeeManager.find(employeeId);
        if(ee == null) return ResponseFormat.buildFailedResult("employee not found.");

        // Check project & package ID =============
        if (projectId == null) return ResponseFormat.buildFailedResult("invalid project id.");
        if (workPackageId == null) return ResponseFormat.buildFailedResult("invalid package id.");
        ProjectPackageEntity projPackage = projectPackageManager.find(projectId, workPackageId);
        if(projPackage == null) return ResponseFormat.buildFailedResult("work package not found.");

        // Check Date validity (yyyy-MM-dd)========
        if (weekEnding == null) return ResponseFormat.buildFailedResult("invalid date.");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date weekEndingDate = null;
        try {
            LocalDate localDate = sdf.parse(weekEnding).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            weekEndingDate = Date.valueOf(localDate);
        } catch (ParseException e) {
            return ResponseFormat.buildErrorResult("Date is not in yyyy-MM-dd format.");
        }

        // Find for timesheetRow in DB
        List<TimesheetrowEntity> ts = timesheetRowManager.find(weekEndingDate, projPackage.getProjectId(), projPackage.getWorkPackageId(), ee.getEmployeeId());
        if (ts == null || ts.size() <= 0) return ResponseFormat.buildErrorResult("timesheet row not found with given parameter.");

        // delete the matched timesheetRows
        for(TimesheetrowEntity tsRow : ts){
            timesheetRowManager.remove(tsRow);
        }

        // Return status
        return ResponseFormat.buildSuccessResult("successfully deleted");
    }


    /**
     * approve/reject/submit timesheet
     */
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/{employeeId}/{weekEnding}")
    @AuthEMP @AuthAdmin @AuthHR
    public Map<String, Object> approveTimesheet(@PathParam("employeeId") String employeeId,
                                                @PathParam("weekEnding") String weekEnding,
                                                HashMap<String, String> body) {
        String approve = body.get("isApproved");
        if (approve == null) return ResponseFormat.buildErrorResult("isApproved is required.");

        // Check employee ID ======================
        if (employeeId == null) return ResponseFormat.buildFailedResult("invalid employee id.");
        EmployeeEntity ee = employeeManager.find(employeeId);
        if(ee == null) return ResponseFormat.buildFailedResult("employee not found.");


        // Check Date validity (yyyy-MM-dd)========
        if (weekEnding == null) return ResponseFormat.buildFailedResult("invalid date.");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date weekEndingDate = null;
        try {
            LocalDate localDate = sdf.parse(weekEnding).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            localDate = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            weekEndingDate = Date.valueOf(localDate);
        } catch (ParseException e) {
            return ResponseFormat.buildErrorResult("Date is not in yyyy-MM-dd format.");
        }

        // check if flex time is valid
        BigDecimal requestedFlexTime = null;
        if (body.get("isApproved").equalsIgnoreCase(ApproveStatus.SUBMITTED.getKey()) && body.get("flexTime") != null) {
            requestedFlexTime = new BigDecimal(body.get("flexTime"));
            if (requestedFlexTime.compareTo(new BigDecimal(0)) < 0) {
                return ResponseFormat.buildErrorResult("Flex time cannot be negative.");
            }
            BigDecimal availableFlexTime = ee.getFlexTime();
            if (availableFlexTime.compareTo(requestedFlexTime) < 0) {
                return ResponseFormat.buildErrorResult("Flex time cannot be greater than available flex time.");
            }
        }

        // Check approve word =====================
        ApproveStatus approveStatus = ApproveStatus.getStatus(approve);
        if (approveStatus == null) return ResponseFormat.buildErrorResult("isApproved can only be approved / unapproved / submitted");
        Byte approvedByte = approveStatus.getValue();

        // Find for timesheet in DB
        TimesheetEntity ts = timesheetManager.find(employeeId, weekEndingDate);
        if (ts == null) return ResponseFormat.buildErrorResult("timesheet not found with given parameter.");

        //trying to submit but status is not unapproved
        if (ts.getIsApproved() != ApproveStatus.UNAPPROVED.getValue() && approveStatus.getValue() == ApproveStatus.SUBMITTED.getValue()) return ResponseFormat.buildErrorResult("Trying to submit but this timesheet is not in status UNAPPROVED.");

        // if this is reject request, set flexTime to 0 and increase the flexTime of employee
        if (approveStatus == ApproveStatus.UNAPPROVED) {
            BigDecimal flexTime = ts.getFlexTime();
            if (flexTime != null) {
                ee.setFlexTime(ee.getFlexTime().add(flexTime));
                employeeManager.merge(ee);
                ts.setFlexTime(new BigDecimal(0));
            }
        }

        // if this is approve or reject request, only approver or supervisor can do this operation (admin is allowed)
        if (approveStatus == ApproveStatus.APPROVED || approveStatus == ApproveStatus.UNAPPROVED) {
            EmployeeEntity approver = ts.getEmployeeEntity().getTimesheetApprover();
            EmployeeEntity supervisor = ts.getEmployeeEntity().getSupervisor();
            EmployeeEntity requester = (EmployeeEntity)httpServletRequest.getAttribute("requester");
            if (!requester.getRole().equals(Role.ADMIN.getValue())) {
                if (approver != null && !approver.getEmployeeId().equals(requester.getEmployeeId())) return ResponseFormat.buildErrorResult("You are not the approver of this timesheet.");
                if (supervisor != null && !supervisor.getEmployeeId().equals(requester.getEmployeeId())) return ResponseFormat.buildErrorResult("You are not the supervisor of this timesheet.");
            }
        }

        // update flex time on employee
        if (requestedFlexTime != null) {
            ee.setFlexTime(ee.getFlexTime().subtract(requestedFlexTime));
            employeeManager.merge(ee);
        }

        // Update approve value and flex time
        ts.setFlexTime(requestedFlexTime);
        ts.setIsApproved(approvedByte);
        timesheetManager.merge(ts);

        JSONTimesheet jsonTs = new JSONTimesheet(ts);
        return ResponseFormat.buildSuccessResult(jsonTs);
    }
}
