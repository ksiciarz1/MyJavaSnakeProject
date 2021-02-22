package main;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

class MyMainGameWindow<i> {

    boolean spawnFruit = false;
    GLFWKeyCallback KeyPressed;
    boolean addSize = false;
    TileManager tileManager = new TileManager();
    ArrayList<Tile> tileMap = tileManager.generateMap(15, 15);
    Snake snake = new Snake();
    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Snake", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        // glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
        //     if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        //         glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        // });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowSize(window, 300, 300);

            // Center the window
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
            // Center the window but really not just for joke
            // glfwSetWindowPos(window, 50, 50);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        // glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    int tilePositionToTileNumber(Position tilePosition) {
        int computedNumber = 0;
        for (int i = 0; i < tilePosition.x; i++) {
            computedNumber += 15;
        }
        computedNumber += tilePosition.y;
        return computedNumber;
    }

    Container.CONTAINER checkForCollision(Position collisionPosition) {
        return tileMap.get(tilePositionToTileNumber(collisionPosition)).container.inside;
    }

    Position tileNumberToPosition(int tileNumber) {
        Position computedPosition;
        int x = 0, y = 0;
        while (tileNumber % 15 == 0) {
            tileNumber = tileNumber / 15;
            x++;
        }
        y = tileNumber;
        computedPosition = new Position(x, y);
        return computedPosition;
    }

    Position getForwardSnakePosition() {
        Position snakeForwardPosition = new Position(snake.snakePositions.get(0));
        switch (snake.direction) {
            case 0:
                snakeForwardPosition.y--;
                break;
            case 1:
                snakeForwardPosition.x++;
                break;
            case 2:
                snakeForwardPosition.y++;
                break;
            case 3:
                snakeForwardPosition.x--;
                break;
        }
        return snakeForwardPosition;
    }

    void updateSnakePosition() {
        // Handle snake direction
        for (int i = snake.snakePositions.size() - 1; i >= 0; i--) {
            if (i == 0) {
                switch (snake.direction) {
                    case 0:
                        snake.snakePositions.get(i).y--;
                        break;
                    case 1:
                        snake.snakePositions.get(i).x++;
                        break;
                    case 2:
                        snake.snakePositions.get(i).y++;
                        break;
                    case 3:
                        snake.snakePositions.get(i).x--;
                        break;
                }
            } else {
                float differanceX = snake.snakePositions.get(i).x - snake.snakePositions.get(i - 1).x;
                float differanceY = snake.snakePositions.get(i).y - snake.snakePositions.get(i - 1).y;
                // System.out.println("X: " + differanceX + " Y: " + differanceY);
                switch ((int) differanceX) {
                    case -1:
                        snake.snakePositions.get(i).x++;
                        break;
                    case 0:
                        break;
                    case 1:
                        snake.snakePositions.get(i).x--;
                        break;
                    default:
                        System.out.println("FUCKING DEFAULT X");
                        break;
                }
                switch ((int) differanceY) {
                    case -1:
                        snake.snakePositions.get(i).y++;
                        break;
                    case 0:
                        break;
                    case 1:
                        snake.snakePositions.get(i).y--;
                        break;
                    default:
                        System.out.println("FUCKING DEFAULT Y");
                        break;
                }
            }
        }
        snake.directionChanged = false;
    }


    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 1.0f, 0.0f);

        // Keyboard Events
        glfwSetKeyCallback(window, KeyPressed = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                switch (action) {
                    case GLFW_PRESS:
                        switch (key) {
                            case GLFW_KEY_W:
                            case GLFW_KEY_UP:
                                snake.setDirection(Snake.DIRECTION.UP);
                                break;
                            case GLFW_KEY_D:
                            case GLFW_KEY_RIGHT:
                                snake.setDirection(Snake.DIRECTION.RIGHT);
                                break;
                            case GLFW_KEY_S:
                            case GLFW_KEY_DOWN:
                                snake.setDirection(Snake.DIRECTION.DOWN);
                                break;
                            case GLFW_KEY_A:
                            case GLFW_KEY_LEFT:
                                snake.setDirection(Snake.DIRECTION.LEFT);
                                break;
                            case GLFW_KEY_ESCAPE:
                                glfwSetWindowShouldClose(window, true);
                                break;
//                            case GLFW_KEY_G:
//                                addSize = true;
//                                break;
                        }
                        break;
                    case GLFW_RELEASE:
                        switch (key) {
                        }
                        break;
                }
            }
        });

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        // This is a perspective
        glOrtho(0, 300, 300, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        int gameTimer = 120;
        int gaminateOnMap = -1;
        int vapeOnMap = -1;
        Random random = new Random();
        int vapeTimer = random.nextInt(5) + 5;

        snake.start(3, 3);

        while (!glfwWindowShouldClose(window)) {
            glfwSwapBuffers(window); // swap the color buffers
            gameTimer++;

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            // Tiles loop
            for (Tile tile : tileMap) {
                boolean changed = false;
                // Looking for boosters
                if (tilePositionToTileNumber(tile.position) == gaminateOnMap) {
                    changed = true;
                    tile.container.inside = Container.CONTAINER.BOOSTER_GAMINATE;
                    glColor3f(1, 1, 1);
                } else if (tilePositionToTileNumber(tile.position) == vapeOnMap) {
                    changed = true;
                    tile.container.inside = Container.CONTAINER.BOOSTER_VAPE;
                    glColor3f(1, 0, 1);
                } else {
                    glColor3f(0, 1, 0);
                }
                glBegin(GL_QUAD_STRIP);
                glVertex2f(tile.position.x * 20, tile.position.y * 20);
                glVertex2f(tile.position.x * 20, (tile.position.y + 1) * 20);
                glVertex2f((tile.position.x + 1) * 20, (tile.position.y + 1) * 20);
                glVertex2f((tile.position.x + 1) * 20, tile.position.y * 20);
                glEnd();
                // Checking for tiles with snake on it
                for (Position position : snake.snakePositions) {
                    if ((tile.position.x == position.x) && (tile.position.y == position.y)) {
                        changed = true;
                        tile.container.inside = Container.CONTAINER.SNAKE_BODY;
                        glColor3f(1, 0, 0);
                        if (position == snake.snakePositions.get(0) || position == snake.snakePositions.get(snake.snakePositions.size() - 1)) {
                            glBegin(GL_QUAD_STRIP);
                        } else {
                            glBegin(GL_QUADS);
                        }
                        glVertex2f((tile.position.x * 20) + 2, (tile.position.y * 20) + 2);
                        glVertex2f((tile.position.x * 20) + 2, ((tile.position.y + 1) * 20) - 2);
                        glVertex2f(((tile.position.x + 1) * 20) - 2, ((tile.position.y + 1) * 20) - 2);
                        glVertex2f(((tile.position.x + 1) * 20) - 2, (tile.position.y * 20) + 2);
                        glEnd();
                    }
                }
                // Empty tiles reset to being empty
                if (!changed) {
                    tile.makeEmpty();
                }
            }
            if (gameTimer >= 60) {
                // Snake collision game over
                for (int i = 0; i < snake.snakePositions.size(); i++) {
                    if ((snake.snakePositions.get(i).x == getForwardSnakePosition().x) && (snake.snakePositions.get(i).y == getForwardSnakePosition().y)) {
                        System.out.println("Collision Game Over");
                        glfwSetWindowShouldClose(window, true);
                    }
                }

                // Out of bounds game over
                if ((snake.snakePositions.get(0).x < 0 || snake.snakePositions.get(0).y < 0) || (snake.snakePositions.get(0).x > 14 || snake.snakePositions.get(0).y > 14)) {
                    System.out.println("Out of map Game Over");
                    glfwSetWindowShouldClose(window, true);
                }
                // Vape game over
                if (tileMap.get(tilePositionToTileNumber(getForwardSnakePosition())).container.inside == Container.CONTAINER.BOOSTER_VAPE) {
                    System.out.println("Vape Game Over");
                    glfwSetWindowShouldClose(window, true);
                }
                // Gaminate +1 snake body length
                if (tileMap.get(tilePositionToTileNumber(getForwardSnakePosition())).container.inside == Container.CONTAINER.BOOSTER_GAMINATE) {
                    System.out.println("Snake body Added");
                    snake.addSize();
                    tileMap.get(tilePositionToTileNumber(getForwardSnakePosition())).container.destory();
                    gaminateOnMap = -1;
                }

                // Handling game tick
                updateSnakePosition();
                gameTimer = 0;

                // If Gaminete isn't on map spawn one in empty tile
                if (gaminateOnMap == -1) {
                    gaminateOnMap = tileManager.getRandomTile(true);
                }
                // Handle vape teleportation
                if (vapeTimer <= 0) {
                    Position vapePosition;
                    int differance = 3;
                    int counter = 0;
                    do {
                        vapeOnMap = tileManager.getRandomTile(true);
                        vapePosition = new Position(tileNumberToPosition(vapeOnMap));
                        System.out.println(counter);
                        counter++;
                    }
                    while (
                            (vapePosition.x - snake.snakePositions.get(0).x < differance && vapePosition.x - snake.snakePositions.get(0).x > -differance) &&
                                    (vapePosition.y - snake.snakePositions.get(0).y < differance && vapePosition.y - snake.snakePositions.get(0).y > -differance));
                    vapeTimer = random.nextInt(10) + 5; // This should avoid vape spawning to near snake head
                }
                // For debugging
//                if (addSize) {
//                    snake.addSize();
//                    addSize = false;
//                }
                vapeTimer--;
                System.out.println("////////////");
            }
        }
    }
}