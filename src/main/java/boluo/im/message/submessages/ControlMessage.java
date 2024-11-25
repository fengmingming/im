package boluo.im.message.submessages;

import boluo.im.message.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ControlMessage extends Message {

    private String command;
    private List<Object> args;

}
