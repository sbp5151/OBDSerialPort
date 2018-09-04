package com.jld.obdserialport.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jld.obdserialport.R;

import org.w3c.dom.Text;

import java.util.List;

public class ObdDataAdapter extends RecyclerView.Adapter<ObdDataAdapter.ViewHolder> {

    public static final String TAG = "ObdDataAdapter";
    private List<String> mDatas;
    private Context mContext;
    private String[] mSignificantDatas = {"判断汽车正在熄火",};
    public int mDataFilter = 0;

    public ObdDataAdapter(List<String> datas, Context context) {
        mDatas = datas;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.obd_recycle_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

//        <item>ALL</item>
//        <item>ONOFF</item>
//        <item>GPS</item>
//        <item>RT</item>
//        <item>TT</item>
//        <item>HBT</item>
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mDataFilter == 1 && (TextUtils.isEmpty(mDatas.get(position)) || !mDatas.get(position).contains("判断汽车正在"))) {
            return;
        } else if (mDataFilter == 2 && (TextUtils.isEmpty(mDatas.get(position)) || !mDatas.get(position).contains("longitude"))) {
            return;
        } else if (mDataFilter == 3 && (TextUtils.isEmpty(mDatas.get(position)) || !mDatas.get(position).contains("RT"))) {
            return;
        } else if (mDataFilter == 4 && (TextUtils.isEmpty(mDatas.get(position)) || !mDatas.get(position).contains("TT"))) {
            return;
        } else if (mDataFilter == 5 && (TextUtils.isEmpty(mDatas.get(position)) || !mDatas.get(position).contains("HBT"))) {
            return;
        }

        if (mDatas.get(position).equals("判断汽车正在熄火") || mDatas.get(position).equals("判断汽车正在点火")
                || mDatas.get(position).equals("-TT") || mDatas.get(position).equals("-HBT"))
            holder.mData_item.setTextColor(mContext.getResources().getColor(R.color.important_text));
        else
            holder.mData_item.setTextColor(mContext.getResources().getColor(R.color.ordinary_text));
        holder.mData_item.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mData_item;

        public ViewHolder(View itemView) {
            super(itemView);
            mData_item = itemView.findViewById(R.id.tv_data_item);
        }
    }
}
