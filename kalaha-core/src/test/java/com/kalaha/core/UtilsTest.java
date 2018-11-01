package com.kalaha.core;


import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    public static final String PLAYER1 = "player1";
    public static final String PLAYER2 = "player2";

    @Test
    public void isGameOver() {
        Game board = Game.getInstance(new int [] {
                0, 0, 0, 0, 0, 0, 30,    // player 1
                6, 6, 6, 6, 6, 6, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertTrue(Utils.isGameOver(board));
        board.increment(0);
        assertFalse(Utils.isGameOver(board));
    }

    @Test
    public void getWinner() {
        Game board = Game.getInstance(new int [] {
                0, 0, 0, 0, 0, 0, 30,    // player 1
                6, 6, 6, 6, 6, 6, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertEquals(PLAYER1, Utils.getWinner(board));
        board.increment(0);
        assertNull(Utils.getWinner(board));
    }

    @Test
    public void getLeader() {
        Game board = Game.getInstance(new int [] {
                0, 0, 0, 0, 0, 0, 30,    // player 1
                6, 6, 6, 6, 6, 6, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertEquals(PLAYER1, Utils.getLeader(board));

        board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertNull(Utils.getLeader(board));
    }

    @Test
    public void getKalahaIndex() {
        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertEquals(6, Utils.getKalahaIndex(board, PLAYER1));
        assertEquals(13, Utils.getKalahaIndex(board, PLAYER2));
    }

    @Test
    public void getKalaha() {
        Game board = Game.getInstance(new int [] {
                0, 0, 0, 0, 0, 0, 30,    // player 1
                6, 6, 6, 6, 6, 6, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertEquals(30, Utils.getKalaha(board, PLAYER1));
        assertEquals(0, Utils.getKalaha(board, PLAYER2));
    }

    @Test
    public void isValidPlayer() {
        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertTrue(Utils.isValidPlayer(board, PLAYER1));
        assertTrue(Utils.isValidPlayer(board, PLAYER2));
        assertFalse(Utils.isValidPlayer(board, "junk"));
    }

    @Test
    public void getOtherPlayer() {
        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertEquals(PLAYER2, Utils.getOtherPlayer(board, PLAYER1));
        assertEquals(PLAYER1, Utils.getOtherPlayer(board, PLAYER2));
    }

    @Test
    public void isOtherKalaha() {
        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertTrue(Utils.isOtherKalaha(board, PLAYER1, 13));
        assertFalse(Utils.isOtherKalaha(board, PLAYER1, 1));
        assertFalse(Utils.isOtherKalaha(board, PLAYER1, 6));
        assertTrue(Utils.isOtherKalaha(board, PLAYER2, 6));
        assertFalse(Utils.isOtherKalaha(board, PLAYER2, 10));
        assertFalse(Utils.isOtherKalaha(board, PLAYER2, 13));
    }

    @Test
    public void getPlayerPits() {
        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertTrue(Utils.getPlayerPits(board, PLAYER1).anyMatch(p -> p == 0));
        assertTrue(Utils.getPlayerPits(board, PLAYER1).anyMatch(p -> p == 5));
        assertFalse(Utils.getPlayerPits(board, PLAYER1).anyMatch(p -> p == 6));
        assertTrue(Utils.getPlayerPits(board, PLAYER2).anyMatch(p -> p == 7));
        assertTrue(Utils.getPlayerPits(board, PLAYER2).anyMatch(p -> p == 12));
        assertFalse(Utils.getPlayerPits(board, PLAYER2).anyMatch(p -> p == 13));

        board = Game.getInstance(3);
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertTrue(Utils.getPlayerPits(board, PLAYER1).anyMatch(p -> p == 0));
        assertTrue(Utils.getPlayerPits(board, PLAYER1).anyMatch(p -> p == 2));
        assertFalse(Utils.getPlayerPits(board, PLAYER1).anyMatch(p -> p == 3));
        assertTrue(Utils.getPlayerPits(board, PLAYER2).anyMatch(p -> p == 4));
        assertTrue(Utils.getPlayerPits(board, PLAYER2).anyMatch(p -> p == 6));
        assertFalse(Utils.getPlayerPits(board, PLAYER2).anyMatch(p -> p == 7));
        assertEquals(2, Utils.getPlayerPits(board, PLAYER1).max().getAsInt());
    }

    @Test
    public void getWhosNext(){
        Game board = Game.getInstance();
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertEquals(PLAYER1, Utils.getWhosNext(board, PLAYER1, 6));
        assertEquals(PLAYER2, Utils.getWhosNext(board, PLAYER1, 5));
        assertEquals(PLAYER2, Utils.getWhosNext(board, PLAYER2, 13));
        assertEquals(PLAYER1, Utils.getWhosNext(board, PLAYER2, 4));
    }

    @Test
    public void generateBoard() {
        generateBoard(6);
        generateBoard(10);
        generateBoard(3);
    }

    private void generateBoard(int size){
        assertEquals(size*size*2, Utils.generateBoard(size)
                .sum()
        );
    }

    @Test
    public void getOpositePit(){
        Game board = Game.getInstance();
        assertEquals(0, Utils.getOpositePit(board, 12));
        assertEquals(1, Utils.getOpositePit(board, 11));
        assertEquals(8, Utils.getOpositePit(board, 4));
        assertEquals(7, Utils.getOpositePit(board, 5));

        board = Game.getInstance(4);
        assertEquals(0, Utils.getOpositePit(board, 8));
        assertEquals(1, Utils.getOpositePit(board, 7));
        assertEquals(8, Utils.getOpositePit(board, 0));
        assertEquals(7, Utils.getOpositePit(board, 1));
    }

    @Test
    public void canCaptureStones(){
        Game board = Game.getInstance(new int [] {
                0, 0, 6, 6, 0, 0, 30,  // player 1
                6, 6, 0, 0, 6, 6, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertTrue(Utils.canCaptureStones(board, PLAYER1, 0));
        assertTrue(Utils.canCaptureStones(board, PLAYER1, 1));
        assertFalse(Utils.canCaptureStones(board, PLAYER1, 3));
        assertTrue(Utils.canCaptureStones(board, PLAYER2, 9));
        assertTrue(Utils.canCaptureStones(board, PLAYER2, 10));
        assertFalse(Utils.canCaptureStones(board, PLAYER2, 11));
    }

    @Test
    public void captureStones(){
        Game board = Game.getInstance(new int [] {
                0, 0, 6, 6, 0, 0, 36,  // player 1
                6, 6, 0, 0, 6, 5, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);
        assertTrue(Utils.captureStones(board, PLAYER1, 0));
        assertEquals(0, board.get(12));
        assertEquals(42, board.get(6));

        board.decrement(7);
        assertTrue(Utils.captureStones(board, PLAYER2, 9));
        assertEquals(0, board.get(3));
        assertEquals(7, board.get(13));

        board = Game.getInstance(new int [] {
                0, 0, 6, 10, 6, 0, 26,  // player 1
                6, 6, 0, 0,  6, 5, 0    // player 2
        });
        board.setPlayer1(PLAYER1);
        board.setPlayer2(PLAYER2);

        GameEngine game = GameEngine.getInstance(board);
        game.executeMove(PLAYER1, 3);
        assertEquals(0, board.get(0));
        assertEquals(0, board.get(12));
        assertEquals(34, board.get(6));
    }

}