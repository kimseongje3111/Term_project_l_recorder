package com.example.seongje.l_recorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class NewAdapter extends ArrayAdapter<ListViewItems> {

    private ArrayList<ListViewItems> listViewItemList = new ArrayList<ListViewItems>();
    private int layout;
    private int resourceId;


    public NewAdapter(Context context, int resource, ArrayList<ListViewItems> items) {
        super(context, resource);
        this.listViewItemList = items;
        this.resourceId = resource;

    }

    // public int setLayout()

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public ListViewItems getItem(int position) {
        return new ListViewItems();
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;//final로 선언하는 이유
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_items, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        TextView dateView = (TextView) convertView.findViewById(R.id.date);
        TextView recordView = (TextView) convertView.findViewById(R.id.recordingTime);

        ListViewItems listViewItem = listViewItemList.get(position);

        nameView.setText(listViewItem.getTitle());
        dateView.setText(listViewItem.getDate());
        recordView.setText(listViewItem.getTime());

        return convertView;

    }

    public void addItem(String title, String date, String time) {
        ListViewItems item = new ListViewItems();
        item.setTitle(title);
        item.setDate(date);
        item.setTime(time);
    }

}