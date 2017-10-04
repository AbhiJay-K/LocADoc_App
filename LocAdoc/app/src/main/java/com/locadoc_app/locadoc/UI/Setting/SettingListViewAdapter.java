package com.locadoc_app.locadoc.UI.Setting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.locadoc_app.locadoc.R;

import java.util.ArrayList;

/**
 * Created by Dainomix on 10/2/2017.
 */

public class SettingListViewAdapter extends BaseAdapter {
    private ArrayList<SettingListViewItem> listViewItemList = new ArrayList<SettingListViewItem>();

    // Constructor
    public SettingListViewAdapter() {

    }

    // method for Adding item data
    public void addItem(String title, String desc) {
        SettingListViewItem newItem = new SettingListViewItem();

        newItem.setTitle(title);
        newItem.setDesc(desc);

        listViewItemList.add(newItem);
    }

    /* Accessor */
    // Necessary:: the num of data which is used in Adapter
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // Necessary: return the position of the data
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Necessary: return the data in position
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // Inflate the Layout of Listview_item then get convertView
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.setting_listview_item, parent, false);
        }

        // get the reference about the widget from the View(which layout is inflated) in present
        TextView titleTextView = (TextView) convertView.findViewById(R.id.setting_item_title);
        TextView descTextView = (TextView) convertView.findViewById(R.id.setting_item_subtitle);

        // get Data reference from the Data Set(listViewitem) in position
        SettingListViewItem listViewItem = listViewItemList.get(position);

        // Set data into each Item(Image, title, desc)
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());

        return convertView;
    }






}
