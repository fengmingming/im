package boluo.im.message;

import boluo.im.message.submessages.CustomMessage;
import boluo.im.message.submessages.StringMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "msgType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringMessage.class, name = "text"),
        @JsonSubTypes.Type(value = CustomMessage.class, name = "custom")
})
@JsonIgnoreProperties(ignoreUnknown = true, value = {"msgType"})
public abstract class Message {

    private String msgId;
    private String tenantId;
    private String from;
    private String to;

    public boolean isGroup() {
        return to != null && to.startsWith("GROUP:");
    }

    public String getGroupId() {
        if(isGroup()) {
            return to.substring(6).trim();
        }
        return null;
    }

}
