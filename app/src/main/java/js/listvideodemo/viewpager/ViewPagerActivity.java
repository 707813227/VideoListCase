package js.listvideodemo.viewpager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import js.listvideodemo.R;
import js.nch.videoplay.FullScreenTouch;
import js.nch.videoplay.VideoPlayView;
import js.nch.videoplay.VideoPlayViewManage;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by peter on 16/8/31.
 */
public class ViewPagerActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager vpVideo;
    private ArrayList<String> data = new ArrayList<>();
    private RelativeLayout fullScreen;
    private VideoPlayView videoPlayView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        fullScreen = (RelativeLayout) findViewById(R.id.full_screen);
        videoPlayView = VideoPlayViewManage.getManage().initialize(this);

        vpVideo = (ViewPager) findViewById(R.id.vp_video);
        data.add("one");
        data.add("two");
        data.add("three");

        VideoFramentAdapter adapter = new VideoFramentAdapter(ViewPagerActivity.this,getSupportFragmentManager(), data,videoPlayView);
        vpVideo.setAdapter(adapter);
        tabs.setupWithViewPager(vpVideo);
        tabs.setTabsFromPagerAdapter(adapter);


        fullScreen.setOnTouchListener(new FullScreenTouch(this, true));

        //播放完还原播放界面
        videoPlayView.setCompletionListener(new VideoPlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {

                videoPlayView.release();
                ViewGroup last = (ViewGroup) videoPlayView.getParent();//找到videoitemview的父类，然后remove
                if (last != null && last.getChildCount() > 0) {
                    last.removeAllViews();
                    View itemView = (View) last.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }
    //点击播放视频的回调
    private ConfigurationChanged onConfigurationChanged;
    public void setonConfigurationChanged(ConfigurationChanged onConfigurationChanged){
        this.onConfigurationChanged=onConfigurationChanged;
    }
    public  interface ConfigurationChanged{
        void Changed();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoPlayView != null) {
            videoPlayView.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                fullScreen.setVisibility(View.GONE);
                fullScreen.removeAllViews();
                if (null!=onConfigurationChanged){
                    onConfigurationChanged.Changed();
                }

                videoPlayView.setContorllerVisiable();
                int mShowFlags =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                fullScreen.setSystemUiVisibility(mShowFlags);
            } else {

                ViewGroup viewGroup = (ViewGroup) videoPlayView.getParent();
                if (viewGroup == null)
                    return;
                viewGroup.removeAllViews();
                fullScreen.addView(videoPlayView);
                fullScreen.setVisibility(View.VISIBLE);

                int mHideFlags =
                        View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        ;
                fullScreen.setSystemUiVisibility(mHideFlags);
            }
        } else {
            fullScreen.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayView.stopPlayVideo();
    }

}
