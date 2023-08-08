package com.example.securingweb;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // Replace sender@example.com with your "From" address.
    // This address must be verified with Amazon SES.
    static final String FROM = "sonuogale@gmail.com";

    // Replace recipient@example.com with a "To" address. If your account
    // is still in the sandbox, this address must be verified.
    static final String TO = "maheshogale@hotmail.com";

    // The configuration set to use for this email. If you do not want to use a
    // configuration set, comment the following variable and the
    // .withConfigurationSetName(CONFIGSET); argument below.
    static final String CONFIGSET = "ConfigSet";

    // The subject line for the email.
    static final String SUBJECT = "Twitter data download completed - ";

    // The HTML body for the email.
//    static final String HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
//            + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
//            + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
//            + "AWS SDK for Java</a>";

    // The email body for recipients with non-HTML email clients.
//    static final String TEXTBODY = "This email was sent through Amazon SES "
//            + "using the AWS SDK for Java.";

    public void sendEmail(String query, String body) {
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion("eu-west-1")
//                        .withCredentials(new ProfileCredentialsProvider("eb-cli")).build();
                        .withCredentials(new DefaultAWSCredentialsProviderChain()).build();

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(TO))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(body)))
//                                .withText(new Content()
//                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT + query)))
                .withSource(FROM);
        client.sendEmail(request);
        System.out.println("Email sent!");
    }
}
