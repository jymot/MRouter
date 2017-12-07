package im.wangchao.mrouter;

import android.content.Context;

/**
 * <p>Description  : RouterService.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 下午1:34.</p>
 */
public interface IRouterService {
    void push(Context context, RouteIntent route, int requestCode, RouterCallback callback) throws Exception;

    void pop(Context context, RouteIntent route, int resultCode, RouterCallback callback) throws Exception;
}
