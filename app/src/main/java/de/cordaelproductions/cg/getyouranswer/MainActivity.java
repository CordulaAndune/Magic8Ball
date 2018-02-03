package de.cordaelproductions.cg.getyouranswer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Boolean hasAccelerometer;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private TextView answerTextView;
    private ImageView answerImageView;
    private int[][] allAnswers;
    private int oldAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oldAnswer = -1;

        // get TextView and ImageView
        answerTextView = findViewById(R.id.answerText);
        answerImageView = findViewById(R.id.answerImage);

        allAnswers = new int[][]{{R.string.yes, R.drawable.yes},
                {R.string.no, R.drawable.no},
                {R.string.dont_count, R.drawable.dont_count},
                {R.string.source_no, R.drawable.source_no},
                {R.string.better_not_tell, R.drawable.better_not_tell},
                {R.string.meh, R.drawable.meh},
                {R.string.ask_later, R.drawable.ask_later},
                {R.string.doubt_it, R.drawable.doubt_it},
                {R.string.without_doubt, R.drawable.without_doubt},
                {R.string.certain, R.drawable.certain},
                {R.string.definitely, R.drawable.definitly},};


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        hasAccelerometer = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        if (hasAccelerometer) {
            answerTextView.setText(R.string.shake);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
                @Override
                public void onShake(long Count) {
                    chooseAnswer();
                }
            });
        } else {
            answerTextView.setText(R.string.click_button);
            answerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseAnswer();
                }
            });
        }

        if (savedInstanceState != null) {
            answerImageView.setImageResource(savedInstanceState.getInt("answerImageTag"));
            answerTextView.setText(savedInstanceState.getChar("textAnswer"));
        } else {
            answerImageView.setImageResource(R.drawable.question);
            answerImageView.setTag(R.drawable.question);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("answerImageTag", (int) answerImageView.getTag());
        savedInstanceState.putCharSequence("textAnswer", answerTextView.getText());
    }

    @Override
    public void onResume() {
        super.onResume();
        // register the Session Manager Listener onResume
        if (hasAccelerometer) {
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        // unregister the Sensor Manager onPause
        if (hasAccelerometer) {
            mSensorManager.unregisterListener(mShakeDetector);
        }
        super.onPause();
    }

    /**
     * Choose the answer by selecting a randomNumber as index of the answer in the answer Array
     */
    private void chooseAnswer() {
        int randomAnswer = setRandomNumber();
        answerTextView.setText(allAnswers[randomAnswer][0]);
        answerImageView.setImageResource(allAnswers[randomAnswer][1]);
        answerImageView.setTag(allAnswers[randomAnswer][1]);
        oldAnswer = randomAnswer;
    }

    /**
     * select a random Number, avoid the same answer two times in series by recursion
     *
     * @return random number
     */
    private int setRandomNumber() {
        int randomAnswer;
        Random random = new Random(System.currentTimeMillis());
        randomAnswer = (random.nextInt(allAnswers.length));
        if (randomAnswer == oldAnswer) {
            return setRandomNumber();
        } else {
            return randomAnswer;
        }
    }


}
