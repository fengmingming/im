package boluo.im.common;

import cn.hutool.core.util.StrUtil;

import java.net.URL;

public class URLUtil {

    public static String appendGroupQuery(String url, String tenantId, String groupId) {
        StringBuilder sb = new StringBuilder(handleUrl(url));
        sb.append(Constants.TENANT_ID).append("=").append(tenantId);
        sb.append("&");
        sb.append(Constants.GROUP_ID).append("=").append(groupId);
        return sb.toString();
    }

    public static String appendQuery(String url, String query) {
        return handleUrl(url) + query;
    }

    public static String handleUrl(String str) {
        URL url = cn.hutool.core.util.URLUtil.url(str);
        if(StrUtil.isNotBlank(url.getQuery())) {
            return str + "&";
        }
        if(str.indexOf("?") == -1) {
            return str + "?";
        }
        return str;
    }

}
