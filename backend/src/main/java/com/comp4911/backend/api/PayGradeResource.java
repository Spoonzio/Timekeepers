package com.comp4911.backend.api;

import com.comp4911.backend.access.PaygradeManager;
import com.comp4911.backend.api.filters.annotations.AuthAdmin;
import com.comp4911.backend.api.filters.annotations.AuthEMP;
import com.comp4911.backend.api.filters.annotations.AuthHR;
import com.comp4911.backend.lib.ResponseFormat;
import com.comp4911.backend.models.JSONModels.JSONPaygrade;
import com.comp4911.backend.models.PaygradeEntity;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.*;

@Path("/paygrade")
public class PayGradeResource {

    @Inject
    private PaygradeManager paygradeManager;

    @GET
    @Produces("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Map<String, Object> readRate() {
        List<PaygradeEntity> found = paygradeManager.findAll();
        List<JSONPaygrade> jsonPaygrades = new ArrayList<>();
        for (PaygradeEntity entity : found) {
            jsonPaygrades.add(new JSONPaygrade(entity));
        }
        return ResponseFormat.buildSuccessResult(jsonPaygrades);
    }


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @AuthAdmin @AuthHR
    public Map<String, Object> createPayGrade(HashMap<String, String> newPayGrade) {
        String grade = newPayGrade.get("grade");
        String year = newPayGrade.get("year");

        // if year is not specified, create current year
        if (year == null) {
            year = "" + Calendar.getInstance().get(Calendar.YEAR);
        }

        BigDecimal rate = null;
        try {
            rate = new BigDecimal(newPayGrade.get("rate"));
        } catch (NumberFormatException e) {
            return ResponseFormat.buildErrorResult("rate is not a number");
        }

        if (grade == null) {
            return ResponseFormat.buildFailedResult("grade is required");
        }
        grade = grade.toUpperCase();

        PaygradeEntity found = paygradeManager.find(grade, year);
        if (found != null) {
            return ResponseFormat.buildFailedResult("grade already exists");
        }

        PaygradeEntity newEntity = new PaygradeEntity();
        newEntity.setGrade(grade);
        newEntity.setRate(rate);
        newEntity.setYear(year);

        paygradeManager.persist(newEntity);
        return ResponseFormat.buildSuccessResult(new JSONPaygrade(newEntity));
    }

    @PUT
    @Path("/{grade}")
    @Consumes("application/json")
    @Produces("application/json")
    @AuthAdmin @AuthHR
    public Map<String, Object> updatePayGrade(@PathParam("grade") String grade, @QueryParam("year") String year, HashMap<String, String> rate) {
        BigDecimal newRate = null;
        try {
            newRate = new BigDecimal(rate.get("rate"));
        } catch (NumberFormatException e) {
            return ResponseFormat.buildErrorResult("rate is not a number");
        }

        // if year is not specified, update the current year
        if (year == null) {
            year = "" + Calendar.getInstance().get(Calendar.YEAR);
        }

        grade = grade.toUpperCase();
        PaygradeEntity found = paygradeManager.find(grade, year);
        if (found == null) {
            return ResponseFormat.buildErrorResult("grade not found");
        }
        found.setRate(newRate);
        paygradeManager.merge(found);
        return ResponseFormat.buildSuccessResult(new JSONPaygrade(found));
    }

    @DELETE
    @Path("/{grade}")
    @Consumes("application/json")
    @Produces("application/json")
    @AuthAdmin @AuthHR
    public Map<String, Object> deletePayGrade(@PathParam("grade") String grade, @QueryParam("year") String year) {

        // if year is not specified, delete current year
        if (year == null) {
            year = "" + Calendar.getInstance().get(Calendar.YEAR);
        }

        grade = grade.toUpperCase();
        PaygradeEntity found = paygradeManager.find(grade, year);
        if (found == null) {
            return ResponseFormat.buildErrorResult("grade not found");
        }
        if (found.getEmployeeEntities().size() != 0){
            return ResponseFormat.buildErrorResult("Employee(s) are still assigned to this pay grade");
        }

        paygradeManager.remove(found);
        return ResponseFormat.buildSuccessResult("successfully deleted");
    }

    @GET
    @Path("/{grade}")
    @Produces("application/json")
    @AuthAdmin @AuthHR @AuthEMP
    public Response readRate(@PathParam("grade") String grade, @QueryParam("year") String year) {

        // if year is not specified, get the current year
        if (year == null) {
            year = "" + Calendar.getInstance().get(Calendar.YEAR);
        }

        grade = grade.toUpperCase();
        PaygradeEntity found = paygradeManager.find(grade, year);
        if (found == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(new JSONPaygrade(found)).build();
    }
}
