package com.jld.obdserialport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ObdDataAdapter extends RecyclerView.Adapter<ObdDataAdapter.ViewHolder> {

    public static final String TAG = "ObdDataAdapter";
    private List<String> mDatas;
    private Context mContext;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
