package js.nch.videoplay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import js.nch.videoplay.video.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
/**
 * Description 播放view
 */
public class VideoPlayView extends RelativeLayout implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private String TAG = "VideoPlayView :-- ";
    public VideoPlayContoller videoPlayController;
    private View  view;
    public IjkVideoView mVideoView;
    private Handler handler = new Handler();

    private View rView;
    private Context mContext;
    private boolean portrait;

    public VideoPlayView(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public void setContext(Context context){
        mContext = context;
        videoPlayController = new VideoPlayContoller(mContext, rView);
    }

    private void initViews() {

        rView = LayoutInflater.from(mContext).inflate(R.layout.view_video_item, this, true);
        view = findViewById(R.id.media_contoller);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        videoPlayController = new VideoPlayContoller(mContext, rView);
        mVideoView.setMediaController(videoPlayController);
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                view.setVisibility(View.GONE);
                if (videoPlayController.getScreenOrientation((Activity) mContext)
                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //横屏播放完毕，重置
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    setLayoutParams(layoutParams);
                }
                if (completionListener != null)
                    completionListener.completion(mp);
            }
        });

    }


    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }

    public void start(String video_url, String title) {
        Uri uri = Uri.parse(video_url);
        if (videoPlayController != null)
            videoPlayController.setTitle(title);
        videoPlayController.start();

        mVideoView.stopPlayback();
        mVideoView.setVideoURI(uri);
        mVideoView.start();
    }


    public void start(){
        if (mVideoView.isPlaying()){
            mVideoView.start();
        }
    }

    public void setContorllerVisiable(){
        videoPlayController.setVisiable();
    }

    public void seekTo(int msec){
        mVideoView.seekTo(msec);
    }

    public void onChanged(Configuration configuration) {
        portrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }

    public void doOnConfigurationChanged(final boolean portrait) {
        if (mVideoView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                        LogUtil.e(TAG+"handler "+ "400");
                        setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
//                        LogUtil.e(TAG+"handler "+ "height==" + heightPixels + "\nwidth==" + widthPixels);
                        setLayoutParams(layoutParams);
                    }
                }
            });
//            orientationEventListener.enable();
        }
    }

    public void stop() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    public void stopPlayVideo() {
        if (this != null) {
            mVideoView.stopPlayback();
        }
    }

    public void showView() {
        ViewGroup last = (ViewGroup) this.getParent();//找到videoitemview的父类，然后remove
        if (last != null) {
            last.removeAllViews();
            View itemView = (View) last.getParent();
            if (itemView != null) {
                View viewById = itemView.findViewById(R.id.showview);
                if (viewById != null)
                    viewById.setVisibility(View.VISIBLE);
            }
        }
    }


    public  void stopVideo() {
        if (this != null) {
            showView();
            stop();
            release();
        }
    }

    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
//        orientationEventListener.disable();
    }

    private void setFullScreen(boolean fullScreen) {
        if (mContext != null && mContext instanceof Activity) {
            WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    public void setShowContoller(boolean isShowContoller) {
        videoPlayController.setShowContoller(isShowContoller);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public long getPalyPostion() {
        return mVideoView.getCurrentPosition();
    }

    public void release() {
        mVideoView.release(true);
    }

    public int VideoStatus() {
        return mVideoView.getCurrentStatue();
    }

    private CompletionListener completionListener;

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }
    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }
}
