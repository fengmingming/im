package boluo.im.client.repository;

import boluo.im.client.Account;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountRepository {

    public Mono<List<Account>> findByGroupId(String tenantId, String groupId);

}
