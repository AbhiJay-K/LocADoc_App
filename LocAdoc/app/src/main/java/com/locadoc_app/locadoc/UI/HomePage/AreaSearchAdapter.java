package com.locadoc_app.locadoc.UI.HomePage;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by AbhiJay_PC on 11/10/2017.
 */

public class AreaSearchAdapter implements Filterable {
    private Map<String,Integer> AreaList;
    private HomePageActivity activity;
    private AreaFilter areaFilter;
    private Typeface typeface;
   // @Override
    public int getCount() {
        return AreaList.size();
    }

    /**
     * Get specific item from user list
     */
    public Object getItem(String ar) {
        return AreaList.get(ar);
    }

    /**
     * Get user list item id
     * @param i item index
     * @return current item id
     */
    public long getItemId(int i) {
        return i;
    }
    @Override
    public Filter getFilter() {
        if (areaFilter == null) {
            areaFilter = new AreaFilter();
        }

        return areaFilter;
    }

    static class ViewHolder {
        TextView iconText;
        TextView name;
    }

    public View getView(int position, View view, ViewGroup parent) {
       /* // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        final ViewHolder holder;
        final String user = (String) getItem(position);

        if (view == null) {
            *LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           / view = layoutInflater.inflate(R.layout.area_list_view, parent, false);
            holder = new ViewHolder();
            holder.iconText = (TextView) view.findViewById(R.id.icon_text);
            holder.name = (TextView) view.findViewById(R.id.friend_list_row_layout_name);
            holder.iconText.setTypeface(typeface, Typeface.BOLD);
            holder.iconText.setTextColor(activity.getResources().getColor(R.color.white));
            holder.name.setTypeface(typeface, Typeface.NORMAL);

            view.setTag(holder);
        } else {
            // get view holder back
            holder = (ViewHolder) view.getTag();
        }

        // bind text with view holder content view for efficient use
        holder.iconText.setText("#");
        holder.name.setText(user.getEmail());
        view.setBackgroundResource(R.drawable.friend_list_selector);*/

        return view;
    }
    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class AreaFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                Map<String,Integer> tempList = AreaSQLHelper.getSearchValue(constraint.toString());
                AreaList = tempList;
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = AreaList.size();
                filterResults.values = AreaList;
            }
            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            AreaList = (Map<String,Integer>) results.values;
            //notifyDataSetChanged();
        }
    }
}
