package main;

import java.util.ArrayList;
import java.util.Random;

public class TileManager {
    public ArrayList<Tile> tileMap = new ArrayList<Tile>();

    // default width, height = 15
    ArrayList<Tile> generateMap(int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tileMap.add(new Tile(i, j));
            }
        }
        return tileMap;
    }

    int getRandomTile(boolean empty) {
        Random random = new Random();
        if (empty) {
            int randomInt;
            do {
                randomInt = random.nextInt(tileMap.size());
            } while (!tileMap.get(randomInt).isEmpty());
            return randomInt;
        } else {
            return random.nextInt(tileMap.size());
        }
    }
}
