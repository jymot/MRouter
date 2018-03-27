package im.wangchao.mrouter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import static im.wangchao.mrouter.RouteIntent.DEFAULT_POP_URI;

/**
 * <p>Description  : Router.
 *                   app://module/path</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 上午9:26.</p>
 */
public final class Router {
    private static boolean sInit;

    private Router(){}

    /**
     * Router 模块初始化方法，一般在 Application::onCreate 中调用
     */
    public static void init(){
        RouterRepository.init();
        sInit = true;
    }

    public static void push(Context context, String uri){
        push(context, uri, null, null);
    }

    public static void push(Context context, String uri, RouterCallback callback){
        push(context, uri, null, callback);
    }

    public static void push(Context context, String uri, Bundle bundle){
        push(context, uri, bundle, -1);
    }

    public static void push(Context context, String uri, Bundle bundle, RouterCallback callback){
        push(context, uri, bundle, -1, callback);
    }

    public static void push(Context context, String uri, int flags){
        push(context, uri, null, flags);
    }

    public static void push(Context context, String uri, int flags, RouterCallback callback){
        push(context, uri, null, flags, callback);
    }

    public static void push(Context context, String uri, Bundle bundle, int flags){
        pushForResult(context, uri, bundle, flags, -1);
    }

    public static void push(Context context, String uri, Bundle bundle, int flags, RouterCallback callback){
        pushForResult(context, uri, bundle, flags, -1, callback);
    }

    public static void pushForResult(Context context, String uri, int requestCode){
        pushForResult(context, uri, null, -1, requestCode);
    }

    public static void pushForResult(Context context, String uri, int requestCode, RouterCallback callback){
        pushForResult(context, uri, null, -1, requestCode, callback);
    }

    public static void pushForResult(Context context, String uri, Bundle bundle, int requestCode){
        pushForResult(context, uri, bundle, -1, requestCode);
    }

    public static void pushForResult(Context context, String uri, Bundle bundle, int requestCode, RouterCallback callback){
        pushForResult(context, uri, bundle, -1, requestCode, callback);
    }

    public static void pushForResult(Context context, String uri, int requestCode, int flags){
        pushForResult(context, uri, null, flags, requestCode);
    }

    public static void pushForResult(Context context, String uri, int requestCode, int flags, RouterCallback callback){
        pushForResult(context, uri, null, flags, requestCode, callback);
    }

    public static void pushForResult(Context context, String uri, Bundle bundle, int requestCode, int flags){
        push(context, RouteIntent.of(uri, bundle, flags), requestCode, null);
    }

    public static void pushForResult(Context context, String uri, Bundle bundle, int requestCode, int flags, RouterCallback callback){
        push(context, RouteIntent.of(uri, bundle, flags), requestCode, callback);
    }

    public static void pop(Context context){
        pop(context, DEFAULT_POP_URI, null, Activity.RESULT_CANCELED);
    }

    public static void pop(Context context, RouterCallback callback){
        pop(context, DEFAULT_POP_URI, null, Activity.RESULT_CANCELED, callback);
    }

    public static void pop(Context context, String uri, Bundle bundle){
        pop(context, uri, bundle, Activity.RESULT_OK);
    }

    public static void pop(Context context, String uri, Bundle bundle, RouterCallback callback){
        pop(context, uri, bundle, Activity.RESULT_OK, callback);
    }

    public static void pop(Context context, String uri, int resultCode){
        pop(context, uri, null, resultCode);
    }

    public static void pop(Context context, Bundle bundle, int resultCode){
        pop(context, DEFAULT_POP_URI, bundle, resultCode);
    }

    public static void pop(Context context, Bundle bundle, int resultCode, RouterCallback callback){
        pop(context, DEFAULT_POP_URI, bundle, resultCode, callback);
    }

    public static void pop(Context context, String uri, Bundle bundle, int resultCode){
        pop(context, RouteIntent.of(uri, bundle), resultCode, null);
    }

    public static void pop(Context context, String uri, Bundle bundle, int resultCode, RouterCallback callback){
        pop(context, RouteIntent.of(uri, bundle), resultCode, callback);
    }

    public static void request(String uri){
        request(uri, null, null);
    }

    public static void request(String uri, RouterCallback callback){
        request(uri, null, callback);
    }

    public static void request(String uri, Bundle bundle){
        request(uri, bundle, null);
    }

    public static void request(String uri, Bundle bundle, RouterCallback callback){
        request(RouteIntent.of(uri, bundle), callback);
    }

    /**
     * 当前页面入栈
     *
     * @param context 当前页面 Context
     * @param route RouteIntent
     * @param requestCode 请求码
     */
    public static void push(Context context, RouteIntent route, int requestCode, RouterCallback callback){
        check();
        RouterRepository.getRouterServiceCenter().push(context, route, requestCode, callback);
    }

    /**
     * 当前页面出栈
     *
     * @param context 当前页面 Context
     * @param route RouteIntent
     * @param resultCode 响应码
     */
    public static void pop(Context context, RouteIntent route, int resultCode, RouterCallback callback){
        check();
        RouterRepository.getRouterServiceCenter().pop(context, route, resultCode, callback);
    }

    /**
     * 模块之间请求数据
     *
     * @param route RouteIntent
     * @param callback RouterCallback
     */
    public static void request(RouteIntent route, RouterCallback callback){
        check();
        RouterRepository.getRouterServiceCenter().onReceiver(route, callback);
    }

    private static void check(){
        if (!sInit){
            throw new RuntimeException("You must invoke Router.init() first.");
        }
    }
}
