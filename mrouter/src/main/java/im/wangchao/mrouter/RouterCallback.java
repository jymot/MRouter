package im.wangchao.mrouter;

/**
 * <p>Description  : RouterCallback.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/16.</p>
 * <p>Time         : 下午4:12.</p>
 */
public interface RouterCallback {

    /**
     * 响应回调
     * @param route RouteIntent
     */
    void onResponse(RouteIntent route);
}
