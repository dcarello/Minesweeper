package pkgSlRenderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils.*;
import org.lwjgl.opengl.GL30.*;
import java.nio.IntBuffer.*;
import java.nio.*;
import static org.lwjgl.opengl.GL33.*;

import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import pkgKeyListener.DCKeyListener;
import pkgPingPong.DCPingPong;

import static org.lwjgl.opengl.GL11.*;

public class DCPolygonRenderer extends slRenderEngine{

    private float[][] center_coords;
    private int NUM_ROWS;
    private int NUM_COLS;
    private int FRAME_DELAY;

    private int eboID;
    private int VPT = 4;
    private int[] rgVertexIndices;
    private IntBuffer VertexIndicesBuffer;

    // Thread to handle Interactive controls
    private void startInteractiveThread(DCPingPong myPingPong){
        Thread InteractiveThread = new Thread(() ->{
            while (!my_wm.isGlfwWindowClosed()) {
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
            }
        });
        InteractiveThread.start();
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
        C_RADIUS = radiusFinder(NUM_ROWS, NUM_COLS) * 1.9f;
        MAX_POLYGONS = numPolygons(NUM_ROWS, NUM_COLS);
        FRAME_DELAY = FRAME_DELAY_INPUT;

        initializeArrays();
        findCenterCoords(NUM_COLS);

        // Set the color factor (this can be adjusted to any color you want)
        Vector4f COLOR_FACTOR = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);


        startInteractiveThread(myPingPong);

        while (!my_wm.isGlfwWindowClosed()) {
            updateRandVerticesRandColors();

            glfwPollEvents();

//            glClearColor(0.0f, 0.0f, 1.0f, 1.0f); // Set the clear color to blue
            glClear(GL_COLOR_BUFFER_BIT);

            if (FRAME_DELAY != 0){
                Delay(FRAME_DELAY);
            }

            // Loop through rows and columns to draw each square
            for (int row = 0; row < NUM_ROWS; row++) {
                for (int col = 0; col < NUM_COLS; col++) {
                    my_so.loadVector4f("COLOR_FACTOR", COLOR_FACTOR);
                    renderTile(row, col);
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
        rgVertexIndices = new int[] {va_offset, va_offset+1, va_offset+2, va_offset+2, va_offset+3, va_offset};
        VertexIndicesBuffer = BufferUtils.createIntBuffer(rgVertexIndices.length);
        VertexIndicesBuffer.put(rgVertexIndices).flip();
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexIndicesBuffer, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, rgVertexIndices.length, GL_UNSIGNED_INT, 0);
    } // public void renderTile(...)

    // Method to render a specific tile (square) at the given row and column
//    public void renderTile(int row, int col) {
//        int va_offset = getVAVIndex(row, col); // Get the starting index of the tile's vertices in the vertex array
//
//        // Indices for the two triangles that form the square
//        int[] rgVertexIndices = new int[] {
//                va_offset, va_offset + 1, va_offset + 2,  // First triangle
//                va_offset + 2, va_offset + 3, va_offset   // Second triangle
//        };
//
//        // Create a buffer for storing indices
//        VertexIndicesBuffer = BufferUtils.createIntBuffer(6);
//
//        // Update the index buffer for this tile
//
//        VertexIndicesBuffer.clear();
//        VertexIndicesBuffer.put(rgVertexIndices).flip();
//
//        // Generate and bind the Element Buffer Object (EBO)
//        eboID = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexIndicesBuffer, GL_STATIC_DRAW);
//
//        // Use the shader program
//        glUseProgram(my_so.getShader_program());
//        checkGLErrors("Using Shader Program");
//
//        // Set the color factor (this can be adjusted to any color you want)
//        Vector4f COLOR_FACTOR = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
//        my_so.loadVector4f("COLOR_FACTOR", COLOR_FACTOR); // Green
//
//        // Bind the VAO
//        glBindVertexArray(vaoID);
//        checkGLErrors("Binding VAO");
//
//        // Draw the tile using the calculated indices
//        glDrawElements(GL_TRIANGLES, rgVertexIndices.length, GL_UNSIGNED_INT, 0);
//        checkGLErrors("Drawing Elements");
//    }

    // Method to calculate the vertex array index for a given tile
    public int getVAVIndex(int row, int col) {
        return (row * NUM_COLS + col) * VPT; // Each square has 4 vertices, 2 rows and 2 columns
    }

    private void checkGLErrors(String stage) {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            System.out.println("OpenGL error (" + stage + "): " + error);
        }
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
