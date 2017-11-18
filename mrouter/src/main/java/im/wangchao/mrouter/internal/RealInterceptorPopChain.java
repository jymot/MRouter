package im.wangchao.mrouter.internal;

import android.content.Context;

import java.util.List;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;

/**
 * <p>Description  : RealInterceptorPopChain.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/17.</p>
 * <p>Time         : 下午6:01.</p>
 */
public final class RealInterceptorPopChain implements IInterceptor.PopChain {

    private final List<IInterceptor> mInterceptors;
    private final int mIndex;
    private final RouteIntent mRoute;

    private final int mSize;

    public RealInterceptorPopChain(List<IInterceptor> interceptors, int index, RouteIntent route){
        this.mInterceptors = interceptors;
        this.mIndex = index;
        this.mRoute = route;
        this.mSize = interceptors.size();
    }

    @Override public RouteIntent route() {
        return mRoute;
    }

    @Override public RouteIntent proceed(Context context, RouteIntent route, int resultCode) {
        if (mIndex >= mSize) throw new AssertionError();

        // Call the next interceptor in the chain.
        RealInterceptorPopChain next = new RealInterceptorPopChain(mInterceptors, mIndex + 1, mRoute);
        IInterceptor interceptor = mInterceptors.get(mIndex);
        return interceptor.popInterceptor(context, next, resultCode);
    }
}
