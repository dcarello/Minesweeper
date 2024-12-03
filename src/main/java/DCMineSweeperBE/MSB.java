package DCMineSweeperBE;

import java.util.ArrayList;
import java.util.Collections;

import static pkgDriver.slSpot.*;


public class MSB {

    private int currentScore = 0;
    private final int GOLD = 0, MINE = 1;
    private Cell[][] msboard;
    private final int TOTAL_MINES = 14;



    private class Cell{
        public int points;
        public int tile_type;
        public boolean EXPOSED;
    }

    public MSB() {
        msboard = new Cell[NUM_POLY_ROWS][NUM_POLY_COLS];

        // Initialize each Cell in the board
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                msboard[row][col] = new Cell();
            }
        }

        initializeMines();
        initializePoints();
        initializeExposed();
    }

    private void initializePoints(){
        int num_mines;
        int num_gold;
        final int total_neighbors = 8;

        for (int row = 0; row < NUM_POLY_ROWS; row++){
            for (int col = 0; col < NUM_POLY_COLS; col++){
                if (msboard[row][col].tile_type == MINE){
                    msboard[row][col].points = 0;
                }else{
                    num_mines = nextNearestNeighbor(row, col);
                    num_gold = total_neighbors - num_mines;
                    msboard[row][col].points = (10 * num_mines + 5 * num_gold);
                }
            }
        }
    }

    private void initializeMines(){
        ArrayList<Integer> my_al = new ArrayList<Integer>(Collections.nCopies(NUM_POLY_ROWS * NUM_POLY_COLS, 0));
        for (int i = 0; i < TOTAL_MINES; i++){
            my_al.set(i, MINE);
        }
        Collections.shuffle(my_al);

        for (int row = 0; row < NUM_POLY_ROWS; row++){
            for (int col = 0; col < NUM_POLY_COLS; col++){
                msboard[row][col].tile_type = my_al.get(NUM_POLY_COLS * row + col);
            }
        }
    }

    private void initializeExposed(){
        for (int row = 0; row < NUM_POLY_ROWS; row++){
            for (int col = 0; col < NUM_POLY_COLS; col++){
                msboard[row][col].EXPOSED = false;
            }
        }
    }



    public int nextNearestNeighbor(int row, int col) {
        int next_row = (row + 1) % NUM_POLY_ROWS;
        int next_col = (col + 1) % NUM_POLY_COLS;
        int prev_row = (row - 1 + NUM_POLY_ROWS) % NUM_POLY_ROWS;
        int prev_col = (col - 1 + NUM_POLY_COLS) % NUM_POLY_COLS;

        int count = 0;

        if (msboard[row][prev_col].tile_type == MINE) count++;
        if (msboard[row][next_col].tile_type == MINE) count++;
        if (msboard[prev_row][col].tile_type == MINE) count++;
        if (msboard[next_row][col].tile_type == MINE) count++;

        if (msboard[prev_row][prev_col].tile_type == MINE) count++;
        if (msboard[prev_row][next_col].tile_type == MINE) count++;
        if (msboard[next_row][prev_col].tile_type == MINE) count++;
        if (msboard[next_row][next_col].tile_type == MINE) count++;

        return count;

    }

    public void setCellExposed(int row, int col, boolean exposed){
        msboard[row][col].EXPOSED = exposed;
    }

    public void setAllCellsExposed(){
        for (int row = 0; row < NUM_POLY_ROWS; row++){
            for (int col = 0; col < NUM_POLY_COLS; col++){
                msboard[row][col].EXPOSED = true;
            }
        }
    }

    public boolean getCellExposed(int row, int col){
        return msboard[row][col].EXPOSED;
    }

    public boolean getCellMine(int row, int col){
        return msboard[row][col].tile_type == MINE;
    }

    public void printMineBoard(){
        for (int row = NUM_POLY_ROWS - 1; row > -1; row--){
            for (int col = 0; col < NUM_POLY_COLS; col++){
                if (msboard[row][col].tile_type == MINE){
                    System.out.print("M");
                }else{
                    System.out.print("G");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void printPointsBoard(){
        for (int row = NUM_POLY_ROWS - 1; row > -1; row--){
            for (int col = 0; col < NUM_POLY_COLS; col++){
                System.out.printf("%2d ", msboard[row][col].points);
            }
            System.out.println();
        }
    }
}
