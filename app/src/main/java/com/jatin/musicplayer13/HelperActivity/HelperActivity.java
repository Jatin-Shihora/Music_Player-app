package com.jatin.musicplayer13.HelperActivity;

import android.app.Activity;
import android.os.Bundle;

public class HelperActivity extends Activity {
    private HelperActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        String action=(String) getIntent().getExtras().get("DO");
        if (action.equals("radio")) {
            //Your code
        } else if (action.equals("volume")) {
            //Your code
        } else if (action.equals("reboot")) {
            //Your code
        } else if (action.equals("top")) {
            //Your code
        } else if (action.equals("app")) {
            //Your code
        }
        if (!action.equals("reboot"))
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
