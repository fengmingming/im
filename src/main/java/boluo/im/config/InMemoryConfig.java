package boluo.im.config;

import boluo.im.client.repository.AccountBrokerRepository;
import boluo.im.client.repository.AccountRepository;
import boluo.im.client.repository.DefaultAccountRepository;
import boluo.im.client.repository.InMemoryAccountBrokerRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(RedisConfig.class)
public class InMemoryConfig {

    @Bean
    public AccountBrokerRepository accountBrokerRepository() {
        return new InMemoryAccountBrokerRepository();
    }

    @Bean
    public AccountRepository defaultAccountRepository() {
        return new DefaultAccountRepository();
    }

}
