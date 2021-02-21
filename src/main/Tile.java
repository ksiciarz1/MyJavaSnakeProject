package main;

public class Tile {
    boolean isEmpty = true;

    Position position;
    Container container = new Container();

    Tile(int x, int y) {
        position = new Position(x, y);
    }

    void makeEmpty() {
        isEmpty = true;
        container.destory();
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}

// 60px 1 Tile