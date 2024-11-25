package boluo.im.message.submessages;

import boluo.im.message.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImageMessage extends Message {

    private Image thumbImage;
    private Image largeImage;
    private Image originImage;

    @Setter
    @Getter
    public static class Image {
        private Integer width;
        private Integer height;
        private Integer size;
        private String url;
    }

}
