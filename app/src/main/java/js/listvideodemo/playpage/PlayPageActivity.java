package js.listvideodemo.playpage;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import js.listvideodemo.ImageLoaderOptions;
import js.listvideodemo.R;
import js.listvideodemo.Utils;
import js.nch.videoplay.FullScreenTouch;
import js.nch.videoplay.VideoPlayView;
import js.nch.videoplay.VideoPlayViewManage;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by peter on 16/9/1.
 */
public class PlayPageActivity extends AppCompatActivity{

    private VideoPlayView videoPlayView;
    private LinearLayout llContent;
    private RelativeLayout showView;
    private FrameLayout layoutVideo;
    private ImageView ivVideoImg;
    private ImageView ivVideoPlay;
    private RelativeLayout rlBack;
    private RelativeLayout fullScreen;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        videoPlayView = VideoPlayViewManage.getManage().initialize(this);

        initUI();

        initVideoPlay();
    }

    private void initVideoPlay() {
        ivVideoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVideoImg.setVisibility(View.GONE);
                ivVideoPlay.setVisibility(View.GONE);
                ViewGroup last = (ViewGroup) videoPlayView.getParent();
                if (last != null) {
                    last.removeAllViews();
                }
                videoPlayView.showView();
                layoutVideo.removeAllViews();
                videoPlayView.showView();
                layoutVideo.addView(videoPlayView);
                videoPlayView.setContorllerVisiable();
                videoPlayView.start("http://106.38.75.98:8092/video/2016/0901/12/b4fcb03e407bdc39.mp4", "title");

            }
        });
        //播放完还原播放界面
        videoPlayView.setCompletionListener(new VideoPlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {

                videoPlayView.release();
                ViewGroup last = (ViewGroup) videoPlayView.getParent();//找到videoitemview的父类，然后remove
                if (last != null) {
                    last.removeAllViews();
                }
                ivVideoImg.setVisibility(View.VISIBLE);
                ivVideoPlay.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage("http://p1.qiaonaoke.com/image/2016/0703/23/30ef1d22ee3deb70.jpg",
                        ivVideoImg, ImageLoaderOptions.list_options);

            }
        });
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fullScreen.setOnTouchListener(new FullScreenTouch(this, true));
        //找到videoitemview的父类，然后remove
        ViewGroup last = (ViewGroup) videoPlayView.getParent();
        if (last != null) {
            last.removeAllViews();
        }
        videoPlayView.showView();
        layoutVideo.removeAllViews();
        videoPlayView.showView();
        layoutVideo.addView(videoPlayView);
        videoPlayView.setContorllerVisiable();
        videoPlayView.videoPlayController.setBackIcon();
    }

    private void initUI() {

        llContent = (LinearLayout) findViewById(R.id.ll_content);
        showView = (RelativeLayout) findViewById(R.id.showview);
        layoutVideo = (FrameLayout) findViewById(R.id.layout_video);
        ivVideoImg = (ImageView) findViewById(R.id.iv_video_img);
        ivVideoPlay = (ImageView) findViewById(R.id.iv_video_play);
        rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        fullScreen = (RelativeLayout) findViewById(R.id.full_screen);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) showView.getLayoutParams();
        layoutParams.height = Utils.getVideoHeight();
        showView.setLayoutParams(layoutParams);
        textView = new TextView(this);
        textView.setText("我的视频播放页的内容");
        textView.setTextSize(35);
        llContent.addView(textView);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoPlayView != null) {
            videoPlayView.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //小屏播放的UI处理
                fullScreen.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                fullScreen.removeAllViews();
                layoutVideo.removeAllViews();
                layoutVideo.addView(videoPlayView);
                videoPlayView.setShowContoller(true);
                //如果有虚拟控件 显示虚拟控件 没有的虚拟控件则无效
                int mShowFlags =
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                fullScreen.setSystemUiVisibility(mShowFlags);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getWindow().setAttributes(lp);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            } else {
                //全屏播放的UI处理
                ViewGroup viewGroup = (ViewGroup) videoPlayView.getParent();
                if (viewGroup == null)
                    return;
                viewGroup.removeAllViews();
                fullScreen.addView(videoPlayView);
                fullScreen.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                //如果有虚拟控件 隐藏虚拟控件 没有的虚拟控件则无效
                int mHideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                fullScreen.setSystemUiVisibility(mHideFlags);
            }
        } else {
            fullScreen.setVisibility(View.GONE);
        }
        videoPlayView.videoPlayController.setBackIcon();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayView.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayView.pause();
    }
}
