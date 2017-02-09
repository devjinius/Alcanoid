package eugene.alcanoid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by eugene on 2017-02-08.
 */

public class Pad implements DrawableItem {
    // 패드는 좌우로만 움직이므로 상하는 final
    private final float mTop;
    private final float mBottom;
    private float mLeft;
    private float mRight;

    public Pad(float mTop, float mBottom) {
        this.mTop = mTop;
        this.mBottom = mBottom;
    }

    // 좌우 좌표를 설정하는 메서드
    public void setLeftRight(float Left, float Right) {
        mLeft = Left;
        mRight = Right;
    }

    public float getTop() {
        return mTop;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
    }
}
