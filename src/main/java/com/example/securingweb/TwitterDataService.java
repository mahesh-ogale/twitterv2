package com.example.securingweb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.jobrunr.jobs.annotations.Job;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TwitterDataService {

    public static final long EXECUTION_TIME = 5000L;

    private Logger logger = LoggerFactory.getLogger(TwitterDataService.class);

    private AtomicInteger count = new AtomicInteger();

    @Autowired
    EmailService emailService;

    @Autowired
    SseController sseController;

    @Job(name = "The sample job with variable %0", retries = 2)
    public void executeSampleJob(String variable) {

        logger.info("The sample job has begun. The variable you passed is {}", variable);
        try {
            Thread.sleep(EXECUTION_TIME);
        } catch (InterruptedException e) {
            logger.error("Error while executing sample job", e);
        } finally {
            count.incrementAndGet();
            logger.info("Sample job has finished...");
        }
    }

    @Async("asyncExecutor")
    public void downloadTweets(TwitterCountRequest twitterCountRequest) throws InterruptedException, IOException {
        SseEmitter sseEmitter = sseController.getEmitter();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            String clientRegion = "eu-west-1";
            String bucketName = "psa-tweets-data";
            LocalDateTime timeFolderName = LocalDateTime.now();
            String folderName = "archive-query-data/" + twitterCountRequest.getQueryName() + "/" + timeFolderName + "/page";

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
//                    .withCredentials(new ProfileCredentialsProvider("eb-cli"))
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();

            s3Client.putObject(bucketName, "archive-query-data/" + twitterCountRequest.getQueryName() + "/" + timeFolderName + "/query", twitterCountRequest.getQuery());
            postToSseIfNotNull("archive-query-data/" + twitterCountRequest.getQueryName() + "/" + timeFolderName + "/query", sseEmitter);
            long start = Clock.systemUTC().millis();
            JSONParser parser = new JSONParser();
            boolean error = false;
            JSONObject jsonObject;
            String nextToken = null;
            String baseGetQuery = "https://api.twitter.com/2/tweets/search/recent?max_results=100&query=" + UriUtils.encode(twitterCountRequest.getQuery(), StandardCharsets.UTF_8.toString());
            int page = 1;

            logger.info("Starting the tweets download process for query name {}", twitterCountRequest.getQueryName());
            while (true) {
                String getQuery = null;
                if (nextToken != null) {
                    getQuery = String.format("%s&next_token=%s", baseGetQuery, nextToken);
                } else {
                    getQuery = String.format("%s", baseGetQuery);
                }
                HttpResponse<String> response = Unirest.get(getQuery)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAL7x8QAAAAAA1dGPIO3EUkSc9MawaBuZIGfPscM%3DMD5nBaB6oSzcHWfQdm309a0IlUWr1nqpAvuKN2y55JNBxLsZ4t")
                        .header("cache-control", "no-cache")
                        .header("Postman-Token", "4d607bef-8c2d-416a-9aa8-5721051a2eb8")
                        .asString();
                jsonObject = (JSONObject) parser.parse(response.getBody());
                if (response.getStatus() == 200) {
                    try {
                        // Upload a file as a new object with ContentType and title specified.
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentType("application/json");

                        PutObjectRequest request = new PutObjectRequest(bucketName,  folderName + page, response.getRawBody(), metadata);
                        s3Client.putObject(request);
                        postToSseIfNotNull("Uploaded page " + page + " to s3 folder " + folderName + page, sseEmitter);
//                        Files.write(Paths.get("/Users/maheshogale/Documents/source/twitterv2/page"+page), response.getRawBody().readAllBytes());


                        nextToken = (String)((HashMap) jsonObject.get("meta")).get("next_token");
                        if (nextToken == null) {
                            break;
                        }

                    } catch(AmazonServiceException e) {
                        // The call was transmitted successfully, but Amazon S3 couldn't process
                        // it, so it returned an error response.
                        postToSseIfNotNull(e.getMessage(), sseEmitter);
                    } catch(SdkClientException e) {
                        // Amazon S3 couldn't be contacted for a response, or the client
                        // couldn't parse the response from Amazon S3.
                        postToSseIfNotNull(e.getMessage(), sseEmitter);
                    }
                    ++page;
                }
                if (response.getStatus() == 400) {
                    if (jsonObject.get("errors") != null) {
                        error = true;
                        break;
                    }
                }
                if (response.getStatus() == 429) {
                    String rateLimitResetTimeString = response.getHeaders().getFirst("x-rate-limit-reset");
                    long currentTime = Instant.now().getEpochSecond();
                    long rateLimitResetTime = Long.valueOf(rateLimitResetTimeString);
                    sseEmitter = null;
                    if ((rateLimitResetTime - currentTime) > 1200) {
                        logger.info("Exiting the process as rate limit reset time is too high {}", LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(rateLimitResetTimeString)), ZoneOffset.UTC));
                        break;
                    }
                    postToSseIfNotNull("Rate limit reached, pausing the process for now, going to try after = " + LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(rateLimitResetTimeString)), ZoneOffset.UTC),
                            sseEmitter);
                    // get current epoch
                    // put thread to sleep for resetEpoch - current epoch + 5 sec more as buffer
                    TimeUnit.SECONDS.sleep((rateLimitResetTime - currentTime) + 5);
                }
            }
            long end = Clock.systemUTC().millis();
            if (error) {
                String errorMessage = (String)jsonObject.get("detail");
                postToSseIfNotNull(errorMessage, sseEmitter);
                logger.info("Error happened while getting query count for query name {}", twitterCountRequest.getQueryName());
                emailService.sendEmail(twitterCountRequest.getQueryName(), errorMessage);
            } else {
                logger.info("Success while downloading tweets for query {}", twitterCountRequest.getQueryName());
                String finalStatus = page + " pages uploaded to s3 in folder " + folderName + " in " + (end - start) + " ms";
                postToSseIfNotNull(finalStatus, sseEmitter);
                emailService.sendEmail(twitterCountRequest.getQueryName(), finalStatus);
            }
        } catch(Exception e){
            logger.error("Error while downloading tweets for query", e);
            postToSseIfNotNull("Exception occurred while downloading tweets, " + e.getMessage(), sseEmitter);
            emailService.sendEmail(twitterCountRequest.getQueryName(), "Exception occurred while downloading tweets" + e.getMessage());
        } finally{
            sseEmitter.complete();
        }
        logger.info("tweets downloaded successfully for query{}", twitterCountRequest.getQueryName());
    }

    private void postToSseIfNotNull(String message, SseEmitter sseEmitter) throws IOException {
        logger.info(message);
        if (sseEmitter != null) {
            sseEmitter.send(message, MediaType.TEXT_PLAIN);
        }
    }
}
