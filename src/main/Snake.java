package main;

import java.util.ArrayList;

public class Snake {
    public int direction = 1;
    public boolean addSize = false;
    public boolean directionChanged = false;
    ArrayList<Position> snakePositions = new ArrayList<Position>();

    void start(int x, int y) {
        snakePositions.add(new Position(x, y));
    }

    void addSize() {
        Position lastSnakePosition = new Position(snakePositions.get(snakePositions.size() - 1).x, snakePositions.get(snakePositions.size() - 1).y);

        switch (direction) {
            case 0:
                snakePositions.add(new Position(lastSnakePosition.x, lastSnakePosition.y--));
                break;
            case 1:
                snakePositions.add(new Position(lastSnakePosition.x++, lastSnakePosition.y));
                break;
            case 2:
                snakePositions.add(new Position(lastSnakePosition.x, lastSnakePosition.y++));
                break;
            case 3:
                snakePositions.add(new Position(lastSnakePosition.x--, lastSnakePosition.y));
                break;
        }
    }

    void setDirection(DIRECTION direction) {
        if (!directionChanged) {
            switch (direction) {
                case UP:
                    if (this.direction != 2) {
                        this.direction = 0;
                        directionChanged = true;
                    }
                    break;
                case RIGHT:
                    if (this.direction != 3) {
                        this.direction = 1;
                        directionChanged = true;
                    }
                    break;
                case DOWN:
                    if (this.direction != 0) {
                        this.direction = 2;
                        directionChanged = true;
                    }
                    break;
                case LEFT:
                    if (this.direction != 1) {
                        this.direction = 3;
                        directionChanged = true;
                    }
                    break;
            }
        }
    }

    public enum DIRECTION {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}
