package im.wangchao.examplemodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import im.wangchao.mrouter.Router;
import im.wangchao.mrouter.annotations.Route;

/**
 * <p>Description  : ExampleActivity.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/24.</p>
 * <p>Time         : 下午4:02.</p>
 */
@Route(path = "/test", routerName = "example")
public class ExampleActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        Log.e("wcwcwc", "receive params: v0 = " + getIntent().getStringExtra("v0"));

        findViewById(R.id.button).setOnClickListener(v -> {
            Router.push(this, "one:///one");
        });
    }
}
