package boluo.im.message.submessages;

import boluo.im.message.Message;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomMessage extends Message {

    @NotNull(message = "data is null")
    private Object data;

    @Override
    public String getMsgType() {
        return "custom";
    }

}
