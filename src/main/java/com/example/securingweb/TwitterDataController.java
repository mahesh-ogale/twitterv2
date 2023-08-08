package com.example.securingweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class TwitterDataController {

    Logger logger = LoggerFactory.getLogger(TwitterDataController.class);

    @Autowired
    private TwitterDataService twitterService;

    @Autowired
    EmailService emailService;

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
        logger.info("Twitter count - query name {}, query {}, from date {}. to date {}", twitterCountRequest.getQueryName(), twitterCountRequest.getQuery());

        if (twitterCountRequest.getQuery().length() > 1024) {
//            model.addAllAttributes("message", "Query length exceeded beyond 1024 chars");
            return "hello";
        }

        if (StringUtils.isEmpty(twitterCountRequest.getQueryName()) || StringUtils.isEmpty(twitterCountRequest.getQuery())) {
//            model.put("message", "QueryName, query, fromDate, toDate all are required");
            return "hello";
        }

        logger.info("Basic access enabled: " + twitterCountRequest.getBasicAccess());

//        logger.info("Creating twitter count request for query {}", queryName);
//        TwitterCountRequest countRequest = new TwitterCountRequest();
//        countRequest.setQuery(query);
//        countRequest.setFromDate(fromDate);
//        countRequest.setToDate(toDate);
//        countRequest.setBucket("day");
//
//        int twitterCallsMade = 0;
//        int totalCount = 0;
//        JSONParser parser = new JSONParser();
//        JSONObject jsonObject;
//        boolean error = false;
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//            while (true) {
//                String jsonString = mapper.writeValueAsString(countRequest);
//                HttpResponse<String> response = Unirest.post("https://api.twitter.com/2/tweets/counts/recent")
//                        .header("Content-Type", "application/json")
//                        .header("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAL7x8QAAAAAA1dGPIO3EUkSc9MawaBuZIGfPscM%3DMD5nBaB6oSzcHWfQdm309a0IlUWr1nqpAvuKN2y55JNBxLsZ4t")
//                        .header("cache-control", "no-cache")
//                        .header("Postman-Token", "4d607bef-8c2d-416a-9aa8-5721051a2eb8")
//                        .body(jsonString)
//                        .asString();
//
//                jsonObject = (JSONObject) parser.parse(response.getBody());
//                if (jsonObject.get("error") != null) {
//                    error = true;
//                    break;
//                }
//                Long count = (Long) jsonObject.get("totalCount");
//                totalCount += count;
//
//                String next = (String) jsonObject.get("next");
//                countRequest.setNext(next);
//
//                twitterCallsMade++;
//                if (next == null) {
//                    logger.info("Total twitter count queries made {}", twitterCallsMade);
//                    break;
//                }
//            }
//            if (error) {
//                model.put("message", ((HashMap)jsonObject.get("error")).get("message"));
//                logger.info("Error happened while getting query count for query name {}", queryName);
//            } else {
//                logger.info("Success getting query count for query name {}", queryName);
//                model.put("message", "Total Tweets for the query: " + totalCount);
//            }
//        } catch (Exception e) {
//            model.put("message", e.toString());
//            return "hello";
//        }
        logger.info("Done with all the processing for query name {}", twitterCountRequest.getQueryName());
        return "hello";
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public String download(@RequestParam("queryName") String queryName, @RequestParam("query") String query, @RequestParam("fromDate") String fromDate,
                           @RequestParam("toDate") String toDate, Map<String, Object> model) {
        logger.info("Twitter download12 - query name {}, query {}, from date {}. to date {}", queryName, query, fromDate, toDate);

        if (query.length() > 1024) {
            model.put("message", "Query length exceeded beyond 1024 chars");
            return "hello";
        }

        if (StringUtils.isEmpty(queryName) || StringUtils.isEmpty(query) || StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate)) {
            model.put("message", "QueryName, query, fromDate, toDate all are required");
            return "hello";
        }

        logger.info("Twitter download - calling download tweets");
//        twitterService.downloadTweets(queryName, query, fromDate, toDate);
        model.put("message", "http://twitterdata.eu-west-1.elasticbeanstalk.com/status/" + queryName);
        logger.info("Twitter download complete for query {}", queryName);
        return "hello";
    }
}
