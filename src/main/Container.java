package main;

public class Container {
    CONTAINER inside;

    void insertIntoContainer(CONTAINER containerEnum) {
        switch (containerEnum) {
            /*
             *   fruits
             *   boosters
             *   snake
             */
        }
    }

    void destory() {
        inside = null;
    }

    public enum CONTAINER {
        FRUIT_APPLE,
        FRUIT_ORANGE,
        FRUIT_PINAPPLE,
        BOOSTER_VAPE,
        BOOSTER_GAMINATE,
        SNAKE_HEAD,
        SNAKE_BODY,
        SNAKE_TAIL,
        SNAKE_TIP
    }
}
