package com.example.ahmet.securemailclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.example.ahmet.securemailclient.database.DatabaseManager;

import java.util.List;

public class PatternActivity extends AppCompatActivity {

    private PatternLockView mPatternLockView;
    private TextView title;
    private TextView subTitle;
    private TextView message;
    private DatabaseManager manager;

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
            message.setText("Pull your finger when finished.");
            message.setTextColor(getResources().getColor(android.R.color.white,null));
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            if (PatternLockUtils.patternToMD5(mPatternLockView,pattern).equals(Constants.patternAsMD5)) {
                setResult(RESULT_OK);
                finish();
            }
            else {
                mPatternLockView.clearPattern();
                message.setText("Wrong pattern.");
                message.setTextColor(getResources().getColor(android.R.color.holo_red_light,null));
            }
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);
        title=findViewById(R.id.pattern_title);
        subTitle=findViewById(R.id.pattern_subtitle);
        message=findViewById(R.id.pattern_message);
        manager=new DatabaseManager(SecureClientApplication.getAppContext());
        message.setVisibility(TextView.VISIBLE);
        message.setText("Draw your pattern.");
        message.setTextColor(getResources().getColor(android.R.color.white,null));
        title.setText("Secure Email Client");
        subTitle.setText("You should draw pattern to decrypt your encrypted emails.");
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.colorAccent));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);
    }
}
