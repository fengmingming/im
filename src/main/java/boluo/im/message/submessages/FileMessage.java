package boluo.im.message.submessages;

import boluo.im.message.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileMessage extends Message {

    private String fileUUID;//文件唯一标识
    private String fileUrl;//文件地址
    private String fileSize;//文件大小
    private String fileName;//文件名称

}
