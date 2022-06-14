package com.comp4911.backend.lib;

import java.util.HashMap;
import java.util.Map;

public class ResponseFormat {
    /**
     * Build a successful object that to be sent to the client
     *
     * @param obj the object to be wrapped
     * @return an object that to be sent to the client
     */
    public static Map<String, Object> buildSuccessResult(Object obj){
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", obj);
        return result;
    }

    /**
     * Build a successful object with message that to be sent to the client
     *
     * @param obj the object to be wrapped
     * @param message the message to be sent
     * @return an object that to be sent to the client
     */
    public static Map<String, Object> buildSuccessResult(Object obj, String message){
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", message);
        result.put("data", obj);
        return result;
    }

    /**
     * Build a failed object that to be sent to the client
     *
     * @param message the failed message to be sent
     * @return an object that to be sent to the client
     */
    public static Map<String, Object> buildFailedResult(String message){
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("message", message);
        return result;
    }

    /**
     * Build an error object that to be sent to the client
     *
     * @param message the error message to be sent
     * @return an object that to be sent to the client
     */
    public static Map<String, Object> buildErrorResult(String message){
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", 2);
        result.put("message", message);
        return result;
    }
}
