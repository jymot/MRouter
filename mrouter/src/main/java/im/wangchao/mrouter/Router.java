package im.wangchao.mrouter;

import android.content.Context;

/**
 * <p>Description  : Router.
 *                   app://module/path</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 上午9:26.</p>
 */
public class Router {
    private static volatile Router sRouter;

    private Router(){
    }

    public static Router instance(){
        if (sRouter == null){
            synchronized (Router.class){
                if (sRouter == null){
                    sRouter = new Router();
                }
            }
        }

        return sRouter;
    }

    public static void init(){
        RouterRepository.init();
    }

    public void push(Context context, RouteIntent route){
        push(context, route, -1);
    }

    public void push(Context context, RouteIntent route, int requestCode){
        RouterRepository.getRouterServiceCenter().push(context, route, requestCode);
    }

    public void pop(Context context, RouteIntent route){
        pop(context, route, -1);
    }

    public void pop(Context context, RouteIntent route, int resultCode){
        RouterRepository.getRouterServiceCenter().pop(context, route, resultCode);
    }


}
