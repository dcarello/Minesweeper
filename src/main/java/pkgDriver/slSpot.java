package pkgDriver;

public class slSpot {
//    public static final int WIN_WIDTH = 800, WIN_HEIGHT = 800;

    public static String WINDOW_TITLE = "CSC 133: MineSweeper";
    public static int POLY_OFFSET = 20, POLYGON_LENGTH = 50, POLY_PADDING = 20;
    public static int NUM_POLY_ROWS = 9, NUM_POLY_COLS = 7;

    public static int WIN_WIDTH =
            2*POLY_OFFSET + (NUM_POLY_COLS-1)*POLY_PADDING + NUM_POLY_COLS*POLYGON_LENGTH;
    public static int WIN_HEIGHT =
            2*POLY_OFFSET + (NUM_POLY_ROWS-1)*POLY_PADDING + NUM_POLY_ROWS*POLYGON_LENGTH;
    public static final float FRUSTUM_LEFT = 0.0f,   FRUSTUM_RIGHT = (float)WIN_WIDTH,
            FRUSTUM_BOTTOM = 0.0f, FRUSTUM_TOP = (float)WIN_HEIGHT,
            Z_NEAR = 0.0f, Z_FAR = 100.0f;
}