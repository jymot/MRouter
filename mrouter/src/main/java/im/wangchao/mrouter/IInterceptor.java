package im.wangchao.mrouter;

import android.content.Context;

/**
 * <p>Description  : Interceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 上午11:27.</p>
 */
public interface IInterceptor {

    RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode);

    RouteIntent popInterceptor(Context context, PopChain chain, int resultCode);

    RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback);

    interface PushChain {

        RouteIntent route();

        RouteIntent proceed(Context context, RouteIntent route, int requestCode);
    }

    interface PopChain {

        RouteIntent route();

        RouteIntent proceed(Context context, RouteIntent route, int resultCode);
    }

    interface RequestChain {

        RouteIntent route();

        RouteIntent proceed(RouteIntent route, RouterCallback callback);
    }
}
