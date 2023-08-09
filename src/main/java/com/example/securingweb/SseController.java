package com.example.securingweb;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
public class SseController {

    private SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() throws IOException, InterruptedException {

        // Add the emitter to a list of subscribers or handle it in another way

        return emitter;
    }

    public SseEmitter getEmitter() {
        return emitter;
    }
}