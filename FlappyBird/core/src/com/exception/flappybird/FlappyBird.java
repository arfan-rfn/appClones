package com.exception.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;
import java.util.regex.Pattern;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background, gameOver;
	ShapeRenderer shapeRenderer;

	Texture birds[];
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	Circle birdCircle;
	Rectangle[] topTubeRect;
	Rectangle[] bottomTubeRect;

	Texture topTube;
	Texture bottomTube;

	float gap = 800;
	float maxTubeOffSet;
	Random random;
	float tubeVelocity = 4;
	int numOfTubes = 4;
	float[] tubeX = new float[numOfTubes];
    float[] tubeOffset = new float[numOfTubes];
    float distanceBetweenTube;
    int score = 0;
    int scoreingTube = 0;

    BitmapFont font;


    @Override
	public void create () {

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().scale(10 );

        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        topTubeRect = new Rectangle[numOfTubes];
        bottomTubeRect = new Rectangle[numOfTubes];

		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		maxTubeOffSet = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		random = new Random();
		distanceBetweenTube = Gdx.graphics.getWidth()*3/4;

		startGame();
	}

	public void startGame(){
        birdY =  (Gdx.graphics.getHeight()/2)-(birds[flapState].getHeight()/2);
        for (int i = 0; i < numOfTubes; i++){
            tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTube;
            tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()-gap - 200);
            topTubeRect[i] = new Rectangle();
            bottomTubeRect[i] = new Rectangle();
        }
    }


	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED);

        if (gameState == 1) {
            if (tubeX[scoreingTube] < Gdx.graphics.getWidth()/2){
                score++;
                Gdx.app.log("collession", "fuck the Score: " + score);

                if (scoreingTube < numOfTubes-1 ){
                    scoreingTube++;
                }else {
                    scoreingTube = 0;
                }
            }
            if (Gdx.input.justTouched()){
                velocity = -30;

            }
            for (int i = 0; i < numOfTubes; i++) {
                if (tubeX[i] < - topTube.getWidth()) {
                    tubeX[i] += numOfTubes * distanceBetweenTube;
                    tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()-gap - 200);

                }else {

                    tubeX[i] = tubeX[i] - 4;
                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i]);

                topTubeRect[i].set(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRect[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }


            if (birdY > 0) {
                velocity = velocity+2;
                birdY -= velocity;
            }else {
                gameState = 2;
            }

        }else if (gameState == 0){

            if (Gdx.input.justTouched()){
                gameState = 1;
            }
        } else if (gameState == 2) {
            batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2- gameOver.getHeight()/2);
            if (Gdx.input.justTouched()){
                gameState = 1;
                startGame();
                score = 0;
                scoreingTube = 0;
                velocity = 0;
            }
        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        font.draw(batch, String.valueOf(score), 100, 200);

        batch.draw(birds[flapState], (Gdx.graphics.getWidth() / 2) - (birds[flapState].getWidth() / 2), birdY);
        batch.end();
        birdCircle.set(Gdx.graphics.getWidth()/2, birdY+birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        for (int i = 0; i < numOfTubes; i++) {
//            shapeRenderer.rect(topTubeRect[i].x, topTubeRect[i].y, topTubeRect[i].width, topTubeRect[i].height);
//            shapeRenderer.rect(bottomTubeRect[i].x, bottomTubeRect[i].y, bottomTubeRect[i].width, bottomTubeRect[i].height);

            if (Intersector.overlaps(birdCircle, topTubeRect[i]) || Intersector.overlaps(birdCircle, bottomTubeRect[i])){
                gameState = 2;
            }


        }
//        shapeRenderer.end();

	}

}
