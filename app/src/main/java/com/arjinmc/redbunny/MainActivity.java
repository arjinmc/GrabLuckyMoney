package com.arjinmc.redbunny;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private AppCompatCheckBox cbWechat;
    private AppCompatCheckBox cbQQ;
    private TextView tvStatus;
    private Button btnSettingAccessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void initView() {
        cbWechat = (AppCompatCheckBox) findViewById(R.id.cb_wechat);
        cbQQ = (AppCompatCheckBox) findViewById(R.id.cb_qq);
        btnSettingAccessibility = (Button) findViewById(R.id.btn_setting_accessibility);
        tvStatus = (TextView) findViewById(R.id.tv_status);
    }

    @Override
    public void initListener() {
        btnSettingAccessibility.setOnClickListener(this);
        cbWechat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.setStatus(MainActivity.this,SPUtils.WECHAT,isChecked);
            }
        });
        cbQQ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.setStatus(MainActivity.this,SPUtils.QQ,isChecked);
            }
        });
    }

    @Override
    public void initData() {
        cbWechat.setChecked(SPUtils.getStatus(this,SPUtils.WECHAT));
        cbQQ.setChecked(SPUtils.getStatus(this,SPUtils.QQ));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_setting_accessibility:
                AccessibilityUtils.openSetting(this);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AccessibilityUtils.isOpen(this)){
            tvStatus.setText(getString(R.string.fetch_service_on));
        }else{
            tvStatus.setText(getString(R.string.fetch_service_off));
        }
    }
}
