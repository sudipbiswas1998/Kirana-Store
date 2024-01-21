package com.sudip.kiranastore.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class ResponseUtil {

    private boolean success;
    private String message;
    private Map<String, Object> data;

    public static ResponseUtil success(String message, Object data) {
        Map<String, Object> dataMap = new HashMap<>();
        if (data != null) {
            dataMap.put("data", data);
        }
        return new ResponseUtil(true, message, dataMap);
    }

    public static ResponseUtil failure(String message) {
        return new ResponseUtil(false, message, null);
    }

}
