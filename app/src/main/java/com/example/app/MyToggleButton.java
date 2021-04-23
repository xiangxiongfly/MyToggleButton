package com.example.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MyToggleButton extends View implements View.OnClickListener {
    private static int MAX_SLIDE_INSTANCE;
    private static final boolean DEFAULT_OPEN = false;

    private Paint paint;
    private Bitmap backgrounBitmap;
    private Bitmap slideBitmap;
    private boolean isOpen = DEFAULT_OPEN;
    private int slideInstance = 0;
    private boolean isClickEnable = false;

    public MyToggleButton(Context context) {
        this(context, null);
    }

    public MyToggleButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgrounBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_background);
        slideBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button);
        MAX_SLIDE_INSTANCE = backgrounBitmap.getWidth() - slideBitmap.getWidth();
        if (isOpen) {
            slideInstance = MAX_SLIDE_INSTANCE;
        } else {
            slideInstance = 0;
        }
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isClickEnable) {
            isOpen = !isOpen;
            refreshView();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(backgrounBitmap.getWidth(), backgrounBitmap.getHeight());
    }

    private int startX;
    private int lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int eventX = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = lastX = eventX;
                isClickEnable = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = eventX - lastX;
                slideInstance += offsetX;
                if (slideInstance < 0) {
                    slideInstance = 0;
                } else if (slideInstance > MAX_SLIDE_INSTANCE) {
                    slideInstance = MAX_SLIDE_INSTANCE;
                }
                invalidate();
                lastX = eventX;

                //如果滑动了，则不响应点击事件
                if (Math.abs(lastX - startX) > 5) {
                    isClickEnable = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isClickEnable) {
                    if (slideInstance > MAX_SLIDE_INSTANCE / 2) {
                        isOpen = true;
                    } else {
                        isOpen = false;
                    }
                    refreshView();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backgrounBitmap, 0, 0, paint);
        canvas.drawBitmap(slideBitmap, slideInstance, 0, paint);
    }

    private void refreshView() {
        if (isOpen) {
            slideInstance = MAX_SLIDE_INSTANCE;
        } else {
            slideInstance = 0;
        }
        invalidate();
    }
}
