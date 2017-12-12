package im.wangchao.examplemodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import im.wangchao.mrouter.IRouterService;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;
import im.wangchao.mrouter.annotations.RouterService;

/**
 * <p>Description  : ExampleRouterService.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/24.</p>
 * <p>Time         : 下午4:07.</p>
 */
@RouterService("example")
public class ExampleRouterService implements IRouterService {

    @Override public void push(Context context, RouteIntent route, int requestCode, RouterCallback callback) {
        Log.e("wcwcwc", "ExampleRouterService : push() -> " + route.targetClass());
        final Intent intent = route.getPushIntent(context);

        if (requestCode > 0) {
            ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
        } else {
            ActivityCompat.startActivity(context, intent, null);
        }
    }

    @Override public void pop(Context context, RouteIntent route, int resultCode, RouterCallback callback) {
        Log.e("wcwcwc", "Module one service: pop()");
    }
}
