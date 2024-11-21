package boluo.im.client.repository;

import boluo.im.client.Account;
import boluo.im.client.AccountBroker;

public interface AccountBrokerRepository {

    public void save(AccountBroker ab);

    public AccountBroker find(Account account);

    public void remove(AccountBroker ab);
}
