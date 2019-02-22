/*
Examples are taken from Google Cloud pubsub documentation.
 */
package com.example.pubsub;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.StorageOptions;
import com.google.pubsub.v1.ProjectTopicName;

import java.io.FileInputStream;
import java.io.IOException;

public class CreateTopic {

    /**
     * Create a new topic
     * @param args topicID
     * @throws Exception exception if operation is unsuccessful
     */

    public static void main(String... args ) throws Exception {

        //This is the GCP project ID
        String projectID = ServiceOptions.getDefaultProjectId();
        String topicID = args[0]; // name of the topic "my-topic" etc

        //Creating the new topic with topicID
        ProjectTopicName mynewtopic = ProjectTopicName.of(projectID, topicID);
        try(TopicAdminClient topicAdminClient = TopicAdminClient.create() ) {
            topicAdminClient.createTopic(mynewtopic);
        } catch (ApiException e) {
            System.out.println(e.getStatusCode().getCode());
            System.out.print(e.isRetryable());
        }
        //System.out.printf("Topic %s:%s created: \n ", mynewtopic.getProject(), mynewtopic.getTopic());
        System.out.printf("%s created \n", mynewtopic.getTopic());
    }

}
