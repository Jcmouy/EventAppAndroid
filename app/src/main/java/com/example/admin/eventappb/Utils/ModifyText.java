package com.example.admin.eventappb.Utils;

import android.graphics.Color;
import android.widget.EditText;

/**
 * Created by JC on 21/2/2018.
 */

public class ModifyText {

    public EditText disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);

        return editText;
    }

}
