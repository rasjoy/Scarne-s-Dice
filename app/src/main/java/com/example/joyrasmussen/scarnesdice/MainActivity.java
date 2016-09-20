package com.example.joyrasmussen.scarnesdice;

import android.content.DialogInterface;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;
import android.os.Handler;
public class MainActivity extends AppCompatActivity {
    public static final String ROLL = "Roll";
    private int userScore, computerScore, round;
    private Random rand = new Random();
    private int[] diceImg = new int[]{
            R.drawable.dice1,R.drawable.dice2, R.drawable.dice3, R.drawable.dice4, R.drawable.dice5, R.drawable.dice6
    };
    TextView scoreText;
    ImageView dieImg;
    Button holdButton, resetButton, rollButton;
    Handler handler ;
    private final int updateRoll = 11;
    private final int updateScore = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreText = (TextView)findViewById(R.id.textView);
        dieImg = (ImageView)findViewById(R.id.imageView);
        holdButton = (Button)findViewById(R.id.button);
        resetButton = (Button)findViewById(R.id.button3);
        rollButton = (Button)findViewById(R.id.button2);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == updateRoll) {


                    Bundle bundle = new Bundle(msg.getData());
                    if (bundle != null) {
                        updateComputerRoll(bundle.getInt(ROLL, 1));
                    }
                }else if(msg.what == updateScore){
                    computerScore += round;
                    round = 0;

                    scoreText.setText(String.format(Locale.US, "Your Score: %d  Computer's Score: %d Your Round Score: %d", userScore,computerScore, round) );

                    rollButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    holdButton.setEnabled(true);
                    if(computerScore >= 100){
                        restart();

                    }
                }
                return false;
            }
        });

    }

    public void hold(View view) {
        addScore();
        if(userScore < 100) {
            computerTurn();
        }

    }

    private void addScore() {

        userScore += round;
        round = 0;
        scoreText.setText(String.format(Locale.US, "Your Score: %d  Computer's Score: %d Computer's Round Score: %d" ,userScore, computerScore, round) );
        if(userScore >= 100){
            restart();

        }
    }



    private void restart() {

        new AlertDialog.Builder(this)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setMessage(userScore >= 100 ? "You Won!" : "Computer Won :'(").create().show();
        rollButton.setEnabled(false);
        holdButton.setEnabled(false);
    }

    public void reset(View view) {
        userScore = computerScore = round = 0;
        scoreText.setText(R.string.defaultScore);
        rollButton.setEnabled(true);
        holdButton.setEnabled(true);
    }

    public void rollDice(View view) {
        roll();
        if(round == 0){
            computerTurn();
        }
    }

    private void roll() {

        int cRoll = rand.nextInt(6)+ 1;

        dieImg.setImageDrawable(ContextCompat.getDrawable(this,diceImg[cRoll-1]));
        if(cRoll != 1){
            round+=cRoll;

        }else{

            round = 0;

        }
        scoreText.setText(String.format(Locale.US, "Your Score: %d  Computer's Score: %d Your Round Score: %d" ,userScore, computerScore, round) );
    }

    private void updateComputerRoll(int roll){

        dieImg.setImageDrawable(ContextCompat.getDrawable(this,diceImg[roll-1]));


           // Log.d("UPDATE", Integer.toString(round));


        //Log.d("UPRound", Integer.toString(round));
        scoreText.setText(String.format(Locale.US, "Your Score: %d  Computer's Score: %d Computer Round Score: %d", userScore,computerScore, round) );

    }
    private void computerTurn(){

        rollButton.setEnabled(false);

        holdButton.setEnabled(false);
        resetButton.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {

                int roll = rand.nextInt(6) + 1;

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt(ROLL, roll);
                msg.what = updateRoll;
                msg.setData(bundle);
                handler.sendMessage(msg);

                if (roll != 1) {
                    round += roll;
                } else {
                    round = 0;
                    msg = new Message();
                    msg.what = updateScore;
                    handler.sendMessage(msg);
                    return;
                }
                if (round < 20){
                    //if( (computerScore + round - userScore < 8 || computerScore < 20 || round < 10 ) && round < 25){
                    // if compscore < 20 || compScore + round - userScore > 8
                    //Log.d("TRound", Integer.toString(round));

                    handler.postDelayed(this, 500);

            }

            else

            {
                msg = new Message();
                msg.what = updateScore;
                handler.sendMessage(msg);

            }
        }
        }).start();
       // Log.d("CRound", Integer.toString(round));

    }

}
