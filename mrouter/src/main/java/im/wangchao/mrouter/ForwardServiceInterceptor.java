package im.wangchao.mrouter;

import android.content.Context;

/**
 * <p>Description  : ForwardServiceInterceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午10:54.</p>
 */
/*package*/ class ForwardServiceInterceptor implements IInterceptor {

    private final IRouterService mRouterService;

    ForwardServiceInterceptor(IRouterService routerService){
        mRouterService = routerService;
    }

    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode, RouterCallback callback) {
        mRouterService.push(context, chain.route(), requestCode, callback);
        return null;
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode, RouterCallback callback) {
        mRouterService.pop(context, chain.route(), resultCode, callback);
        return null;
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback) {
        return null;
    }
}
