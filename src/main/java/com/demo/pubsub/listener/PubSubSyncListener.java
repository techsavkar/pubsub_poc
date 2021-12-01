package com.demo.pubsub.listener;

import java.util.Timer;
import java.util.TimerTask;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubMessageSource;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
@Slf4j
public class PubSubSyncListener {

    @Bean
    public MessageChannel inputMessageChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    @InboundChannelAdapter(channel = "inputMessageChannel", poller = @Poller(fixedDelay = "100"))
    public MessageSource<Object> pubsubAdapter(PubSubTemplate pubSubTemplate) {
        PubSubMessageSource messageSource = new PubSubMessageSource(pubSubTemplate, "deltasub");
        messageSource.setAckMode(AckMode.MANUAL);
        messageSource.setPayloadType(String.class);
        messageSource.setBlockOnPull(true);
        return messageSource;
    }

    @ServiceActivator(inputChannel = "inputMessageChannel")
    public void receiveMessage(String payload, @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) AcknowledgeablePubsubMessage message) throws InterruptedException {
        log.info("received message is : {}", payload);
        Timer timer = new Timer("ack_deadline_extension_timer");
        //extendAckDeadline(message, timer);
        //Sleeping for 10 mins
        Thread.sleep(600000);
        message.ack();
        timer.cancel();
        log.info("message is acked : {}",payload);
    }

    private void extendAckDeadline(AcknowledgeablePubsubMessage message, Timer timer) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("Extending deadline every 50 seconds");
                message.modifyAckDeadline(60);
            }
        }, 0, 50000);
    }
}
