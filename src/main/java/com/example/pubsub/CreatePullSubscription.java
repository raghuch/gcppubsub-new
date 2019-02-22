package com.example.pubsub;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;

import java.io.IOException;

/**
 * Creates pull subscription
 * @param args topic subscriptionID
 */
public class CreatePullSubscription {

    public static void main(String... args) {
        String projectID = ServiceOptions.getDefaultProjectId();
        String topicID = args[0];
        String subscriptionID = args[1];

        ProjectTopicName topicName = ProjectTopicName.of(projectID, topicID);
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectID, subscriptionID);

        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            //create pull subscription client with default 10s ack time
            Subscription subscription = subscriptionAdminClient.createSubscription(subscriptionName, topicName,
                    PushConfig.getDefaultInstance(), 0);
        } catch (ApiException e) {
            System.out.print(e.getStatusCode().getCode());
            System.out.print(e.isRetryable());
        } catch (IOException e) {
            //
        }
        System.out.printf("%s created", subscriptionName.getSubscription());
        //System.out.printf("Subscription %s:%s is created: ",subscriptionName.getProject(), subscriptionName.getSubscription());
    }
}
