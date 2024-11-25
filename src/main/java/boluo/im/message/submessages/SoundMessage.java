package boluo.im.message.submessages;

import boluo.im.message.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SoundMessage extends Message {

    private String soundUUID;//索引id
    private String soundUrl; //地址
    private Long soundSize; //大小
    private Integer soundSeconds; //时长

}
