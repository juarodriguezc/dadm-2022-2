package co.edu.unal.tictactoe;


import java.util.Random;

public class TicTacToeGameOnline {

    public static final int PLAYER_HOST = 1;
    public static final int PLAYER_CLIENT = 2;
    public static final int OPEN_SPOT = 0;


    public static final int BOARD_SIZE = 9;
    private int[] mBoard = new int[BOARD_SIZE];


    public int getBOARD_SIZE() {
        return BOARD_SIZE;
    }

    private final Random mRand;

    public TicTacToeGameOnline() {

        // Seed the random number generator
        clearBoard();
        mRand = new Random();

    }

    public int[] getBoardState() {
        return mBoard;
    }

    public void setmBoard(int[] mBoard) {
        this.mBoard = mBoard;
    }

    public void setBoardState(int[] board) {
        mBoard = board.clone();
    }


    //Public methods

    /**
     * Clear the board of all X's and O's by setting all spots to OPEN_SPOT.
     */
    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    /**
     * Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player   - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public boolean setMove(int player, int location) {
        if (mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
            return true;
        }
        return false;
    }

    /**
     * Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     *
     * @return The best move for the computer to make (0-8).
     */


    public int getNumMov() {
        int count = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT)
                count++;
        }
        return BOARD_SIZE - count;
    }


    public int[] checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3) {
            if (mBoard[i] == PLAYER_HOST &&
                    mBoard[i + 1] == PLAYER_HOST &&
                    mBoard[i + 2] == PLAYER_HOST)
                return new int[]{2, i, i + 1, i + 2};
            if (mBoard[i] == PLAYER_CLIENT &&
                    mBoard[i + 1] == PLAYER_CLIENT &&
                    mBoard[i + 2] == PLAYER_CLIENT) {
                return new int[]{3, i, i + 1, i + 2};
            }

        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == PLAYER_HOST &&
                    mBoard[i + 3] == PLAYER_HOST &&
                    mBoard[i + 6] == PLAYER_HOST)
                return new int[]{2, i, i + 3, i + 6};
            //return 2;
            if (mBoard[i] == PLAYER_CLIENT &&
                    mBoard[i + 3] == PLAYER_CLIENT &&
                    mBoard[i + 6] == PLAYER_CLIENT) {
                //return 3;
                return new int[]{3, i, i + 3, i + 6};
            }


        }
        // Check for diagonal wins for Human Player
        if (mBoard[0] == PLAYER_HOST &&
                mBoard[4] == PLAYER_HOST &&
                mBoard[8] == PLAYER_HOST)
            return new int[]{2, 0, 4, 8};

        if (mBoard[2] == PLAYER_HOST &&
                mBoard[4] == PLAYER_HOST &&
                mBoard[6] == PLAYER_HOST)
            return new int[]{2, 2, 4, 6};

        // Check for diagonal wins for Computer
        if (mBoard[0] == PLAYER_CLIENT &&
                mBoard[4] == PLAYER_CLIENT &&
                mBoard[8] == PLAYER_CLIENT) {
            return new int[]{3, 0, 4, 8};
        }


        if (mBoard[2] == PLAYER_CLIENT &&
                mBoard[4] == PLAYER_CLIENT &&
                mBoard[6] == PLAYER_CLIENT) {
            return new int[]{3, 2, 4, 6};
        }


        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != PLAYER_HOST && mBoard[i] != PLAYER_CLIENT)
                return new int[]{0, 0, 0, 0};
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return new int[]{1, 0, 0, 0};
    }

    public int getBoardOccupant(int position) {
        return mBoard[position];
    }

}