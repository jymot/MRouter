package im.wangchao.mrouterapp;

import android.content.Context;
import android.util.Log;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;
import im.wangchao.mrouter.annotations.Interceptor;

/**
 * <p>Description  : GlobalLevelTwoIntercepor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午10:04.</p>
 */
@Interceptor(priority = 2)
public class GlobalLevelTwoInterceptor implements IInterceptor {

    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode) {
        Log.e("wcwcwc", "Global Two Interceptor: push()");
        return chain.proceed(context, chain.route(), requestCode);
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode) {
        Log.e("wcwcwc", "Global Two Interceptor: pop()");
        return chain.proceed(context, chain.route(), resultCode);
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback) {
        Log.e("wcwcwc", "Global Two Interceptor: request()");
        return chain.proceed(chain.route(), callback);
    }
}
