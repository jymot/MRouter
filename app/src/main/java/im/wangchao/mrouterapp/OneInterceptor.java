package im.wangchao.mrouterapp;

import android.content.Context;
import android.util.Log;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;
import im.wangchao.mrouter.annotations.Interceptor;

/**
 * <p>Description  : OneInterceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午10:06.</p>
 */
@Interceptor(routerName = "one")
public class OneInterceptor implements IInterceptor {
    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode, RouterCallback callback) {
        Log.e("wcwcwc", "One Interceptor: push()");
        return chain.proceed(context, chain.route(), requestCode, callback);
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode, RouterCallback callback) {
        Log.e("wcwcwc", "One Interceptor: pop()");
        return chain.proceed(context, chain.route(), resultCode, callback);
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback) {
        Log.e("wcwcwc", "One Interceptor: request()");
        return chain.proceed(chain.route(), callback);
    }
}
