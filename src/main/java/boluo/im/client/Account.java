package boluo.im.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Account {

    private String tenantId;
    private String account;

    @JsonCreator
    public Account(@JsonProperty("tenantId") String tenantId, @JsonProperty("account") String account) {
        Objects.requireNonNull(tenantId, "tenantId is null");
        Objects.requireNonNull(account, "account is null");
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


}
