package com.initmrd.flabbybird.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.initmrd.flabbybird.R;

/**
 * Created by initMrd@gmail.com on 17/2/20.
 */

public class FlabbyBird extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private SurfaceHolder mSurfaceHolder;

    private Canvas mCanvas;

    private Thread mThread;

    private boolean isRunning;

    private int mWidth;

    private int mHeight;

    private RectF mRectF = new RectF();

    private Bitmap mBackground;

//    小鸟
    private Bird bird;

    private Bitmap mBird;


    public FlabbyBird(Context context){
        this(context ,null);
    }

    public FlabbyBird(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        //设置画布背景透明
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        //获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);

        //设置屏幕常亮
        this.setKeepScreenOn(true);

        initBitmap();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning){
            long start = System.currentTimeMillis();
            draw();
            long stop = System.currentTimeMillis();

            if (stop-start<50){
                try {
                    Thread.sleep(50-(stop-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        mRectF.set(0,0,w,h);
        bird = new Bird(getContext(), w, h, mBird);
    }

    private void drawBackground(){
        mCanvas.drawBitmap(mBackground,null,mRectF,null);
    }

    private void drawBird(){
        bird.draw(mCanvas);
    }

    private void initBitmap(){
        mBackground = loadImageByResId(R.drawable.background);
        mBird = loadImageByResId(R.drawable.bird);
    }

    //根据id绘制画布
    private Bitmap loadImageByResId(int resId)
    {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    private void draw(){
        try {
            //绘制时锁定画布
            mCanvas = mSurfaceHolder.lockCanvas();
            if(mCanvas !=null){
                drawBackground();
                drawBird();

            }
        }catch (Exception e){

        }finally {
            if(mCanvas !=null){
                //绘制完成,解锁画布
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
