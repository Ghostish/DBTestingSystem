package com.bbt.kangel.dbtesingsystem.util;

/**
 * Created by Kangel on 2016/2/18.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}