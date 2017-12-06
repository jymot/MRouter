package im.wangchao.examplemodule;

import android.content.Context;
import android.util.Log;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterRequestCallback;
import im.wangchao.mrouter.annotations.Interceptor;

/**
 * <p>Description  : ExampleInterceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/24.</p>
 * <p>Time         : 下午4:08.</p>
 */
@Interceptor(routerName = "example")
public class ExampleInterceptor implements IInterceptor {
    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode) {
        Log.e("wcwcwc", "ExampleInterceptor: push()");
        return chain.proceed(context, chain.route(), requestCode);
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode) {
        Log.e("wcwcwc", "ExampleInterceptor: pop()");
        return chain.proceed(context, chain.route(), resultCode);
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterRequestCallback callback) {
        Log.e("wcwcwc", "ExampleInterceptor: request()");
        return chain.proceed(chain.route(), callback);
    }
}
