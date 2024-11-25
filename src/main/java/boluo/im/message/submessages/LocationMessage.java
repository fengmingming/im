package boluo.im.message.submessages;

import boluo.im.message.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationMessage extends Message {

    private String latitude;
    private String longitude;
    private String desc;

}
