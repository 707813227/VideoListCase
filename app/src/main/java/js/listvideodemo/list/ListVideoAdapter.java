package js.listvideodemo.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import js.listvideodemo.ImageLoaderOptions;
import js.listvideodemo.R;
import js.listvideodemo.Utils;

/**
 * Created by peter on 16/8/31.
 */
public class ListVideoAdapter extends RecyclerView.Adapter<ListVideoAdapter.VideoViewHolder> {
    private final Context mContext;
    private ArrayList<String> dataListImage = new ArrayList<>();
    private ArrayList<String> dataListTitle = new ArrayList<>();
    private ArrayList<String> dataListName = new ArrayList<>();

    public ListVideoAdapter(Context context){
        this.mContext = context;
        dataListImage.add("http://p1.qiaonaoke.com/image/2016/0703/23/30ef1d22ee3deb70.jpg");
        dataListImage.add("http://p1.qiaonaoke.com/image/2016/0703/23/30ef1d22ee3deb70.jpg");
        dataListImage.add("http://p1.qiaonaoke.com/image/2016/0703/23/30ef1d22ee3deb70.jpg");
        dataListImage.add("http://p1.qiaonaoke.com/image/2016/0703/23/30ef1d22ee3deb70.jpg");
        dataListImage.add("http://p1.qiaonaoke.com/image/2016/0703/23/30ef1d22ee3deb70.jpg");
        dataListTitle.add("Context");
        dataListTitle.add("Context");
        dataListTitle.add("Context");
        dataListTitle.add("Context");
        dataListTitle.add("Context");
        dataListName.add("dataListName");
        dataListName.add("dataListName");
        dataListName.add("dataListName");
        dataListName.add("dataListName");
        dataListName.add("dataListName");
    }
    @Override
    public ListVideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_video, null);
        VideoViewHolder holder = new VideoViewHolder(view);
        view.setTag(holder);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListVideoAdapter.VideoViewHolder holder, int position) {
        holder.update(position);
    }

    @Override
    public int getItemCount() {
        return dataListName.size();
    }
    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private  TextView time; //时间
        private  ImageView image; //视频图片
        private  ImageView ivVideoHead;//发布视频的头像
        private  TextView tvVideoName;//发布视频的名称
        private  TextView tvVideoWatchCount;//播放次数
        private  RelativeLayout layout;
        private  ImageView ivVideoShare;//分享图标
        private  ImageView ivVideoMore;//更多图标
        private  RelativeLayout rlVideoMore;
        public RelativeLayout showView;//播放视频控件
        private TextView title;//名称

        public VideoViewHolder(View itemView) {
            super(itemView);

            showView= (RelativeLayout) itemView.findViewById(R.id.showview);
            rlVideoMore = (RelativeLayout) itemView.findViewById(R.id.rl_video_more);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            title= (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            tvVideoName = (TextView) itemView.findViewById(R.id.tv_video_name);
            tvVideoWatchCount = (TextView) itemView.findViewById(R.id.tv_video_watch_count);
            image = (ImageView) itemView.findViewById(R.id.image_bg);
            ivVideoHead = (ImageView) itemView.findViewById(R.id.iv_video_head);
            ivVideoShare = (ImageView) itemView.findViewById(R.id.iv_video_share);
            ivVideoMore = (ImageView) itemView.findViewById(R.id.iv_video_more);
            if (layout!=null){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                layoutParams.height = Utils.getVideoHeight();
                layout.setLayoutParams(layoutParams);
            }
        }

        public void update(final int position) {
            //分享点击
            ivVideoShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            //更多点击
            rlVideoMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            //更多点击
            ivVideoMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //视频图片
            ImageLoader.getInstance().displayImage(dataListImage.get(position),
                    image, ImageLoaderOptions.list_options);
            //发布者头像
            ImageLoader.getInstance().displayImage(dataListImage.get(position),
                    ivVideoHead, ImageLoaderOptions.list_options);
            //标题
            title.setText(dataListTitle.get(position));
            //时长
            time.setText(dataListTitle.get(position));
            //发布者的名字
            tvVideoName.setText(dataListName.get(position));
            //播放次数
            tvVideoWatchCount.setText(dataListName.get(position));
            //点击回调 播放视频
            showView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playclick != null)
                        playclick.onPlayclick(position, showView);
                }
            });
        }
    }

    private onPlayClick playclick;
    public void setPlayClick(onPlayClick playclick){
        this.playclick=playclick;
    }
    public  interface onPlayClick{
        void onPlayclick(int position,RelativeLayout image);
    }

}
