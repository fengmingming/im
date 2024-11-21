package boluo.im.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class AccountBroker {

    private Account account;
    private Set<Broker> brokers = new CopyOnWriteArraySet<>();

    @JsonCreator
    public AccountBroker(@JsonProperty("account") Account account) {
        Objects.requireNonNull(account, "account is null");
        this.account = account;
    }

}
