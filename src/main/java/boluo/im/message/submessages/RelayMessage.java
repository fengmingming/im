package boluo.im.message.submessages;

import boluo.im.message.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RelayMessage extends Message {

    private List<Message> relayMessages;

}
