package com.kalaha.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Game {

    private LinkedList<Integer> data = new LinkedList<>();
    private String id;
    private String player1;
    private String player2;
    private String whosNext;
    private String winner;

    public static Game getInstance(int size){
        return new Game(size);
    }

    static Game getInstance(int[] data){
        return new Game(data);
    }

    public static Game getInstance(){
        return new Game();
    }

    public Game(){
        this(6);
    }

    public Game(int size){
        Utils.generateBoard(size).forEach(this.data::addLast);
    }

    Game(int[] d){
        Arrays.stream(d).forEach(this.data::addLast);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int get(int pit){
        return data.get(pit);
    }

    public void increment(int pit){
        increment(pit, 1);
    }

    public void increment(int pit, int amout){
        data.set(pit, data.get(pit) + amout);
    }

    public int clear(int pit){
        int rt = get(pit);
        data.set(pit, 0);
        return rt;
    }

    public void decrement(int pit){
        data.set(pit, data.get(pit)-1);
    }

    public LinkedList<Integer> getData() {
        return data;
    }

    public void setData(LinkedList<Integer> data) {
        this.data = data;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public void setWhosNext(String whosNext) {
        this.whosNext = whosNext;
    }

    public String getWhosNext() {
        return whosNext !=null ? whosNext : player1;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }
}