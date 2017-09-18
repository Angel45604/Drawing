package com.marcos.angel.drawing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.enrico.colorpicker.colorDialog;
import java.util.ArrayList;

/**
 * Created by angel on 16/09/2017.
 */

public class polygonDraw extends WallpaperService {
    private boolean mVisible;
    Canvas canvas = new Canvas();
    int drawSpeed=10;
    Context mContext;

    int backgroundColor = Color.parseColor("#FF000000");
    int linecolor = Color.parseColor("#FFFFFFFF");

    int width=100000;
    int height=100;
    private float[] hsv = {(float) 0, 1, (float) 1};
    private float[] hsvo = {0, (float) 1, (float) 0};
    private int vertexNum = 5;
    private double rotoffset = 0;
    private double veloffset =0;
    private int margin = 200;
    private int colorMode;
    private float hue = 0;
    private float paintWidth=5;
    //private int width = getContext().getResources().getDisplayMetrics().widthPixels;
    //private int height = getContext().getResources().getDisplayMetrics().heightPixels;
    private int radius = (width - margin) / 2;
    private int widthc = width / 2;
    private int heightc = height / 2;

    Paint paint = new Paint();
    Paint paintText = new Paint();

    ArrayList<Point> vertexes = new ArrayList<>();



    @Override
    public Engine onCreateEngine() {
        mContext=this;
        return new LiveWall();
    }

    public class LiveWall extends Engine{
        final Handler mHandler = new Handler();

        private final Runnable mDrawFrame = new Runnable(){
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            vertexNum = Integer.parseInt(sharedPreferences.getString("VertexNumber", "5"));
            veloffset = Double.parseDouble(sharedPreferences.getString("velocity","0"));
            colorMode = Integer.parseInt(sharedPreferences.getString("colorMode","0"));
            paintWidth = Float.parseFloat(sharedPreferences.getString("paintWidth","5"));
            radius = Integer.parseInt(sharedPreferences.getString("radius",""+radius));
            backgroundColor = colorDialog.getPickerColor(getBaseContext(),1);
            linecolor = colorDialog.getPickerColor(getBaseContext(),2);
            start();

            width=mContext.getResources().getDisplayMetrics().widthPixels;
            height = mContext.getResources().getDisplayMetrics().heightPixels;
            widthc = width / 2;
            heightc = height / 2;

            radius = (width - margin) / 2;
            mHandler.post(mDrawFrame);
        }


        @Override
        public boolean isPreview(){
            return super.isPreview();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder){
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onDestroy(){
            super.onDestroy();
            mHandler.removeCallbacks(mDrawFrame);
        }

        @Override
        public void onVisibilityChanged(boolean visible){
            mVisible=visible;

            if(visible){
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                vertexNum = Integer.parseInt(sharedPreferences.getString("VertexNumber", "5"));
                veloffset = Double.parseDouble(sharedPreferences.getString("velocity","0"));
                colorMode = Integer.parseInt(sharedPreferences.getString("colorMode","0"));
                paintWidth = Float.parseFloat(sharedPreferences.getString("paintWidth","5"));
                radius = Integer.parseInt(sharedPreferences.getString("radius",""+radius));
                backgroundColor = colorDialog.getPickerColor(getBaseContext(),1);
                linecolor = colorDialog.getPickerColor(getBaseContext(),2);
                mHandler.post(mDrawFrame);
            }else{
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                vertexNum = Integer.parseInt(sharedPreferences.getString("VertexNumber", "5"));
                veloffset = Double.parseDouble(sharedPreferences.getString("velocity","0"));
                colorMode = Integer.parseInt(sharedPreferences.getString("colorMode","0"));
                paintWidth = Float.parseFloat(sharedPreferences.getString("paintWidth","5"));
                radius = Integer.parseInt(sharedPreferences.getString("radius",""+radius));
                backgroundColor = colorDialog.getPickerColor(getBaseContext(),1);
                linecolor = colorDialog.getPickerColor(getBaseContext(),2);
                mHandler.removeCallbacks(mDrawFrame);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder){
            super.onSurfaceDestroyed(holder);
            mVisible=false;
            mHandler.removeCallbacks(mDrawFrame);


        }

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

        public void drawFrame(){
            final SurfaceHolder holder = getSurfaceHolder();

            canvas = null;

            try{
                canvas = holder.lockCanvas();
                if(canvas!=null){
                    canvasDraw();
                }
            }finally {
                if(canvas!=null){
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            mHandler.removeCallbacks(mDrawFrame);
            if(mVisible){
                mHandler.postDelayed(mDrawFrame,10);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height){
            super.onSurfaceChanged(holder,format,width,height);
        }

        void canvasDraw(){
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.HSVToColor(255, hsvo));
            canvas.drawRect(0, 0, width, height, paint);
            hsv[2] = 1;
            float color = 0;

            paintText.setColor(Color.WHITE);
            paintText.setTextSize(20);
            canvas.drawText("Lados: "+vertexNum, 10, 25, paintText);
            canvas.drawText("Velocidad: "+veloffset,10,50,paintText);

            paint.setStyle(Paint.Style.STROKE);

            paint.setStrokeWidth((float)paintWidth);
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
        }


    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousX2;
        private float mPreviousY2;
        private float mPreviousX3;
        private float mPreviousY3;
    private double d1;
        private double dy1;
    private double d2;


    @Override
    public void onTouchEvent(MotionEvent e) {
        if(isPreview()) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sharedPreferences.edit();

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
                                editor.putString("paintWidth", "" + paintWidth);
                                editor.commit();
                            }
                        } else {
                            paintWidth+=1;
                            update();
                            editor.putString("paintWidth", "" + paintWidth);
                            editor.commit();
                        }

                        break;
                }

            }else if (e.getPointerCount() == 2) {
                float x2 = e.getX(1);
                float y2 = e.getY(1);
                double d2 = Math.pow((Math.pow(x2 - x, 2) + (Math.pow((y2 - y), 2))), 0.5);
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mPreviousX = e.getX(0);
                        mPreviousY = e.getY(0);
                        mPreviousX2 = e.getX(1);
                        mPreviousY2 = e.getY(1);
                        d1 = Math.pow((Math.pow(mPreviousX2 - mPreviousX, 2) + (Math.pow((mPreviousY2 - mPreviousY), 2))), 0.5);
                    case MotionEvent.ACTION_MOVE:
                        if (d1 - d2 > 0) {
                            radius -= 15;
                            update();
                            editor.putString("radius", "" + radius);
                            editor.commit();
                        } else {
                            radius += 15;
                            update();
                            editor.putString("radius", "" + radius);
                            editor.commit();
                        }

                        break;
                }
            } else {
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
                                editor.putString("VertexNumber", "" + vertexNum);
                                editor.commit();
                            }
                        } else if ((mPreviousY - 150) > y) {
                            vertexNum++;
                            vertexes.add(new Point());
                            editor.putString("VertexNumber", "" + vertexNum);
                            editor.commit();
                            update();
                        } else if (mPreviousX + 150 < x) {
                            veloffset -= 0.05;
                            editor.putString("velocity", "" + veloffset);
                            editor.commit();
                        } else if (mPreviousX - 150 > x) {
                            veloffset += 0.05;
                            editor.putString("velocity", "" + veloffset);
                            editor.commit();
                        } else {
                            colorMode++;
                            if (colorMode > 2) {
                                colorMode = 0;
                            }
                            if(colorMode>0) {
                                editor.putString("colorMode", "" + (colorMode - 1));
                                editor.commit();
                            }else if(colorMode==0){
                                editor.putString("colorMode",""+colorMode+2);
                                editor.commit();
                            }

                        }
                        break;
                }
            }
            super.onTouchEvent(e);
        }
    }
    }
}
