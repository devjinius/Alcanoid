package eugene.alcanoid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.os.Handler;

import java.util.ArrayList;

import static android.R.id.message;

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
    // block 만 관리하는 arraylist 추가 후 clear 판정을 위해 남은 블록의 개수를 세자
    private ArrayList<Block> mBlockList;

    // pad 생성 및 패드의 크기 설정
    private Pad mPad;
    private float mPadHalfWidth;

    // Ball 생성 및 크기 설정
    private Ball mBall;
    private float mBallRadius;

    // 사용자가 touch한 좌표
    volatile private float mTouchedX;
    volatile private float mTouchedY;

    private float mBlockWidth;
    private float mBlockHeight;
    static final int BLOCK_COUNT = 10;
    private int mLife;

    // 시간을 기록하기 위한 변수 선언
    private long mGameStartTime;

    // 스레드 처리를 위한 handler 변수 선언
    private Handler mHandler;

    // 상태를 저장하기 위해 final Stirng 변수 선언
    private static final String KEY_LIFE = "life";
    private static final String KEY_GAME_START_TIME = "game_start_time";
    private static final String KEY_BALL = "ball";
    private static final String KEY_BLOCK = "block";

    // 저장된 상태를 다루는 변수 선언
    // 생성시 인수로 받아서 readyObjects에서 상태를 복원
    private final Bundle mSavedInstanceState;

    public void start(){
        // Runnable 의 run()을 내부클래스로 구현
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                while (true) {
                    long startTime = System.currentTimeMillis();
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

                        mBall.move();
                        // ball 충돌판정
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;
                        // 가로방향 벽에 부딪혔을 때
                        if (ballLeft < 0 && mBall.getSpeedX() < 0
                                        || ballRight >= getWidth()
                                        && mBall.getSpeedX() > 0) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                        }
                        // 위 벽에 부딪혔을 때
                        if (ballTop < 0) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                        }
                        // 공이 바닥으로 떨어짐
                        if (ballTop > getHeight()) {
                            if (mLife > 0) {
                                // 생명이 남았을 경우 mLife--
                                mLife--;
                                mBall.reset();
                            } else{
                                // 생명이 안남았는데 죽었을 경우
                                // message와 bundle을 생성하여 message에 bundle을 넣는다.
                                unlockCanvasAndPost(canvas);
                                Message message = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, false);
                                bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                                bundle.putLong(ClearActivity.EXTRA_TIME,
                                        System.currentTimeMillis()-mGameStartTime);
                                message.setData(bundle);
                                // handler를 이용해서 sendMessage한다.
                                // UI Thread 에서 처리할 수 있게 하는 메서드다.
                                mHandler.sendMessage(message);
                                return;
                            }
                        }
                        // block과 충돌판정 처리
                        Block leftBlock = getBlock(ballLeft, mBall.getY());
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block topBlock = getBlock(mBall.getX(), ballTop);
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);

                        //게임 클리어 판정 추가 (공이 블럭에 마지막으로 부딪혀야 끝나니깐!)
                        boolean isCollision = false; //flag

                        if (leftBlock != null) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                            isCollision = true;
                        }
                        if (rightBlock != null) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                            isCollision = true;
                        }
                        if (topBlock != null) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                            isCollision = true;
                        }
                        if (bottomBlock != null) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                            isCollision = true;
                        }
                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();

                        if (ballBottom > padTop && ballBottom - ballSpeedY < padTop
                                && padLeft < ballRight && padRight > ballLeft) {
                            if (ballSpeedY < mBlockHeight / 3) {
                                ballSpeedY *= -1.05f;
                            } else{
                                ballSpeedY = -ballSpeedY;
                            }
                            float ballSpeedX = mBall.getSpeedX() + (mBall.getX() - mTouchedX) / 10;
                            if (ballSpeedX > mBlockWidth / 5) {
                                ballSpeedX = mBlockWidth / 5;
                            }
                            mBall.setSpeedX(ballSpeedX);
                            mBall.setSpeedY(ballSpeedY);
                        }

                        // block과 pad 한꺼번에 그리기
                        // mItemList는 100개의 블럭과 1개의 패드를 가지고 있다.
                        for (DrawableItem item : mItemList) {
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);

                        // lock이 풀린 상태에서 flag가 true이면서 블럭이 남지 않았으면 gameClear
                        if (isCollision && getBlockCount() == 0) {
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, true);
                            bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, 0);
                            bundle.putLong(ClearActivity.EXTRA_TIME,
                                    System.currentTimeMillis() - mGameStartTime);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }
                    /* 기기마다 처리하는 속도가 다르므로 공이 움직이는 속도가 다를 것이다.
                       따라서 속도를 맞춰주기 위해 우리는 while루프를 1/60초에 한번 실행하도록 하자.
                       16밀리초는 약 1/60초다.
                    */
                    long sleepTime = 16 - (System.currentTimeMillis() - startTime);
                    // while 루프를 돈 시간이 16밀리초(1/60)초 보다 적게 걸렸으면
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {

                        }
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
    public GameView(final Context context, Bundle savedInstanceState) {
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        // 인수로 가져온 bundle 추가
        mSavedInstanceState = savedInstanceState;
        // new Handler() -> 호출한 Thread에서 실행된다. 따라서 생성자를 호출하는 UI Thread 에서 실행된다.
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message message){
                //실행할 처리를 override 해서 지정할 수 있다.
                // context는 GameActivity 에서 this로 호출 했으니 GameActivity의 instance 다.
                Intent intent = new Intent(context, ClearActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(message.getData());
                context.startActivity(intent);
            }
        };
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

    // pad와 ball 객체도 여기서 같이만들어 주자.
    public void readyObjects(int width, int height) {
        // 목숨 5개로 초기값 설정
        mLife = 5;
        // 블럭생성
        mBlockWidth = width/10;
        // 세로는 화면의 절반이므로 /10와 /2를 해준다.
        mBlockHeight = height/20;
        // mItemList 는 <DrawableItem> 리스트다.
        mItemList = new ArrayList<>();
        // blockList 초기화
        mBlockList = new ArrayList<>();
        // 블럭을 그릴때 시작하는 좌표가 필요하다. Canvas.drawRect 메서드의 사용법이다.
        for (int i = 0; i < BLOCK_COUNT; i++) { // 100개 그릴꺼니까 for 문으로 100번 반복
            // (0,0) (0,1) (0,2) ... (0,10) (1,0) (1,1) 순으로 그린다.
            float blockTop = i / 10 * mBlockHeight;
            float blockLeft = i % 10 * mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            // mBlockLisg에 저장하고 루프를 빠져나간 뒤
            mBlockList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
        }
        // addAll()을 이용해서 ItemList에 저장한다.
        mItemList.addAll(mBlockList);

        // 패드생성 화면 높이의 아래에서 20% 정도에 위치하고 두께는 0.05
        // 넓이는 화면 크기의 1/5
        mPad = new Pad(height * 0.85f, height * 0.9f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;

        // 볼 생성, 크기는 화면의 너비나 높이중 작은쪽의 1/40
        // 초기 위치는 화면의 중앙
        mBallRadius = (width > height) ? (height / 40) : (width / 40);
        mBall = new Ball(mBallRadius, width / 2, height / 1.5f);
        mItemList.add(mBall);
        //생명값 초기화
        mLife = 5;
        //시간값 초기화
        mGameStartTime = System.currentTimeMillis();

        // mSavedInstanceState에 값이 있는 경우 복원화 시켜주고 아니면 초기화
        if (mSavedInstanceState != null) {
            mLife = mSavedInstanceState.getInt(KEY_LIFE);
            mGameStartTime = mSavedInstanceState.getLong(KEY_GAME_START_TIME);
            mBall.restore(mSavedInstanceState.getBundle(KEY_BALL), width, height);
            for (int i = 0; i < BLOCK_COUNT; i++) {
                mBlockList.get(i).restore(mSavedInstanceState.getBundle(KEY_BLOCK + i));
            }
        }
    }

    //특정 좌표에있는 블럭을 가져오는 메서드
    public Block getBlock(float x, float y) {
        // 블럭을 그렸던 역으로 찾아간다.
        int index = (int) (x / mBlockWidth) + (int) (y / mBlockHeight) * 10;
        if (0 <= index && index < BLOCK_COUNT) {
            Block block = (Block) mItemList.get(index);
            if (block.iSExist()) {
                return block;
            }
        }
        return null;
    }

    private int getBlockCount(){
        int count = 0;
        for (Block block : mBlockList) {
            if (block.iSExist()) {
                count++;
            }
        }
        return count;
    }

    // 값을 저장하는 메서드, 화면이 바뀔 때 사용한다.
    public void onSaveInstanceState(Bundle outState){
        outState.putInt(KEY_LIFE, mLife);
        outState.putLong(KEY_GAME_START_TIME, mGameStartTime);
        // 공은 save()가 번들은 return 하니까 번들안에 번들을 넣는다
        outState.putBundle(KEY_BALL, mBall.save(getWidth(), getHeight()));
        // 블럭은 BLOCK_COUNT 만큼 반복문을 돌려서 mHard 를 검사해야 한다.
        for (int i = 0; i < BLOCK_COUNT; i++) {
            outState.putBundle(KEY_BLOCK + i, mBlockList.get(i).save());
        }
    }
}