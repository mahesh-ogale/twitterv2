package com.example.securingweb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StatusController {
    private final List<SseEmitter> emitters = new ArrayList<>();

    @GetMapping("/status/{queryName}")
    public SseEmitter stream(@PathVariable("queryName") String queryName) {
        return SseEmitters.getAll().get(queryName);
    }
}
