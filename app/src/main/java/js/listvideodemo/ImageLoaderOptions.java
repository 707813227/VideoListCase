package js.listvideodemo;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by peter on 16/8/31.
 */
public class ImageLoaderOptions {

    // 在listview中使用的设置
    public static DisplayImageOptions list_options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_launcher)
            .showImageForEmptyUri(R.drawable.ic_launcher)
            .showImageOnFail(R.drawable.ic_launcher)
            .cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true)// 会识别图片的方向信息
            .displayer(new FadeInBitmapDisplayer(0)).build();// 渐渐显示的动画

}
