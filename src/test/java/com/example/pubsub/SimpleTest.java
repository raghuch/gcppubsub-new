package com.example.pubsub;

import com.google.api.client.util.Lists;
import com.google.api.gax.paging.Page;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.StorageOptions;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.ProjectSubscriptionName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
//@SuppressWarnings()

public class SimpleTest {

    private ByteArrayOutputStream bout;

    private String projectID = ServiceOptions.getDefaultProjectId();
    private String topicID = formatForTest("test-topic-1");
    private String subscriptionID = formatForTest("sub-topic-1");
    private int msgCount = 5000;
    private static final String jsonPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

    class SubscriberRunnable implements Runnable {

        private String subscriptionID;
        SubscriberRunnable(String subscriptionID) {
            this.subscriptionID = subscriptionID;
        }
        @Override
        public void run() {
            try{
                mySubscriber.main(subscriptionID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

   // @Rule public Timeout globaltimeout = Timeout.seconds(1000);

    @Before
    public void setUp() {
        bout = new ByteArrayOutputStream();
        System.out.println(jsonPath);
        Auth.main(jsonPath);
        PrintStream out = new PrintStream(bout);
        System.setOut(out);
        try {
            deleteTestSubscription();
            deleteTestTopic();
        } catch (Exception e) {

        }
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
        //deleteTestSubscription();
        //deleteTestTopic();
    }

    @Test
    public void testSimple() throws Exception {
        //creating a topic
        CreateTopic.main(topicID);
        String got = bout.toString();
        assertThat(got).contains(topicID + " created");

        //creating subscriber
        CreatePullSubscription.main(topicID, subscriptionID);
        got = bout.toString();
        assertThat(got).contains(subscriptionID+" created");

        //publish messages
        bout.reset();
        myPublisher.main(topicID, String.valueOf(msgCount));
        String[] msgIDs = bout.toString().split("\n");
        assertThat(msgIDs).hasLength(msgCount);

        //receive messages
        bout.reset();
        Thread subscriberThread = new Thread(new SubscriberRunnable(subscriptionID));
        subscriberThread.start();
        Set<String> expectedMsgIDs = new HashSet<>();
        List<String> receivedMsgIDs = new ArrayList<>();
        expectedMsgIDs.addAll(Arrays.asList(msgIDs));

        while(!expectedMsgIDs.isEmpty()){
            for(String expectedID: expectedMsgIDs){
                if(bout.toString().contains(expectedID)){
                    receivedMsgIDs.add(expectedID);
                }
            }
            expectedMsgIDs.removeAll(receivedMsgIDs);
        }
        assertThat(expectedMsgIDs).isEmpty();
    }

    private String formatForTest(String name){
        return name + "--" + java.util.UUID.randomUUID().toString();
    }

    private void deleteTestTopic() throws  Exception{
        try(TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            topicAdminClient.deleteTopic(ProjectTopicName.of(projectID, topicID));
        } catch (IOException e) {
            System.err.println("Error deleting the topic: "+e.getMessage());
        }
    }

    private void deleteTestSubscription() throws Exception {
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.deleteSubscription(ProjectSubscriptionName.of(projectID, subscriptionID));
        } catch (IOException e) {
            System.err.println("Error deleting subscription: "+e.getMessage());
        }
    }





}
