package com.example.securingweb;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class JobRunrController {

    private JobScheduler jobScheduler;
    private TwitterDataService twitterDataService;

    private JobRunrController(JobScheduler jobScheduler, TwitterDataService twitterDataService) {
        this.jobScheduler = jobScheduler;
        this.twitterDataService = twitterDataService;
    }

    public String enqueue(@PathVariable("input") @DefaultValue("default-input") String input) {
        jobScheduler.enqueue(() -> twitterDataService.executeSampleJob(input));
        return "job enqueued successfully";
    }

    public String downloadTweets() {

        jobScheduler.enqueue(() -> twitterDataService.downloadTweets("", ""));
        return "job enqueued successfully";
    }
}