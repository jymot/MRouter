package im.wangchao.mrouterapp;

import im.wangchao.mrouter.IProvider;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterRequestCallback;
import im.wangchao.mrouter.annotations.Provider;

/**
 * <p>Description  : ModuleTwoProvider.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午10:02.</p>
 */
@Provider(name = "test", routerName = "two")
public class ModuleTwoProvider implements IProvider {

    @Override public void onReceiver(RouteIntent route, RouterRequestCallback callback) {
        callback.onResponse(route.newBuilder().addParameter("result", "haha").build());
    }
}
