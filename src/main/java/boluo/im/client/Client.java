package boluo.im.client;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Client {

    private String tenantId;
    private String account;
    private List<Device> devices = List.of();

}
