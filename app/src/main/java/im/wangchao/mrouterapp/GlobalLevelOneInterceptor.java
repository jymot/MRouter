package im.wangchao.mrouterapp;

import android.content.Context;
import android.util.Log;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;
import im.wangchao.mrouter.annotations.Interceptor;

/**
 * <p>Description  : GlobalInterceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午9:41.</p>
 */
@Interceptor(priority = 1)
public class GlobalLevelOneInterceptor implements IInterceptor{

    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode) {
        Log.e("wcwcwc", "Global One Interceptor: push()");
        return chain.proceed(context, chain.route(), requestCode);
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode) {
        Log.e("wcwcwc", "Global One Interceptor: pop()");
        return chain.proceed(context, chain.route(), resultCode);
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback) {
        Log.e("wcwcwc", "Global One Interceptor: request()");
        return chain.proceed(chain.route(), callback);
    }
}
