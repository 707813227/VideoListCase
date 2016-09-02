package js.nch.videoplay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import js.nch.videoplay.video.IMediaController;
import js.nch.videoplay.video.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @Description ${控制器}
 */
public class VideoPlayContoller implements IMediaController {

    private String TAG = "VideoPlayContoller :-- ";
    private static final int SET_VIEW_HIDE = 1;
    private static final int TIME_OUT = 5000;
    private static final int MESSAGE_SHOW_PROGRESS = 2;
    private static final int PAUSE_IMAGE_HIDE = 3;
    private static final int BOTTO_MPROGRESS_BAR = 4;
    private View itemView;
    private View view;
    private boolean isShow;
    private IjkVideoView videoView;
    public SeekBar seekBar;
    AudioManager audioManager;
    private ProgressBar progressBar;
    private boolean isDragging;
    private boolean isShowContoller;
    private ImageView sound, play;
    private RelativeLayout full;
    private TextView time, allTime;
    private Context context;
    private ImageView pauseImage;
    private Bitmap bitmap;
    private boolean isShowBottomprogressba = false;
    private TextView tvTitle;
    private ProgressBar bottomProgressbar;
    private RelativeLayout close;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_VIEW_HIDE:
                    isShow = false;
                    itemView.setVisibility(View.INVISIBLE);
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShow) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
                case PAUSE_IMAGE_HIDE:
                    pauseImage.setVisibility(View.GONE);
                    break;
                case BOTTO_MPROGRESS_BAR:
                    if (isShowBottomprogressba) {
                        setbottomprogressbar();
                        handler.sendEmptyMessageDelayed(BOTTO_MPROGRESS_BAR, 500);
                    }

                    break;
            }
        }
    };


    public VideoPlayContoller(Context context, View view) {
        this.view = view;
        itemView = view.findViewById(R.id.media_contoller);
        this.videoView = (IjkVideoView) view.findViewById(R.id.main_video);
        itemView.setVisibility(View.INVISIBLE);
        isShow = false;
        isDragging = false;

        isShowContoller = true;
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initView();
        initAction();
    }

    public void initView() {
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        bottomProgressbar = (ProgressBar) view.findViewById(R.id.bottom_progressbar);
        tvTitle = (TextView) view.findViewById(R.id.title);
        close = (RelativeLayout) view.findViewById(R.id.close);
        seekBar = (SeekBar) itemView.findViewById(R.id.seekbar);
        allTime = (TextView) itemView.findViewById(R.id.all_time);
        time = (TextView) itemView.findViewById(R.id.time);
        full = (RelativeLayout) itemView.findViewById(R.id.full);
        sound = (ImageView) itemView.findViewById(R.id.sound);
        play = (ImageView) itemView.findViewById(R.id.player_btn);
        pauseImage = (ImageView) view.findViewById(R.id.pause_image);
    }


    public void setbottomprogressbar() {

        int duration = getDuration();
        int position = getCurrentPositionWhenPlaying();
        int progress = position * 100 / (duration == 0 ? 1 : duration);
        bottomProgressbar.setProgress(progress);
        String string = stringForTime(position);
        time.setText(string);
    }


    public void start() {
        isShowBottomprogressba = true;
        handler.sendEmptyMessageDelayed(BOTTO_MPROGRESS_BAR, 500);
//        startTimer();
        bottomProgressbar.setProgress(0);
        bottomProgressbar.setSecondaryProgress(0);
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);
        pauseImage.setVisibility(View.GONE);
        itemView.setVisibility(View.INVISIBLE);
        play.setImageResource(R.drawable.uu_pause_normal);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setTitle(String title) {
        close.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    public void pause() {
        isShowBottomprogressba = false;
        play.setImageResource(R.drawable.uu_play_normal);
        videoView.pause();
        bitmap = videoView.getBitmap();
        if (bitmap != null) {
            pauseImage.setImageBitmap(bitmap);
            pauseImage.setVisibility(View.VISIBLE);
        }
    }

    public void reStart() {
        isShowBottomprogressba = true;
        handler.sendEmptyMessageDelayed(BOTTO_MPROGRESS_BAR, 500);
        play.setImageResource(R.drawable.uu_pause_normal);
        videoView.start();
        if (bitmap != null) {
            handler.sendEmptyMessageDelayed(PAUSE_IMAGE_HIDE, 100);
            bitmap.recycle();
            bitmap = null;
//                        pauseImage.setVisibility(View.GONE);
        }
    }

    public int getDuration() {
        int duration = 0;
        try {
            duration = videoView.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    protected int getCurrentPositionWhenPlaying() {
        int position = 0;
        try {
            position = videoView.getCurrentPosition();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return position;
        }
        return position;
    }

    private long duration;


    private void initAction() {
        videoView.setUpdateListener(new IjkVideoView.UpdateListener() {
            @Override
            public void onUpdateListener(int percent) {

                bottomProgressbar.setSecondaryProgress(percent);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String string = stringForTime(getCurrentPositionWhenPlaying());
                time.setText(string);
//                LogUtil.e(TAG+"onProgressChanged ="+progress);
                bottomProgressbar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgress();
                isDragging = true;
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                show();
                handler.removeMessages(SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                videoView.seekTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                isDragging = false;
                handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
                show();
            }
        });

        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

//                LogUtil.e(TAG+"setOnInfoListener = "+what );
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        if (progressBar.getVisibility() == View.GONE)
                            progressBar.setVisibility(View.VISIBLE);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        progressBar.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                        statusChange(STATUS_PLAYING);
                        progressBar.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        progressBar.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    pause();
                } else {
                    reStart();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    int left = Utils.dip2px(context,10);
                    close.setVisibility(View.GONE);
                    tvTitle.setPadding(left, left, left, left);
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    int left = Utils.dip2px(context,10);
                    int left4 = Utils.dip2px(context,40);
                    tvTitle.setPadding(left4, left, left, left);
                    close.setVisibility(View.VISIBLE);
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    Log.e("full", "close");
                    close.setVisibility(View.GONE);
                    int left = Utils.dip2px(context,10);
                    tvTitle.setPadding(left, left, left, left);
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    Log.e("full", "full");
                    setBackIcon();
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }

        });
    }

    public void setBackIcon() {
        close.setVisibility(View.VISIBLE);
        int left = Utils.dip2px(context, 10);
        int left4 = Utils.dip2px(context,40);
        tvTitle.setPadding(left4, left, left, left);
    }

    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(SET_VIEW_HIDE);
        itemView.setVisibility(View.INVISIBLE);
    }

    public int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }


    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void setAnchorView(View view) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
    }

    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, timeout);
    }

    @Override
    public void hide() {
        Log.e(TAG , "hide");
        if (isShow) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(SET_VIEW_HIDE);
            itemView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void show() {
        Log.e(TAG , "show");
        if (!isShowContoller)
            return;
        isShow = true;
        progressBar.setVisibility(View.GONE);

        itemView.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        show(TIME_OUT);
    }

    @Override
    public void showOnce(View view) {
    }

    private String generateTime(long time) {
        int totalSeconds = 0;
        if (time == -1000) {
            totalSeconds = (getDuration() / 1000);
        } else {
            totalSeconds = (getCurrentPositionWhenPlaying() / 1000);
        }

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public static String stringForTime(int timeMs) {
//        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
//            return "00:00";
//        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public void setVisiable() {
        show();
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        this.duration = duration;
        if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(-1000));
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        String string = generateTime((long) (duration * seekBar.getProgress() * 1.0f / 100));
        time.setText(string);
        return position;
    }


    public void setPauseImageHide() {
        pauseImage.setVisibility(View.GONE);
    }
}
