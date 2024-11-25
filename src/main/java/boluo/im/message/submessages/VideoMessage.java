package boluo.im.message.submessages;

import boluo.im.message.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VideoMessage extends Message {

    private String videoUrl;
    private String videoUUID;
    private Integer videoSize;
    private Integer videoSeconds;
    private String videoFormat;

    private String thumbUrl;
    private String thumbUUID;
    private Integer thumbSize;
    private Integer thumbWidth;
    private Integer thumbHeight;
    private String thumbFormat;

}
