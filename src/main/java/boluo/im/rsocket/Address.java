package boluo.im.rsocket;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public class Address {

    private final String ip;
    private final Integer port;

    public Address(String ip, Integer port) {
        Objects.requireNonNull(ip, "ip is null");
        Objects.requireNonNull(port, "port is null");
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Address address = (Address) o;
        return address.ip.equals(this.ip) && address.port.equals(this.port);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.ip, this.port});
    }

    @Override
    public String toString() {
        return this.ip + ":" + this.port;
    }

}
