package js.listvideodemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import js.listvideodemo.list.ListActivity;
import js.listvideodemo.small.ListSmallActivity;
import js.listvideodemo.viewpager.ViewPagerActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int windowWith = (int) (wm.getDefaultDisplay().getWidth() * 0.5652f);
        Utils.setVideoHeight(windowWith);

        findViewById(R.id.list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListActivity.class));
            }
        });
        findViewById(R.id.list_vp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ViewPagerActivity.class));
            }
        });
        findViewById(R.id.list_small).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListSmallActivity.class));
            }
        });
    }
}
