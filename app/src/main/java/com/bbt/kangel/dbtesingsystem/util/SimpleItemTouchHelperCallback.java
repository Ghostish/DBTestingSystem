package com.bbt.kangel.dbtesingsystem.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Kangel on 2016/2/18.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {


    private final ItemTouchHelperActivity mActivity;

    public SimpleItemTouchHelperCallback(ItemTouchHelperActivity activity) {
        mActivity = activity;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mActivity.onItemSwiped(viewHolder.getAdapterPosition());
    }
}