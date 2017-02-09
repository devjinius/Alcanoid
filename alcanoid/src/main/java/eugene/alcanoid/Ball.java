package eugene.alcanoid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by eugene on 2017-02-09.
 */

public class Ball implements DrawableItem {
    // ball 의 위치, 속도(좌표의 이동너비), 반지름
    private float mX;
    private float mY;
    private float mSpeedX;
    private float mSpeedY;
    private final float mRadius;
    private final float mInitialX;
    private final float mInitialY;
    private final float mInitialSpeedX;
    private final float mInitialSpeedY;


    public Ball(float radius, float initialX, float initialY) {
        mRadius = radius;
        mSpeedX = radius/5;
        mSpeedY = -radius/5;
        mX = initialX;
        mY = initialY;
        mInitialX = mX;
        mInitialY = mY;
        mInitialSpeedX = mSpeedX;
        mInitialSpeedY = mSpeedY;
    }

    public void move(){
        mX += mSpeedX;
        mY += mSpeedY;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX, mY, mRadius, paint);
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getSpeedX() {
        return mSpeedX;
    }

    public float getSpeedY() {
        return mSpeedY;
    }

    public void setSpeedX(float SpeedX) {
        mSpeedX = SpeedX;
    }

    public void setSpeedY(float SpeedY) {
        mSpeedY = SpeedY;
    }

    public void reset(){

        mX = mInitialX;
        mY = mInitialY;
        mSpeedX = mInitialSpeedX * ((float)Math.random()-0.5f);
        mSpeedY = mInitialSpeedY;
    }
}
