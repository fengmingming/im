package boluo.im.config;

import boluo.im.client.repository.AccountRepository;
import boluo.im.client.repository.DefaultAccountRepository;
import boluo.im.client.repository.RedissonAccountRepository;
import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "spring.redis.redisson.config", matchIfMissing = false)
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient(@Value("${spring.redis.redisson.config}")String file) throws IOException {
        Config config = Config.fromYAML(file);
        return Redisson.create(config);
    }

    @Bean
    public AccountRepository accountRepository(IMConfig imConfig) {
        String groupKeyTemplate = imConfig.getGroupKeyTemplate();
        if(StrUtil.isNotBlank(groupKeyTemplate) && groupKeyTemplate.contains("{groupId}") && groupKeyTemplate.contains("{tenantId}")) {
            return new RedissonAccountRepository();
        }else {
            return new DefaultAccountRepository();
        }
    }

}
