package racer.road;

import com.javarush.engine.cell.Game;
import racer.*;

import java.util.ArrayList;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private static final int PLAYER_CAR_DISTANCE = 12;
    private List<RoadObject> items = new ArrayList<>();
    private int passedCarsCount = 0;

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if (type == RoadObjectType.THORN) return new Thorn(x, y);
        else if (type == RoadObjectType.DRUNK_CAR) return new MovingCar(x, y);
        else return new Car(type, x, y);
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);

        RoadObject roadObject = createRoadObject(type, x, y);

        if (isRoadSpaceFree(roadObject)) items.add(createRoadObject(type, x, y));
    }

    public void draw(Game game) {
        items.forEach(roadObject -> roadObject.draw(game));
    }

    public void move(int boost) {
        items.forEach(roadObject -> roadObject.move(boost + roadObject.speed, items));
        deletePassedItems();
    }

    private boolean isThornExists() {
        for (RoadObject roadObject: items) {
            if (roadObject instanceof Thorn) return true;
        }

        return false;
    }

    private void generateThorn(Game game) {
        int randomNumber = game.getRandomNumber(100);

        if (randomNumber < 10 && !isThornExists()) addRoadObject(RoadObjectType.THORN, game);
    }

    public void generateNewRoadObjects(Game game) {
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void deletePassedItems() {
        for(RoadObject roadObject: new ArrayList<>(items)) {
            if (roadObject.y >= RacerGame.HEIGHT) {
                items.remove(roadObject);

                if (!(roadObject instanceof Thorn)) {
                    passedCarsCount++;
                }
            }
        }
    }

    public boolean checkCrush(PlayerCar playerCar) {
        for (RoadObject roadObject: items) {
            final boolean isCollision = roadObject.isCollision(playerCar);

            if (isCollision) return true;
        }

        return false;
    }

    private void generateRegularCar(Game game) {
        int randomNumber = game.getRandomNumber(100);
        int carTypeNumber = game.getRandomNumber(4);

        if (randomNumber < 30) addRoadObject(RoadObjectType.values()[carTypeNumber], game);
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        for (RoadObject roadObject: items) {
            if (roadObject.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE)) {
                return false;
            }
        }

        return true;
    }

    private boolean isMovingCarExists() {
        for (RoadObject roadObject: items) {
            if (roadObject instanceof MovingCar) return true;
        }

        return false;
    }

    private void generateMovingCar(Game game) {
        int randomNumber = game.getRandomNumber(100);

        if (randomNumber < 10 && !isMovingCarExists()) addRoadObject(RoadObjectType.DRUNK_CAR, game);
    }

    public int getPassedCarsCount() {
        return passedCarsCount;
    }
}
