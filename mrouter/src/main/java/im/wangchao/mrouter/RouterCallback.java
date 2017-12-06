package im.wangchao.mrouter;

/**
 * <p>Description  : RouterRequestCallback.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/16.</p>
 * <p>Time         : 下午4:12.</p>
 */
public interface RouterCallback {

    /**
     * 响应成功
     * @param route RouterIntent
     */
    void onSuccess(RouteIntent route);

    /**
     * 响应失败
     * @param route RouterIntent
     * @param e 相关异常信息，可能为null
     */
    void onFailure(RouteIntent route, Exception e);
}
