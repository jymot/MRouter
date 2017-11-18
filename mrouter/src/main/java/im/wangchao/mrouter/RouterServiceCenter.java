package im.wangchao.mrouter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import im.wangchao.mrouter.annotations.Constants;
import im.wangchao.mrouter.annotations.RouterService;
import im.wangchao.mrouter.internal.RealInterceptorPopChain;
import im.wangchao.mrouter.internal.RealInterceptorPushChain;

import static im.wangchao.mrouter.RouterServiceCenter.NAME;

/**
 * <p>Description  : RouterServiceCenter.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 下午1:47.</p>
 */
@RouterService(NAME)
public class RouterServiceCenter implements IRouterService, IProvider{
    static final String NAME = Constants.ROUTER_SERVICE_NAME;

    @Override public void push(Context context, RouteIntent route, int requestCode) {
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();

        // Load target class
        route = route.newBuilder().targetClass(RouterRepository.getTargetClass(scheme, path)).build();

        List<IInterceptor> interceptors = new ArrayList<>();

        // Global interceptor.
        List<IInterceptor> globalInterceptor = RouterRepository.getInterceptors(NAME);
        if (!isListEmpty(globalInterceptor)){
            interceptors.addAll(globalInterceptor);
        }

        if (!TextUtils.equals(scheme, NAME)){
            // exec other RouterService
            if (pushServiceProceed(interceptors, scheme, context, route, requestCode)){
                return;
            }
        }

        interceptors.add(new RealCallInterceptor());

        RealInterceptorPushChain chain = new RealInterceptorPushChain(globalInterceptor, 0, route);
        chain.proceed(context, route, requestCode);
    }

    @Override public void pop(Context context, RouteIntent route, int resultCode) {
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();

        List<IInterceptor> interceptors = new ArrayList<>();

        // Global interceptor.
        List<IInterceptor> globalInterceptor = RouterRepository.getInterceptors(NAME);
        if (!isListEmpty(globalInterceptor)){
            interceptors.addAll(globalInterceptor);
        }

        if (!TextUtils.equals(scheme, NAME)){
            // exec other RouterService
            if (popServiceProceed(interceptors, scheme, context, route, resultCode)){
                return;
            }
        }

        interceptors.add(new RealCallInterceptor());

        RealInterceptorPushChain chain = new RealInterceptorPushChain(globalInterceptor, 0, route);
        chain.proceed(context, route, resultCode);
    }

    @Override public void onReceiver(RouteIntent route, RouterCallback callback) {
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();

        // Global interceptor.
        // todo
    }

    private boolean pushServiceProceed(List<IInterceptor> globalInterceptor, String name, Context context, RouteIntent route, int requestCode){
        IRouterService service = RouterRepository.getRouterService(name);
        if (service == null){
            return false;
        }

        // current
        List<IInterceptor> interceptors = RouterRepository.getInterceptors(name);
        if (!isListEmpty(interceptors)){
            globalInterceptor.addAll(interceptors);
        }

        RealInterceptorPushChain chain = new RealInterceptorPushChain(globalInterceptor, 0, route);
        service.push(context, chain.proceed(context, route, requestCode), requestCode);

        return true;
    }

    private boolean popServiceProceed(List<IInterceptor> globalInterceptor, String name, Context context, RouteIntent route, int resultCode){
        IRouterService service = RouterRepository.getRouterService(name);
        if (service == null){
            return false;
        }

        // current
        List<IInterceptor> interceptors = RouterRepository.getInterceptors(name);
        if (!isListEmpty(interceptors)){
            globalInterceptor.addAll(interceptors);
        }

        RealInterceptorPopChain chain = new RealInterceptorPopChain(globalInterceptor, 0, route);
        service.pop(context, chain.proceed(context, route, resultCode), resultCode);

        return true;
    }

    private boolean isListEmpty(List list){
        return list == null || list.size() == 0;
    }
}
