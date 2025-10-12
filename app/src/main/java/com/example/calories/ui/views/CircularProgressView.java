package com.example.calories.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.core.content.ContextCompat;

import com.example.calories.R;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rectF;
    private float progress = 0f;
    private float animatedProgress = 0f;

    private static final float START_ANGLE = 140f; // זווית התחלה (מצד שמאל למטה)
    private static final float SWEEP_ANGLE = 260f; // כמה מעלות הקשת תכסה (קשת במקום עיגול מלא)

    public CircularProgressView(Context context) {
        super(context);
        init(context);
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // צבע רקע (אפור בהיר)
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE8F4FF);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(30f);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        // צבע התקדמות (כחול)
        progressPaint = new Paint();
        int color = ContextCompat.getColor(context, R.color.tertiary);
        progressPaint.setColor(color);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(30f);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 40;

        int centerX = width / 2;
        int centerY = height / 2;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // ציור קשת הרקע
        canvas.drawArc(rectF, START_ANGLE, SWEEP_ANGLE, false, backgroundPaint);

        // ציור קשת התקדמות
        float progressSweepAngle = SWEEP_ANGLE * animatedProgress;
        canvas.drawArc(rectF, START_ANGLE, progressSweepAngle, false, progressPaint);
    }

    public void setProgress(float progress) {
        this.progress = Math.min(1.0f, Math.max(0.0f, progress));
        animateToProgress();
    }

    private void animateToProgress() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, progress);
        animator.setDuration(2000); // אנימציה של 2 שניות
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedProgress = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animator.start();
    }

    public float getProgress() {
        return progress;
    }

    public float getAnimatedProgress() {
        return animatedProgress;
    }

}
/*
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rectF;
    private float progress = 0f;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // צבע רקע (אפור בהיר)
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE8F4FF);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(20f);
        backgroundPaint.setAntiAlias(true);

        // צבע התקדמות (כחול)
        progressPaint = new Paint();
        progressPaint.setColor(0xFF2196F3);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20f);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 40;

        int centerX = width / 2;
        int centerY = height / 2;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // ציור המעגל הרקע
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // ציור התקדמות
        float sweepAngle = 360 * progress;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
    }

    public void setProgress(float progress) {
        this.progress = Math.min(1.0f, Math.max(0.0f, progress));
        invalidate();
    }

    public float getProgress() {
        return progress;
    }
}

 */