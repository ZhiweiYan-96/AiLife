package com.record.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.record.bean.Tomato;
import com.record.myLife.R;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import java.util.ArrayList;

public class TomatoListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Tomato> items = new ArrayList();

    class ViewHolder {
        TextView content;
        TextView title;

        ViewHolder() {
        }
    }

    public TomatoListAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public void setItems(ArrayList<Tomato> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(Tomato tomato) {
        this.items.remove(tomato);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.items != null ? this.items.size() : 0;
    }

    public Object getItem(int position) {
        return this.items != null ? (Tomato) this.items.get(position) : null;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.ll_unhandler_tomato, null);
            setFind(convertView, viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (this.items != null && this.items.size() > 0) {
            setUi(viewHolder, (Tomato) this.items.get(position));
        }
        return convertView;
    }

    public void setFind(View view, ViewHolder viewHolder) {
        viewHolder.title = (TextView) view.findViewById(R.id.tv_title);
        viewHolder.content = (TextView) view.findViewById(R.id.tv_content);
    }

    public void setUi(ViewHolder holder, Tomato ui) {
        holder.title.setText(DateTime.format(DateTime.pars2Calender(ui.startTime), "MM月dd日 HH:mm"));
        holder.content.setText(FormatUtils.format_0fra(ui.length) + "min");
    }

    public void setUiNone(ViewHolder holder) {
        holder.title.setText("暂无数据");
        holder.content.setText("");
    }
}
