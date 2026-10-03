package com.counselor.order.mq;

import com.counselor.order.dto.AppointmentCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppointmentNotifyConsumer {

    /**
     * 消费预约成功消息，发送通知
     */
    @KafkaListener(
            topics = AppointmentMessageProducer.TOPIC_APPOINTMENT_CREATED,
            groupId = "appointment-notify-group"
    )
    public void onAppointmentCreated(AppointmentCreatedMessage message, Acknowledgment ack) {
        log.info("收到预约成功消息: orderId={}, userId={}, scheduleId={}",
                message.getOrderId(), message.getUserId(), message.getScheduleId());

        try {
            // 模拟发送短信
            sendSms(message);

            // 模拟发送站内信
            sendStationMessage(message);

            // 手动提交 offset
            ack.acknowledge();
            log.info("通知发送成功，已提交 offset: orderId={}", message.getOrderId());
        } catch (Exception e) {
            log.error("通知发送失败: orderId={}", message.getOrderId(), e);
            // 不 ack，Kafka 会重新投递（需配死信队列防止无限重试）
        }
    }

    private void sendSms(AppointmentCreatedMessage message) {
        // 模拟：真实场景调短信服务商 API
        log.info("【模拟短信】用户 {}，您的预约已成功，订单号 {}，预约时间 {}",
                message.getUserId(), message.getOrderNo(), message.getAppointmentTime());
    }

    private void sendStationMessage(AppointmentCreatedMessage message) {
        // 模拟：真实场景写站内信表
        log.info("【模拟站内信】用户 {}，您有一条约会预约，订单号 {}",
                message.getUserId(), message.getOrderNo());
    }
}
