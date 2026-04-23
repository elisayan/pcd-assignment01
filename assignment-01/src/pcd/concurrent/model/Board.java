package pcd.concurrent.model;

import pcd.common.BoardConf;
import pcd.common.Boundary;
import pcd.common.P2d;

import java.util.List;

public class Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;

    private int playerScore;
    private int botScore;

    public static final double HOLE_RADIUS = 0.12;
    public static final P2d LEFT_HOLE_POS = new P2d(-1.5, 1.0);
    public static final P2d RIGHT_HOLE_POS = new P2d(1.5, 1.0);

    public Board(){}

    public synchronized void init(BoardConf conf) {
    	balls = conf.getSmallBalls();
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
        playerScore = 0;
        botScore = 0;
    }

    public synchronized void updateState(long dt) {

    	playerBall.updateState(dt, this);
        botBall.updateState(dt, this);

    	for (var b: balls) {
    		b.updateState(dt, this);
    	}

        // risolve collisioni tra palline piccole
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }

        // collisioni player/bot con palline piccole
    	for (var b: balls) {
    		Ball.resolveCollision(playerBall, b);
            Ball.resolveCollision(botBall, b);
    	}

        Ball.resolveCollision(playerBall, botBall);

        //todo implementazione delle palline quando spariscono nei buchi neri

    }

    public synchronized List<Ball> getBalls(){
    	return balls;
    }

    public synchronized Ball getPlayerBall() {
    	return playerBall;
    }

    public synchronized Ball getBotBall() {
        return botBall;
    }

    public synchronized int getPlayerScore() {
        return playerScore;
    }

    public synchronized int getBotScore() {
        return botScore;
    }

    public  Boundary getBounds(){
        return bounds;
    }

    public synchronized void addPointPlayer() {
        playerScore++;
    }

    public synchronized void addPointBot() {
        botScore++;
    }
}
