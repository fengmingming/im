package boluo.im.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RemoteMessage {

    private final String sessionId;
    private final String message;

    @JsonCreator
    public RemoteMessage(@JsonProperty("sessionId") String sessionId, @JsonProperty("message") String message) {
        this.sessionId = sessionId;
        this.message = message;
    }

}
