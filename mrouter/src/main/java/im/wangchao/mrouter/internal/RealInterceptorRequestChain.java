package im.wangchao.mrouter.internal;

import java.util.List;

import im.wangchao.mrouter.IInterceptor;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;

/**
 * <p>Description  : RealInterceptorRequestChain.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/17.</p>
 * <p>Time         : 下午6:01.</p>
 */
public final class RealInterceptorRequestChain implements IInterceptor.RequestChain {

    private final List<IInterceptor> mInterceptors;
    private final int mIndex;
    private final RouteIntent mRoute;

    private final int mSize;

    public RealInterceptorRequestChain(List<IInterceptor> interceptors, int index, RouteIntent route){
        this.mInterceptors = interceptors;
        this.mIndex = index;
        this.mRoute = route;
        this.mSize = interceptors.size();
    }

    @Override public RouteIntent route() {
        return mRoute;
    }

    @Override public RouteIntent proceed(RouteIntent route, RouterCallback callback) {
        if (mIndex >= mSize) throw new AssertionError();

        // Call the next interceptor in the chain.
        RealInterceptorRequestChain next = new RealInterceptorRequestChain(mInterceptors, mIndex + 1, mRoute);
        IInterceptor interceptor = mInterceptors.get(mIndex);
        return interceptor.requestInterceptor(next, callback);
    }
}
