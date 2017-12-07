package im.wangchao.mrouter;

import android.content.Context;

/**
 * <p>Description  : Interceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 上午11:27.</p>
 */
public interface IInterceptor {

    /**
     * 拦截 push 方法
     * @param context 当前页面 Context
     * @param chain push 拦截链
     * @param requestCode 请求码
     */
    RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode, RouterCallback callback);

    /**
     * 拦截 pop 方法
     * @param context 当前页面 Context
     * @param chain pop 拦截链
     * @param resultCode 返回码
     */
    RouteIntent popInterceptor(Context context, PopChain chain, int resultCode, RouterCallback callback);

    /**
     * 拦截 request 方法
     * @param chain request 拦截链
     * @param callback 回调函数
     */
    RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback);

    interface PushChain {

        RouteIntent route();

        /**
         * 继续执行下一个拦截器
         *
         * @param context 当前页面 Context
         * @param route RouteIntent
         * @param requestCode 请求码
         */
        RouteIntent proceed(Context context, RouteIntent route, int requestCode, RouterCallback callback);
    }

    interface PopChain {

        RouteIntent route();

        /**
         * 继续执行下一个拦截器
         *
         * @param context 当前页面 Context
         * @param route RouteIntent
         * @param resultCode 返回码
         */
        RouteIntent proceed(Context context, RouteIntent route, int resultCode, RouterCallback callback);
    }

    interface RequestChain {

        RouteIntent route();

        /**
         * 继续执行下一个拦截器
         * @param route RouteIntent
         * @param callback 回调函数
         */
        RouteIntent proceed(RouteIntent route, RouterCallback callback);
    }
}
