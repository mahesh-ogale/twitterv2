package com.example.securingweb;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SseEmitters {

    private static Map<String, SseEmitter> emitterMap = new HashMap();

    public static void addSseEmitter(String queryName) {
        emitterMap.put(queryName, new SseEmitter(Long.MAX_VALUE));
    }

    public static Map<String, SseEmitter> getAll() {
        return emitterMap;
    }

    public static void emptyMap() {
        emitterMap.clear();
    }
}
