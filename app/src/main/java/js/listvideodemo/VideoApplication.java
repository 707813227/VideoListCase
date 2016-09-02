package js.listvideodemo;

import android.app.Application;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by peter on 16/8/31.
 */
public class VideoApplication extends Application {
    private static Handler mHandler = new Handler();;
    @Override
    public void onCreate() {
        initUniversalImageLoader();
    }
    public static Handler getMainHandler(){
        return mHandler;
    }
    /**
     * 初始化ImageLoader
     */
    private void initUniversalImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for releaseAllVideos app
        // config.defaultDisplayImageOptions(getDefaultDisplayImageOption());
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
