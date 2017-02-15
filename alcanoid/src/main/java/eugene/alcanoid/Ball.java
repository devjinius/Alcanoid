package eugene.alcanoid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

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

    //bundle에 저장할 키 final로 생성
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_SPEED_X = "xSpeed";
    private static final String KEY_SPEED_Y = "ySpeed";


    public Ball(float radius, float initialX, float initialY) {
        mRadius = radius;
        mSpeedX = radius/3;
        mSpeedY = -radius/3;
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

    // 화면이 바뀔 때 저장하는 메서드
    public Bundle save(int width, int height) {
        Bundle bundle = new Bundle();
        bundle.putFloat(KEY_X, mX / width);
        bundle.putFloat(KEY_Y, mY / height);
        bundle.putFloat(KEY_SPEED_X, mSpeedX / width);
        bundle.putFloat(KEY_SPEED_Y, mSpeedY / height);
        return bundle;
    }

    // 화면이 바뀔 때 복원하는 메서드
    public void restore(Bundle inState, int width, int height) {
        mX = inState.getFloat(KEY_X) * width;
        mY = inState.getFloat(KEY_Y) * height;
        mSpeedX = inState.getFloat(KEY_SPEED_X) * width;
        mSpeedY = inState.getFloat(KEY_SPEED_Y) * height;
    }
}
