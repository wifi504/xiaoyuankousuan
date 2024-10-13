package com.lhl.xyks.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Symbol Mapper 的 pojo 类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_2:58
 */
public class Symbol {
    /*
        Json 原型
        {"char": ">", "points": [
          [12, 52, false],
          [88, 93, true],
          [13, 124, true]
        ]}
     */

    @JsonProperty("char")
    private char character;
    private ArrayList<Point> points;

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "character=" + character +
                ", points=" + points +
                '}';
    }
}
