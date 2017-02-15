package eugene.alcanoid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

/**
 * Created by eugene on 2017-02-05.
 * 알카노이드 게임의 블럭을 만드는 클래스
 *
 * 2017-02-15 1. 화면을 가로로 전환하면 Thread가 재시작하는 오류 발견
 * 액티비티가 내용을 저장하게 만들어 해결
 * onSaveInstance()의 인수로 bundle을 넘겨주어야 한다. 이 bundle 에 남은 블럭을 저장한다.
 * bundle 에 블록이 파괴되었는지 아닌지 내구성을 저장해서 불러오자
 */

public class Block implements DrawableItem{
    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;// 두께

    //1번 버그를 해결하기 위해 bundle에 저장할 키 final로 생성
    private static final String KEY_HARD = "hard";

    // 공에 부딪혔는지 판단하는 flag
    private boolean mIsCollision = false;

    public void collision() {
        mIsCollision = true; // 충돌했는지 여부만 알려준다. 실제로 지우는건 draw()에서 처리하자.
    }

    private boolean mIsExist = true; // 블럭이 존재하는가?

    public boolean iSExist(){
        return mIsExist;
    }

    public Block(float mTop, float mLeft, float mBottom, float mRight) {
        this.mTop = mTop;
        this.mLeft = mLeft;
        this.mBottom = mBottom;
        this.mRight = mRight;
        this.mHard = 1; // 내구성
    }

    public void draw(Canvas canvas, Paint paint){
        if (mIsExist) {
            //내구성이 0 이상인 경우
            if (mIsCollision) {
                mHard--;
                mIsCollision = false;
                if (mHard <= 0) { // 블럭이 파괴되면 나간다.
                    mIsExist = false;
                    return;
                }
            }

//            if(mHard == 1) {
//                paint.setColor(Color.BLUE);
//            } else if(mHard == 2){
//                paint.setColor(Color.RED);
//            }
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

    // 화면이 바뀔 때 상태를 저장하는 메서드
    public Bundle save() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_HARD, mHard);
        return bundle;
    }

    // 화면이 바뀔 때 복원하는 메서드
    public void restore(Bundle inState) {
        mHard = inState.getInt(KEY_HARD);
        mIsExist = mHard > 0;
    }
}
