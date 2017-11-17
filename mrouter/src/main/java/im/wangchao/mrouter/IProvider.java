package im.wangchao.mrouter;

/**
 * <p>Description  : IProvider.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/16.</p>
 * <p>Time         : 下午3:58.</p>
 */
public interface IProvider {

    void onReceiver(RouteIntent route, RouterCallback callback);
}
