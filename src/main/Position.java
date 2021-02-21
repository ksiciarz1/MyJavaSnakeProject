package main;

public final class Position {
    public float x;
    public float y;

    Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }


    boolean contains(float x, float y) {
        if (this.x == x && this.y == y) {
            return true;
        } else return false;
    }

    boolean contains(float[] tab) {
        if (this.x == tab[0] && this.y == tab[1]) {
            return true;
        } else return false;
    }

    float[] getFloatArray() {
        float[] array = new float[2];
        array[0] = x;
        array[1] = y;
        return array;
    }

    @Override
    public String toString() {
        return x + ":" + y;
    }
}
