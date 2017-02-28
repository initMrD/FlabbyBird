package com.initmrd.flabbybird.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.initmrd.flabbybird.R;
import com.initmrd.flabbybird.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by initMrd@gmail.com on 17/2/20.
 */

public class FlabbyBird extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private static final String TAG = "FlabbyBird";

    private SurfaceHolder mSurfaceHolder;

    private Paint mPaint;

    private Canvas mCanvas;

    private Thread mThread;

    private boolean isRunning;

    private int mWidth;

    private int mHeight;

    private RectF mRectF = new RectF();

    private Bitmap mBackground;

//    ===============小鸟部分===============
    private Bird bird;

    private Bitmap mBird;

//    ===============地板部分===============
    private Floor floor;

    private Bitmap mFloor;

    private int mSpeed;

//    ===============管道部分===============

//    上管子
    private Bitmap pipeTop;

//    下管子
    private Bitmap pipeBottom;

//    管子图像
    private RectF pipeRect;

//    管子宽度的Dp
    private int pipeWidth;

//    管子宽度
    private static final int PIPE_WIDTH = 60;


//    管道列表
    private List<Pipe> pipeList = new ArrayList<Pipe>();

//    移除的管道列表
    private List<Pipe> removePipeList = new ArrayList<Pipe>();

//    两个管子的距离
    private final static int PIPE_BETWEEN_PIPE = 600;

//    缓存的移动距离
    private int tempMove;

//    ===============分数部分===============
    private int[] num = new int[]{R.drawable.n0,R.drawable.n1,R.drawable.n2,R.drawable.n3,R.drawable.n4,
            R.drawable.n5,R.drawable.n6,R.drawable.n7,R.drawable.n8,R.drawable.n9};

    private int mGrade = 0;

    private Bitmap[] mNumBitmap;

    private static final float NUM_HEIGHT = 1/15F;

    private int numWidth,numHeight;

    private RectF numRect;

//    ===============录音相关===============
    private MediaRecorder mediaRecorder;

    private static final int JUMP = 600;

    //状态类型
    private enum GameStatus{
        WAITING, RUNNING, STOP
    }

    //当前状态
    private GameStatus mStatus = GameStatus.WAITING;

    //鸟点击之后上升的距离
    private static final int TOUCH_UP_SIZE = -16;

    //将上升的距离转化为dp
    private final int birdUpDp = Util.dp2px(getContext(), TOUCH_UP_SIZE);

    private int tmpBird;

    //自动下落速度
    private final int autoDownSpeed = Util.dp2px(getContext(), 2);

    //逻辑部分
    private void logic(){
        switch (mStatus){
            case RUNNING:
                //开始计数
                mGrade = 0;
                //创建管道
                tempMove += mSpeed;
                //当画面每移动PIPE_BETWEEN_PIPE就生成一个管子
                if(tempMove >= PIPE_BETWEEN_PIPE){
                    Pipe pipe = new Pipe(getContext(), getWidth(), getHeight(), pipeTop, pipeBottom);
                    pipeList.add(pipe);
                    tempMove = 0;
                }
                //每移除的一根管子记一分
                mGrade += removePipeList.size();

                //管道处理
                for(Pipe pipe: pipeList){
                    //穿过管子加一分
                    if(pipe.getX() + pipeWidth < bird.getX()){
                        mGrade++;
                    }
                    //标记待移除管道
                    if(pipe.getX() < -pipeWidth){
                        removePipeList.add(pipe);
                        continue;
                    }
                    //移动管道
                    pipe.setX(pipe.getX() - mSpeed);
                }

                //移除管道
                pipeList.removeAll(removePipeList);

//                Log.d(TAG, "剩余管道数量" + pipeList.size());

                //移动地板
                floor.setX(floor.getX() - mSpeed);

                //鸟的移动
                //默认下落 点击上升
                tmpBird += autoDownSpeed;
                bird.setY(bird.getY() + tmpBird);

                //判断游戏结束
                checkGameOver();

                break;
            case STOP:
                //如果鸟撞了管子
                if(mediaRecorder !=null){
                    stopRecode();
                }
                if(bird.getY() < floor.getY() - bird.getHeight()){
                    //让鸟进行自由落体
                    tmpBird += autoDownSpeed;
                    bird.setY(bird.getY() + tmpBird);
                }else {
                    //初始化状态
                    mStatus = GameStatus.WAITING;
                    initPosition();
                }
                break;
            default:
                break;
        }
    }

    private void initPosition(){
        pipeList.clear();
        removePipeList.clear();
        bird.setY(mHeight * 1 / 3);
        tmpBird = 0;
    }

    private void stopRecode(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }


    private void checkGameOver(){
        //地板判断
        if(bird.getY() > floor.getY() - bird.getHeight()){
            mStatus = GameStatus.STOP;
        }
        //管子判断
        for(Pipe pipe:pipeList){
            //已经穿过的管子
            if(pipe.getX() + pipeWidth < bird.getX()){
                //跳过
                continue;
            }
            //没穿过的管子,判断时候碰到
            if(pipe.touchBird(bird)){
                mStatus = GameStatus.STOP;
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN){
            switch (mStatus){
                case RUNNING:
                    tmpBird = birdUpDp;
                    break;
                case WAITING:
                    try {
                        mediaRecorder = new MediaRecorder();
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FlabbyBird" + System.currentTimeMillis() + ".3gp";
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                        mediaRecorder.setOutputFile(filePath);
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mStatus = GameStatus.RUNNING;
                    break;
                default:
                    break;
            }
        }

        return true;
    }

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
            if(mediaRecorder != null){
//                Log.d(TAG,(mediaRecorder.getMaxAmplitude() > 20) +"");
                int tempsound = mediaRecorder.getMaxAmplitude();
                if(tempsound > JUMP){
                    Log.d(TAG,tempsound +"");
                    tmpBird = birdUpDp;
                }
            }
            logic();
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
//            pipe.setX(pipe.getX() - mSpeed);
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

//                floor.setX(floor.getX() - mSpeed);
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
