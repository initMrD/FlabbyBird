package com.initmrd.flabbybird.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by initMrd@gmail.com on 17/2/23.
 */

public class Pipe {

    //上下管距离
    private static final float BETWEEN_UP_DOWN = 1/5F;

    //上管子的最大高度
    private static final float MAX_UP_PIPE_HEIGHT = 2/5F;

    //上管子的最小高度
    private static final float MIN_UP_PIPE_HEIGHT = 1/5F;

    //管道的X坐标,上管道的高度,上下管距离的实际高度
    private int x,upHeight,margin;

    //上管道图片,下管道图片
    private Bitmap upPipe,downPipe;

    private static Random random = new Random();

    public Pipe(Context context, int width, int height, Bitmap upPipe, Bitmap downPipe) {

        this.upPipe = upPipe;
        this.downPipe = downPipe;

        //上下管距离的实际高度
        margin = (int) (height * BETWEEN_UP_DOWN);

        //管子从右边出现
        x = width;

        //随机上管道高度
        randomHeight(height);

    }

    private void randomHeight(int height){
        upHeight = random.nextInt((int) (height * (MAX_UP_PIPE_HEIGHT - MIN_UP_PIPE_HEIGHT)));
        upHeight = (int) (upHeight + upHeight * MIN_UP_PIPE_HEIGHT);
    }

    public void draw(Canvas canvas, RectF rectF){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        //将上管道至于天花板
        canvas.translate(x, -(rectF.bottom - upHeight));
        canvas.drawBitmap(upPipe, null, rectF, null);

        //下管道
        canvas.translate(0, rectF.bottom + margin);
        canvas.drawBitmap(downPipe, null, rectF, null);
        canvas.restore();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
