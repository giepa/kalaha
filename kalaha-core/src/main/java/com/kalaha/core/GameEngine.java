package com.kalaha.core;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameEngine {

    private final Game board;

    public GameEngine(Game board) {
        this.board = board;
    }

    public static GameEngine getInstance(Game board){
        return new GameEngine(board);
    }

    public class MoveIterator implements Iterator<Game> {

        final Game board;
        final String player;
        final int startingPoint;
        final int moves;
        int currentPos;

        public MoveIterator(Game b, String player, int pit){
            this.board = b;
            this.player = player;
            this.startingPoint = pit;
            this.currentPos = startingPoint;
            this.moves = board.get(currentPos);
        }

        @Override
        public boolean hasNext() {
            boolean rt =  board.get(startingPoint) > 0;
            return rt;
        }

        private void increment(){
            if(currentPos >= board.getData().size() -1){
                currentPos = 0;
            }else{
                currentPos ++;
            }
        }

        @Override
        public Game next() {
            increment();
            if(Utils.isOtherKalaha(board, player, currentPos)){
                increment();
            }
            board.decrement(startingPoint);
            board.increment(currentPos);
            if(!hasNext()){
                board.setWhosNext(Utils.getWhosNext(board, player, currentPos));
                board.setWinner(Utils.getWinner(board));
            }
            return board;
        }

        public int totalMoves(){
            return moves;
        }
    }

    protected MoveIterator iterator(String player, int pit){
        GameEngine.validateMove(board, player, pit);
        return new MoveIterator(board, player, pit);
    }

    public rx.Observable<Game> executeMove(String player, int pit, int interval, TimeUnit t){
        try{
            MoveIterator iterator = iterator(player, pit);
            return rx.Observable.interval(interval, t)
                    .take(iterator.totalMoves())
                    .map(d -> iterator.next());
        } catch (GameEngine.Error e) {
            return rx.Observable.error(e);
        }
    }

    public Game executeMove(String player, int pit){
        return executeMove(player, pit, r-> {});
    }

    public Game executeMove(String player, int pit, Consumer<Game> consumer){
        iterator(player, pit).forEachRemaining(consumer);
        return board;
    }

    public static boolean validateMove(Game board, String player, int pit){
        if(board.getPlayer1() == null || board.getPlayer2()== null)
            Errors.NO_PLAYERS.throwNow();
        if (board.getWinner() != null)
            Errors.GAME_OVER.throwNow();
        if (!Utils.isValidPlayer(board, player))
            Errors.INVALID_PLAYER.throwNow();
        if (!board.getWhosNext().equals(player))
            Errors.NOT_YOUR_TURN.throwNow();
        if(Utils.getPlayerPits(board, player).noneMatch(p -> p == pit))
            Errors.INVALID_PIT.throwNow();
        if (board.get(pit) == 0)
            Errors.PIT_IS_EMPTY.throwNow();
        return true;
    }

    public enum Errors{
        NO_PLAYERS("No player has joined this game"),
        GAME_OVER("This game is over"),
        INVALID_PLAYER("This player is not part of the game"),
        NOT_YOUR_TURN("This is not your turn"),
        INVALID_PIT("You cannot play this pit"),
        PIT_IS_EMPTY("You cannot play an empty pit");

        private final String msg;

        Errors(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void throwNow(){
            throw new Error(this);
        }
    }

    public static class Error extends IllegalArgumentException{

        private final Errors error;

        public Error(Errors error){
            super(error.getMsg());
            this.error = error;
        }

        public Errors getError() {
            return error;
        }
    }
}