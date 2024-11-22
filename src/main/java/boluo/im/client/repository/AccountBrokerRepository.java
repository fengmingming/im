package boluo.im.client.repository;

import boluo.im.client.Account;
import boluo.im.client.AccountBroker;
import reactor.core.publisher.Mono;

public interface AccountBrokerRepository {

    public void save(AccountBroker ab);

    public Mono<AccountBroker> find(Account account);

    public void remove(AccountBroker ab);

}
