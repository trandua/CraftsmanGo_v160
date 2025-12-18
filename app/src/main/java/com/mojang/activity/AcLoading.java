package com.mojang.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ironsource.mediationsdk.IronSource;

public class AcLoading {
    private Activity mActivity;
    private Dialog loadingDialog;

    private TextView txtLoading;
    private TextView txtCount;
    private LinearLayout lnLoading;
    private ProgressBar prBar;

    private CountDownTimer countDownTimer;
    private long interval = 1000; // Khoảng thời gian giữa các lần gọi onTick() (1 giây = 1,000 milliseconds)

    private int _time = 2;
    public AcLoading(Activity mActivity_) {
        this.mActivity = mActivity_;
    }


    public void init(){
        initLayout();
        loadingDialog = new Dialog(mActivity);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ///TODO Setup Loading View
//        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setContentView(lnLoading);

        loadingDialog.setCancelable(false);

        startTimer();
    }
    private void initLayout(){
        lnLoading = new LinearLayout(mActivity);
        lnLoading.setOrientation(LinearLayout.VERTICAL);
        lnLoading.setGravity(Gravity.CENTER);

        //Init ProgressBar
        prBar = new ProgressBar(mActivity);
        prBar.setIndeterminate(false);


        //Init TextView
        txtLoading = new TextView(mActivity);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.topMargin = 15;
        txtLoading.setText("This action maybe contain ads...");

        txtCount = new TextView(mActivity);
        txtCount.setTextSize(30.0f);
        txtCount.setText("" + _time);

        //Add to View
        lnLoading.addView(prBar);
        lnLoading.addView(txtLoading, textParams);
        lnLoading.addView(txtCount, textParams);

    }
    private void startTimer() {
        countDownTimer = new CountDownTimer(_time * 1000, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Được gọi sau mỗi khoảng thời gian interval
                long secondsRemaining = millisUntilFinished / 1000;
                // Cập nhật giao diện người dùng với thời gian còn lại
                // Ví dụ: textView.setText(String.valueOf(secondsRemaining));
                txtCount.setText("" + secondsRemaining);
            }

            @Override
            public void onFinish() {
                // Được gọi khi bộ đếm thời gian kết thúc
                // Thực hiện các hành động sau khi kết thúc bộ đếm thời gian
                txtCount.setText("SHOW");
                countDownTimer.cancel();
            }
        };
    }
    public void show(){
        loadingDialog.show();

        countDownTimer.start(); // Bắt đầu bộ đếm thời gian
        Thread adThread = new Thread(){
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(_time* 1000);
                }catch (Exception _ex){
                }finally {
                    dismiss();
//                    IronSource.showInterstitial();
                }
            }
        };
        adThread.start();
    }
    public void dismiss(){
        loadingDialog.dismiss();
    }
}
