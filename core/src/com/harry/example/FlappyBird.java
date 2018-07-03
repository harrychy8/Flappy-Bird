package com.harry.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    //ShapeRenderer shapeRenderer;

    Texture gameover, restart, board, newIcon;
    Texture bronzeMedal, silverMedal, goldMedal;
    Texture floor;

    Texture[] birds;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;
    Circle birdCircle;
    int score = 0;
    int highScore;
    boolean newHighScore;
    int scoringTube = 0;
    BitmapFont font, boardFont;

    int gameState = 0;
    float gravity = 2;

    Texture topTube;
    Texture bottomTube;
    float gap = 450;
    float maxTubeOffset;
    Random randomGenerator;
    float tubeVelocity = 4;
    float floorVelocity = 4;
    int numberOfTubes = 4;
    int numberOfFloors = 4;

    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float[] floorX = new float[numberOfFloors];

    float distanceBetweenTubes;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    Preferences prefs;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        board = new Texture("board.png");
        restart = new Texture("restart.png");
        newIcon = new Texture("new.png");
        floor = new Texture("ground.png");
        bronzeMedal = new Texture("bronzeMedal.png");
        silverMedal = new Texture("silverMedal.png");
        goldMedal = new Texture("goldMedal.png");

        //shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Exo-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 160;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 12;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);

        parameter.size = 80;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 6;
        parameter.color = Color.WHITE;
        boardFont = generator.generateFont(parameter);

        generator.dispose(); // don't forget to dispose to avoid memory leaks!


        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");


        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        startGame();

        newHighScore = false;
        prefs = Gdx.app.getPreferences("My Preferences");
        highScore = prefs.getInteger("HighScore", -1);

        //if the user doesnt have a high score
        if (highScore == -1) {
            highScore = 0;
            prefs.putInteger("HighScore", highScore);
            prefs.flush();
        }

    }

    private void startGame() {

        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++) {

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            tubeOffset[i] = randomGenerator.nextFloat() *
                    (Gdx.graphics.getHeight() - gap - 400 - floor.getHeight());

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }

        for (int i = 0; i < numberOfFloors; i++) {

            floorX[i] = i * floor.getWidth();

        }

    }

    @Override
    public void render() {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;

                Gdx.app.log("Score", String.valueOf(score));

                if (scoringTube < numberOfTubes - 1) {

                    scoringTube++;

                } else {

                    scoringTube = 0;

                }

            }

            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < -topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes - tubeVelocity;
                    tubeOffset[i] = randomGenerator.nextFloat() *
                            (Gdx.graphics.getHeight() - gap - 400 - floor.getHeight());

                } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;

                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() - 200 - tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i],
                        Gdx.graphics.getHeight() - gap - 200 - tubeOffset[i] - bottomTube.getHeight());

                topTubeRectangles[i] = new Rectangle(tubeX[i],
                        Gdx.graphics.getHeight() - 200 - tubeOffset[i],
                        topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i],
                        Gdx.graphics.getHeight() - gap - 200 - tubeOffset[i] - bottomTube.getHeight(),
                        bottomTube.getWidth(), bottomTube.getHeight());
            }

            if (Gdx.input.justTouched()) {

                velocity = -30;

            }

            if (birdY > floor.getHeight()) {

                velocity = velocity + gravity;
                birdY -= velocity;

            } else {

                gameState = 2;

            }

        } else if (gameState == 0) {

            if (Gdx.input.justTouched()) {

                gameState = 1;
                tubeVelocity = 4;

            }

        } else if (gameState == 2) {
            floorVelocity = 0;
            //let the bird drop onto the floor first
            if (birdY > floor.getHeight()) {
                velocity = velocity + gravity;
                birdY -= velocity;
            } else {

                if (score > highScore) {
                    newHighScore = true;
                    highScore = score;
                    prefs.putInteger("HighScore", highScore);
                    prefs.flush();
                }
                if (Gdx.input.justTouched()) {

                    //check if restart button is clicked
                    float x = Gdx.input.getX();
                    float y = Gdx.graphics.getHeight() - Gdx.input.getY();
                    if (x > Gdx.graphics.getWidth() / 2 - restart.getWidth() / 2
                            && x < Gdx.graphics.getWidth() / 2 + restart.getWidth() / 2
                            && y > Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 4 - 100
                            && y < Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 4 - 100 + restart.getHeight()) {
                        gameState = 1;
                        startGame();
                        score = 0;
                        scoringTube = 0;
                        velocity = 0;
                        floorVelocity = 4;
                        newHighScore = false;
                    }

                }
            }

        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        //draw the tubes

        if (gameState == 2) {
            for (int i = 0; i < numberOfTubes; i++) {

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() - 200 - tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i],
                        Gdx.graphics.getHeight() - gap - 200 - tubeOffset[i] - bottomTube.getHeight());
            }
        }

        for (int i = 0; i < numberOfFloors; i++) {

            if (floorX[i] < -floor.getWidth()) {

                floorX[i] += numberOfFloors * floor.getWidth() - floorVelocity;

            } else {

                floorX[i] = floorX[i] - floorVelocity;
            }

            batch.draw(floor, floorX[i], 0);
        }

        //game over screen
        if (gameState == 2 && birdY < floor.getHeight()) {
            //draw board
            batch.draw(gameover,
                    Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 + Gdx.graphics.getHeight() / 5);
            batch.draw(restart,
                    Gdx.graphics.getWidth() / 2 - restart.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 4 - 100);
            batch.draw(board,
                    Gdx.graphics.getWidth() / 2 - board.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - board.getHeight() / 2);

            //draw score
            GlyphLayout temp = new GlyphLayout();
            temp.setText(boardFont, String.valueOf(score));
            float w = temp.width;
            boardFont.draw(batch, temp,
                    Gdx.graphics.getWidth() / 2 + board.getWidth() / 2 - 70 - w,
                    Gdx.graphics.getHeight() / 2 + 80);

            temp = new GlyphLayout();
            temp.setText(boardFont, String.valueOf(highScore));
            w = temp.width;
            boardFont.draw(batch, temp,
                    Gdx.graphics.getWidth() / 2 + board.getWidth() / 2 - 70 - w,
                    Gdx.graphics.getHeight() / 2 - 80);

            // draw new
            if (newHighScore) {
                batch.draw(newIcon, Gdx.graphics.getWidth() / 2 + 100, Gdx.graphics.getHeight() / 2 - 75);
            }

            // draw medal

            if (score >= 5) {

                Texture currentMedal;
                if (score < 10) {
                    currentMedal = bronzeMedal;
                } else if (score < 15) {
                    currentMedal = silverMedal;
                } else {
                    currentMedal = goldMedal;
                }

                batch.draw(currentMedal, Gdx.graphics.getWidth() / 2 - board.getWidth() / 3 - 45,
                        Gdx.graphics.getHeight() / 2 - 110);
            }


        }

        if (gameState != 2) {
            batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
        } else {
            batch.draw(birds[0], Gdx.graphics.getWidth() / 2 - birds[0].getWidth() / 2, birdY);
        }

        if (gameState != 2) {
            GlyphLayout temp = new GlyphLayout();
            temp.setText(font, String.valueOf(score));
            float w = temp.width;
            font.draw(batch, temp, Gdx.graphics.getWidth() / 2 - w / 2, Gdx.graphics.getHeight() / 6 * 5);
        }

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 4);


        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for (int i = 0; i < numberOfTubes; i++) {

            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());


            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

                gameState = 2;
                Gdx.input.vibrate(2000);

            }

        }

        batch.end();

        //shapeRenderer.end();


    }


}
