package com.locadoc_app.locadoc.UI.Setting;

/**
 * Created by Dainomix on 10/2/2017.
 */

public class SettingListViewItem {
    //private Drawable icon;
    private String title;
    private String desc;

    /* Accessor */
    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    /* Settor */
    public void setTitle(String newTitle) {
        title = newTitle ;
    }

    public void setDesc(String newDesc) {
        desc = newDesc ;
    }
}
