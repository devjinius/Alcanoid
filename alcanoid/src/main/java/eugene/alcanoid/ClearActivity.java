package eugene.alcanoid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClearActivity extends AppCompatActivity {
    public static final String EXTRA_IS_CLEAR = "eugene.alcanoid.EXTRA.IS_CLEAR";
    public static final String EXTRA_BLOCK_COUNT = "eugene.alcanoid.EXTRA.BLOCK_COUNT";
    public static final String EXTRA_TIME = "eugene.alcanoid.EXTRA.TIME";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        //activity 끼리 데이터를 주고받을 때 bundle을 사용한다.
        //getIntent()로 Intent 객체를 받고 Extras로 받아와서 사용한다.
        //bundle 은 (key와 value)(물건)를 가지는 박스고 intent는 박스를 옮기는 트럭의 개념이다.
        Bundle receiveExtras = getIntent().getExtras();
        if (receiveExtras == null) finish();
        // get~~(String Key, ~~ defaultValue 값이 없을 때 초기값)
        boolean isClear = receiveExtras.getBoolean(EXTRA_IS_CLEAR, false);
        int blockCount = receiveExtras.getInt(EXTRA_BLOCK_COUNT, 0);
        long clearTime = receiveExtras.getLong(EXTRA_TIME, 0);

        TextView textTitle = (TextView) findViewById(R.id.textTitle);
        TextView textBlockCount = (TextView) findViewById(R.id.textBlockCount);
        TextView textClearTime = (TextView) findViewById(R.id.textClearTime);
        Button gameStart = (Button) findViewById(R.id.buttonGameStart);

        if (isClear) {
            textTitle.setText(R.string.clear);
        } else {
            textTitle.setText(R.string.game_over);
        }
        textBlockCount.setText(getString(R.string.block_count, blockCount));
        textClearTime.setText(getString(R.string.time, clearTime/1000, clearTime%1000));

        gameStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClearActivity.this, GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

    }

}
