package com.candy.videoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candy.videoplayer.R;
import com.candy.videoplayer.entity.MainEntity;
import com.candy.videoplayer.fragment.VideoPlayFragment;
import com.candy.videoplayer.utils.UIUtils;

import java.util.ArrayList;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　candy 版权所有(c) 2017 <br>
 * <b>作者</b>：　　　candy<br>
 * <b>创建日期</b>：　17/6/13 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */

public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<MainEntity> mList;
    private LayoutInflater mInflater;
    private ClickListenerImpl mClickListener;

    public HomePageAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<MainEntity> list) {
        this.mList = list;
    }

    public void setListener(ClickListenerImpl listener) {
        this.mClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainItemHolder) {
            MainItemHolder mainItemHolder = (MainItemHolder) holder;
            Glide.with(mContext)
                    .load(mList.get(position).getThumbUrl())
                    .placeholder(0)
                    .into(mainItemHolder.imgView);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UIUtils.getScreenWidth(),
                    UIUtils.getScreenWidth() * 9 / 16);
            mainItemHolder.imgView.setLayoutParams(params);

            mainItemHolder.desView.setOnClickListener(new ClickListener(position, mainItemHolder.layout));
            mainItemHolder.playView.setOnClickListener(new ClickListener(position, mainItemHolder.layout));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainItemHolder(mInflater.inflate(R.layout.item_home_page, parent, false));
    }

    @Override
    public int getItemCount() {
        if (null == mList) {
            return 0;
        }
        return mList.size();
    }

    public MainEntity getItem(int position) {
        if(position >= 0 && mList.size() > position)
            return mList.get(position);
        else
            return null;

    }

    class MainItemHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layout;
        private ImageView imgView;
        private ImageView playView;
        private TextView desView;

        public MainItemHolder(View itemView) {
            super(itemView);
            this.layout = (RelativeLayout) itemView.findViewById(R.id.rl_layout);
            this.imgView = (ImageView) itemView.findViewById(R.id.iv_img);
            this.playView = (ImageView) itemView.findViewById(R.id.iv_play);
            this.desView = (TextView) itemView.findViewById(R.id.tv_des);
        }
    }

    class ClickListener implements View.OnClickListener{

        private int mPos;
        private RelativeLayout mLayout;

        public ClickListener(int position, RelativeLayout layout) {
            this.mPos = position;
            this.mLayout = layout;
        }

        @Override
        public void onClick(View v) {
             if(v.getId() == R.id.tv_des) {
                 mClickListener.playInDetail(mPos);
             } else if(v.getId() == R.id.iv_play) {
                 mClickListener.playInItem(mPos, mLayout);
             }
        }
    }

    public interface ClickListenerImpl {
        void playInItem(int position, RelativeLayout layout);

        void playInDetail(int position);
    }
}
