package com.example.user1.mq;

import com.alibaba.fastjson.JSON;

import com.rabbitmq.client.Channel;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JuseMqSender implements ApplicationContextAware {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Map<String, JuseMqHandler> receiverMapping = new HashMap();



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> receivers = applicationContext.getBeansWithAnnotation(JuseMqReceiver.class);
        if (!CollectionUtils.isEmpty(receivers)) {
            for (Object r : receivers.values()) {
                JuseMqReceiver receiver = r.getClass().getAnnotation(JuseMqReceiver.class);
                String prefix = receiver.value();
                for (Method method : r.getClass().getDeclaredMethods()) {
                    JuseMqReceiver methodReceiver = AnnotationUtils.getAnnotation(method, JuseMqReceiver.class);
                    if (null != methodReceiver) {
                        String key = (StringUtils.isEmpty(prefix) ? "" : prefix + ".") + methodReceiver.value();
                        receiverMapping.put(key, new JuseMqHandler(r, method));
                    }
                }
            }
        }
    }
    /**
     * 对外发送消息的方法
     *
     * @param server
     * @param key
     * @param message
     * @throws Exception
     */
    public <T> void send(String server, String key, Object message) {
//        CorrelationData correlationData = new CorrelationData();
//        correlationData.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("test", "user.senderuser", message);
    }

}
