package co.edu.unal.tictactoe;


import android.view.View;

import java.sql.SQLOutput;
import java.util.Random;

public class TicTacToeGame {

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    public enum DifficultyLevel {Easy, Harder, Expert, God}


    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public static final int BOARD_SIZE = 9;
    private char[] mBoard = new char[BOARD_SIZE];


    public int getBOARD_SIZE() {
        return BOARD_SIZE;
    }

    private final Random mRand;

    public TicTacToeGame() {

        // Seed the random number generator
        clearBoard();
        mRand = new Random();

    }

    public char[] getBoardState() {
        return mBoard;
    }

    public void setBoardState(char[] board) {
        mBoard = board.clone();
    }


    public void setmDifficultyLevel(String mDifficultyLevel) {
        this.mDifficultyLevel = DifficultyLevel.valueOf(mDifficultyLevel);
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
    public boolean setMove(char player, int location) {
        if (mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
            return true;
        } else if (mDifficultyLevel == DifficultyLevel.God && player == COMPUTER_PLAYER) {
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
    public int getComputerMove() {
        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Expert) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.God) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1) {
                move = getCornerMove();
            }
            if (move == -1) {
                if (getNumMov() < 7)
                    move = getBlockingMove();
                else
                    move = getGodMove();
            }
            if (move == -1) {
                //Tomar el centro
                if (mBoard[4] == OPEN_SPOT)
                    move = 4;
                else if (mBoard[4] == COMPUTER_PLAYER) {
                    //Bloquear posibles jugadas del rival
                    if (mBoard[1] == OPEN_SPOT)
                        move = 1;
                    else if (mBoard[3] == OPEN_SPOT)
                        move = 3;
                    else if (mBoard[5] == OPEN_SPOT)
                        move = 5;
                    else if (mBoard[7] == OPEN_SPOT)
                        move = 7;
                    else
                        move = -1;
                }
            }
            //Tomar las diagonales
            if (move == -1) {
                if (mBoard[0] == OPEN_SPOT)
                    move = 0;
                else if (mBoard[2] == OPEN_SPOT)
                    move = 2;
                else if (mBoard[6] == OPEN_SPOT)
                    move = 6;
                else if (mBoard[8] == OPEN_SPOT)
                    move = 8;
                else
                    move = -1;
            }
            if (move == -1)
                move = getRandomMove();
        }
        return move;

    }

    public int getRandomMove() {
        int move;
        // Generate random move
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);

        return move;
    }

    public int getWinningMove() {
        // First see if there's a move O can make to win
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner()[0] == 3 || checkForWinner()[0] == 4) {
                    mBoard[i] = curr;
                    return i;
                }
                mBoard[i] = curr;
            }
        }
        return -1;
    }

    public int getBlockingMove() {
        // See if there's a move O can make to block X from winning
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];   // Save the current number
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner()[0] == 2) {
                    mBoard[i] = curr;
                    return i;
                }
                mBoard[i] = curr;
            }
        }
        return -1;
    }

    public int getCornerMove() {
        int move = -1;
        if (mBoard[1] == HUMAN_PLAYER && mBoard[3] == HUMAN_PLAYER && mBoard[1] == OPEN_SPOT)
            move = 1;
        else if (mBoard[1] == HUMAN_PLAYER && mBoard[5] == HUMAN_PLAYER && mBoard[2] == OPEN_SPOT)
            move = 2;
        else if (mBoard[3] == HUMAN_PLAYER && mBoard[7] == HUMAN_PLAYER && mBoard[6] == OPEN_SPOT)
            move = 6;
        else if (mBoard[5] == HUMAN_PLAYER && mBoard[7] == HUMAN_PLAYER && mBoard[8] == OPEN_SPOT)
            move = 8;
        return move;
    }

    public int getGodMove() {
        //Make a copy of the board
        char[] mBoardCopy = mBoard.clone();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == HUMAN_PLAYER)
                mBoard[i] = OPEN_SPOT;
        }
        int move = getWinningMove();
        if (move == -1) {
            move = 4;
        }
        for (int i = 0; i < mBoard.length; i++) {
            mBoard[i] = mBoardCopy[i];
        }
        return move;
    }

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
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 1] == HUMAN_PLAYER &&
                    mBoard[i + 2] == HUMAN_PLAYER)
                return new int[]{2, i, i + 1, i + 2};
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i + 1] == COMPUTER_PLAYER &&
                    mBoard[i + 2] == COMPUTER_PLAYER) {
                if (mDifficultyLevel == DifficultyLevel.God && getNumMov() >= 7) {
                    //Return 4 if God move won
                    return new int[]{4, i, i + 1, i + 2};
                }
                return new int[]{3, i, i + 1, i + 2};
            }

        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 3] == HUMAN_PLAYER &&
                    mBoard[i + 6] == HUMAN_PLAYER)
                return new int[]{2, i, i + 3, i + 6};
            //return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i + 3] == COMPUTER_PLAYER &&
                    mBoard[i + 6] == COMPUTER_PLAYER) {
                if (mDifficultyLevel == DifficultyLevel.God && getNumMov() >= 7) {
                    //Return 4 if God move won
                    return new int[]{4, i, i + 3, i + 6};
                }
                //return 3;
                return new int[]{3, i, i + 3, i + 6};
            }


        }

        // Check for diagonal wins for Human Player
        if (mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER)
            return new int[]{2, 0, 4, 8};

        if (mBoard[2] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[6] == HUMAN_PLAYER)
            return new int[]{2, 2, 4, 6};

        // Check for diagonal wins for Computer
        if (mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) {
            if (mDifficultyLevel == DifficultyLevel.God && getNumMov() >= 7) {
                //Return 4 if God move won
                return new int[]{4, 0, 4, 8};
            }
            return new int[]{3, 0, 4, 8};
        }


        if (mBoard[2] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[6] == COMPUTER_PLAYER) {
            if (mDifficultyLevel == DifficultyLevel.God && getNumMov() >= 7) {
                //Return 4 if God move won
                return new int[]{4, 2, 4, 6};
            }
            return new int[]{3, 2, 4, 6};
        }


        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return new int[]{0, 0, 0, 0};
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return new int[]{1, 0, 0, 0};
    }

    public char getBoardOccupant(int position) {
        return mBoard[position];
    }

}