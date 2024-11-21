package boluo.im.client.repository;

import boluo.im.client.Account;
import boluo.im.client.AccountBroker;
import boluo.im.client.Broker;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单机版 AccountBrokerRepository 实现
 * */
public class InMemoryAccountBrokerRepository implements AccountBrokerRepository {

    private static final ConcurrentHashMap<Account, Set<Broker>> cache = new ConcurrentHashMap<>(2048);

    @Override
    public void save(AccountBroker ab) {
        Set<Broker> brokers = cache.get(ab.getAccount());
        if(brokers == null) {
            brokers = cache.putIfAbsent(ab.getAccount(), ab.getBrokers());
            if(brokers == null) return;
        }
        //处理同一个用户不同设备同时退出和进入的操作
        brokers.addAll(ab.getBrokers());
        if(cache.putIfAbsent(ab.getAccount(), brokers) != brokers) {
            save(ab);
        }
    }

    @Override
    public AccountBroker find(Account account) {
        AccountBroker ab = new AccountBroker(account);
        Set<Broker> brokers = cache.get(account);
        if(brokers != null) {
            ab.getBrokers().addAll(brokers);
        }
        return ab;
    }

    @Override
    public void remove(AccountBroker ab) {
        Set<Broker> brokers = cache.get(ab.getAccount());
        if(brokers != null) {
            brokers.removeAll(ab.getBrokers());
            if(brokers.size() == 0) {
                cache.remove(ab.getAccount());
            }
        }
    }

}
