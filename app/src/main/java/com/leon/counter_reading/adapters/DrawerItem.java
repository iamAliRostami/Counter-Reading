package com.leon.counter_reading.adapters;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class DrawerItem {
    final String ItemName;
    Drawable drawable;

    DrawerItem(String itemName, Drawable drawable) {
        this.ItemName = itemName;
        this.drawable = drawable;
    }

    public static ArrayList<DrawerItem> createItemList(String[] menu, TypedArray drawable) {
        ArrayList<DrawerItem> drawerItems = new ArrayList<>();
        int numItem = menu.length;
        for (int i = 0; i < numItem; i++) {
            drawerItems.add(new DrawerItem(menu[i], drawable.getDrawable(i)));
        }
        return drawerItems;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
