package eugene.alcanoid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by eugene on 2017-02-05.
 * 알카노이드 게임의 블럭을 만드는 클래스
 */

public class Block implements DrawableItem{
    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;// 두께

    public Block(float mTop, float mLeft, float mBottom, float mRight) {
        this.mTop = mTop;
        this.mLeft = mLeft;
        this.mBottom = mBottom;
        this.mRight = mRight;
        this.mHard = 1;
    }

    public void draw(Canvas canvas, Paint paint){
        if (mHard > 0) {
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            // 안에들어가는 블럭을 만든다.
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            // 블럭을 나누는 선을 그려준다.
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
        }
    }
}
