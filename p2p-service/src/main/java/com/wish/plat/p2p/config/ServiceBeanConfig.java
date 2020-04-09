package com.wish.plat.p2p.config;

import com.wish.plat.p2p.rpc.GetTokenService;
import com.wish.plat.p2p.rpc.QueryMsgService;
import com.wish.plat.p2p.rpc.ReceiveMsgService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ： yangxd
 * @date ：Created in 2020/3/10 10:45
 * @description ：自动注入配置
 * @modified By：
 * @version: v1.0
 */
@Configuration
@ComponentScan(basePackageClasses = {ServiceBeanConfig.class})
public class ServiceBeanConfig {

    @ConditionalOnMissingBean(GetTokenService.class)
    @Bean
    public GetTokenService GetTokenService() {
        return new GetTokenService();
    }

    @ConditionalOnMissingBean(ReceiveMsgService.class)
    @Bean
    public ReceiveMsgService ReceiveMsgService() {
        return new ReceiveMsgService();
    }

    @ConditionalOnMissingBean(QueryMsgService.class)
    @Bean
    public QueryMsgService QueryMsgService() {
        return new QueryMsgService();
    }
}
