package im.wangchao.mrouterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.Router;
import im.wangchao.mrouter.RouterCallback;
import im.wangchao.mrouter.annotations.Route;

/**
 * <p>Description  : Module2Activity.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午9:37.</p>
 */
@Route(path = "/two")
public class ModuleTwoActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_two);
        findViewById(R.id.testBtn).setOnClickListener(v -> {
            Router.push(this, "example:///test?v0=111");
        });

        findViewById(R.id.requestBtn).setOnClickListener(v -> {
            Router.request("example://test", new RouterCallback() {
                @Override public void onSuccess(RouteIntent route) {
                    String result = route.bundle().getString("result");
                    Log.e("wcwcwc", "result =>> " + result);
                }

                @Override public void onFailure(RouteIntent route, Exception e) {

                }
            });
        });
    }

    @Override public void onBackPressed() {
        Router.pop(this);
    }
}
