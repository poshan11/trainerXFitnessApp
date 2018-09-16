package com.cecs453.trainerx.ui;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cecs453.trainerx.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.utils.DrawableUtils;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * <b>Tip:</b> Consider to extend directly from
 * {@link eu.davidea.flexibleadapter.items.AbstractFlexibleItem} to benefit of the already
 * implemented methods (getter and setters).
 */
public class SimpleItem extends AbstractFlexibleItem<SimpleItem.SimpleViewHolder> {

    private String docId;
    private String fname;
    private String lname;
    private String url;

    public SimpleItem(String fname, String lname, String url, String docId) {
        this.fname = fname;
        this.lname = lname;
        this.url = url;
        this.docId = docId;
        setSelectable(true);
        setDraggable(false);
        setSwipeable(false);
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    static int dpToPx(Context context) {
        return Math.round(4f * getDisplayMetrics(context).density);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_clients;
    }

    @Override
    public SimpleViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new SimpleViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, SimpleViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        holder.textView.setText(getTitle());
        Glide.with(context).load(getUrl()).into(holder.imageView);

        Drawable drawable = DrawableUtils.getSelectableBackgroundCompat(
                ContextCompat.getColor(context, R.color.material_color_blue_grey_400),             // normal background
                ContextCompat.getColor(context, R.color.material_color_orange_300), // pressed background
                Color.LTGRAY);                 // ripple color
        DrawableUtils.setBackgroundCompat(holder.itemView, drawable);
    }

    private String getTitle() {
        return fname + " " + lname;
    }

    private String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "SimpleItem[" + super.toString() + "]";
    }

    static final class SimpleViewHolder extends FlexibleViewHolder {

        ImageView imageView;
        TextView textView;

        Context mContext;

        SimpleViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.mContext = view.getContext();
            this.textView = view.findViewById(R.id.textViewName);
            this.imageView = view.findViewById(R.id.imageViewClient);

        }

        @Override
        public void toggleActivation() {
            super.toggleActivation();
            // Here we use a custom Animation inside the ItemView
        }

        @Override
        public float getActivationElevation() {
            return dpToPx(itemView.getContext());
        }

        @Override
        public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
            if (mAdapter.getRecyclerView().getLayoutManager() instanceof GridLayoutManager ||
                    mAdapter.getRecyclerView().getLayoutManager() instanceof StaggeredGridLayoutManager) {
                if (position % 2 != 0)
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
            } else {
                //Linear layout
                if (mAdapter.isSelected(position))
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
            }
        }

    }

}