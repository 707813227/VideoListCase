package js.nch.videoplay;

import android.content.Context;

/**
 * Created by peter on 16/8/31.
 */
public class VideoPlayViewManage {
    public static VideoPlayViewManage videoPlayViewManage ;
    private  VideoPlayView videoPlayView;
    private VideoPlayViewManage(){

    }
    public static  VideoPlayViewManage getManage(){
        if (videoPlayViewManage == null){
            videoPlayViewManage = new VideoPlayViewManage();
        }
        return videoPlayViewManage;
    }

    public   VideoPlayView initialize(Context context){
        if (videoPlayView ==null){
            videoPlayView =  new VideoPlayView(context);
        }else {
            videoPlayView.setContext(context);
        }
        return videoPlayView;
    }
}
