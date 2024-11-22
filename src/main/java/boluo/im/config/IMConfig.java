package boluo.im.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "boluo.im")
@Setter
@Getter
public class IMConfig {

    /**
     * 跨域配置
     * */
    private String originPatterns;
    /**
     * 本地ip，多网卡情况
     * */
    private String localIp;
    /**
     * 群成员url
     * */
    private String groupUrl;
    /**
     * 群组模板
     * */
    private String groupKeyTemplate;
    /**
     * 身份认证url
     * */
    private String authUrl;
    /**
     * 单位秒
     * */
    private int authTimeout = 5;

}
