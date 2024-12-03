package pkgSlRenderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static pkgDriver.slSpot.*;

import DCMineSweeperBE.MSB;
import org.joml.Vector4f;

import java.nio.*;
import static org.lwjgl.opengl.GL33.*;

import org.lwjgl.BufferUtils;
import pkgKeyListener.DCKeyListener;
import pkgKeyListener.DCMouseListener;
import pkgPingPong.DCPingPong;

public class DCPolygonRenderer extends slRenderEngine{

    private float[][] center_coords;
    private int NUM_ROWS;
    private int NUM_COLS;
    private int FRAME_DELAY;

    private int eboID;
    private int VPT = 4;
    private int[] rgVertexIndices;
    private IntBuffer VertexIndicesBuffer;
    private float[] VERTICES;
    private final int FloatsPerSquare = 20;

    private final int TOTAL_TEXTURES = 3;
    private final int MYSTERY_TEXTURE = 0;
    private final int GOLD_TEXTURE = 1;
    private final int MINE_TEXTURE = 2;
    DCTextureObject[] my_to = new DCTextureObject[TOTAL_TEXTURES];

    private MSB board = new MSB();



    // Thread to handle Interactive controls
    private void startInteractiveThread(DCPingPong myPingPong){
        Thread InteractiveThread = new Thread(() ->{
            while (!my_wm.isGlfwWindowClosed()) {
                // Interactive Keyboard Listener
                if (DCKeyListener.isKeyPressed(GLFW_KEY_I) ) {
                    FRAME_DELAY += 500;
                    System.out.println("+++ Frame delay is now: " + FRAME_DELAY + " ms!");
                    DCKeyListener.resetKeypressEvent(GLFW_KEY_I);
                }
                if (DCKeyListener.isKeyPressed(GLFW_KEY_D)) {
                    if (FRAME_DELAY < 500){
                        FRAME_DELAY = 0;
                        System.out.println("--- Frame delay is now: " + FRAME_DELAY + " ms!");
                        DCKeyListener.resetKeypressEvent(GLFW_KEY_D);
                    }else{
                        FRAME_DELAY -= 500;
                        System.out.println("--- Frame delay is now: " + FRAME_DELAY + " ms!");
                        DCKeyListener.resetKeypressEvent(GLFW_KEY_D);
                    }

                }
                if (DCKeyListener.isKeyPressed(GLFW_KEY_R)){
                    myPingPong.boardReset();
                    DCKeyListener.resetKeypressEvent(GLFW_KEY_R);

                }

                // Interactive Mouse Listener
                if (DCMouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)){
                    float mx = DCMouseListener.getX();
                    float my = DCMouseListener.getY();
                    float col, row;
                    float spaceInBetween = ((float)POLYGON_LENGTH / (POLYGON_LENGTH + POLY_PADDING));

                    col =  ((mx - POLY_OFFSET) / (POLYGON_LENGTH + POLY_PADDING));
                    row =  ((my - POLY_OFFSET) / (POLYGON_LENGTH + POLY_PADDING));
                    if ((row > 0 && col > 0)){
                        if ((row == (int)row || row < (int)row + spaceInBetween) && (col == (int)col || col < (int)col + spaceInBetween)){
                            if ((int)row < NUM_ROWS && (int)col < NUM_COLS){
                                System.out.println("(" + (int)col + ", " + (int)row + ")");
                                board.setCellExposed((NUM_POLY_ROWS - 1) - (int)row, (int)col, true);
                                
                            }
                        }
                    }

                    DCMouseListener.mouseButtonDownReset(GLFW_MOUSE_BUTTON_1);
                }
            }
        });
        InteractiveThread.start();
    }

    private void initPipeline(){
        // vertex array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // vertex buffer object
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        // Get Vertices of Squares
        VERTICES = new float[NUM_ROWS * NUM_COLS * FloatsPerSquare];
        getVertexData(NUM_ROWS, NUM_COLS);

        // connect data to vbo
        FloatBuffer myFB = BufferUtils.createFloatBuffer(VERTICES.length);
        myFB.put(VERTICES);
        myFB.flip();
        glBufferData(GL_ARRAY_BUFFER, myFB, GL_STATIC_DRAW);

        // Attributes
        int loc0 = 0, loc1 = 1, positionStride = 3, vertexStride = 5, tstride = 2;
        glVertexAttribPointer(loc0, positionStride, GL_FLOAT, false, vertexStride * Float.BYTES, 0); // Positions
        glEnableVertexAttribArray(loc0);
        glVertexAttribPointer(loc1, tstride, GL_FLOAT, false, vertexStride * Float.BYTES, positionStride * Float.BYTES); // Textures
        glEnableVertexAttribArray(loc1);

        // Shader Object
        my_so = new DCShaderObject("assets/shaders/vs_texture_1.glsl", "assets/shaders/fs_texture_2.glsl");
        my_so.compileShader();
        my_so.setShaderProgram();

        // Camera Object
        my_c = new DCCamera();
        my_so.loadMatrix4f("uProjMatrix", my_c.getProjectionMatrix());
        my_so.loadMatrix4f("uViewMatrix", my_c.getViewMatrix());
    }

    // First overload given frame delay, num rows, num cols calculates radius to render the polygons
    public void render(int FRAME_DELAY, int NUM_ROWS, int NUM_COLS) {
        C_RADIUS = radiusFinder(NUM_ROWS, NUM_COLS);
        MAX_POLYGONS = numPolygons(NUM_ROWS, NUM_COLS);

        initializeArrays();
        findCenterCoords(NUM_COLS);

        while (!my_wm.isGlfwWindowClosed()) {
            polygonPrinting(FRAME_DELAY);
        } // while (!my_wm.isGlfwWindowClosed())
        my_wm.destroyGlfwWindow();
    } // public void render(...)

    // Second Overload given radius renders in a calculated number of polygons
    public void render(float RADIUS) {
        C_RADIUS = RADIUS;
        rowColFinder();
        MAX_POLYGONS = numPolygons(NUM_ROWS, NUM_COLS);
        int FRAME_DELAY = 500;

        initializeArrays();
        findCenterCoords(NUM_COLS);

        while (!my_wm.isGlfwWindowClosed()) {
            polygonPrinting(FRAME_DELAY);
        } // while (!my_wm.isGlfwWindowClosed())
        my_wm.destroyGlfwWindow();
    } // public void render(...)

    // Third Default Overload
    public void render() {
        NUM_ROWS = 30;
        NUM_COLS = 30;
        int FRAME_DELAY = 500;
        C_RADIUS = radiusFinder(NUM_ROWS, NUM_COLS);
        MAX_POLYGONS = numPolygons(NUM_ROWS, NUM_COLS);

        initializeArrays();
        findCenterCoords(NUM_COLS);

        while (!my_wm.isGlfwWindowClosed()) {
           polygonPrinting(FRAME_DELAY);
        } // while (!my_wm.isGlfwWindowClosed())
        my_wm.destroyGlfwWindow();
    } // public void render(...)

    public void render(int FRAME_DELAY_INPUT, int NUM_ROWS, int NUM_COLS, DCPingPong myPingPong){
        this.NUM_COLS = NUM_COLS;
        this.NUM_ROWS = NUM_ROWS;
        initializeArrays();
        initPipeline();

//        FRAME_DELAY = 1000;

        // Set the color factor (this can be adjusted to any color you want)
        Vector4f COLOR_FACTOR = new Vector4f(1.f, 1.0f, 1.0f, 1.0f);

        my_to[0] = new DCTextureObject("assets/images/MysteryBox_2.PNG");
        my_to[1] = new DCTextureObject("assets/images/ShiningDiamond_2.PNG");
        my_to[2] = new DCTextureObject("assets/images/MineBomb_2.PNG");

        startInteractiveThread(myPingPong);

        board.printMineBoard();

        System.out.println();

        board.printPointsBoard();

        while (!my_wm.isGlfwWindowClosed()) {
            updateRandVerticesRandColors();

            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT);

            if (FRAME_DELAY != 0){
                Delay(FRAME_DELAY);
            }

            // Loop through rows and columns to draw each square
            int renderedTiles = 0;
            int MAX_TILES = NUM_POLY_COLS * NUM_POLY_ROWS;

                for (int row = 0; row < NUM_ROWS; row++) {
                    for (int col = 0; col < NUM_COLS; col++) {
//                        if (renderedTiles >= MAX_TILES) break;

                        // Load specific texture only once per type
                        my_so.loadVector4f("COLOR_FACTOR", COLOR_FACTOR);

                        // Determine the rendering condition
                        if (!board.getCellExposed(row, col)) {
                            my_to[MYSTERY_TEXTURE].loadImageToTexture();
                            renderTile(row, col);
                            renderedTiles++;
                        } else if (board.getCellExposed(row, col) && !board.getCellMine(row, col)) {
                            my_to[GOLD_TEXTURE].loadImageToTexture();
                            renderTile(row, col);
                            renderedTiles++;
                        } else {
                            my_to[MINE_TEXTURE].loadImageToTexture();
                            renderTile(row, col);
                            renderedTiles++;
                        }
                    }
                }

            my_wm.swapBuffers();
        } // while (!my_wm.isGlfwWindowClosed())
        my_wm.destroyGlfwWindow();
    }

    //    Render the particular tile
    public void renderTile(int row, int col) {
        // Compute the vertexArray offset
        int va_offset = getVAVIndex(row, col); // vertex array offset of tile
        rgVertexIndices = new int[] {va_offset, va_offset+1, va_offset+2, va_offset, va_offset+2, va_offset+3};
        VertexIndicesBuffer = BufferUtils.createIntBuffer(rgVertexIndices.length);
        VertexIndicesBuffer.put(rgVertexIndices).flip();
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexIndicesBuffer, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, rgVertexIndices.length, GL_UNSIGNED_INT, 0);
    } // public void renderTile(...)

    // Method to calculate the vertex array index for a given tile
    public int getVAVIndex(int row, int col) {
        return (row * NUM_COLS + col) * VPT; // Each square has 4 vertices, 2 rows and 2 columns
    }

    private void drawSquare(float x, float y, float size, boolean alive) {
        glBegin(GL_TRIANGLES);
        if (alive){
            glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
        }else{
            glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        }

        // Draw the first triangle
        glVertex2f(x - size / 2, y + size / 2);   // Top-left
        glVertex2f(x + size / 2, y + size / 2);   // Top-right
        glVertex2f(x - size / 2, y - size / 2);   // Bottom-left

        // Draw the second triangle
        glVertex2f(x + size / 2, y + size / 2);   // Top-right
        glVertex2f(x + size / 2, y - size / 2);   // Bottom-right
        glVertex2f(x - size / 2, y - size / 2);   // Bottom-left
        glEnd();
    }

    // Initializes the arrays for random colors, and the coordinates
    private void initializeArrays(){
        center_coords = new float[MAX_POLYGONS][NUM_3D_COORDS];
        rand_colors = new float[MAX_POLYGONS][NUM_RGBA];
        rand_coords = new float[MAX_POLYGONS][NUM_3D_COORDS];
    }

    // Finds the radius given the number of rows and columns
    private float radiusFinder(int NUM_ROWS, int NUM_COLS) {
        float radius;
        if (NUM_COLS > NUM_ROWS){
            radius = 1.0f / NUM_COLS;
        }else{
            radius = 1.0f / NUM_ROWS;
        }
        return radius;
    }

    // given the radius it determines the amound of rows and columns
    private void rowColFinder(){
        NUM_COLS = (int) (1 / C_RADIUS);
        NUM_ROWS = (int) (1 / C_RADIUS);
    }

    // Determines the total number of polygons in the array based on the number of rows and columns
    private int numPolygons(int NUM_ROWS, int NUM_COLS){
        return NUM_ROWS * NUM_COLS;
    }

    private void getVertexData(int NUM_ROWS, int NUM_COLS){
        int index = 0;
        float x = 0.0f + POLY_OFFSET;
        float y = 0.0f + POLY_OFFSET;
        for (int row = 0; row < NUM_ROWS; row++){
            for (int col = 0; col < NUM_COLS; col++){
                VERTICES[index++] = x; VERTICES[index++] = y; VERTICES[index++] = 0.0f; VERTICES[index++] = 0.0f; VERTICES[index++] = 0.0f; // Bottom Left
                VERTICES[index++] = x + POLYGON_LENGTH; VERTICES[index++] = y; VERTICES[index++] = 0.0f; VERTICES[index++] = 1.0f; VERTICES[index++] = 0.0f; // Bottom Right
                VERTICES[index++] = x + POLYGON_LENGTH; VERTICES[index++] = y + POLYGON_LENGTH; VERTICES[index++] = 0.0f; VERTICES[index++] = 1.0f; VERTICES[index++] = 1.0f; // Top Right
                VERTICES[index++] = x; VERTICES[index++] = y + POLYGON_LENGTH; VERTICES[index++] = 0.0f; VERTICES[index++] = 0.0f; VERTICES[index++] = 1.0f; // Top Left

                x += POLYGON_LENGTH + POLY_PADDING;
            }
            y += POLYGON_LENGTH + POLY_PADDING;
            x = 0.0f + POLY_OFFSET;
        }
    }

    // Finds the center coordinates for each polygon in the array
    private void findCenterCoords(int NUM_COLS) {
        float stepDiameter = 2 * C_RADIUS;
        float leftBorder = -1.0f;
        float floatingPointAdjust = 0.000001f;
        float rightBorder = 1.0f + floatingPointAdjust;
        float topBorder = 1.0f;

        float x = leftBorder + C_RADIUS;
        float y = topBorder - C_RADIUS;
        int colNum = 1;

        for (int polygon = 0; polygon < MAX_POLYGONS; polygon++){
            center_coords[polygon][0] = x;
            center_coords[polygon][1] = y;

            if (colNum == NUM_COLS){
                x = leftBorder + C_RADIUS;
                y -= stepDiameter;
                colNum = 1;
                continue;
            }
            colNum++;
            x += stepDiameter;
        }
    }

    // Delay between frames
    private void Delay(int FRAME_DELAY){
        try {
            Thread.sleep(FRAME_DELAY);
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted during sleep.");
        }
    }

    // Prints each of the polygons to the screen
    private void polygonPrinting(int FRAME_DELAY){
        if (NUMBER_OF_SIDES >= 20){
            NUMBER_OF_SIDES = 3;
        }

        updateRandVerticesRandColors();

        glfwPollEvents();

        glClear(GL_COLOR_BUFFER_BIT);

        if (FRAME_DELAY != 0){
            Delay(FRAME_DELAY);
        }

        for (int polygon = 0; polygon < MAX_POLYGONS; polygon++){
            renderPolygon(center_coords[polygon][0], center_coords[polygon][1], rand_colors[0]);
        }
        // Increases number of sides on polygon by 1
        NUMBER_OF_SIDES++;

        my_wm.swapBuffers();
    }
}
