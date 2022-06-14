package com.comp4911.backend.api;

import com.comp4911.backend.access.ProjectPackageManager;
import com.comp4911.backend.access.TimesheetRowManager;
import com.comp4911.backend.lib.ApproveStatus;
import com.comp4911.backend.models.JSONModels.JSONTimesheetrow;
import com.comp4911.backend.models.ProjectPackageEntity;
import com.comp4911.backend.models.TimesheetrowEntity;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationPath("/api")
public class TimesheetApplication extends Application {

}