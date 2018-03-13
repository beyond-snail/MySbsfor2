package com.uuzuche.lib_zxing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

import com.uuzuche.lib_zxing.R;

/**
 * Initial the camera
 * <p>
 * 默认的二维码扫描Activity
 */
public class CaptureActivity extends FragmentActivity {

    private int titleType = 0; //是否显示title


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera);

        ZXingLibrary.initDisplayOpinion(this);


        titleType = getIntent().getIntExtra("title", 0);
        CaptureFragment captureFragment = new CaptureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", ""+titleType);
        captureFragment.setArguments(bundle);
        captureFragment.setOnListener(new CaptureFragment.onListener() {

            @Override
            public void doSomeThing(String messgae, int type) {
                Log.e("TAG", "doSomeThing: "+type);
                if (type == 1){
                    CaptureActivity.this.finish();
                }else if (type == 2){
                    Intent intent = new Intent();
                    CaptureActivity.this.setResult(2, intent);
                    CaptureActivity.this.finish();
                }
            }
        });
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();
        captureFragment.setCameraInitCallBack(new CaptureFragment.CameraInitCallBack() {
            @Override
            public void callBack(Exception e) {
                if (e == null) {

                } else {
                    Log.e("TAG", "callBack: ", e);
                }
            }
        });

    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            resultIntent.putExtra(CodeUtils.RESULT_STRING, result);
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            resultIntent.putExtra(CodeUtils.RESULT_STRING, "");
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();
        }
    };
}