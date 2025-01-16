package boluo.im.message.submessages;

import boluo.im.message.Message;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationMessage extends Message {

    @NotBlank(message = "latitude is blank")
    private String latitude;
    @NotBlank(message = "longitude is blank")
    private String longitude;
    private String desc;

    @Override
    public String getMsgType() {
        return "location";
    }

}
