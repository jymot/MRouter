package im.wangchao.mrouter;

/**
 * <p>Description  : IProvider.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/16.</p>
 * <p>Time         : 下午3:58.</p>
 */
public interface IProvider {

    /**
     * 接收请求方法
     *
     * @param route RouteIntent
     * @param callback 回调函数
     */
    void onReceiver(RouteIntent route, RouterCallback callback);
}
