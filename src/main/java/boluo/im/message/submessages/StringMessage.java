package boluo.im.message.submessages;

import boluo.im.message.Message;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StringMessage extends Message {

    @NotBlank(message = "content is blank")
    private String content;

}
