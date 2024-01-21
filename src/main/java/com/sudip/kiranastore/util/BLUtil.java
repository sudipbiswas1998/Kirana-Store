package com.sudip.kiranastore.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class BLUtil {
    public static boolean validateResponseStatus(Map<String, Object> responseMap) {

        String status = (String) responseMap.get("status");
        if (status.equalsIgnoreCase("success"))
            return true;
        return false;
    }
}
