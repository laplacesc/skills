package sc.laplace.test.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jxwu
 */
@Slf4j
@Configuration
public class RabbitMessageQueueConfig {
    /**
     * rabbit mq 同步信息的类型定义，三种，新增，删除，更新
     */
    public static final String OPERATOR = "operator";
    public static final String OPERATION_ADD = "add";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "delete";

    public static final String TAG_MAP_EXCHANGE_NAME = "exchange.fanout.tip.library.tag.map";
    public static final String TAG_MAP_LIBRARY_QUEUE_NAME_PREFIX = "queue.anonymous.tip.library.tag.map.";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    /**
     * 如果没有指定其他的container factory默认使用自定义的container factory
     */
    @Bean(name = "rabbitContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setErrorHandler(new ConditionalRejectingErrorHandler(t -> {
            log.error(t.getMessage(), t);
            return true;
        }));
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    /**
     * 用于tag 映射关系同步的exchange
     */
    @Bean(name = "tagMapExchange")
    public FanoutExchange tagMapExchange() {
        return ExchangeBuilder.fanoutExchange(TAG_MAP_EXCHANGE_NAME).build();
    }

    @Bean(name = "anonyTagMapQueue")
    public Queue anonyTagMapQueue() {
        return new AnonymousQueue(new Base64UrlNamingStrategy(TAG_MAP_LIBRARY_QUEUE_NAME_PREFIX));
    }

    @Bean(name = "tagMapBinding")
    public Binding tagMapBinding(@Qualifier(value = "tagMapExchange") FanoutExchange exchange,
                                 @Qualifier(value = "anonyTagMapQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange);
    }
}
