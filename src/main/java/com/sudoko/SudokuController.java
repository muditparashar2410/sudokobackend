package com.sudoko;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.net.ObjectWriter;

@RestController
@RequestMapping("/api/sudoku")
@CrossOrigin(origins = "*")
public class SudokuController {
	@Autowired
    private final SudokuGame sudokuGame;
    private boolean gameStarted;
    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    public SudokuController(SudokuGame sudokuGame,ObjectMapper objectMapper) {
        this.sudokuGame = sudokuGame;
        this.gameStarted = false;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/start")
    public String startGame(@RequestBody String requestBody) {
//        if ("START".equalsIgnoreCase(requestBody)) {
    
            gameStarted = true;
            sudokuGame.resetGame();
            return "READY";
//        }
//        return "Invalid request";
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateSudokuPuzzle() {
    	
    	   if (!gameStarted) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game has not been started yet.");
           }

           int[][] puzzle = sudokuGame.generatePuzzle();
           if (puzzle == null || puzzle.length == 0) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate puzzle.");
           }
           sudokuGame.display();
           System.out.println("__________________________-");

           ObjectMapper mapper = new ObjectMapper();
           try {
               String puzzleJson = mapper.writeValueAsString(puzzle);
               return ResponseEntity.ok(puzzleJson);
           } catch (JsonProcessingException e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to serialize puzzle.");
           }
       }
    @GetMapping("/puzzle")
    public ResponseEntity<String> getCurrentPuzzle() {
        if (!gameStarted) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game has not been started yet.");
        }

        int[][] puzzle = sudokuGame.getPuzzle();
        if (puzzle == null || puzzle.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve puzzle.");
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            String puzzleJson = mapper.writeValueAsString(puzzle);
            return ResponseEntity.ok(puzzleJson);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to serialize puzzle.");
        }

    }
    
    @PostMapping("/validate")
    public String validateCell(@RequestBody CellRequest cellRequest) {
        int row = cellRequest.getRow();
        int col = cellRequest.getColumn();
        int value = cellRequest.getValue();

        int[][] puzzle = sudokuGame.getPuzzle();

        if (!isValidCell(row, col)) {
            return "Invalid cell";
        }

        if (value < 1 || value > SudokuGame.SIZE) {
            return "Invalid value";
        }

        puzzle[row][col] = value;

        boolean isValid = sudokuGame.isValidMove(puzzle, row, col, value);

        if (isValid) {
            return "Valid";
        } else {
            puzzle[row][col] = SudokuGame.EMPTY_CELL;
            return "Invalid move";
        }
    }
    @GetMapping("hint")
    public ResponseEntity<CellRequest> getHint() {
        CellRequest hint = sudokuGame.generateHint();
        if (hint != null) {
            return ResponseEntity.ok(hint);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/fill")
    public ResponseEntity<String> fillCell(@RequestBody CellRequest cellRequest) {
    	int row = cellRequest.getRow();
    	    int col = cellRequest.getColumn();
    	    int value = cellRequest.getValue();

    	    // Validate the row, column, and value inputs
    	    if (row < 0 || row >= SudokuGame.SIZE || col < 0 || col >= SudokuGame.SIZE || value < 1 || value > SudokuGame.SIZE) {
    	        return ResponseEntity.badRequest().body("Invalid cell coordinates or value.");
    	    }

    	    // Check if the cell is already filled
//    	    if (sudokuGame.getPuzzle()[row][col] != SudokuGame.EMPTY_CELL) {
//    	        return ResponseEntity.badRequest().body("The cell is already filled.");
//    	    }

    	    // Check if the move is valid
    	    if (!sudokuGame.isValidMove(sudokuGame.getPuzzle(), row, col, value)) {
    	        return ResponseEntity.badRequest().body("Invalid move. Please check the Sudoku rules.");
    	    }

    	    // Fill the cell with the given value
    	    sudokuGame.getPuzzle()[row][col] = value;
    	    
    	    sudokuGame.display();
    	    System.out.println("____________________________");
    	    if (sudokuGame.isGameCompleted()) {
    	        return ResponseEntity.ok("Congratulations! You have won the game.");
    	    }
    	    // Check if the puzzle is now solved
//    	    if (sudokuGame.isPuzzleSolved()) {
//    	        return ResponseEntity.ok("Congratulations! You solved the puzzle.");
//    	    }

    	    return ResponseEntity.ok("Cell filled successfully.");
    }
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < SudokuGame.SIZE &&
                col >= 0 && col < SudokuGame.SIZE;
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetGame() {
        sudokuGame.resetGame(); // Reset the Sudoku game
        gameStarted = false;
        return ResponseEntity.ok("Game reset successfully.");
    }
}

