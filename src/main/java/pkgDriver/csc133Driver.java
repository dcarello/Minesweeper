package pkgDriver;

import pkgPingPong.DCPingPong;
import pkgSlRenderer.DCPolygonRenderer;
import pkgSlRenderer.slRenderEngine;
import static pkgDriver.slSpot.*;
import pkgSlUtils.slWindowManager;


public class csc133Driver {
    public static void main(String[] my_args) {
        final int FRAME_DELAY = 0;

        slRenderEngine my_re = new DCPolygonRenderer();
        slWindowManager.get().initGLFWWindow(WIN_WIDTH, WIN_HEIGHT, WINDOW_TITLE);
        my_re.initOpenGL(NUM_POLY_ROWS, NUM_POLY_COLS, slWindowManager.get());


        final float RADIUS = 0.5f;

        DCPingPong myPingPong = new DCPingPong(NUM_POLY_ROWS, NUM_POLY_COLS);


        my_re.render(FRAME_DELAY, NUM_POLY_ROWS, NUM_POLY_COLS, myPingPong);
//        my_re.render(RADIUS);
//        my_re.render();
    } // public static void main(String[] my_args)
} // public class csc133Driver(...)