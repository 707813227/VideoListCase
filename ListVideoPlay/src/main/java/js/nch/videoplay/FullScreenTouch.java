package js.nch.videoplay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 控制全屏音量跟进度条的触摸事件
 * Created by peter on 16/8/31.
 */
public class FullScreenTouch implements OnTouchListener {

    private final Activity activity;
    private  boolean isChangeVolume;
    private VideoPlayView videoPlayView ;

    private ProgressBar mDialogProgressBar;//进度条
    private TextView mDialogCurrentTime;
    private TextView mDialogTotalTime;
    private ImageView mDialogIcon;
    public Dialog mProgressDialog;
    public Dialog mVolumeDialog;
    private int mResultTimePosition;
    private ProgressBar mDialogVolumeProgressBar;
    private float mDownX;
    private float mDownY;
    private boolean mChangeVolume;
    private boolean mChangePosition;
    private int mDownPosition;
    private AudioManager mAudioManager;
    private int mDownVolume;
    private int mScreenWidth;
    private int mScreenHeight;
    protected int mThreshold = 80;


    public FullScreenTouch(Activity activity ,boolean isChangeVolume) {
        this.isChangeVolume = isChangeVolume;
        this.activity = activity;
        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mScreenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        videoPlayView = VideoPlayViewManage.getManage().initialize(activity);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mChangeVolume = false;
                mChangePosition = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);

                if (!mChangePosition && !mChangeVolume) {
                    if (absDeltaX > mThreshold || absDeltaY > mThreshold) {
                        if (absDeltaX >= mThreshold) {
                            mChangePosition = true;
                            mDownPosition = getCurrentPositionWhenPlaying();
                        } else {
                            mChangeVolume = true;
                            mDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        }
                    }
                }
                if (mChangePosition) {
                    showProgressDialog(deltaX);
                }
                if (mChangeVolume) {
                    if (isChangeVolume) {
                        showVolumDialog(-deltaY);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if (mVolumeDialog != null) {
                    mVolumeDialog.dismiss();
                }
                if (mChangePosition) {

                    videoPlayView.mVideoView.seekTo(mResultTimePosition);
                    int duration = getDuration();
                    int progress = mResultTimePosition * 100 / (duration == 0 ? 1 : duration);
                    videoPlayView.videoPlayController.seekBar.setProgress(progress);
                }

                break;

        }

        return true;
    }

    protected int getCurrentPositionWhenPlaying() {
        int position = 0;
        if (videoPlayView.mVideoView.isPlaying()) {
            try {
                position = videoPlayView.mVideoView.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }
    /**
     * 显示加减进度
     *
     * @param deltaX
     */
    protected void showProgressDialog(float deltaX) {
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(activity).inflate(R.layout.uu_progress_dialog, null);
            mDialogProgressBar = ((ProgressBar) localView.findViewById(R.id.duration_progressbar));
            mDialogCurrentTime = ((TextView) localView.findViewById(R.id.tv_current));
            mDialogTotalTime = ((TextView) localView.findViewById(R.id.tv_duration));
            mDialogIcon = ((ImageView) localView.findViewById(R.id.duration_image_tip));
            mProgressDialog = new Dialog(activity, R.style.uu_style_dialog_progress);
            mProgressDialog.setContentView(localView);
            mProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
            mProgressDialog.getWindow().addFlags(32);
            mProgressDialog.getWindow().addFlags(16);
            mProgressDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mProgressDialog.getWindow().getAttributes();
            localLayoutParams.gravity = 49;
            localLayoutParams.y = activity.getResources().getDimensionPixelOffset(R.dimen.uu_progress_dialog_margin_top);
            mProgressDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        int totalTime = getDuration();
        mResultTimePosition = (int) (mDownPosition + deltaX * totalTime / mScreenWidth);
        if (mResultTimePosition > totalTime) mResultTimePosition = totalTime;
        mDialogCurrentTime.setText(Utils.stringForTime(mResultTimePosition));
        mDialogTotalTime.setText(" / " + Utils.stringForTime(totalTime) + "");
        mDialogProgressBar.setProgress(mResultTimePosition * 100 / totalTime);
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.uu_forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(R.drawable.uu_backward_icon);
        }
    }

    /**
     * 显示音量加减
     *
     * @param deltaY
     */
    protected void showVolumDialog(float deltaY) {
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(activity).inflate(R.layout.uu_volume_dialog, null);
            mDialogVolumeProgressBar = ((ProgressBar) localView.findViewById(R.id.volume_progressbar));
            mVolumeDialog = new Dialog(activity, R.style.uu_style_dialog_progress);
            mVolumeDialog.setContentView(localView);
            mVolumeDialog.getWindow().addFlags(8);
            mVolumeDialog.getWindow().addFlags(32);
            mVolumeDialog.getWindow().addFlags(16);
            mVolumeDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mVolumeDialog.getWindow().getAttributes();
            localLayoutParams.gravity = 19;
            localLayoutParams.x = activity.getResources().getDimensionPixelOffset(R.dimen.uu_volume_dialog_margin_left);
            mVolumeDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mDownVolume + deltaV, 0);
        int transformatVolume = (int) (mDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
        mDialogVolumeProgressBar.setProgress(transformatVolume);
    }

    public int getDuration() {
        int duration = 0;
        try {
            duration = videoPlayView.mVideoView.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

}
