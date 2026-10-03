package com.counselor.order.mq;

import com.counselor.order.dto.AppointmentCreatedMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AppointmentMessageProducer {

    public static final String TOPIC_APPOINTMENT_CREATED = "appointment-created";

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送预约成功消息
     */
    @Async
    public void sendAppointmentCreated(AppointmentCreatedMessage message) {
        // key 用 orderId，保证同一订单进同一分区
        String key = String.valueOf(message.getOrderId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC_APPOINTMENT_CREATED, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Kafka 消息发送成功: topic={}, partition={}, offset={}, orderId={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        message.getOrderId());
            } else {
                log.error("Kafka 消息发送失败: orderId={}", message.getOrderId(), ex);
                // 生产环境：这里应该落库到"本地消息表"，由定时任务补偿
            }
        });
    }
}