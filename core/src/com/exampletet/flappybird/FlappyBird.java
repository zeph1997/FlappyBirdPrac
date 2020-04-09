package com.exampletet.flappybird;

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

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	BitmapFont font;
	Texture gameover;
	//ShapeRenderer shapeRenderer;

	Texture[] birds;
	Texture topTube;
	Texture bottomTube;
	//to overlap bird for collision detection
	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	int score = 0;
	int scoringTube = 0;

	int flapState = 0;
	int gameState = 0;
	float birdY;
	float velocity= 0;
	float gravity = 2;
	float gap = 400;
	float tubeVelocity = 4;
	float maxTubeOffset;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	Random randomGenerator;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5,5);
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		gameover = new Texture("gameover.png");

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()* 3/4;

		startGame();

	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
		//set initial 4 tubes
		for(int i =0; i<numberOfTubes;i++){
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() - topTube.getWidth() + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1){

			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;
				if(scoringTube < numberOfTubes - 1){
					scoringTube++;
				}else{
					scoringTube = 0;
				}
			}
			if(Gdx.input.justTouched()) {
				velocity = -30;
				//flapState = 1;
			}
			//Dont let bird drop below screen
			if(birdY > 0) {
				velocity = velocity + gravity;
				birdY -= velocity;
				//flapState = 0;
			}else{
				gameState = 2;
			}
			for(int i = 0;i<numberOfTubes;i++){
				//If tube moves out of screen to the right then reset with new gap height
				if(tubeX[i] < -topTube.getWidth()){
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}else{
					//Just continue moving tube right
					tubeX[i] = tubeX[i] - tubeVelocity;
				}
				//Adjustment of height by placing the y coordinates from the center first
				batch.draw(topTube,tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);
				batch.draw(bottomTube,tubeX[i],Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(), bottomTube.getHeight());

			}
		}else if (gameState == 0){
			if(Gdx.input.justTouched()){
				gameState = 1;
				flapState = 0;
			}
		}else if (gameState == 2){
			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2,Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);
			if(Gdx.input.justTouched()){
				gameState = 1;
				flapState = 0;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		if(flapState == 0){
			flapState = 1;
		}else{
			flapState = 0;
		}

		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2-birds[flapState].getWidth()/2,birdY);
		font.draw(batch,"Score: "+String.valueOf(score),50,Gdx.graphics.getHeight()-50);
		//shapes go by center of shape object
		birdCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

		for(int i = 0; i<numberOfTubes;i++){
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(), bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle,topTubeRectangles[i])||Intersector.overlaps(birdCircle,bottomTubeRectangles[i])){
				gameState = 2;
			}
		}

		batch.end();
		//shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
