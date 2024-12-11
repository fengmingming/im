package boluo.im.client.repository;

import boluo.im.client.Account;
import boluo.im.client.Group;
import boluo.im.config.IMConfig;
import cn.hutool.core.collection.ListUtil;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.redisson.api.RListReactive;
import org.redisson.api.RedissonClient;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import reactor.core.publisher.Mono;

import java.util.List;

@Setter
public class RedissonAccountRepository extends DefaultAccountRepository {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private IMConfig imConfig;

    @Override
    public Mono<List<Account>> findByGroupId(String tenantId, String groupId) {
        Group group = new Group();
        group.setTenantId(tenantId);
        group.setGroupId(groupId);

        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext("{", "}");
        String key = parser.parseExpression(imConfig.getGroupKeyTemplate(), parserContext).getValue(group, String.class);
        RListReactive<Account> accounts = redissonClient.reactive().getList(key);
        Mono<List<Account>> superAccounts = super.findByGroupId(tenantId, groupId).cache();
        return accounts.readAll().onErrorReturn(ListUtil.empty())
                .flatMap(its -> its.isEmpty() ?superAccounts:Mono.just(its))
                .switchIfEmpty(superAccounts);
    }

}
