package com.sudoko;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class SudokuGame {
    static final int SIZE = 9;
    static final int EMPTY_CELL = 0;
    private int[][] puzzle;

    public SudokuGame() {
        puzzle = new int[SIZE][SIZE];
    }
    public int[][] generatePuzzle() {
        solve(puzzle); // Generate a complete Sudoku puzzle
        removeCells(puzzle); // Remove some cells to create a playable puzzle
        return puzzle;
    }

    public boolean solve(int[][] puzzle) {
        return solveRecursive(puzzle, 0, 0);
    }

    private boolean solveRecursive(int[][] puzzle, int row, int col) {
        if (row == SIZE) {
            row = 0;
            if (++col == SIZE) {
                return true; // All cells have been filled
            }
        }

        if (puzzle[row][col] != EMPTY_CELL) {
            return solveRecursive(puzzle, row + 1, col);
        }

        for (int num = 1; num <= SIZE; num++) {
            if (isValidMove(puzzle, row, col, num)) {
                puzzle[row][col] = num;
                if (solveRecursive(puzzle, row + 1, col)) {
                    return true;
                }
            }
        }

        puzzle[row][col] = EMPTY_CELL; // Undo the move if no solution is found
        return false;
    }

    boolean isValidMove(int[][] puzzle, int row, int col, int num) {
        return !isNumInRow(puzzle, row, num) &&
                !isNumInColumn(puzzle, col, num) &&
                !isNumInBox(puzzle, row - row % 3, col - col % 3, num);
    }

    private boolean isNumInRow(int[][] puzzle, int row, int num) {
        for (int col = 0; col < SIZE; col++) {
            if (puzzle[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumInColumn(int[][] puzzle, int col, int num) {
        for (int row = 0; row < SIZE; row++) {
            if (puzzle[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumInBox(int[][] puzzle, int boxStartRow, int boxStartCol, int num) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (puzzle[row + boxStartRow][col + boxStartCol] == num) {
                    return true;
                }
            }
        }
        return false;
    }
    public void resetGame() {
        puzzle = new int[SIZE][SIZE];
    }
    public int[][] getPuzzle() {
        return puzzle;
    }
    private void removeCells(int[][] puzzle) {
        Random random = new Random();
        int cellsToRemove = SIZE * SIZE / 2; // Remove half of the cells

        while (cellsToRemove > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            if (puzzle[row][col] != EMPTY_CELL) {
                puzzle[row][col] = EMPTY_CELL;
                cellsToRemove--;
            }
        }
    }
    public void display() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(puzzle[row][col] + " ");
            }
            System.out.println();
        }
    }
	public boolean isPuzzleSolved() {
		    return solve(puzzle);
		}
	public CellRequest generateHint() {
	    for (int row = 0; row < SIZE; row++) {
	        for (int col = 0; col < SIZE; col++) {
	            if (puzzle[row][col] == EMPTY_CELL) {
	                for (int num = 1; num <= SIZE; num++) {
	                    if (isValidMove(puzzle, row, col, num)) {
	                        puzzle[row][col] = num;
	                        CellRequest hint = new CellRequest();
	                        hint.setRow(row);
	                        hint.setColumn(col);
	                        hint.setValue(num);
	                        puzzle[row][col] = EMPTY_CELL; // Restore the cell to empty state
	                        return hint;
	                    }
	                }
	            }
	        }
	    }
	    return null; // No hint available
	}
	public boolean isGameCompleted() {
	    // Check if all cells are filled and valid
	    
	    // Iterate through each row and column
	    for (int row = 0; row < SIZE; row++) {
	        for (int col = 0; col < SIZE; col++) {
	            if (puzzle[row][col] == EMPTY_CELL || !isValidMove(puzzle, row, col, puzzle[row][col])) {
	                // Found an empty cell or an invalid value
	                return false; // Game is not completed
	            }
	        }
	    }
	    
	    return true; // All cells are filled and valid, game is completed
	}
}


