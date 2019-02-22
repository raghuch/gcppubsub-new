package com.example.pubsub;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class mySubscriber {

    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private static final BlockingQueue<PubsubMessage> msgs = new LinkedBlockingDeque<>();

     static class MessageReceiverExample implements MessageReceiver {
         @Override
         public void receiveMessage(PubsubMessage msg, AckReplyConsumer consumer){
             msgs.offer(msg);
             consumer.ack();
        }
    }

    // To receive messages over a subscription
    public static void main(String... args) throws Exception {

         String subscriptionID = args[0];
         ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, subscriptionID);
         Subscriber subscriber = null;

         try{
             subscriber = Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
             subscriber.startAsync().awaitRunning();

             while(true){
                 PubsubMessage msg = msgs.take();
                 System.out.println("Message ID: " + msg.getMessageId());
                 System.out.println("Data: " + msg.getData().toStringUtf8());
             }

         } finally {
             if(subscriber!= null){
                 subscriber.stopAsync();
             }
         }
    }
}
