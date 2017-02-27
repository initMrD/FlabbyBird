package com.initmrd.flabbybird.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.initmrd.flabbybird.R;
import com.initmrd.flabbybird.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by initMrd@gmail.com on 17/2/20.
 */

public class FlabbyBird extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private SurfaceHolder mSurfaceHolder;

    private Paint mPaint;

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

//    地板
    private Floor floor;

    private Bitmap mFloor;

    private int mSpeed;

//    管道
    private Bitmap pipeTop;

    private Bitmap pipeBottom;

    private RectF pipeRect;

    private int pipeWidth;

    private static final int PIPE_WIDTH = 60;

    private List<Pipe> pipeList = new ArrayList<Pipe>();

//    分数
    private int[] num = new int[]{R.drawable.n0,R.drawable.n1,R.drawable.n2,R.drawable.n3,R.drawable.n4,
            R.drawable.n5,R.drawable.n6,R.drawable.n7,R.drawable.n8,R.drawable.n9};

    private int mGrade = 100;

    private Bitmap[] mNumBitmap;

    private static final float NUM_HEIGHT = 1/15F;

    private int numWidth,numHeight;

    private RectF numRect;

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

        //设置画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        initBitmap();

        //初始化速度
        mSpeed = Util.dp2px(getContext(), 2);

        //初始化管道宽度
        pipeWidth = Util.dp2px(getContext(), PIPE_WIDTH);
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
        mRectF.set(0, 0, w, h);

        bird = new Bird(getContext(), w, h, mBird);

        floor = new Floor(mWidth, mHeight, mFloor);

        pipeRect = new RectF(0, 0, pipeWidth, mHeight);
        Pipe pipe = new Pipe(getContext(), w, h, pipeTop, pipeBottom);
        pipeList.add(pipe);

        numHeight = (int) (h * NUM_HEIGHT);
        numWidth = (int) (numHeight * 1.0f / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        numRect = new RectF( 0, 0, numWidth, numHeight);
    }

    private void drawBackground(){
        mCanvas.drawBitmap(mBackground,null,mRectF,null);
    }

    private void drawBird(){
        bird.draw(mCanvas);
    }

    private void drawFloor(){
        floor.draw(mCanvas,mPaint);
    }

    private void drawPipe(){
        for(Pipe pipe: pipeList){
            pipe.setX(pipe.getX() - mSpeed);
            pipe.draw(mCanvas,pipeRect);
        }
    }

    private void drawNum(){
        String grade = mGrade + "";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth / 2 - grade.length() * numWidth / 2, 1F / 8 * mHeight );

        for(int i = 0; i < grade.length(); i++){
            String numStr = grade.substring( i, i+1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num], null, numRect, null);
            mCanvas.translate(numWidth, 0);
        }
        mCanvas.restore();
    }

    private void initBitmap(){
        mBackground = loadImageByResId(R.drawable.background);
        mBird = loadImageByResId(R.drawable.bird);
        mFloor = loadImageByResId(R.drawable.floor_bg2);
        pipeTop = loadImageByResId(R.drawable.waterpipe2);
        pipeBottom = loadImageByResId(R.drawable.waterpipe1);

        mNumBitmap = new Bitmap[num.length];
        for (int i = 0; i<num.length ;i++){
            mNumBitmap[i] = loadImageByResId(num[i]);
        }
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
                drawPipe();
                drawFloor();
                drawNum();

                floor.setX(floor.getX() - mSpeed);
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
