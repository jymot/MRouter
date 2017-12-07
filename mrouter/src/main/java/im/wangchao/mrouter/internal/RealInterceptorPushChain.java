package im.wangchao.mrouter.internal;

import android.content.Context;

import java.util.List;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;

/**
 * <p>Description  : RealInterceptorChain.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/17.</p>
 * <p>Time         : 下午5:32.</p>
 */
public final class RealInterceptorPushChain implements IInterceptor.PushChain {

    private final List<IInterceptor> mInterceptors;
    private final int mIndex;
    private final RouteIntent mRoute;

    private final int mSize;

    public RealInterceptorPushChain(List<IInterceptor> interceptors, int index, RouteIntent route){
        this.mInterceptors = interceptors;
        this.mIndex = index;
        this.mRoute = route;
        this.mSize = interceptors.size();
    }

    @Override public RouteIntent route() {
        return mRoute;
    }

    @Override public RouteIntent proceed(Context context, RouteIntent route, int requestCode, RouterCallback callback) {
        if (mIndex >= mSize) throw new AssertionError();

        // Call the next interceptor in the chain.
        RealInterceptorPushChain next = new RealInterceptorPushChain(mInterceptors, mIndex + 1, mRoute);
        IInterceptor interceptor = mInterceptors.get(mIndex);
        return interceptor.pushInterceptor(context, next, requestCode, callback);
    }
}
