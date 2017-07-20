package sid.gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lamar.cs.whoo.R;

import java.util.List;

/**
 * Created by sid on 7/20/17.
 */

public class CustomListViewAdapter extends BaseAdapter {
    private Context context;
    private List<RowItem> rowItems;

    public CustomListViewAdapter(Activity context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RowItem rowItem = (RowItem) getItem(position);
        holder.imageView.setImageBitmap(rowItem.getBitmapImage());

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}