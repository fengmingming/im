package boluo.im.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class Broker {

    private String ip;
    private Integer port;
    private String sessionId;
    private Device device;

    @JsonCreator
    public Broker(@JsonProperty("ip") String ip,@JsonProperty("port") Integer port,@JsonProperty("sessionId") String sessionId) {
        Objects.requireNonNull(ip, "ip is null");
        Objects.requireNonNull(port, "port is null");
        Objects.requireNonNull(sessionId, "sessionId is null");
        this.ip = ip;
        this.port = port;
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Broker broker = (Broker) o;
        return ip.equals(broker.ip) && port.equals(broker.port) && sessionId.equals(broker.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, sessionId);
    }
}
