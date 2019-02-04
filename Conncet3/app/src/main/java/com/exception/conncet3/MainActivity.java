package com.exception.conncet3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    boolean redTurn = false;
    boolean gameIsActive = true;
    boolean moveLeft;
    int[] status = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    int[][] winingPoss = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    LinearLayout layout;
    GridLayout gridLayout;

    public void dropIn(View view){

        ImageView imageView = (ImageView) view;
        int statusNum = Integer.parseInt(imageView.getTag().toString());


        if (status[statusNum] == -1 && gameIsActive) {
            imageView.setTranslationY(-1000f);
            if (redTurn) {
                imageView.setImageResource(R.drawable.red);
                redTurn = false;
                status[statusNum] = 0;

            } else if (!redTurn) {
                imageView.setImageResource(R.drawable.green);
                redTurn = true;
                status[statusNum] = 1;
            }

            imageView.animate().translationYBy(1000f).setDuration(300);

            for (int[] win: winingPoss ){
                if (status[win[0]] == status[win[1]] && status[win[1]] == status[win[2]] && status[win[0]] != -1){

                    gameIsActive = false;

                    String winner = "red";
                    if (status[win[0]] == 1){
                        winner = "green";
                    }

                    TextView winnerText = findViewById(R.id.winnerText);
                    winnerText.setText(winner + " has won");

                    layout.setVisibility(View.VISIBLE);
                    layout.animate().rotationBy(360).setDuration(200);
                    break;

                }
            }

            moveLeft = isAnyMoveLeft(status);
            if (!moveLeft){
                gameIsActive = false;
                TextView winnerText = findViewById(R.id.winnerText);
                winnerText.setText("the Fuck is draw");

                layout.setVisibility(View.VISIBLE);
                layout.animate().rotationBy(360).setDuration(200);
            }

        }
    }

    public void playAgain(View view){
        gameIsActive = true;

        layout.setVisibility(View.INVISIBLE);
        for (int i = 0; i < status.length; i++){
            status[i] = -1;
        }
        gridLayout = findViewById(R.id.gridView);
        for (int i = 0; i < gridLayout.getChildCount(); i++){
            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
        }
    }

    public boolean isAnyMoveLeft(int[] lst){
        for (int i: lst){
            if (i == -1){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.popUp);

    }
}
