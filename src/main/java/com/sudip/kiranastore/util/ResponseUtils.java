package com.sudip.kiranastore.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ResponseUtils {

    public static Map<String, Object> failureResponse(String message, String reasonCode) {
        log.debug("Reason for failure {}", message);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", "fail");
        Map<String, String> reasonMap = new HashMap<>();
        reasonMap.put("reason_code", reasonCode);
        reasonMap.put("reason_eng", message);
        map.put("reason", reasonMap);
        map.put("response_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return map;
    }

    public static Map<String, Object> failureResponse(String message, String reasonCode, Map<String, Object> fixType) {
        log.debug("Reason for failure {}", message);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", "fail");
        Map<String, Object> reasonMap = new LinkedHashMap<>();
        reasonMap.put("reason_code", reasonCode);
        reasonMap.put("reason_eng", message);
        reasonMap.put("additional_info", fixType);
        map.put("reason", reasonMap);
        map.put("response_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return map;
    }


    public static Map<String, Object> successResponse(Map<String, Object> jsonMap) {
        log.debug("response map {}", jsonMap);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("status", "success");
        responseMap.put("result", jsonMap);
        responseMap.put("response_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return responseMap;
    }

    public static Map<String, Object> successResponse(Object object) {
        log.debug("response object {}", object);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("status", "success");
        responseMap.put("result", object);
        responseMap.put("response_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return responseMap;
    }



    public static Map<String, Object> ok() {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("status", "success");
        responseMap.put("response_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return responseMap;
    }
}

