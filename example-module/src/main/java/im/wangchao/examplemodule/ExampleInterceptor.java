package im.wangchao.examplemodule;

import android.content.Context;
import android.util.Log;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;
import im.wangchao.mrouter.annotations.Interceptor;

/**
 * <p>Description  : ExampleInterceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/24.</p>
 * <p>Time         : 下午4:08.</p>
 */
@Interceptor(routerName = "example")
public class ExampleInterceptor implements IInterceptor {
    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode, RouterCallback callback) {
        Log.e("wcwcwc", "ExampleInterceptor: push()");
        return chain.proceed(context, chain.route(), requestCode, callback);
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode, RouterCallback callback) {
        Log.e("wcwcwc", "ExampleInterceptor: pop()");
        return chain.proceed(context, chain.route(), resultCode, callback);
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback) {
        Log.e("wcwcwc", "ExampleInterceptor: request()");
        return chain.proceed(chain.route(), callback);
    }
}
