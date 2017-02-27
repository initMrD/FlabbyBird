package com.initmrd.flabbybird.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.initmrd.flabbybird.util.Util;

/**
 * Created by initMrd@gmail.com on 17/2/22.
 */

public class Bird {
    //鸟的初始高度
    private static final float BIRD_START_POS = 2/3F;

    //鸟的大小
    private static final int BIRD_SIZE = 30;

    //鸟的横坐标,纵坐标,宽度,高度
    private int x,y,width,height;

    //鸟的bitmap
    private Bitmap birdBitmap;

    private RectF birdRect = new RectF();

    public Bird(Context context, int width, int height, Bitmap birdBitmap) {

        this.birdBitmap = birdBitmap;

        //鸟的位置
        this.x = width / 2 - this.birdBitmap.getWidth() / 2;
        this.y = (int) (height * BIRD_START_POS);

        // 计算鸟的宽度和高度
        this.width = Util.dp2px(context, BIRD_SIZE);
        this.height = (int) (this.width * 1.0f / this.birdBitmap.getWidth() * this.birdBitmap.getHeight());
    }

    public void draw(Canvas canvas){
        birdRect.set(x, y, x + width, y + height);
        canvas.drawBitmap(birdBitmap, null, birdRect, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setY(int y) {
        this.y = y;
    }
}
