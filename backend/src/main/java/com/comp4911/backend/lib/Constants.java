package com.comp4911.backend.lib;

import java.math.BigDecimal;

public class Constants {
    public static final BigDecimal DEFAULT_FLEX_TIME = new BigDecimal(20.00);
    public static final BigDecimal MINIMUM_WORKING_HOURS = new BigDecimal(40.00);
    public static final String SICK_LEAVE_CODE = "SICK";
    public static final String VACATION_LEAVE_CODE = "VACATION";
    public static final Integer LOGIN_TIMEOUT_IN_MINUTES = 24 * 60;
}
