package com.arjinmc.redbunny;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Eminem on 2016/10/24.
 */

public class BaseActivity extends AppCompatActivity implements BaseActivityInterface, View.OnClickListener{

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void init() {
        initView();
        initListener();
        initData();
    }
}
