package boluo.im.rsocket;

import boluo.im.message.RemoteRouter;
import cn.hutool.cache.impl.TimedCache;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.service.RSocketServiceProxyFactory;
import org.springframework.stereotype.Component;

@Component
@Setter
public class RSocketFactory {

    private TimedCache<Address, RemoteRouter> cache = new TimedCache<>(1000 * 60 * 10);
    @Resource
    private RSocketRequester.Builder requestBuilder;

    public RemoteRouter getRemoteRouter(Address address) {
        RemoteRouter router = cache.get(address);
        if(router == null) {
            router = createRemoteRouter(address);
        }
        return router;
    }

    protected synchronized RemoteRouter createRemoteRouter(Address address) {
        RSocketRequester requester = requestBuilder.tcp(address.getIp(), address.getPort());
        RemoteRouter remoteRouter = RSocketServiceProxyFactory.builder(requester).build().createClient(RemoteRouter.class);
        cache.put(address, remoteRouter);
        return remoteRouter;
    }

}
