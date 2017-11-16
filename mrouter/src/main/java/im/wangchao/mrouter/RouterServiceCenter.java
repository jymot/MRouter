package im.wangchao.mrouter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.List;

import im.wangchao.mrouter.annotations.Constants;
import im.wangchao.mrouter.annotations.RouterService;

import static im.wangchao.mrouter.RouteIntent.DEFAULT_POP_URI;
import static im.wangchao.mrouter.RouterServiceCenter.NAME;

/**
 * <p>Description  : RouterServiceCenter.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 下午1:47.</p>
 */
@RouterService(NAME)
public class RouterServiceCenter implements IRouterService {
    static final String NAME = Constants.ROUTER_SERVICE_NAME;

    @Override public void push(Context context, RouteIntent route, int requestCode) {
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();

        // Load target class
        route = route.newBuilder().targetClass(RouterRepository.getTargetClass(scheme, path)).build();

        // Global interceptor.
        route = pushProceed(RouterRepository.getInterceptors(NAME), context, route, requestCode);

        if (!TextUtils.equals(scheme, NAME)){
            // exec other RouterService
            if (pushServiceProceed(scheme, context, route, requestCode)){
                return;
            }
        }

        Intent intent = route.getPushIntent(context);

        if (requestCode > 0) {
            ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
        } else {
            ActivityCompat.startActivity(context, intent, null);
        }
    }

    @Override public void pop(Context context, RouteIntent route, int resultCode) {
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();

        // Global interceptor.
        route = popProceed(RouterRepository.getInterceptors(NAME), context, route, resultCode);

        if (!TextUtils.equals(scheme, NAME)){
            // exec other RouterService
            if (popServiceProceed(scheme, context, route, resultCode)){
                return;
            }
        }

        Intent intent = route.getPopIntent();

        if (TextUtils.equals(uri.toString(), DEFAULT_POP_URI)){
            ((Activity) context).setResult(resultCode, intent);
            ((Activity) context).finish();
        } else {
            final String targetClass = RouterRepository.getTargetClass(scheme, path);
            ComponentName componentName = new ComponentName(context, targetClass);
            intent.setComponent(componentName);
            context.startActivity(intent);
        }
    }

    private boolean pushServiceProceed(String name, Context context, RouteIntent route, int requestCode){
        IRouterService service = RouterRepository.getRouterService(name);
        if (service == null){
            return false;
        }

        // current
        List<IInterceptor> interceptors = RouterRepository.getInterceptors(name);
        route = pushProceed(interceptors, context, route, requestCode);
        service.push(context, route, requestCode);

        return true;
    }

    private boolean popServiceProceed(String name, Context context, RouteIntent route, int resultCode){
        IRouterService service = RouterRepository.getRouterService(name);
        if (service == null){
            return false;
        }

        // current
        List<IInterceptor> interceptors = RouterRepository.getInterceptors(name);
        route = popProceed(interceptors, context, route, resultCode);
        service.pop(context, route, resultCode);

        return true;
    }

    private RouteIntent pushProceed(List<IInterceptor> interceptors, Context context, RouteIntent route, int requestCode){
        if (!isListEmpty(interceptors)){
            for (IInterceptor interceptor: interceptors){
                RouteIntent temp = interceptor.pushProceed(context, route, requestCode);
                if (temp != null){
                    route = temp;
                }
            }
        }
        return route;
    }

    private RouteIntent popProceed(List<IInterceptor> interceptors, Context context, RouteIntent route, int resultCode){
        if (!isListEmpty(interceptors)){
            for (IInterceptor interceptor: interceptors){
                RouteIntent temp = interceptor.popProceed(context, route, resultCode);
                if (temp != null){
                    route = temp;
                }
            }
        }
        return route;
    }

    private boolean isListEmpty(List list){
        return list == null || list.size() == 0;
    }
}
