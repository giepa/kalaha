package com.kalaha.core;

import java.util.stream.IntStream;

public class Utils {

    public static boolean isGameOver(Game board){
        return getPlayerPits(board, board.getPlayer1())
                .map(board::get)
                .sum() == 0
            || getPlayerPits(board, board.getPlayer2())
                .map(board::get)
                .sum() == 0;
    }

    public static String getWhosNext(Game board, String player, int lastMove){
        return getKalahaIndex(board, player) == lastMove
                ? player
                : getOtherPlayer(board, player);
    }

    public static String getWinner(Game board) {
        return isGameOver(board)
                ? getLeader(board)
                : null;
    }

    public static String getLeader(Game board){
        return getKalaha(board, board.getPlayer1()) > getKalaha(board, board.getPlayer2())
                ? board.getPlayer1()
                : board.getPlayer2();
    }

    public static int getKalahaIndex(Game board, String player){
        return player.equals(board.getPlayer1())
                ? (board.getData().size() / 2) -1
                : board.getData().size() -1;
    }

    public static int getKalaha(Game board, String player){
        return board.get(getKalahaIndex(board, player));
    }

    public static boolean isValidPlayer(Game board, String player){
        return board.getPlayer1().equals(player) || board.getPlayer2().equals(player);
    }

    public static String getOtherPlayer(Game board, String player){
        return player.equals(board.getPlayer1())
                ? board.getPlayer2()
                : board.getPlayer1();
    }

    public static boolean isOtherKalaha(Game board, String player, int pit){
        return player.equals(board.getPlayer1())
                ? getKalahaIndex(board, board.getPlayer2()) == pit
                : getKalahaIndex(board, board.getPlayer1()) == pit;
    }

    public static IntStream getPlayerPits(Game board, String player){
        return player.equals(board.getPlayer1())
                ? IntStream.range(0, (board.getData().size()/2) - 1)
                : IntStream.range( (board.getData().size()/2) , board.getData().size() - 1);
    }

    public static IntStream generateBoard(int size){
        return IntStream.range(0, (size +1)*2)
                .map(i -> {
                    if(i == size) return 0;
                    if(i == (size*2)+1) return 0;
                    return size;
                });
    }
}
