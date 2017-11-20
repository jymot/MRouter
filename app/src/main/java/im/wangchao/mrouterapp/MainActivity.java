package im.wangchao.mrouterapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import im.wangchao.mrouter.Router;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.jumpOne).setOnClickListener(v -> Router.push(this, "one:///one"));
        findViewById(R.id.jumpTwo).setOnClickListener(v -> Router.push(this, "router:///two"));
    }
}
