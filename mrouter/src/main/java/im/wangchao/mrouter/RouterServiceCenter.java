package im.wangchao.mrouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
    public static final String NAME = "RouterServiceCenter";

    private static Map<String, IRouterService> mRouterServices = new HashMap<>();
    private static Map<String, List<IInterceptor>> mInterceptors = new HashMap<>();

    static IRouterService getRouterService(String name){
        return mRouterServices.get(name);
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
        mRouterServices.put(name, service);
    }

    private synchronized static void addInterceptorImpl(String name, IInterceptor interceptor){
        if (TextUtils.isEmpty(name)){
            name = NAME;
        }

        List<IInterceptor> list = mInterceptors.get(name);
        if (list == null) {
            list = new ArrayList<>();
            mInterceptors.put(name, list);
        }
        list.add(interceptor);
    }

    @Override public void push(Context context, RouteIntent route, int requestCode) {
        List<IInterceptor> globalInterceptors = mInterceptors.get(NAME);
        route = pushProceed(globalInterceptors, context, route, requestCode);

        Uri uri = route.uri();
        final String scheme = uri.getScheme();
        if (pushServiceProceed(scheme, context, route, requestCode)){
            return;
        }

        // todo
//        ComponentName componentName = new ComponentName(context, Routes.get(path));
        Intent intent = new Intent();
//        intent.setComponent(componentName);
        // Set flags.
        int flags = route.flags();
        if (-1 != flags) {
            intent.setFlags(flags);
        } else if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (requestCode > 0) {
            ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, route.bundle());
        } else {
            ActivityCompat.startActivity(context, intent, route.bundle());
        }
    }

    @Override public void pop(Context context, RouteIntent route, int resultCode) {

    }

    private boolean pushServiceProceed(String name, Context context, RouteIntent route, int requestCode){
        IRouterService service = mRouterServices.get(name);
        if (service == null){
            return false;
        }

        List<IInterceptor> interceptors = mInterceptors.get(NAME);
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
