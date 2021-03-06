package com.xupt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.xupt.R;
import java.util.List;

public class SimpleTreeAdapter extends TreeListViewAdapter {
    public SimpleTreeAdapter(ListView mTree, Context context, List<Node> data, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(mTree, context, data, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public SimpleTreeAdapter(ListView mTree, Context context, List<Node> data, int defaultExpandLevel) {
        super(mTree, context, data, defaultExpandLevel);
    }

    @Override
    public View getConvertView(final Node node, int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sensor_select_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.cb = (CheckBox) convertView
                    .findViewById(R.id.sensor_select_item_cb_choose);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.sensor_select_item_tv_label);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.sensor_select_item_iv_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(node, viewHolder.cb.isChecked());
            }
        });
        if (node.isChecked()) {
            viewHolder.cb.setChecked(true);
        } else {
            viewHolder.cb.setChecked(false);
        }

        if (node.getIcon() == -1) {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }

        viewHolder.label.setText(node.getName());

        return convertView;
    }

    private final class ViewHolder {
        ImageView icon;
        CheckBox cb;
        TextView label;
    }
}
