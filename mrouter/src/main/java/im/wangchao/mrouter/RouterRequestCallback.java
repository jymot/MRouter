package im.wangchao.mrouter;

/**
 * <p>Description  : RouterRequestCallback.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/16.</p>
 * <p>Time         : 下午4:12.</p>
 */
public interface RouterRequestCallback {

    /**
     * 响应回调
     * @param route RouteIntent
     */
    void onResponse(RouteIntent route);
}
