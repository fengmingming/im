package boluo.im.message.submessages;

import boluo.im.message.Message;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FaceMessage extends Message {

    @NotBlank(message = "faceUUID is blank")
    private String faceUUID;//表情唯一标识

    @Override
    public String getMsgType() {
        return "face";
    }

}
