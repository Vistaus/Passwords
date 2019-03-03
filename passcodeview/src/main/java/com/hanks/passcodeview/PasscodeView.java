package com.hanks.passcodeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hanks.passcodeview.PasscodeView.PasscodeViewType.TYPE_CHECK_PASSCODE;
import static com.hanks.passcodeview.PasscodeView.PasscodeViewType.TYPE_SET_PASSCODE;


/**
 * PasscodeView
 * Created by hanks on 2017/4/11.
 */

public class PasscodeView extends FrameLayout implements View.OnClickListener {
    private boolean secondInput;
    private String localPasscode = "";
    private PasscodeViewListener listener;
    private ViewGroup layout_psd;
    private TextView tv_input_tip;
    private TextView number0, number1, number2, number3, number4, number5, number6, number7, number8, number9;
    private ImageView numberB, numberOK;
    private ImageView iv_lock, iv_ok;
    private View cursor;

    private String firstInputTip = "Enter a passcode of 4 digits";
    private String secondInputTip = "Re-enter new passcode";
    private String wrongLengthTip = "Enter a passcode of 4 digits";
    private String wrongInputTip = "Passcode do not match";
    private String correctInputTip = "Passcode is correct";

    private int passcodeLength = 4;
    private int correctStatusColor = 0xFF61C560; //0xFFFF0000
    private int wrongStatusColor = 0xFFF24055;
    private int normalStatusColor = 0xFFFFFFFF;
    private int numberTextColor = 0xFF747474;
    private int passcodeType = TYPE_SET_PASSCODE;

    public PasscodeView(@NonNull Context context) {
        this(context, null);
    }

    public PasscodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(getContext(), R.layout.layout_passcode_view, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasscodeView);
        try {
            passcodeType = typedArray.getInt(R.styleable.PasscodeView_passcodeViewType, passcodeType);
            passcodeLength = typedArray.getInt(R.styleable.PasscodeView_passcodeLength, passcodeLength);
            normalStatusColor = typedArray.getColor(R.styleable.PasscodeView_normalStateColor, normalStatusColor);
            wrongStatusColor = typedArray.getColor(R.styleable.PasscodeView_wrongStateColor, wrongStatusColor);
            correctStatusColor = typedArray.getColor(R.styleable.PasscodeView_correctStateColor, correctStatusColor);
            numberTextColor = typedArray.getColor(R.styleable.PasscodeView_numberTextColor, numberTextColor);
            firstInputTip = typedArray.getString(R.styleable.PasscodeView_firstInputTip);
            secondInputTip = typedArray.getString(R.styleable.PasscodeView_secondInputTip);
            wrongLengthTip = typedArray.getString(R.styleable.PasscodeView_wrongLengthTip);
            wrongInputTip = typedArray.getString(R.styleable.PasscodeView_wrongInputTip);
            correctInputTip = typedArray.getString(R.styleable.PasscodeView_correctInputTip);
        } finally {
            typedArray.recycle();
        }

        firstInputTip = firstInputTip == null ? "Enter a passcode of 4 digits" : firstInputTip;
        secondInputTip = secondInputTip == null ? "Re-enter new passcode" : secondInputTip;
        wrongLengthTip = wrongLengthTip == null ? firstInputTip : wrongLengthTip;
        wrongInputTip = wrongInputTip == null ? "Passcode do not match" : wrongInputTip;
        correctInputTip = correctInputTip == null ? "Passcode is correct" : correctInputTip;

        init();
    }


    private void init() {
        layout_psd = findViewById(R.id.layout_psd);
        tv_input_tip = findViewById(R.id.tv_input_tip);
        cursor = findViewById(R.id.cursor);
        iv_lock = findViewById(R.id.iv_lock);
        iv_ok = findViewById(R.id.iv_ok);

        tv_input_tip.setText(firstInputTip);

        number0 = findViewById(R.id.number0);
        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        number3 = findViewById(R.id.number3);
        number4 = findViewById(R.id.number4);
        number5 = findViewById(R.id.number5);
        number6 = findViewById(R.id.number6);
        number7 = findViewById(R.id.number7);
        number8 = findViewById(R.id.number8);
        number9 = findViewById(R.id.number9);
        numberOK = findViewById(R.id.numberOK);
        numberB = findViewById(R.id.numberB);

        number0.setOnClickListener(this);
        number1.setOnClickListener(this);
        number2.setOnClickListener(this);
        number3.setOnClickListener(this);
        number4.setOnClickListener(this);
        number5.setOnClickListener(this);
        number6.setOnClickListener(this);
        number7.setOnClickListener(this);
        number8.setOnClickListener(this);
        number9.setOnClickListener(this);

        numberB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChar();
            }
        });
        numberOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        tintImageView(iv_lock, numberTextColor);
        tintImageView(numberB, numberTextColor);
        tintImageView(numberOK, numberTextColor);
        tintImageView(iv_ok, correctStatusColor);

        number0.setTag(0);
        number1.setTag(1);
        number2.setTag(2);
        number3.setTag(3);
        number4.setTag(4);
        number5.setTag(5);
        number6.setTag(6);
        number7.setTag(7);
        number8.setTag(8);
        number9.setTag(9);
        number0.setTextColor(numberTextColor);
        number1.setTextColor(numberTextColor);
        number2.setTextColor(numberTextColor);
        number3.setTextColor(numberTextColor);
        number4.setTextColor(numberTextColor);
        number5.setTextColor(numberTextColor);
        number6.setTextColor(numberTextColor);
        number7.setTextColor(numberTextColor);
        number8.setTextColor(numberTextColor);
        number9.setTextColor(numberTextColor);

    }

    @Override
    public void onClick(View view) {
        int number = (int) view.getTag();
        addChar(number);
    }

    public String getLocalPasscode() {
        return localPasscode;
    }

    public PasscodeViewListener getListener() {
        return listener;
    }

    public String getFirstInputTip() {
        return firstInputTip;
    }

    public PasscodeView setFirstInputTip(String firstInputTip) {
        this.firstInputTip = firstInputTip;
        return this;
    }

    public String getSecondInputTip() {
        return secondInputTip;
    }

    public PasscodeView setSecondInputTip(String secondInputTip) {
        this.secondInputTip = secondInputTip;
        return this;
    }

    public String getWrongLengthTip() {
        return wrongLengthTip;
    }

    public PasscodeView setWrongLengthTip(String wrongLengthTip) {
        this.wrongLengthTip = wrongLengthTip;
        return this;
    }

    public String getWrongInputTip() {
        return wrongInputTip;
    }

    public PasscodeView setWrongInputTip(String wrongInputTip) {
        this.wrongInputTip = wrongInputTip;
        return this;
    }

    public String getCorrectInputTip() {
        return correctInputTip;
    }

    public PasscodeView setCorrectInputTip(String correctInputTip) {
        this.correctInputTip = correctInputTip;
        return this;
    }

    public int getPasscodeLength() {
        return passcodeLength;
    }

    public PasscodeView setPasscodeLength(int passcodeLength) {
        this.passcodeLength = passcodeLength;
        return this;
    }

    public int getCorrectStatusColor() {
        return correctStatusColor;
    }

    public PasscodeView setCorrectStatusColor(int correctStatusColor) {
        this.correctStatusColor = correctStatusColor;
        return this;
    }

    public int getWrongStatusColor() {
        return wrongStatusColor;
    }

    public PasscodeView setWrongStatusColor(int wrongStatusColor) {
        this.wrongStatusColor = wrongStatusColor;
        return this;
    }

    public int getNormalStatusColor() {
        return normalStatusColor;
    }

    public PasscodeView setNormalStatusColor(int normalStatusColor) {
        this.normalStatusColor = normalStatusColor;
        return this;
    }

    public int getNumberTextColor() {
        return numberTextColor;
    }

    public PasscodeView setNumberTextColor(int numberTextColor) {
        this.numberTextColor = numberTextColor;
        return this;
    }

    public @PasscodeViewType int getPasscodeType() {
        return passcodeType;
    }

    public PasscodeView setPasscodeType(@PasscodeViewType int passcodeType) {
        this.passcodeType = passcodeType;
        return this;
    }

    /**
     * set  localPasscode
     *
     * @param localPasscode the code will to check
     */
    public PasscodeView setLocalPasscode(String localPasscode) {
        for (int i = 0; i < localPasscode.length(); i++) {
            char c = localPasscode.charAt(i);
            if (c < '0' || c > '9') {
                throw new RuntimeException("must be number digit");
            }
        }
        this.localPasscode = localPasscode;
        this.passcodeType = TYPE_CHECK_PASSCODE;
        return this;
    }

    private void next() {
        if (passcodeType == TYPE_CHECK_PASSCODE && TextUtils.isEmpty(localPasscode)) {
            throw new RuntimeException("must set localPasscode when type is TYPE_CHECK_PASSCODE");
        }

        String psd = getPasscodeFromView();
        if (psd.length() != passcodeLength) {
            tv_input_tip.setText(wrongLengthTip);
            runTipTextAnimation();
            return;
        }

        if (passcodeType == TYPE_SET_PASSCODE && !secondInput) {
            // second input
            tv_input_tip.setText(secondInputTip);
            localPasscode = psd;
            clearChar();
            secondInput = true;
            return;
        }

        if (localPasscode.equals(psd)) {
            // match
            runOkAnimation();
        } else {
            runWrongAnimation();
        }
    }

    private void addChar(int number) {
        if (layout_psd.getChildCount() >= passcodeLength) {
            return;
        }
        CircleView psdView = new CircleView(getContext());
        int size = dpToPx(8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(size, 0, size, 0);
        psdView.setLayoutParams(params);
        psdView.setColor(normalStatusColor);
        psdView.setTag(number);
        layout_psd.addView(psdView);
    }

    private int dpToPx(float valueInDp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void tintImageView(ImageView imageView, int color) {
        imageView.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void clearChar() {
        layout_psd.removeAllViews();
    }

    private void deleteChar() {
        int childCount = layout_psd.getChildCount();
        if (childCount <= 0) {
            return;
        }
        layout_psd.removeViewAt(childCount - 1);
    }

    public void runTipTextAnimation() {
        shakeAnimator(tv_input_tip).start();
    }

    public void runWrongAnimation() {
        cursor.setTranslationX(0);
        cursor.setVisibility(VISIBLE);
        cursor.animate()
                .translationX(layout_psd.getWidth())
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        cursor.setVisibility(INVISIBLE);
                        tv_input_tip.setText(wrongInputTip);
                        setPSDViewBackgroundResource(wrongStatusColor);
                        Animator animator = shakeAnimator(layout_psd);
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setPSDViewBackgroundResource(normalStatusColor);
                                if (listener != null) {
                                    listener.onFail();
                                }
                            }
                        });
                        animator.start();
                    }
                })
                .start();
    }

    private Animator shakeAnimator(View view) {
        return ObjectAnimator
                .ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                .setDuration(500);
    }

    private void setPSDViewBackgroundResource(int color) {
        int childCount = layout_psd.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((CircleView) layout_psd.getChildAt(i)).setColor(color);
        }
    }

    public void runOkAnimation() {
        cursor.setTranslationX(0);
        cursor.setVisibility(VISIBLE);
        cursor.animate()
                .setDuration(600)
                .translationX(layout_psd.getWidth())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        cursor.setVisibility(INVISIBLE);
                        setPSDViewBackgroundResource(correctStatusColor);
                        tv_input_tip.setText(correctInputTip);
                        iv_lock.animate().alpha(0).scaleX(0).scaleY(0).setDuration(500).start();
                        iv_ok.animate().alpha(1).scaleX(1).scaleY(1).setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        if (listener != null) {
                                            listener.onSuccess(getPasscodeFromView());
                                        }
                                    }
                                }).start();
                    }
                })
                .start();

    }

    private String getPasscodeFromView() {
        StringBuilder sb = new StringBuilder();
        int childCount = layout_psd.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = layout_psd.getChildAt(i);
            int num = (int) child.getTag();
            sb.append(num);
        }
        return sb.toString();
    }

    public PasscodeView setListener(PasscodeViewListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * The type for this passcodeView
     */
    @IntDef({TYPE_SET_PASSCODE, TYPE_CHECK_PASSCODE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PasscodeViewType {

        /**
         * set passcode, with twice input
         */
        int TYPE_SET_PASSCODE = 0;

        /**
         * check passcode, must pass the result as parameter {@link PasscodeView#setLocalPasscode(java.lang.String)}
         */
        int TYPE_CHECK_PASSCODE = 1;
    }

    public interface PasscodeViewListener {

        void onFail();

        void onSuccess(String number);
    }
}
