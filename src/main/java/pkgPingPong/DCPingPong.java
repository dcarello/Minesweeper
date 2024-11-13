package pkgPingPong;

import java.util.Random;

public class DCPingPong {
    private int[][] A;
    private int[][] B;
    private int[][] LiveArray;
    private int[][] NextArray;
    private int ROWS;
    private int COLS;

    private Random rand = new Random();

    private void VariableInit(int rows, int cols){
        ROWS = rows;
        COLS = cols;
        A = new int[ROWS][COLS];
        B = new int[ROWS][COLS];
        LiveArray = A;
        NextArray = B;
    }
    // Binary Array Initialization
    public DCPingPong(int rows, int cols){
        VariableInit(rows, cols);
        RandomArrayInit(LiveArray, 0, 1);
    }

    // Uniform Array Initialization
    public DCPingPong(int rows, int cols, int initValue){
        VariableInit(rows, cols);
        uniformArrayInit(LiveArray, initValue);
    }

    // Random Array Initialization
    public DCPingPong(int rows, int cols, int lowerBoundRandValue, int upperBoundRandValue){
        VariableInit(rows, cols);
        RandomArrayInit(LiveArray, lowerBoundRandValue, upperBoundRandValue);
    }

    private void uniformArrayInit(int[][] ArraytoWrite, int initValue){
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                ArraytoWrite[row][col] = initValue;
            }
        }
    }

    private void RandomArrayInit(int[][] ArraytoWrite, int lowerBound, int upperBound){
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                ArraytoWrite[row][col] = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
            }
        }
    }

    private void setLiveArray(int row, int col, int valueToWrite){
        LiveArray[row][col] = valueToWrite;
    }

    private void setNextArray(int row, int col, int valueToWrite){
        NextArray[row][col] = valueToWrite;
    }

    public int get(int row, int col){
        return LiveArray[row][col];
    }

    public void swapBuffer(){
        int[][] tmp = LiveArray;
        LiveArray = NextArray;
        NextArray = tmp;
    }

    public void nearestNeighbor(int row, int col){
        int next_row = (row + 1) % ROWS;
        int next_col = (col + 1) % COLS;
        int prev_row = (row - 1 + ROWS) % ROWS;
        int prev_col = (col - 1 + COLS) % COLS;

        int count = 0;

        if (LiveArray[row][prev_col] == 1) count++;
        if (LiveArray[row][next_col] == 1) count++;
        if (LiveArray[prev_row][col] == 1) count++;
        if (LiveArray[next_row][col] == 1) count++;


        setNextArray(row, col, count);


    }

    public void nextNearestNeighbor(int row, int col){
        int next_row = (row + 1) % ROWS;
        int next_col = (col + 1) % COLS;
        int prev_row = (row - 1 + ROWS) % ROWS;
        int prev_col = (col - 1 + COLS) % COLS;

        int count = 0;

        if (LiveArray[row][prev_col] == 1) count++;
        if (LiveArray[row][next_col] == 1) count++;
        if (LiveArray[prev_row][col] == 1) count++;
        if (LiveArray[next_row][col] == 1) count++;

        if (LiveArray[prev_row][prev_col] == 1) count++;
        if (LiveArray[prev_row][next_col] == 1) count++;
        if (LiveArray[next_row][prev_col] == 1) count++;
        if (LiveArray[next_row][next_col] == 1) count++;

        if (LiveArray[row][col] == 1){
            if (count < 2){
                setNextArray(row,col,0);
            }else if (count == 2 || count == 3){
                setNextArray(row, col, 1);
            }else {
                setNextArray(row,col,0);
            }
        }else {
            if (count == 3){
                setNextArray(row, col, 1);
            }else{
                setNextArray(row,col,0);
            }
        }
       // setNextArray(row, col, count);
    }

    public void boardReset(int lowerBoundRandValue, int upperBoundRandValue){
        RandomArrayInit(LiveArray, lowerBoundRandValue, upperBoundRandValue);
    }

    public void boardReset(){
        RandomArrayInit(LiveArray, 0, 1);
    }

    public void printLiveArray(){
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                System.out.print(LiveArray[row][col] + " ");
            }
            System.out.println();
        }
    }

}
