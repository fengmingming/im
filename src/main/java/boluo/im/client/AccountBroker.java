package boluo.im.client;

import boluo.im.common.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class AccountBroker {

    private final Account account;
    private final Set<Broker> brokers = new CopyOnWriteArraySet<>();

    @JsonCreator
    public AccountBroker(@JsonProperty(Constants.ACCOUNT_ID) Account account) {
        Objects.requireNonNull(account, Constants.ACCOUNT_ID + " is null");
        this.account = account;
    }

}
