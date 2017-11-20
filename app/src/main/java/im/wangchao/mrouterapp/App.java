package im.wangchao.mrouterapp;

import android.app.Application;

import im.wangchao.mrouter.Router;

/**
 * <p>Description  : App.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午10:12.</p>
 */
public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Router.init();
    }
}
