package im.wangchao.mrouter.internal;

import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;

/**
 * <p>Description  : Utils.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/12/7.</p>
 * <p>Time         : 下午5:42.</p>
 */
public class Utils {
    private Utils(){
        throw new AssertionError();
    }

    public static void callbackOrThrow(RouteIntent route, RouterCallback callback, Exception e) throws Exception {
        if (callback != null){
            callback.onFailure(route, e);
        } else {
            throw e;
        }
    }
}
