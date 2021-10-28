package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.helpers.Constants.POSITION;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;

import java.util.List;

public class NavigationDrawerAdapter extends
        RecyclerView.Adapter<DrawerItemHolder> {
    private final List<DrawerItem> drawerItemList;
    private final Context context;

    public NavigationDrawerAdapter(Context context, List<DrawerItem> listItems) {
        this.context = context;
        this.drawerItemList = listItems;
    }

    @NonNull
    @Override
    public DrawerItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View drawerView = inflater.inflate(R.layout.item_navigation_drawer, parent, false);
        return new DrawerItemHolder(drawerView);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerItemHolder holder, int position) {
        DrawerItem drawerItem = drawerItemList.get(position);
        if (position == 8) {
            holder.textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (position == POSITION) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
            int color = typedValue.data;
            holder.textViewTitle.setTextColor(color);
            holder.linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.border_red_3));
        }
        holder.imageViewIcon.setImageDrawable(drawerItem.drawable);
        holder.textViewTitle.setText(drawerItem.ItemName);

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return drawerItemList.size();
    }


}

class DrawerItemHolder extends RecyclerView.ViewHolder {
    final TextView textViewTitle;
    final ImageView imageViewIcon;
    final LinearLayout linearLayout;

    public DrawerItemHolder(View viewItem) {
        super(viewItem);
        this.textViewTitle = viewItem.findViewById(R.id.text_view_title);
        this.imageViewIcon = viewItem.findViewById(R.id.image_view_icon);
        this.linearLayout = viewItem.findViewById(R.id.linear_layout_background);
    }
}