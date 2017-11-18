package im.wangchao.mrouter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import static im.wangchao.mrouter.RouteIntent.DEFAULT_POP_URI;

/**
 * <p>Description  : RealCallInterceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/18.</p>
 * <p>Time         : 上午9:04.</p>
 */
/*package*/ class RealCallInterceptor implements IInterceptor {

    @Override public RouteIntent pushInterceptor(Context context, PushChain chain, int requestCode) {
        final RouteIntent route = chain.route();
        final Intent intent = route.getPushIntent(context);

        if (requestCode > 0) {
            ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
        } else {
            ActivityCompat.startActivity(context, intent, null);
        }
        return null;
    }

    @Override public RouteIntent popInterceptor(Context context, PopChain chain, int resultCode) {
        final RouteIntent route = chain.route();
        final Uri uri = route.uri();
        // Scheme is RouterService name.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();

        final Intent intent = route.getPopIntent();

        if (TextUtils.equals(uri.toString(), DEFAULT_POP_URI)){
            ((Activity) context).setResult(resultCode, intent);
            ((Activity) context).finish();
        } else {
            final String targetClass = RouterRepository.getTargetClass(scheme, path);
            ComponentName componentName = new ComponentName(context, targetClass);
            intent.setComponent(componentName);
            context.startActivity(intent);
        }
        return null;
    }

    @Override public RouteIntent requestInterceptor(RequestChain chain, RouterCallback callback) {
        return null;
    }
}
