package com.marcos.angel.drawing;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class DrawingView extends View{

    private float[] hsv = {(float) 0, 1, (float) 1};
    private float[] hsvo = {0, (float) 1, (float) 0};
    private int vertexNum = 5;
    private double rotoffset = 0;
    private double veloffset =0;
    private int margin = 200;
    private int colorMode;
    private float hue = 0;
    private int width = getContext().getResources().getDisplayMetrics().widthPixels;
    private int height = getContext().getResources().getDisplayMetrics().heightPixels;
    private int radius = (width - margin) / 2;
    private int widthc = width / 2;
    private int heightc = height / 2;
    private int paintWidth=5;

    Paint paint = new Paint();

    ArrayList<Point> vertexes = new ArrayList<>();


    public void start() {
        margin = 10;
        for (int i = 0; i < vertexNum; i++) {
            vertexes.add(new Point());
        }

    }

    public void update() {
        double tmp = (Math.PI * 2) / vertexNum;
        for (int i = 0; i < vertexes.size(); i++) {
            vertexes.get(i).set((int) Math.round(widthc + Math.sin(tmp * i + rotoffset) * radius),
                    (int) Math.round(heightc + Math.cos(tmp * i + rotoffset) * radius));
        }
        rotoffset += veloffset;
        hue += 1;
        if (hue > 360) {
            hue = 0;
        }
    }

    public DrawingView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        start();
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DrawingView, defStyle, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.HSVToColor(255, hsvo));
        canvas.drawRect(0, 0, width, height, paint);
        hsv[2] = 1;
        float color = 0;

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(paintWidth);
        if (colorMode == 0) {
            paint.setColor(Color.WHITE);
        } else if (colorMode == 1) {
            hsv[0] = hue;
            paint.setColor(Color.HSVToColor(255, hsv));
        }
        for (int i = 0; i < vertexes.size(); i++) {
            for (int j = 0; j <= vertexes.size() / 2; j++) {
                if (colorMode == 2) {
                    hsv[0] = color;
                    paint.setColor(Color.HSVToColor(255, hsv));
                }

                if (i + j >= vertexes.size()) {
                    canvas.drawLine(vertexes.get(i).x,
                            vertexes.get(i).y,
                            vertexes.get(i + j - vertexes.size()).x,
                            vertexes.get(i + j - vertexes.size()).y, paint);
                } else {
                    canvas.drawLine(vertexes.get(i).x,
                            vertexes.get(i).y,
                            vertexes.get(i + j).x,
                            vertexes.get(i + j).y, paint);
                }
                color += 360 / vertexNum;
                if (color > 360) {
                    color = 0;
                }
            }
        }
        update();
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        invalidate();

    }

    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousX2;
    private float mPreviousY2;
    private float mPreviousX3;
    private float mPreviousY3;
    private double d1;
    private double d2;


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX(0);
        float y = e.getY(0);

        if(e.getPointerCount()==3){
            float x2 = e.getX(1);
            float y2 = e.getY(1);
            float x3 = e.getX(2);
            float y3 = e.getY(2);
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    mPreviousX = e.getX(0);
                    mPreviousY = e.getY(0);
                    mPreviousX2 = e.getX(1);
                    mPreviousY2 = e.getY(1);
                    mPreviousX3 = e.getX(2);
                    mPreviousY3 = e.getY(2);
                case MotionEvent.ACTION_MOVE:
                    if (y>mPreviousY && y2>mPreviousY2 && y3>mPreviousY3) {
                        if(paintWidth>1) {
                            paintWidth -= 1;
                            update();
                        }
                    } else {
                        paintWidth+=1;
                        update();
                    }

                    break;
            }

        }else if(e.getPointerCount()==2){
            float x2=e.getX(1);
            float y2=e.getY(1);
            double d2=Math.pow((Math.pow(x2-x,2)+(Math.pow((y2-y),2))),0.5);
            switch(e.getActionMasked()){
                case MotionEvent.ACTION_POINTER_DOWN:
                    mPreviousX=e.getX(0);
                    mPreviousY=e.getY(0);
                    mPreviousX2=e.getX(1);
                    mPreviousY2=e.getY(1);
                    d1=Math.pow((Math.pow(mPreviousX2-mPreviousX,2)+(Math.pow((mPreviousY2-mPreviousY),2))),0.5);
                case MotionEvent.ACTION_MOVE:
                    if(d1-d2>0){
                        radius-=15;
                        update();
                    }else{
                        radius+=15;
                        update();
                    }

                    break;
            }
        }else {
            switch (e.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mPreviousX = x;
                    mPreviousY = y;
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;

                case MotionEvent.ACTION_UP:
                    if ((mPreviousY + 150) < y) {
                        if (vertexNum > 2) {
                            vertexNum--;
                            vertexes.remove(vertexes.size() - 1);
                        }
                    } else if ((mPreviousY - 150) > y) {
                        vertexNum++;
                        vertexes.add(new Point());
                        update();
                    } else if (mPreviousX + 150 < x) {
                        veloffset -= 0.05;
                    } else if (mPreviousX - 150 > x) {
                        veloffset += 0.05;
                    } else {
                        colorMode++;
                        if (colorMode > 2)
                            colorMode = 0;
                    }
                    break;
            }
        }
        invalidate();
        return true;

    }
}
