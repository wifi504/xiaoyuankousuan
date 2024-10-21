package com.lhl.xyks.pojo;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_14:49
 */
public class Color extends java.awt.Color {

    /**
     * 分别使用 RGB 色值创建颜色
     *
     * @param r RED
     * @param g GREEN
     * @param b BLUE
     */
    public Color(int r, int g, int b) {
        super(r, g, b);
    }

    /**
     * 自动提取 RGB 色值创建颜色
     *
     * @param rgb RGB 色值
     */
    public Color(int rgb) {
        super(rgb);
    }


    // ------------ 静态方法 ------------ //

    /**
     * 根据十六进制颜色字符串获取颜色对象
     *
     * @param hex 十六进制色
     * @return Color
     */
    public static Color hexToColor(String hex) {
        // 去除 # 前缀（如果有）
        hex = hex.startsWith("#") ? hex.substring(1) : hex;

        // 将十六进制字符串转换为整数，并创建 Color 对象
        int rgb = Integer.parseInt(hex, 16);

        return new Color(rgb);
    }

    /**
     * 根据颜色对象获取对应的十六进制颜色字符串
     *
     * @param color 颜色对象
     * @return 十六进制颜色字符串（带 # 前缀）
     */
    public static String colorToHex(Color color) {
        // 获取红色、绿色、蓝色值并转换为两位十六进制字符串

        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }


    // ------------ 颜色操作 ------------ //

    /**
     * 根据默认宽容度(10)判断颜色是否相似
     *
     * @param color 目标颜色
     * @return boolean
     */
    public boolean like(Color color) {
        return like(color, 10);
    }

    /**
     * 根据宽容度（tolerance）判断颜色是否相似
     *
     * @param color     目标颜色
     * @param tolerance 宽容度
     * @return boolean
     */
    public boolean like(Color color, int tolerance) {
        int redDiff = Math.abs(getRed() - color.getRed());
        int greenDiff = Math.abs(getGreen() - color.getGreen());
        int blueDiff = Math.abs(getBlue() - color.getBlue());
        return redDiff <= tolerance && greenDiff <= tolerance && blueDiff <= tolerance;
    }

    @Override
    public String toString() {
        return "Color{RGB(" +
                getRed() + ", " + getGreen() + ", " + getBlue()
                + ")}";
    }
}
