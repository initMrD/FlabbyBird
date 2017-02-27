package com.initmrd.flabbybird.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * Created by initMrd@gmail.com on 17/2/22.
 */

public class Floor {

    //地板位置
    private static final float FLOOR_Y_POS = 4/5F;

    //坐标
    private int x,y;

    //填充
    private BitmapShader mFloorShader;

    private int width,height;

    public Floor(int width, int height, Bitmap floorbackground) {
        this.width = width;
        this.height = height;
        y = (int) (height*FLOOR_Y_POS);
        mFloorShader = new BitmapShader(floorbackground, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    }

    //绘制地板
    public void draw(Canvas canvas, Paint paint){
        if(-x > width){
            x = x%width;
        }
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        //移动
        canvas.translate(x,y);
        paint.setShader(mFloorShader);
        canvas.drawRect(x, 0, -x+width, height-y, paint);
        canvas.restore();
        paint.setShader(null);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
