package im.wangchao.mrouterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import im.wangchao.mrouter.Router;
import im.wangchao.mrouter.annotations.Route;

/**
 * <p>Description  : Module1Activity.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/20.</p>
 * <p>Time         : 上午9:36.</p>
 */
@Route(path = "/one", routerName = "one")
public class ModuleOneActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_one);
        findViewById(R.id.testBtn).setOnClickListener(v -> {
            Router.request("two://test", route -> {
                String result = route.bundle().getString("result");
                Log.e("wcwcwc", "result =>> " + result);
            });
        });
    }

    @Override public void onBackPressed() {
        Router.pop(this);
    }
}