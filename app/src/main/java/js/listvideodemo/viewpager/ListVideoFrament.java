package js.listvideodemo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import js.listvideodemo.R;
import js.listvideodemo.list.ListVideoAdapter;
import js.nch.videoplay.VideoPlayView;
import js.nch.videoplay.video.IjkVideoView;

/**
 * Created by peter on 16/8/31.
 */
public class ListVideoFrament extends Fragment {

    private ViewPagerActivity viewPagerActivity;
    private VideoPlayView videoPlayView;
    private RecyclerView recyclerView;
    private ListVideoAdapter listVideoAdapter;
    private LinearLayoutManager mLayoutManager;
    private int postion = -1;
    private int lastPostion = -1;
    private View view;
    private String video_url;

    public ListVideoFrament(ViewPagerActivity viewPagerActivity, VideoPlayView videoPlayView) {
        this.videoPlayView = videoPlayView;
        this.viewPagerActivity = viewPagerActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.frament_list, null);
        initUI();
        initVideoPlay();
        return view;
    }

    private void initUI() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        listVideoAdapter = new ListVideoAdapter(getActivity());
        recyclerView.setAdapter(listVideoAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void initVideoPlay() {



        listVideoAdapter.setPlayClick(new ListVideoAdapter.onPlayClick() {
            @Override
            public void onPlayclick(int position, RelativeLayout image) {
                image.setVisibility(View.GONE);
                if (videoPlayView.isPlay() && lastPostion == position) {
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
                video_url = "http://106.38.75.98:8092/video/2016/0901/12/b4fcb03e407bdc39.mp4";
                videoPlayView.start(video_url, "title");
                lastPostion = position;
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            viewPagerActivity.setonConfigurationChanged(new ViewPagerActivity.ConfigurationChanged() {
                @Override
                public void Changed() {
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
                }
            });
        }else {
            videoPlayView.stopVideo();
        }
    }


}
