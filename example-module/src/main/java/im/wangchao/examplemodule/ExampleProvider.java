package im.wangchao.examplemodule;

import im.wangchao.mrouter.IProvider;
import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.RouterCallback;
import im.wangchao.mrouter.annotations.Provider;

/**
 * <p>Description  : ExampleProvider.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/24.</p>
 * <p>Time         : 下午4:11.</p>
 */
@Provider(name = "test", routerName = "example")
public class ExampleProvider implements IProvider{
    @Override public void onReceiver(RouteIntent route, RouterCallback callback) {
        callback.onResponse(route.newBuilder().addParameter("result", "example-success").build());
    }
}
