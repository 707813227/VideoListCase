package js.listvideodemo.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import js.nch.videoplay.VideoPlayView;

/**
 * Created by peter on 16/8/31.
 */
public class VideoFramentAdapter extends FragmentStatePagerAdapter {
    private ViewPagerActivity viewPagerActivity;
    private VideoPlayView videoPlayView;
    private ArrayList<String> data;

    public VideoFramentAdapter(ViewPagerActivity viewPagerActivity, FragmentManager fm, ArrayList<String> data, VideoPlayView videoPlayView) {
        super(fm);
        this.data = data;
        this.videoPlayView = videoPlayView;
        this.viewPagerActivity = viewPagerActivity;
    }

    @Override
    public Fragment getItem(int position) {
        return new ListVideoFrament(viewPagerActivity,videoPlayView);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position);
    }
}
