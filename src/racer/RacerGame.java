package racer;

import com.javarush.engine.cell.*;
import racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2;
    public static final int ROADSIDE_WIDTH = 14;
    private static final int RACE_GOAL_CARS_COUNT = 40;
    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private FinishLine finishLine;
    private ProgressBar progressBar;
    private boolean isGameStopped;
    private int score;

    @Override
    public void initialize() {
        showGrid(false);
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame() {
        score = 3500;
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        isGameStopped = false;
        drawScene();
        setTurnTimer(40);
    }

    private void drawScene() {
        drawField();
        roadMarking.draw(this);
        player.draw(this);
        roadManager.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }

    private void drawField() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == CENTER_X) setCellColor(x, y, Color.WHITE);
                else if (x >= ROADSIDE_WIDTH && x < (WIDTH-ROADSIDE_WIDTH)) setCellColor(x, y, Color.GRAY);
                else setCellColor(x, y, Color.GREEN);
            }
        }
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        if (x < 0 || y < 0 || x > HEIGHT - 1 || y > WIDTH - 1) return;
        super.setCellColor(x, y, color);
    }

    private void moveAll() {
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    @Override
    public void onTurn (int step) {
        setScore(score -= 5);

        if (roadManager.checkCrush(player)) {
            gameOver();
            drawScene();
            return;
        }

        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) finishLine.show();

        if (finishLine.isCrossed(player)) {
            win();
            drawScene();
            return;
        }

        roadManager.generateNewRoadObjects(this);
        drawScene();
        moveAll();
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.RIGHT) player.setDirection(Direction.RIGHT);
        else if (key == Key.LEFT) player.setDirection(Direction.LEFT);
        else if (key == Key.UP) player.speed = 2;
        else if (key == Key.SPACE && isGameStopped) createGame();
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.RIGHT && player.getDirection() == Direction.RIGHT) player.setDirection(Direction.NONE);
        else if (key == Key.LEFT && player.getDirection() == Direction.LEFT) player.setDirection(Direction.NONE);
        else if (key == Key.UP) player.speed = 1;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Вы проиграли!", Color.WHITE, 50);
        stopTurnTimer();
        player.stop();
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Вы победили!", Color.WHITE, 50);
        stopTurnTimer();
    }
}