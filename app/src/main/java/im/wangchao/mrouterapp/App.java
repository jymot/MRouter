package im.wangchao.mrouterapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import im.wangchao.mrouter.Router;

/**
 * <p>Description  : App.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午10:12.</p>
 */
public class App extends Application {

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override public void onCreate() {
        super.onCreate();
        Router.init();
    }
}
