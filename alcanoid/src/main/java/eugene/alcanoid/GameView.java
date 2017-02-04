package eugene.alcanoid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

/**
 * Created by eugene on 2017-02-04.
 */

public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener{
    private Thread mThread;

    //Thread 반복 생성시 while 조건을 위한 변수 선언
    //새로만든 Thread에서 사용되기에 volatile로 설정한다.
    volatile private boolean mIsRunnable;

    // 원래 onTouch Event는 UI Thread 에서 작동한다. 우리는 우리가 만든
    // Thread 에서 작동하게 만들고 싶으므로 volatile 을 이용하고 값을 받아온다
    volatile private float mTouchedX;
    volatile private float mTouchedY;

    public void start(){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                while (mIsRunnable) {
                    Canvas canvas = lockCanvas();
                    if (canvas == null) continue;
                    canvas.drawCircle(mTouchedX, mTouchedY, 50, paint);
                    unlockCanvasAndPost(canvas);
                }
            }
        });
        mIsRunnable = true;
        mThread.start();
    }

    public void stop(){
        mIsRunnable = false;
    }

    //생성자로 부모 생성자 호출
    public GameView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
    }

    //surfaceTextureView 를 사용할 수 있을 때 호출하는 메서드
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    //surfaceTextureView가 폐기될 때 호출되는 메서드
    //폐기하면 true 반환, false가 반환되면 프로그래머가 직접 폐기해야 한다.
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // 딱히 customize 할 필요성이 없으므로 true 를 주어 자동으로 폐기하게 만든다.
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 원래 onTouch Event는 UI Thread 에서 작동한다. 우리는 우리가 만든
        // Thread 에서 작동하게 만들고 싶으므로 volatile 을 이용하고 값을 받아온다
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }
}
