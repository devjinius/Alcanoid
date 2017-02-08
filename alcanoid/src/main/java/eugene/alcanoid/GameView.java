package eugene.alcanoid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by eugene on 2017-02-04.
 */

public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener{
    private Thread mThread;

    //Thread 반복 생성시 while 조건을 위한 변수 선언
    //새로만든 Thread에서 사용되기에 volatile로 설정한다.
    volatile private boolean mIsRunnable;

    // block을 만들기위한 배열 생성
    private ArrayList<DrawableItem> mItemList;

    // pad 생성 및 패드의 크기 설정
    private Pad mPad;
    private float mPadHalfWidth;

    //
    volatile private float mTouchedX;
    volatile private float mTouchedY;


    public void start(){
        // Runnable 의 run()을 내부클래스로 구현
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                while (true) {
                    // lock 을 주어 동기화를 시켜준다. sync() 의 ()에는 객체가 들어간다.
                    // critical area {}를 벗어나면 자동으로 lock을 반납한다.
                    // 그냥 this만 쓰면 현재 내부클래스 객체인 Runnable에 걸리므로 GameView에 걸어준다.
                    synchronized (GameView.this) {
                        if(!mIsRunnable) break;
                        Canvas canvas = lockCanvas();
                        if (canvas == null) continue;
                        canvas.drawColor(Color.BLACK);
                        // readyObject 로 mItemList 에는 100개의 객체를 만들어 놓았다. 그리기만 하자.

                        // pad 세팅
                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);

                        // block과 pad 한꺼번에 그리기
                        // mItemList는 100개의 블럭과 1개의 패드를 가지고 있다.
                        for (DrawableItem item : mItemList) {
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);
                    }
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
        readyObjects(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    //surfaceTextureView가 폐기될 때 호출되는 메서드
    //폐기하면 true 반환, false가 반환되면 프로그래머가 직접 폐기해야 한다.
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // 여기에는 왜 동기화를 하지.... 좀 더 공부해보자.
        // Canvas를 그리는 도중에는 삭제되지 않도록 한다고 한다.
        synchronized (this){
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }

    // 블럭은 화면 크기에 의존한다. 따라서 크기를 정해준 뒤 그려야 한다.
    // 화면크기에 맞춰 100개의 Block 객체를 만드는 메서드다.
    // 여기에 들어가는 width, height 는 화면에서 가져오면 된다.
    // 그게 onSurfaceTextureAvailable 일거고 화면이 수정될 경우 onSurfaceTextureSizeChanged 다.\

    // pad 객체도 여기서 같이만들어 주자.
    public void readyObjects(int width, int height) {
        // 블럭생성

        float blockWidth = width/10;
        // 세로는 화면의 절반이므로 /10와 /2를 해준다.
        float blockHeight = height/20;
        // mItemList 는 <Block> 리스트다.
        mItemList = new ArrayList<DrawableItem>();
        // 블럭을 그릴때 시작하는 좌표가 필요하다. Canvas.drawRect 메서드의 사용법이다.
        for (int i = 0; i < 100; i++) { // 100개 그릴꺼니까 for 문으로 100번 반복
            // (0,0) (0,1) (0,2) ... (0,10) (1,0) (1,1) 순으로 그린다.
            float blockTop = i / 10 * blockHeight;
            float blockLeft = i % 10 * blockWidth;
            float blockBottom = blockTop + blockHeight;
            float blockRight = blockLeft + blockWidth;
            mItemList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
        }

        // 패드생성 화면 높이의 아래에서 20% 정도에 위치하고 두께는 0.05
        // 넓이는 화면 크기의 1/5
        mPad = new Pad(height * 0.85f, height * 0.9f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;
    }
}
