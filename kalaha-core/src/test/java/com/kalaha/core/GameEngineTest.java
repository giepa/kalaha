package com.kalaha.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameEngineTest {

    public static final String PLAYER1 = "player1";
    public static final String PLAYER2 = "player2";

    @Test
    public void testValidateMove(){

        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);

        assertTrue(GameEngine.validateMove(board, PLAYER1, 0));
        try{
            GameEngine.validateMove(board, PLAYER1, 8);
            fail("Failure expected");
        }catch(GameEngine.Error e){
            assertEquals(GameEngine.Errors.INVALID_PIT, e.getError());
        }
        try{
            GameEngine.validateMove(board, PLAYER2, 8);
            fail("Failure expected");
        }catch(GameEngine.Error e){
            assertEquals(GameEngine.Errors.NOT_YOUR_TURN, e.getError());
        }
        try{
            GameEngine.validateMove(board, "junk", 3);
            fail("Failure expected");
        }catch(GameEngine.Error e){
            assertEquals(GameEngine.Errors.INVALID_PLAYER, e.getError());
        }

        board = Game.getInstance(new int [] {
                0, 0, 6, 0, 0, 0, 30,    // player 1
                6, 6, 6, 6, 6, 6, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);

        try{
            GameEngine.validateMove(board, PLAYER1, 0);
            fail("Failure expected");
        }catch(GameEngine.Error e){
            assertEquals(GameEngine.Errors.PIT_IS_EMPTY, e.getError());
        }
    }

    @Test
    public void testIterator(){
        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);

        GameEngine.MoveIterator iterator = GameEngine
                .getInstance(board)
                .iterator(PLAYER1, 0);
        assertEquals(6, iterator.totalMoves());
        for(int i = 5; i > -1; i--){
            board = iterator.next();
            assertEquals(i, board.get(0));
        }
        assertEquals(0, board.get(0));
        assertEquals(7, board.get(1));
        assertEquals(7, board.get(2));
        assertEquals(7, board.get(3));
        assertEquals(7, board.get(4));
        assertEquals(7, board.get(5));
        assertEquals(1, board.get(6));
        assertEquals(6, board.get(7));
        assertFalse(iterator.hasNext());

        iterator = GameEngine.getInstance(board)
                .iterator(PLAYER1, 1);
        assertEquals(7, iterator.totalMoves());
        for(int i = 6; i > -1; i--){
            board = iterator.next();
            assertEquals(i, board.get(1));
        }
        assertEquals(0, board.get(0));
        assertEquals(0, board.get(1));
        assertEquals(8, board.get(2));
        assertEquals(8, board.get(3));
        assertEquals(8, board.get(4));
        assertEquals(8, board.get(5));
        assertEquals(2, board.get(6));
        assertEquals(7, board.get(7));
        assertEquals(7, board.get(8));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testExecuteMove(){
        assertTrue(Utils.isGameOver(simulateGame(3)));
        assertTrue(Utils.isGameOver(simulateGame(6)));
        assertTrue(Utils.isGameOver(simulateGame(10)));
    }

    public Game simulateGame(int size){
        Game board = Game.getInstance(size);
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        GameEngine game = GameEngine.getInstance(board);
        while(board.getWinner() == null){
            if(board.getWhosNext().equals(PLAYER1)){
                int v = Utils.getKalaha(board, PLAYER2);
                game.executeMove(PLAYER1, Utils
                        .getPlayerPits(board,PLAYER1)
                        .filter(p -> board.get(p) > 0)
                        .findAny()
                        .getAsInt()
                );
                assertEquals(v, Utils.getKalaha(board, PLAYER2));
            } else {
                int v = Utils.getKalaha(board, PLAYER1);
                game.executeMove(PLAYER2, Utils
                        .getPlayerPits(board,PLAYER2)
                        .filter(p -> board.get(p) > 0)
                        .findAny()
                        .getAsInt()
                );
                assertEquals(v, Utils.getKalaha(board, PLAYER1));
            }
            assertEquals(size*size*2, board.getData().stream()
                    .mapToInt(i -> i)
                    .sum()
            );
        }
        return board;
    }
}