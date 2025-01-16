package boluo.im.message.submessages;

import boluo.im.message.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileMessage extends Message {

    @NotBlank(message = "fileUUID is blank")
    private String fileUUID;//文件唯一标识
    @NotBlank(message = "fileUrl is blank")
    private String fileUrl;//文件地址
    @NotNull(message = "fileSize is null")
    private Integer fileSize;//文件大小
    @NotBlank(message = "fileName is blank")
    private String fileName;//文件名称

    @Override
    public String getMsgType() {
        return "file";
    }

}
