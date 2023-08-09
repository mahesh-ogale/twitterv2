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
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TwitterDataService {

    public static final long EXECUTION_TIME = 5000L;

    private Logger logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(getClass()));

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

    public int getNumberOfInvocations() {
        return count.get();
    }

//    @Job(name = "tweets download")
    @Async("asyncExecutor")
    public void downloadTweets(String queryName, String query) throws InterruptedException, IOException {

        logger.info("Creating twitter download request for query name {}", queryName);

//        TwitterRequestDownload downloadRequest = new TwitterRequestDownload();
//        downloadRequest.setQuery(query);
//        downloadRequest.setFromDate(fromDate);
//        downloadRequest.setToDate(toDate);
//        downloadRequest.setMaxResults(100l);

//        SseEmitters.emptyMap();
//        SseEmitters.addSseEmitter(queryName);
//        SseEmitter sseEmitter = SseEmitters.getAll().get(queryName);

        int i = 0;
        while (i < 50) {
            sseController.getEmitter().send("working on the query", MediaType.TEXT_PLAIN);
            TimeUnit.SECONDS.sleep(2);
            i++;
        }
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//            int page = 1;
//            String clientRegion = "eu-west-1";
//            String bucketName = "psa-tweets-data";
//            LocalDateTime timeFolderName = LocalDateTime.now();
//            String folderName = "archive-query-data/" + queryName + "/" + timeFolderName + "/page";
//
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                    .withRegion(clientRegion)
////                    .withCredentials(new ProfileCredentialsProvider("eb-cli"))
//                    .withCredentials(new DefaultAWSCredentialsProviderChain())
//                    .build();

//            s3Client.putObject(bucketName, "archive-query-data/" + queryName + "/" + timeFolderName + "/query", downloadRequest.toString());
//            sseEmitter.send("archive-query-data/" + queryName + "/" + timeFolderName + "/query", MediaType.TEXT_PLAIN);
//            long start = Clock.systemUTC().millis();
//            JSONParser parser = new JSONParser();
//            boolean error = false;
//            JSONObject jsonObject;
//            while (true) {
////                String jsonString = mapper.writeValueAsString(downloadRequest);
//
//                HttpResponse<String> response = Unirest.post("https://api.twitter.com/1.1/tweets/search/fullarchive/dev.json")
//                        .header("Content-Type", "application/json")
//                        .header("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAL7x8QAAAAAA1dGPIO3EUkSc9MawaBuZIGfPscM%3DMD5nBaB6oSzcHWfQdm309a0IlUWr1nqpAvuKN2y55JNBxLsZ4t")
//                        .header("cache-control", "no-cache")
//                        .header("Postman-Token", "4d607bef-8c2d-416a-9aa8-5721051a2eb8")
////                        .body(jsonString)
//                        .asString();
//
//                jsonObject = (JSONObject) parser.parse(response.getBody());
//                try {
//                    ObjectMetadata metadata = new ObjectMetadata();
//                    metadata.setContentType("application/json");
//
////                    if (jsonObject.get("error") != null) {
////                        error = true;
////                        break;
////                    }
//                    // Upload a file as a new object with ContentType and title specified.
//                    PutObjectRequest request = new PutObjectRequest(bucketName,  folderName + page, response.getRawBody(), metadata);
//                    s3Client.putObject(request);
////                    sseEmitter.send("Uploaded " + page + " to s3 " + folderName, MediaType.TEXT_PLAIN);
//
//                    TimeUnit.SECONDS.sleep(2);
//                }
//                catch(AmazonServiceException e) {
//                    // The call was transmitted successfully, but Amazon S3 couldn't process
//                    // it, so it returned an error response.
////                    sseEmitter.send(e.getMessage(), MediaType.TEXT_PLAIN);
//                }
//                catch(SdkClientException e) {
//                    // Amazon S3 couldn't be contacted for a response, or the client
//                    // couldn't parse the response from Amazon S3.
////                    sseEmitter.send(e.getMessage(), MediaType.TEXT_PLAIN);
//                }
//
////                String next = (String) jsonObject.get("next");
////                downloadRequest.setNext(next);
//
////                if (next == null) {
////                    logger.info("Total pages downloaded {}", page);
////                    break;
////                }
//                ++page;
//            }
////            long end = Clock.systemUTC().millis();
////            if (error) {
////                String errorMessage = "Error occurred while downloading tweets" + ((HashMap)jsonObject.get("error")).get("message");
//////                sseEmitter.send(errorMessage, MediaType.TEXT_PLAIN);
////                logger.info("Error while downloading tweets for query {}", queryName);
////                emailService.sendEmail(queryName, errorMessage);
////            } else {
////                logger.info("Success while downloading tweets for query {}", queryName);
//////                String finalStatus = page + " pages uploaded to s3 in folder " + folderName + " in " + (end - start) + " ms";
//////                sseEmitter.send(finalStatus, MediaType.TEXT_PLAIN);
//////                emailService.sendEmail(queryName, finalStatus);
////            }
//        } catch (Exception e) {
//            logger.error("Error while downloading tweets for query", e);
//            //                sseEmitter.send("Exception occurred while downloading tweets", MediaType.TEXT_PLAIN);
//            emailService.sendEmail(queryName, "Exception occurred while downloading tweets" + e.getMessage());
//        } finally {
            sseController.getEmitter().complete();
//            SseEmitters.getAll().remove(queryName);
//        }
        logger.info("tweets downloaded successfully");
    }
}