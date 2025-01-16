package boluo.im.message.submessages;

import boluo.im.message.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VideoMessage extends Message {

    @NotBlank(message = "videoUrl is blank")
    private String videoUrl;
    @NotBlank(message = "videoUUID is blank")
    private String videoUUID;
    @NotNull(message = "videoSize is null")
    private Integer videoSize;
    @NotNull(message = "videoSeconds is null")
    private Integer videoSeconds;
    @NotBlank(message = "videoFormat is blank")
    private String videoFormat;
    @NotBlank(message = "thumbUrl is blank")
    private String thumbUrl;
    @NotBlank(message = "thumbUUID is blank")
    private String thumbUUID;
    @NotNull(message = "thumbSize is null")
    private Integer thumbSize;
    @NotNull(message = "thumbWidth is null")
    private Integer thumbWidth;
    @NotNull(message = "thumbHeight is null")
    private Integer thumbHeight;
    @NotBlank(message = "thumbFormat is blank")
    private String thumbFormat;

    @Override
    public String getMsgType() {
        return "video";
    }

}
