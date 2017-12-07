package im.wangchao.mrouter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import im.wangchao.mrouter.annotations.Constants;
import im.wangchao.mrouter.exception.TargetClassNotFoundException;
import im.wangchao.mrouter.internal.RealInterceptorPopChain;
import im.wangchao.mrouter.internal.RealInterceptorPushChain;
import im.wangchao.mrouter.internal.RealInterceptorRequestChain;

import static im.wangchao.mrouter.internal.Utils.callbackOrThrow;

/**
 * <p>Description  : RouterServiceCenter.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 下午1:47.</p>
 */
/*package*/ class RouterServiceCenter implements IRouterService, IProvider{
    static final String NAME = Constants.ROUTER_SERVICE_NAME;

    @Override public void push(Context context, RouteIntent route, int requestCode, RouterCallback callback) throws Exception{
        try {
            final Uri uri = route.uri();
            // Scheme is RouterService name.
            final String scheme = uri.getScheme();
            final String path = uri.getPath();

            // Load target class
            final String targetClass = RouterRepository.getTargetClass(scheme, path);
            if (TextUtils.isEmpty(targetClass)){
                throw new TargetClassNotFoundException(route);
            }
            route = route.newBuilder().targetClass(targetClass).build();

            List<IInterceptor> interceptors = new ArrayList<>();

            // Global interceptor.
            List<IInterceptor> globalInterceptor = RouterRepository.getInterceptors(NAME);
            if (!isListEmpty(globalInterceptor)){
                interceptors.addAll(globalInterceptor);
            }

            if (!TextUtils.equals(scheme, NAME)){
                // exec other RouterService
                if (pushServiceProceed(interceptors, scheme, context, route, requestCode, callback)){
                    return;
                }
            }

            interceptors.add(new RealCallInterceptor());

            RealInterceptorPushChain chain = new RealInterceptorPushChain(interceptors, 0, route);
            chain.proceed(context, route, requestCode, callback);
        } catch (Exception e){
            callbackOrThrow(route, callback, e);
        }

    }

    @Override public void pop(Context context, RouteIntent route, int resultCode, RouterCallback callback) throws Exception{
        try {
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
                if (popServiceProceed(interceptors, scheme, context, route, resultCode, callback)){
                    return;
                }
            }

            interceptors.add(new RealCallInterceptor());

            RealInterceptorPopChain chain = new RealInterceptorPopChain(interceptors, 0, route);
            chain.proceed(context, route, resultCode, callback);
        } catch (Exception e){
            callbackOrThrow(route, callback, e);
        }
    }

    @Override public void onReceiver(RouteIntent route, RouterCallback callback) {
        try {
            final Uri uri = route.uri();
            // Scheme is RouterService name.
            final String scheme = uri.getScheme();
            final String path = uri.getPath();
            final String authority = uri.getAuthority();

            List<IInterceptor> interceptors = new ArrayList<>();
            // Global interceptor.
            List<IInterceptor> globalInterceptor = RouterRepository.getInterceptors(NAME);
            if (!isListEmpty(globalInterceptor)){
                interceptors.addAll(globalInterceptor);
            }

            if (!TextUtils.equals(scheme, NAME)){
                List<IInterceptor> childInterceptor = RouterRepository.getInterceptors(scheme);
                if (!isListEmpty(childInterceptor)){
                    interceptors.addAll(childInterceptor);
                }
            }

            interceptors.add(new RealCallInterceptor());

            RealInterceptorRequestChain chain = new RealInterceptorRequestChain(interceptors, 0, route);
            chain.proceed(route, callback);
        } catch (Exception e){
            if (callback != null){
                callback.onFailure(route, e);
            } else {
                throw e;
            }
        }
    }

    private boolean pushServiceProceed(List<IInterceptor> interceptors,
                                       String name,
                                       Context context,
                                       RouteIntent route,
                                       int requestCode,
                                       RouterCallback callback){
        IRouterService service = RouterRepository.getRouterService(name);
        if (service == null){
            return false;
        }

        // current
        List<IInterceptor> childInterceptors = RouterRepository.getInterceptors(name);
        if (!isListEmpty(childInterceptors)){
            interceptors.addAll(childInterceptors);
        }

        interceptors.add(new ForwardServiceInterceptor(service));

        RealInterceptorPushChain chain = new RealInterceptorPushChain(interceptors, 0, route);
        chain.proceed(context, route, requestCode, callback);
        return true;
    }

    private boolean popServiceProceed(List<IInterceptor> interceptors,
                                      String name,
                                      Context context,
                                      RouteIntent route,
                                      int resultCode,
                                      RouterCallback callback){
        IRouterService service = RouterRepository.getRouterService(name);
        if (service == null){
            return false;
        }

        // current
        List<IInterceptor> childInterceptors = RouterRepository.getInterceptors(name);
        if (!isListEmpty(childInterceptors)){
            interceptors.addAll(childInterceptors);
        }

        interceptors.add(new ForwardServiceInterceptor(service));

        RealInterceptorPopChain chain = new RealInterceptorPopChain(interceptors, 0, route);
        chain.proceed(context, route, resultCode, callback);
        return true;
    }

    private boolean isListEmpty(List list){
        return list == null || list.size() == 0;
    }
}
