package com.leon.counter_reading.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends
        RecyclerView.Adapter<NavigationDrawerAdapter.DrawerItemHolder> {
    private final List<DrawerItem> drawerItemList;
    public Context context;

    public NavigationDrawerAdapter(Context context, List<DrawerItem> listItems) {
        this.context = context;
        this.drawerItemList = listItems;
    }

    @NonNull
    @Override
    public DrawerItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("viewType", String.valueOf(viewType));
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View drawerView = inflater.inflate(R.layout.item_navigation_drawer, parent, false);
        return new DrawerItemHolder(drawerView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull DrawerItemHolder holder, int position) {
        DrawerItem drawerItem = drawerItemList.get(position);
        if (position == 8) {
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.red));
        } else if (position == MyApplication.POSITION) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
            @ColorInt int color = typedValue.data;
            holder.textViewTitle.setTextColor(color);
            holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.border_red_3));
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

    public static class DrawerItem {
        String ItemName;
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

    static class DrawerItemHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView imageViewIcon;
        LinearLayout linearLayout;

        public DrawerItemHolder(View viewItem) {
            super(viewItem);
            this.textViewTitle = viewItem.findViewById(R.id.text_view_title);
            this.imageViewIcon = viewItem.findViewById(R.id.image_view_icon);
            this.linearLayout = viewItem.findViewById(R.id.linear_layout_background);
        }
    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        final OnItemClickListener mListener;
        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView,
                                         OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child != null && mListener != null) {
                                mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                            }
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onLongItemClick(View view, int position);
        }
    }
}
