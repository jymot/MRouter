package im.wangchao.mrouterapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import im.wangchao.mrouter.RouteIntent;
import im.wangchao.mrouter.Router;
import im.wangchao.mrouter.RouterCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.jumpOne).setOnClickListener(v -> Router.push(this, "one:///one", new RouterCallback() {
                @Override public void onSuccess(RouteIntent route) {
                    Log.e("wcwcwc", "push onSuccess: " + route.uri());
                }

                @Override public void onFailure(RouteIntent route, Exception e) {

                }
            }));
        findViewById(R.id.jumpTwo).setOnClickListener(v -> Router.push(this, "router:///two"));
    }
}
