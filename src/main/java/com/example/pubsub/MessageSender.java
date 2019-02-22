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

import java.util.ArrayList;
import java.util.List;


public class MessageSender {

    //private int NUM_THREADS = 10;

    public static class MsgLoaderRunnable implements Runnable {
        private String topicID;
        MsgLoaderRunnable(String topicID){
            this.topicID = topicID;
        }
        @Override
        public void run(){
            try {
                CreateTopic.main(topicID);
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public static ApiFuture<String> loadMessage(String... args) {
        String topicID = args[0];
        Publisher publisher = null;
        String msg = myPublisher.BuildString(topicID);
        ByteString msgData = ByteString.copyFromUtf8(msg);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(msgData).build();

        return publisher.publish(pubsubMessage);
    }

    public static void main(String... args){
        int msgCount = 500;
        String topicID = args[0];
        int NUM_THREADS = 10;
        //String subscriberID = args[1];
        Thread[] topicThread = new Thread [NUM_THREADS];
        List<ApiFuture<String>> futures = new ArrayList<>();

        for(int i = 0; i < topicThread.length; ++i) {
            topicThread[i] = new Thread(new MsgLoaderRunnable(topicID));
            topicThread[i].run();

            ApiFuture<String> future = loadMessage(topicID);
            futures.add(future);
        }




    }


}
