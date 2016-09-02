package js.listvideodemo.list;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import js.listvideodemo.R;
import js.listvideodemo.playpage.PlayPageActivity;
import js.nch.videoplay.FullScreenTouch;
import js.nch.videoplay.VideoPlayView;
import js.nch.videoplay.VideoPlayViewManage;
import js.nch.videoplay.video.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by peter on 16/8/31.
 */
public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RelativeLayout fullScreen;
    private ListVideoAdapter listVideoAdapter;
    private VideoPlayView videoPlayView;
    private LinearLayoutManager mLayoutManager;
    private int postion = -1;
    private int lastPostion = -1;
    private RelativeLayout onPlaypage;
    private boolean isStarActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        videoPlayView = VideoPlayViewManage.getManage().initialize(this);
        initUI();

        initVideoPlay();

    }
    private void initUI() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fullScreen = (RelativeLayout) findViewById(R.id.full_screen);
        onPlaypage = (RelativeLayout) findViewById(R.id.on_playpage);
        mLayoutManager = new LinearLayoutManager(this);
        listVideoAdapter = new ListVideoAdapter(this);
        recyclerView.setAdapter(listVideoAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
    }
    private void initVideoPlay() {
        onPlaypage.setVisibility(View.VISIBLE);
        onPlaypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarActivity= true;
                startActivityForResult(new Intent(ListActivity.this,PlayPageActivity.class),100);
            }
        });
        fullScreen.setOnTouchListener(new FullScreenTouch(this,true));
        listVideoAdapter.setPlayClick(new ListVideoAdapter.onPlayClick() {
            @Override
            public void onPlayclick(int position, RelativeLayout image) {
                image.setVisibility(View.GONE);
                if (videoPlayView.isPlay() && lastPostion == position){
                    return;
                }

                postion = position;
                if (videoPlayView.VideoStatus() == IjkVideoView.STATE_PAUSED) {

                    if (position != lastPostion) {
                        videoPlayView.stopPlayVideo();
                        videoPlayView.release();
                    }
                }
                if (lastPostion != -1) {
                    videoPlayView.setShowContoller(true);
                    videoPlayView.showView();
                }

                View view = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.layout_video);
                frameLayout.removeAllViews();

                videoPlayView.showView();
                frameLayout.addView(videoPlayView);
                videoPlayView.start("http://106.38.75.98:8092/video/2016/0901/12/b4fcb03e407bdc39.mp4","title");
                lastPostion = position;
            }
        });
        //播放完还原播放界面
        videoPlayView.setCompletionListener(new VideoPlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {

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
        //显示||关闭 小窗口
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                int index = recyclerView.getChildAdapterPosition(view);
                view.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                if (index == postion) {

                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.layout_video);
                    frameLayout.removeAllViews();
                    if (videoPlayView != null &&
                            ((videoPlayView.isPlay()) || videoPlayView.VideoStatus() == IjkVideoView.STATE_PAUSED)) {
                        view.findViewById(R.id.showview).setVisibility(View.GONE);
                    }

                    if (videoPlayView.VideoStatus() == IjkVideoView.STATE_PAUSED) {
                        if (videoPlayView.getParent() != null)
                            ((ViewGroup) videoPlayView.getParent()).removeAllViews();
                        frameLayout.addView(videoPlayView);
                        return;
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                int index = recyclerView.getChildAdapterPosition(view);
                if (index == postion) {
                    //是否删除小屏
                    if (true) {
                        if (videoPlayView != null) {
                            videoPlayView.stop();
                            videoPlayView.release();
                        }
                    }
                }
            }
        });
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoPlayView != null) {
            videoPlayView.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                fullScreen.setVisibility(View.GONE);
                fullScreen.removeAllViews();
                recyclerView.setVisibility(View.VISIBLE);
                if (postion <= mLayoutManager.findLastVisibleItemPosition()
                        && postion >= mLayoutManager.findFirstVisibleItemPosition()) {

                    View view = recyclerView.findViewHolderForAdapterPosition(postion).itemView;
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.layout_video);
                    frameLayout.removeAllViews();
                    ViewGroup last = (ViewGroup) videoPlayView.getParent();//找到videoitemview的父类，然后remove
                    if (last != null) {
                        last.removeAllViews();
                    }

                    frameLayout.addView(videoPlayView);
                    videoPlayView.setShowContoller(true);
                } else {
                    videoPlayView.setShowContoller(true);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            View view = recyclerView.findViewHolderForAdapterPosition(postion).itemView;
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.layout_video);
            frameLayout.removeAllViews();
            videoPlayView.showView();
            frameLayout.addView(videoPlayView);
            videoPlayView.setContorllerVisiable();
        }else {
            videoPlayView.release();
            final FrameLayout frameLayout = (FrameLayout) videoPlayView.getParent();
            if (frameLayout != null && frameLayout.getChildCount() > 0) {
                frameLayout.removeAllViews();
                View itemView = (View) frameLayout.getParent();
                if (itemView != null) {
                    itemView.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayView.stopPlayVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayView = VideoPlayViewManage.getManage().initialize(this);
        isStarActivity =false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isStarActivity){
            videoPlayView.mVideoView.pause();
        }
    }
}
