package boluo.im.message;

import java.util.Objects;

public class MessageTool {

    public static void copy(Message src, Message dest) {
        Objects.requireNonNull(src, "src is null");
        Objects.requireNonNull(dest, "dest is null");
        dest.setMsgId(src.getMsgId());
        dest.setTenantId(src.getTenantId());
        dest.setFrom(src.getFrom());
        dest.setTo(src.getTo());
    }

}
