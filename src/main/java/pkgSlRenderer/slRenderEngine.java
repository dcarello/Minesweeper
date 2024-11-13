package pkgSlRenderer;

import pkgPingPong.DCPingPong;
import pkgSlUtils.slWindowManager;
import java.util.Random;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

public abstract class slRenderEngine {
    protected final int NUM_RGBA = 4;
    protected final int NUM_3D_COORDS = 3;
    protected int NUMBER_OF_SIDES = 40;
    protected float C_RADIUS = 0.05f;
    protected int MAX_POLYGONS = 100;
    protected final int UPDATE_INTERVAL = 0;

    protected float[][] rand_colors;
    protected float[][] rand_coords;

    protected final slWindowManager my_wm = slWindowManager.get();
    Random my_rand = new Random();

    // Extended Class Functions
    public abstract void render(int FRAME_DELAY, int NUM_ROWS, int NUM_COLS);
    public abstract void render(float RADIUS);
    public abstract void render();
    public abstract void render(int FRAME_DELAY, int NUM_ROWS, int NUM_COLS, DCPingPong myPingPong);

        public void initOpenGL(slWindowManager my_wm){
            my_wm.setKeyCallback();

            my_wm.updateContextToThis();

            GL.createCapabilities();

            my_wm.enableResizeWindowCallback();

            float CC_RED = 0.0f, CC_GREEN = 0.0f, CC_BLUE = 1.0f, CC_ALPHA = 1.0f;
            glClearColor(CC_RED, CC_GREEN, CC_BLUE, CC_ALPHA);
    }

    protected void updateRandVerticesRandColors(){
        for (int circle = 0; circle < MAX_POLYGONS; circle++){
            rand_coords[circle][0] = (my_rand.nextFloat() * (2.0f * (1 - C_RADIUS)) - (1.0f - C_RADIUS));
            rand_coords[circle][1] = (my_rand.nextFloat() * (2.0f * (1 - C_RADIUS)) - (1.0f - C_RADIUS));

            // Random RGBA color
            rand_colors[circle][0] = my_rand.nextFloat();
            rand_colors[circle][1] = my_rand.nextFloat();
            rand_colors[circle][2] = my_rand.nextFloat();
        }
    }

//    public void render() {
//
//        rand_coords = new float[MAX_POLYGONS][NUM_3D_COORDS];
//        rand_colors = new float[MAX_POLYGONS][NUM_RGBA];
//        // Initial random vertices and colors
//        updateRandVerticesRandColors();
//
//
//        long lastUpdateTime = System.currentTimeMillis();
//
//        while (!my_wm.isGlfwWindowClosed()) {
//            glfwPollEvents();
//
//            glClear(GL_COLOR_BUFFER_BIT);
//
//            // Check if the update interval has passed
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
//                // Update the random positions and colors
//                updateRandVerticesRandColors();
//                lastUpdateTime = currentTime;  // Reset the last update time
//            }
//
//
//            for (int circle = 0; circle < MAX_POLYGONS; circle++){
//                renderPolygon(rand_coords[circle][0], rand_coords[circle][1], rand_colors[circle]);
//            }
//
//            my_wm.swapBuffers();
//        } // while (!my_wm.isGlfwWindowClosed())
//        my_wm.destroyGlfwWindow();
//    } // public void render(...)

    // Renders a polygon given a center coordinate and color
    protected void renderPolygon(float centerX, float centerY, float[] color){
        float theta = 0.0f;
        final float end_angle = (float) (2.0f * Math.PI);

        float delTheta = end_angle / NUMBER_OF_SIDES;

        float x, y, oldX = centerX + C_RADIUS * (float) Math.cos(theta), oldY = centerY + C_RADIUS * (float) Math.sin(theta);

        glBegin(GL_TRIANGLES);

        // Each triangle will require color + 3 vertices as below.
        // For each circle you need 40 of these for the assignment.
        for (int cir_seg = 1; cir_seg <= NUMBER_OF_SIDES; cir_seg++){
            theta += delTheta;

            x =  centerX + C_RADIUS * (float) Math.cos(theta);
            y = centerY + C_RADIUS * (float) Math.sin(theta);
            glColor4f(color[0], color[1], color[2], 1.0f);
            glVertex3f(centerX, centerY, 0.0f);
            glVertex3f(x, y, 0.0f);
            glVertex3f(oldX, oldY, 0.0f);

            oldX = x;
            oldY = y;
        }
        glEnd();
    }
}