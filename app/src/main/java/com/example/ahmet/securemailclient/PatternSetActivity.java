package com.example.ahmet.securemailclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;

import java.util.List;

public class PatternSetActivity extends AppCompatActivity {

    private PatternLockView mPatternLockView;
    private TextView title;
    private TextView subTitle;
    private TextView message;
    private Button cancel;
    private Button ok;

    private boolean isSecond=false;
    private String patternAsString="";

    private final String CANCEL="CANCEL";
    private final String TRY_AGAIN="TRY AGAIN";
    private final String CONTINUE="CONTINUE";
    private final String CONFIRM="CONFIRM";

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
            message.setTextColor(getResources().getColor(android.R.color.white,null));
            message.setText("Pull your finger when finished.");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            String stringPattern=PatternLockUtils.patternToString(mPatternLockView,pattern);
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            mPatternLockView.setInputEnabled(false);
            cancel.setEnabled(true);
            if (!isSecond) {
                if (stringPattern.length()<4) {
                    message.setText("You should draw with minimum 4 pattern dots.");
                    message.setTextColor(getResources().getColor(android.R.color.holo_red_light,null));
                    return;
                }
            } else {
                if (!patternAsString.equals(PatternLockUtils.patternToString(mPatternLockView,pattern))) {
                    message.setText("Patterns aren't equal.");
                    message.setTextColor(getResources().getColor(android.R.color.holo_red_light,null));
                    return;
                }
            }

            ok.setEnabled(true);
            if (!isSecond) {
                patternAsString=PatternLockUtils.patternToString(mPatternLockView,pattern);
                message.setText("Saved your pattern.");
            } else {
                message.setText("Your unlock pattern is set as follows.");
            }
            message.setTextColor(getResources().getColor(android.R.color.holo_green_light,null));
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    private void refresh() {
        mPatternLockView.setInputEnabled(true);
        if (!isSecond) {
            message.setText("Draw your pattern.");
            message.setTextColor(getResources().getColor(android.R.color.white,null));
            ok.setText(CONTINUE);
        } else {
            message.setText("Draw your pattern to confirm.");
            message.setTextColor(getResources().getColor(android.R.color.white,null));
            ok.setText(CONFIRM);
        }
        cancel.setText(TRY_AGAIN);
        ok.setEnabled(false);
        cancel.setEnabled(false);
        mPatternLockView.clearPattern();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_set);
        title=findViewById(R.id.set_pattern_title);
        subTitle=findViewById(R.id.set_pattern_subtitle);
        message=findViewById(R.id.set_pattern_message);
        cancel=findViewById(R.id.set_pattern_cancel_button);
        ok=findViewById(R.id.set_pattern_ok_button);
        title.setText("Secure Email Client");
        subTitle.setText("You should draw pattern to decrypt your encrypted emails.");
        message.setVisibility(TextView.VISIBLE);
        message.setText("Draw your pattern.");
        message.setTextColor(getResources().getColor(android.R.color.white,null));
        ok.setText(CONTINUE);
        cancel.setText(TRY_AGAIN);
        ok.setEnabled(false);
        cancel.setEnabled(false);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ok.getText().toString().equals(CONTINUE)) {
                    isSecond=true;
                    refresh();
                } else {
                    Intent intent=new Intent();
                    intent.putExtra("pattern",PatternLockUtils.patternToMD5(mPatternLockView,PatternLockUtils.stringToPattern(mPatternLockView,patternAsString)));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
        mPatternLockView = (PatternLockView) findViewById(R.id.set_pattern_lock_view);
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
