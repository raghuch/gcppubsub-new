package com.example.pubsub;



import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.ServiceOptions;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.cloud.pubsub.v1.Publisher;
import org.apache.commons.lang3.RandomStringUtils;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class myPublisher {

    //using the default project ID
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

    public static String BuildString(String topicID) {
        int len = 100;
        boolean useLetters = true;
        boolean useNumbers = true;
        String randStr = RandomStringUtils.random(len, useLetters,useNumbers);
        return topicID + "  " + randStr;
    }

  /*  public ApiFuture<String> addStrToQueue(String msg) {
        Publisher publisher = null;
        ByteString msgData = ByteString.copyFromUtf8(msg);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(msgData).build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        return future;
    } */

    public static void main(String... args) throws Exception {
        String topicID = args[0];
        int msgCount = Integer.parseInt(args[1]);
        ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, topicID);
        List<String> msgList = new ArrayList<>();
        List<ApiFuture<String>> futures = new ArrayList<>();
        Publisher publisher = null;

        try {
            //Create a publisher instance,with default settings, bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

            for(int i = 0; i < msgCount; ++i) {
                String msg = BuildString(topicID);
                msgList.add(msg);
            }
            for(String msg: msgList) {
                ByteString msgdata = ByteString.copyFromUtf8(msg);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(msgdata).build();
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                futures.add(future);
            }
            //futures.add(msgList.forEach((msg) -> addStrToQueue(msg) ));
        } finally {
            //Waiting on requests pending
            List<String> msgIDs = ApiFutures.allAsList(futures).get();

            for(String msgID: msgIDs){
                System.out.println(msgID);
            }
            if(publisher!= null){
                publisher.shutdown();
            }
        }
    }
}
