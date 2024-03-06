package id.go.kominfo.mantala.aplikasisqlite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import id.go.kominfo.mantala.aplikasisqlite.R;
import id.go.kominfo.mantala.aplikasisqlite.model.Friend;

public class FriendAdapter extends BaseAdapter {
    private final List<Friend> mList;
    private final Context mContext;

    public FriendAdapter(List<Friend> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_layout, parent, false);

        Friend friend = mList.get(position);
        TextView tvID = convertView.findViewById(R.id.tv_id);
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvAddress = convertView.findViewById(R.id.tv_address);

        tvID.setText(String.valueOf(friend.getId()));
        tvName.setText(String.valueOf(friend.getName()));
        tvAddress.setText(String.valueOf(friend.getAddress()));

        return convertView;
    }
}
