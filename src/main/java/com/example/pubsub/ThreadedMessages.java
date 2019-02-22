package com.example.pubsub;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import javax.naming.NamingException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

public class ThreadedMessages {
    private static ByteArrayOutputStream bout;

    public static void CreatePublishSend(String topicID, String subscriptionID, int msgCount) {
        //ByteArrayOutputStream bout1;

        ByteArrayOutputStream bout;
        bout = new ByteArrayOutputStream(); //define a new Output stream byte array
        PrintStream out = new PrintStream(bout); //new print stream to redirect output away from std output
        System.setOut(out);

        //Create a Topic
        try {
            CreateTopic.main(topicID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //publish messages

        Set<String> expectedMsgs = new HashSet<>();
        List<String> receivedMsgs = new ArrayList<>();

        //expectedMsgs.addAll(Arrays.asList());
        //bout1.reset();
        try{
            bout.reset();
            myPublisher.main(topicID, Integer.toString(msgCount));
            expectedMsgs.add(bout.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        bout.reset();
        CreatePullSubscription.main(topicID, subscriptionID);

        while(!expectedMsgs.isEmpty()){
            for(String msg: expectedMsgs){
                if(bout.toString().contains(msg)){
                    receivedMsgs.add(msg);
                }
            }
            expectedMsgs.removeAll(receivedMsgs);
        }
    }

    public static class CreatePublishSendRunnable implements Runnable {
        private String topicID;
        private String subscriptionID;
        private int msgCount;

        CreatePublishSendRunnable(String topicID, String subscriptionID, int msgCount){
            this.topicID = topicID;
            this.subscriptionID = subscriptionID;
            this.msgCount = msgCount;
        }

        @Override
        public void run() {
            CreatePublishSend(topicID, subscriptionID, msgCount);
        }
    }

    public static void main(String... args) throws InterruptedException {

        //int numTopics = 1;
        //int numsubscribers = 1;
        int numThreads = 10;

        //List<String> topicIDs = new

        Thread[] pubsubThreads = new Thread[numThreads];
        for(int i = 0; i < numThreads; ++i) {
            String topicID = FormatForTest("test-topic-");
            String subscriptionID = FormatForTest("test-sub-");
            int msgCount =5000;
            pubsubThreads[i] = new Thread(new CreatePublishSendRunnable(topicID, subscriptionID, msgCount));
            pubsubThreads[i].start();


        }
        Thread.sleep(1000L);

    }

    private static String FormatForTest(String name) {
        return name + java.util.UUID.randomUUID().toString();
    }
}
