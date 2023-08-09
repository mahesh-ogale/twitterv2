package com.example.securingweb;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TwitterDataController {

    Logger logger = LoggerFactory.getLogger(TwitterDataController.class);

    @Autowired
    private TwitterDataService twitterService;

    @Autowired
    EmailService emailService;

    @Autowired
    JobRunrController jobRunrController;

    @RequestMapping("/")
    public String welcome(Model model) {
//        emailService.sendEmail("test", "10 pages uploaded");
        model.addAttribute("countRequest", new TwitterCountRequest());
        return "hello";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping(value = "/queryCount")
    public String queryCount(@ModelAttribute TwitterCountRequest twitterCountRequest, Model model) {
        model.addAttribute("countRequest", twitterCountRequest);
        logger.info("Processing tweets count request with basic access = "+ twitterCountRequest.getBasicAccess());

        int allowedQueryLength = 512;

        if (StringUtils.isEmpty(twitterCountRequest.getQueryName()) || StringUtils.isEmpty(twitterCountRequest.getQuery())) {
            model.addAttribute("message", "QueryName, query are required");
            return "hello";
        }

        logger.info("Twitter count - query name {}, query {}", twitterCountRequest.getQueryName(), twitterCountRequest.getQuery());

        if (twitterCountRequest.getQuery().length() > allowedQueryLength) {
            model.addAttribute("message", String.format("Query length exceeded beyond %d chars", allowedQueryLength));
            return "hello";
        }

        logger.info("Creating twitter count request for query {}", twitterCountRequest.getQueryName());

        JSONParser parser = new JSONParser();
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.get("https://api.twitter.com/2/tweets/counts/recent?granularity=day&query="+ UriUtils.encode(twitterCountRequest.getQuery(), StandardCharsets.UTF_8.toString()))
                    .header("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAL7x8QAAAAAA1dGPIO3EUkSc9MawaBuZIGfPscM%3DMD5nBaB6oSzcHWfQdm309a0IlUWr1nqpAvuKN2y55JNBxLsZ4t")
                    .header("Cookie", "guest_id=v1%3A169114943501622797; guest_id_ads=v1%3A169114943501622797; guest_id_marketing=v1%3A169114943501622797; personalization_id=\"v1_qK+w8TOGRs8/240Jkks7wQ==\"")
                    .asString();
            JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
            if (response.getStatus() == 200) {
                logger.info("Success getting query count for query name {}", twitterCountRequest.getQueryName());
                String remainingRequestForRateLimitWindow = response.getHeaders().getFirst("x-rate-limit-remaining");
                String rateLimitResetTime = response.getHeaders().getFirst("x-rate-limit-reset");
                model.addAttribute("message", "Total Tweets for the query: " + ((HashMap) jsonObject.get("meta")).get("total_tweet_count") + "\n\n" +
                        String.format("You can do %s more request until %s", remainingRequestForRateLimitWindow,
                                LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(rateLimitResetTime)), ZoneOffset.UTC)));
            }
            if (jsonObject.get("errors") != null) {
                model.addAttribute("message", jsonObject.get("detail"));
                logger.info("Error happened while getting query count for query name {}", twitterCountRequest.getQueryName());
            }
            if (response.getStatus() == 429) {
                String rateLimitResetTime = response.getHeaders().getFirst("x-rate-limit-reset");
                model.addAttribute("message", "You have reached the rate limit, try after = " + LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(rateLimitResetTime)), ZoneOffset.UTC));
                logger.info("Rate limit reached while getting query count for query name {}", twitterCountRequest.getQueryName());
            }
        } catch (Exception e) {
            model.addAttribute("message", e.toString());
            return "hello";
        }
        logger.info("Done with all the processing for query name {}", twitterCountRequest.getQueryName());
        return "hello";
    }

    @PostMapping(value = "/download")
    public String download(@ModelAttribute TwitterCountRequest twitterCountRequest, Model model) {
        model.addAttribute("countRequest", twitterCountRequest);
        logger.info("Twitter download12 - query name {}, query {}, from date {}. to date {}", twitterCountRequest.getQueryName(), twitterCountRequest.getQuery());

        if (twitterCountRequest.getQuery().length() > 1024) {
            model.addAttribute("message", "Query length exceeded beyond 1024 chars");
            return "hello";
        }

        if (StringUtils.isEmpty(twitterCountRequest.getQueryName()) || StringUtils.isEmpty(twitterCountRequest.getQuery())) {
            model.addAttribute("message", "QueryName, query, fromDate, toDate all are required");
            return "hello";
        }

        logger.info("Twitter download - calling download tweets");
//        twitterService.downloadTweets(queryName, query, fromDate, toDate);
        jobRunrController.downloadTweets();
        model.addAttribute("message", "http://localhost:8000/dashboard/overview" + twitterCountRequest.getQueryName());
        logger.info("Twitter download complete for query {}", twitterCountRequest.getQueryName());
        return "hello";
    }
}
