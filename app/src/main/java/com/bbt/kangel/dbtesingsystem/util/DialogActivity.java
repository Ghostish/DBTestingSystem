package com.bbt.kangel.dbtesingsystem.util;

import android.os.Bundle;

/**
 * Created by Kangel on 2015/12/14.
 */
public interface DialogActivity {
    void dismissDialog();
    void doAtPositiveButton(String tag);
    void onDialogItemSelect(Bundle args);
}
