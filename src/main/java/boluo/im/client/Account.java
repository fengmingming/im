package boluo.im.client;

import boluo.im.common.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Account {

    private final String tenantId;
    private final String account;

    @JsonCreator
    public Account(@JsonProperty(Constants.TENANT_ID) String tenantId, @JsonProperty(Constants.ACCOUNT_ID) String account) {
        Objects.requireNonNull(tenantId, Constants.TENANT_ID + " is null");
        Objects.requireNonNull(account,  Constants.ACCOUNT_ID + " is null");
        this.tenantId = tenantId;
        this.account = account;
    }

    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Account ab = (Account) o;
        return ab.tenantId.equals(this.tenantId) && ab.account.equals(this.account);
    }

    public int hashCode() {
        return Objects.hash(this.tenantId, this.account);
    }

    public String toRedisKey() {
        return "Broker:" + tenantId + ":" + account;
    }

}
