package boluo.im.message.submessages;

import boluo.im.message.Message;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImageMessage extends Message {

    @NotNull(message = "thumbImage is null")
    @Valid
    private Image thumbImage;
    @NotNull(message = "largeImage is null")
    @Valid
    private Image largeImage;
    @NotNull(message = "originImage is null")
    @Valid
    private Image originImage;

    @Override
    public String getMsgType() {
        return "image";
    }

    @Setter
    @Getter
    public static class Image {
        @NotNull(message = "width is null")
        private Integer width;
        @NotNull(message = "height is null")
        private Integer height;
        @NotNull(message = "size is null")
        private Integer size;
        @NotBlank(message = "url is blank")
        private String url;
    }

}
