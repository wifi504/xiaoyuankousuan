package com.lhl.xyks.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lhl.xyks.utils.ConfigParser;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_1:00
 */
@JsonDeserialize(using = ConfigParser.PointDeserializer.class)
public class Point extends java.awt.Point {

    /**
     * 参与绘制时，这是否是一个实心点，默认为true
     */
    public boolean isSolid = true;

    /**
     * 点可以携带一个颜色
     */
    public Color color = null;

    public Point() {
        super();
    }

    public Point(int x, int y) {
        super(x, y);
    }

    public Point(int x, int y, boolean isSolid) {
        super(x, y);
        this.isSolid = isSolid;
    }

    public Point(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    /**
     * 将另一个点进行矢量叠加，得到一个叠加后的点
     *
     * @param point 目标点
     * @return 叠加点
     */
    public Point overlay(Point point) {
        return new Point(x + point.x, y + point.y, isSolid);
    }

    /**
     * 以原点对点进行缩放
     *
     * @param scale 缩放倍率
     * @return 缩放后的新实例点
     */
    public Point resize(double scale) {
        return new Point((int) (x * scale), (int) (y * scale), isSolid);
    }


    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", isSolid=" + isSolid +
                ", color=" + color +
                '}';
    }
}
