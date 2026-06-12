package sc.laplace.test.rabbitmq.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sc.laplace.test.rabbitmq.config.RabbitMessageQueueConfig;

/**
 * @author jxwu
 */
@Slf4j
@Component
public class RepositoryMapListener {
    /**
     * 监听tag map队列
     * <p>
     * Headers operator: add
     * <p>
     * 数据接收为byte[]，不是string的时候就是因为没加这个
     * Properties content_type: text/plain
     * <p>
     * Payload {"source":"test-source","key":"test-key","value":"test-value","valid":1}
     * <p>
     * HGET "library:tag:repository:map:hash" test-source:test-key:test-value
     */
    @RabbitListener(queues = "#{anonyTagMapQueue.getName()}", containerFactory = "rabbitContainerFactory")
    public void listener(@Payload String data, @Header(RabbitMessageQueueConfig.OPERATOR) String operator) {
        log.info("tag map listener. operator: {}, data: {}", operator, data);
    }
}
