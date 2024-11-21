package boluo.im.client.repository;

import boluo.im.client.Account;
import boluo.im.common.URLUtil;
import boluo.im.config.IMConfig;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * 未加缓存的实现
 * */
@Setter
public class DefaultAccountRepository implements AccountRepository {it

    @Resource
    private WebClient webClient;
    @Resource
    private IMConfig imConfig;

    public Mono<List<Account>> findByGroupId(String tenantId, String groupId) {
        Objects.requireNonNull(tenantId, "tenantId is null");
        Objects.requireNonNull(groupId, "groupId is null");
        if(StrUtil.isBlank(imConfig.getGroupUrl())) return Mono.empty();
        String url = imConfig.getGroupUrl();
        url = URLUtil.appendGroupQuery(url, tenantId, groupId);
        String urlFinal = url;
        return webClient.get().uri(url).retrieve().bodyToMono(new ParameterizedTypeReference<List<Account>>() {});
    }

}
