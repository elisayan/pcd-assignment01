package pcd.concurrent.view;

import pcd.concurrent.model.Board;
import pcd.common.P2d;

import java.util.ArrayList;
import java.util.List;

record BallViewInfo(P2d pos, double radius, boolean isPlayer, boolean isBot) {}

public class ViewModel {

	private ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
	private BallViewInfo bot;
	private int framePerSec;
	private int playerScore;
	private int botScore;

	public ViewModel() {
		balls = new ArrayList<BallViewInfo>();
		framePerSec = 0;
	}
	
	public synchronized void update(Board board, int framePerSec) {
		balls.clear();
		for (var b: board.getBalls()) {
			balls.add(new BallViewInfo(b.getPos(), b.getRadius(), false, false));
		}
		this.framePerSec = framePerSec;
		var p = board.getPlayerBall();
		player = new BallViewInfo(p.getPos(), p.getRadius(), true, false);

		var b = board.getBotBall();
		bot = new BallViewInfo(b.getPos(), b.getRadius(), false, true);
	}
	
//	public synchronized ArrayList<BallViewInfo> getBalls(){
//		var copy = new ArrayList<BallViewInfo>();
//		copy.addAll(balls);
//		return copy;
//
//	}

	public synchronized List<BallViewInfo> getBalls() {
		return new ArrayList<>(balls);
	}

	public synchronized int getFramePerSec() {
		return framePerSec;
	}

	public synchronized BallViewInfo getPlayerBall() {
		return player;
	}

	public synchronized BallViewInfo getBotBall() {
		return bot;
	}

	public synchronized int getPlayerScore() {
		return playerScore;
	}

	public synchronized int getBotScore() {
		return botScore;
	}
	
}
