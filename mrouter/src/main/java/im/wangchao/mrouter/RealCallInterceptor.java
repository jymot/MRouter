package im.wangchao.mrouter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import static android.app.Activity.RESULT_CANCELED;
import static im.wangchao.mrouter.RouteIntent.DEFAULT_POP_URI;
import static im.wangchao.mrouter.RouteIntent.FLAG_ACTIVITY_PUSH_AND_POP;

/**
 * <p>Description  : RealCallInterceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/18.</p>
 * <p>Time         : 上午9:04.</p>
 */
/*package*/ class RealCallInterceptor implements IInterceptor {

    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode, RouterCallback callback) {
        final RouteIntent route = chain.route();
        final Intent intent = route.getPushIntent(context);

        if (requestCode > 0) {
            ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
        } else {
            ActivityCompat.startActivity(context, intent, null);
        }
        if (route.flags() == FLAG_ACTIVITY_PUSH_AND_POP && context instanceof Activity){
            ((Activity) context).finish();
        }

        if (callback != null){
            callback.onSuccess(route);
        }
        return null;
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode, RouterCallback callback) {
        final RouteIntent route = chain.route();
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();

        final Intent intent = route.getPopIntent();

        if (TextUtils.equals(uri.toString(), DEFAULT_POP_URI)){
            if (resultCode == RESULT_CANCELED){
                ((Activity) context).onBackPressed();
            } else {
                ((Activity) context).setResult(resultCode, intent);
                ((Activity) context).finish();
            }
        } else {
            final String targetClass = RouterRepository.getTargetClass(scheme, path);
            if (TextUtils.isEmpty(targetClass)){
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
            } else {
                ComponentName componentName = new ComponentName(context, targetClass);
                intent.setComponent(componentName);
            }
            context.startActivity(intent);
        }

        if (callback != null){
            callback.onSuccess(route);
        }
        return null;
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback) {
        final RouteIntent route = chain.route();
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String authority = uri.getAuthority();

        IProvider provider = RouterRepository.getProvider(scheme, authority);
        provider.onReceiver(route, callback);
        return null;
    }
}
