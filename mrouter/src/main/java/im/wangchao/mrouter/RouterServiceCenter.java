package im.wangchao.mrouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.wangchao.mrouter.annotations.RouterService;

import static im.wangchao.mrouter.RouterServiceCenter.NAME;

/**
 * <p>Description  : RouterServiceCenter.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 下午1:47.</p>
 */
@RouterService(NAME)
public class RouterServiceCenter implements IRouterService {
    static final String NAME = "RouterServiceCenter";

    private static Map<String, IRouterService> sRouterServices = new HashMap<>();
    private static Map<String, List<IInterceptor>> sInterceptors = new HashMap<>();

    private ILoader mLoader;

    public RouterServiceCenter(ILoader loader){
        this.mLoader = loader;
    }

    static IRouterService instance(){
        return sRouterServices.get(NAME);
    }

    public synchronized static void addRouterService(String name, IRouterService service){
        addRouterServiceImpl(name, service);
    }

    public synchronized static void addInterceptor(IInterceptor interceptor){
        addInterceptor(NAME, interceptor);
    }

    public synchronized static void addInterceptor(String name, IInterceptor interceptor){
        addInterceptorImpl(name, interceptor);
    }

    private synchronized static void addRouterServiceImpl(@NonNull String name, IRouterService service){
        sRouterServices.put(name, service);
    }

    private synchronized static void addInterceptorImpl(String name, IInterceptor interceptor){
        if (TextUtils.isEmpty(name)){
            name = NAME;
        }

        List<IInterceptor> list = sInterceptors.get(name);
        if (list == null) {
            list = new ArrayList<>();
            sInterceptors.put(name, list);
        }
        list.add(interceptor);
    }

    @Override public void push(Context context, RouteIntent route, int requestCode) {
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();

        // Load target class
        route = route.newBuilder().targetClass(mLoader.getTargetClass(scheme, path)).build();

        // Global interceptor.
        route = pushProceed(getInterceptors(NAME), context, route, requestCode);

        if (!TextUtils.equals(scheme, NAME)){
            // exec other RouterService
            if (pushServiceProceed(scheme, context, route, requestCode)){
                return;
            }
        }

        Intent intent = route.getIntent(context);

        if (requestCode > 0) {
            ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
        } else {
            ActivityCompat.startActivity(context, intent, null);
        }
    }

    @Override public void pop(Context context, RouteIntent route, int resultCode) {

    }

    @Nullable private List<IInterceptor> getInterceptors(String name){
        List<IInterceptor> interceptors = sInterceptors.get(name);
        if (interceptors == null){
            interceptors = mLoader.loadInterceptor(name, sInterceptors);
        }
        return interceptors;
    }

    @Nullable private IRouterService getRouterService(String name){
        IRouterService service = sRouterServices.get(name);
        if (service == null){
            service = mLoader.loadRouterService(name, sRouterServices);
        }
        return service;
    }

    private boolean pushServiceProceed(String name, Context context, RouteIntent route, int requestCode){
        IRouterService service = getRouterService(name);
        if (service == null){
            return false;
        }

        // current
        List<IInterceptor> interceptors = getInterceptors(name);
        route = pushProceed(interceptors, context, route, requestCode);
        service.push(context, route, requestCode);

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

    private boolean isListEmpty(List list){
        return list == null || list.size() == 0;
    }
}
