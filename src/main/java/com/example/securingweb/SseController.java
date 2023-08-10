package com.example.securingweb;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class SseController {

    private SseEmitter emitter = null;

    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() throws IOException, InterruptedException {
        return emitter;
    }

    public SseEmitter getEmitter() {
        emitter = new SseEmitter(0l);
        return emitter;
    }
}