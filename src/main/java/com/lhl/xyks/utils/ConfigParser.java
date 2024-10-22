package com.lhl.xyks.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.pojo.Color;
import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.pojo.Symbol;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * 配置文件解析工具类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_2:25
 */
public class ConfigParser {

    /**
     * 自定义 Point 类的反序列化器 (内部类)
     */
    public static class PointDeserializer extends JsonDeserializer<Point> {
        @Override
        public Point deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            List<?> list = jsonParser.readValueAs(List.class);
            return new Point((int) list.get(0), (int) list.get(1), (boolean) list.get(2));
        }
    }

    /**
     * 从 symbol-mapper.json 加载绘制符号映射
     *
     * @return 绘制符号映射 Map
     */
    public static HashMap<Character, ArrayList<Point>> loadSymbolMap() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Symbol> symbols = objectMapper.readValue(new File("symbol-mapper.json"), new TypeReference<>() {
        });
        HashMap<Character, ArrayList<Point>> map = new HashMap<>();
        symbols.forEach(symbol -> map.put(symbol.getCharacter(), symbol.getPoints()));
        return map;
    }


    public static final Config globalConfig = new Config();

    /**
     * 从 xyks-config.properties 加载全局配置文件
     */
    public static void loadGlobalConfig() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("xyks-config.properties")) {
            properties.load(fis);
            // 当前题目识别区域
            // currentArea=0,0,0,0
            String currentArea = properties.getProperty("currentArea");
            globalConfig.currentArea = deserializeArea(currentArea);
            //  下一题目识别区域
            // nextArea=0,0,0,0
            String nextArea = properties.getProperty("nextArea");
            globalConfig.nextArea = deserializeArea(nextArea);
            //  开始答题判定点（左上角返回的黄色区域）
            // point1=0,0,#000000
            String point1 = properties.getProperty("point1");
            globalConfig.point1 = deserializePoint(point1);
            //  答题结束判定点（中间领奖励的弹窗按钮黄色）
            // point2=0,0,#000000
            String point2 = properties.getProperty("point2");
            globalConfig.point2 = deserializePoint(point2);
            //  继续答题操作点（右下角按钮）
            // point3=0,0,#000000
            String point3 = properties.getProperty("point3");
            globalConfig.point3 = deserializePoint(point3);
            //  开始匹配操作点（继续进入匹配按钮）
            // point4=0,0,#000000
            String point4 = properties.getProperty("point4");
            globalConfig.point4 = deserializePoint(point4);
            // 作答区域左侧边缘
            String point5 = properties.getProperty("point5");
            globalConfig.point5 = deserializePoint(point5);
            // 答题期间页眉蓝色区域的任意点
            String point6 = properties.getProperty("point6");
            globalConfig.point6 = deserializePoint(point6);
            //  题目类型
            // questionsType=compare20
            globalConfig.questionsType = properties.getProperty("questionsType");
            //  每轮完成题数
            // questionsNum=10
            String questionsNum = properties.getProperty("questionsNum");
            globalConfig.questionsNum = Integer.parseInt(questionsNum);
            //  答题失败重试最大次数
            // mistakeTryNum=5
            String mistakeTryNum = properties.getProperty("mistakeTryNum");
            globalConfig.mistakeTryNum = Integer.parseInt(mistakeTryNum);
            //  中断答题的快捷键
            // terminateKeyCombination=10
            globalConfig.terminateKeyCombination = properties.getProperty("terminateKeyCombination");
            // 小猿口算软件内题目间切换动画时长（ms）
            // questionAnimationDuration=200
            String questionAnimationDuration = properties.getProperty("questionAnimationDuration");
            globalConfig.questionAnimationDuration = Long.parseLong(questionAnimationDuration);
            //  默认的鼠标进行每单位移动时的时间间隔（ms）
            // defaultMoveInterval=1
            String defaultMoveInterval = properties.getProperty("defaultMoveInterval");
            globalConfig.defaultMoveInterval = Long.parseLong(defaultMoveInterval);
            //  连续绘制符号时，每个符号间的时间间隔（ms）
            // drawSymbolInterval=5
            String drawSymbolInterval = properties.getProperty("drawSymbolInterval");
            globalConfig.drawSymbolInterval = Long.parseLong(drawSymbolInterval);
            //  绘制单个符号的落笔时间（ms）
            // startDrawDuration=10
            String startDrawDuration = properties.getProperty("startDrawDuration");
            globalConfig.startDrawDuration = Long.parseLong(startDrawDuration);
            //  绘制单个符号的停笔时间（ms）
            // endDrawDuration=10
            String endDrawDuration = properties.getProperty("endDrawDuration");
            globalConfig.endDrawDuration = Long.parseLong(endDrawDuration);
            //  绘制单个符号的宽度（px）
            // drawSymbolWidth=50
            String drawSymbolWidth = properties.getProperty("drawSymbolWidth");
            globalConfig.drawSymbolWidth = Integer.parseInt(drawSymbolWidth);
            //  图像识别启用多线程
            // allowOCRMultiThreading=true
            String allowOCRMultiThreading = properties.getProperty("allowOCRMultiThreading");
            globalConfig.allowOCRMultiThreading = Boolean.parseBoolean(allowOCRMultiThreading);
        } catch (IOException e) {
            throw new RuntimeException("配置文件加载失败！", e);
        }
    }

    /**
     * 把当前最新的全局配置文件保存到 xyks-config.properties
     */
    public static void saveGlobalConfig() {
        Properties properties = new Properties();
        try (FileOutputStream fos = new FileOutputStream("xyks-config.properties")) {
            // 当前题目识别区域
            String currentArea = serializeArea(globalConfig.currentArea);
            properties.setProperty("currentArea", currentArea);
            // 下一题目识别区域
            String nextArea = serializeArea(globalConfig.nextArea);
            properties.setProperty("nextArea", nextArea);
            // 开始答题判定点（左上角返回的黄色区域）
            String point1 = serializePoint(globalConfig.point1);
            properties.setProperty("point1", point1);
            // 答题结束判定点（中间领奖励的弹窗按钮黄色）
            String point2 = serializePoint(globalConfig.point2);
            properties.setProperty("point2", point2);
            // 继续答题操作点（右下角按钮）
            String point3 = serializePoint(globalConfig.point3);
            properties.setProperty("point3", point3);
            // 开始匹配操作点（继续进入匹配按钮）
            String point4 = serializePoint(globalConfig.point4);
            properties.setProperty("point4", point4);
            // 作答区域左侧边缘
            String point5 = serializePoint(globalConfig.point5);
            properties.setProperty("point5", point5);
            // 答题期间页眉蓝色区域的任意点
            String point6 = serializePoint(globalConfig.point6);
            properties.setProperty("point6", point6);
            // 题目类型
            properties.setProperty("questionsType", globalConfig.questionsType);
            // 每轮完成题数
            properties.setProperty("questionsNum", String.valueOf(globalConfig.questionsNum));
            // 答题失败重试最大次数
            properties.setProperty("mistakeTryNum", String.valueOf(globalConfig.mistakeTryNum));
            // 中断答题的快捷键
            properties.setProperty("terminateKeyCombination", globalConfig.terminateKeyCombination);
            // 小猿口算软件内题目间切换动画时长（ms）
            properties.setProperty("questionAnimationDuration", String.valueOf(globalConfig.questionAnimationDuration));
            // 默认的鼠标进行每单位移动时的时间间隔（ms）
            properties.setProperty("defaultMoveInterval", String.valueOf(globalConfig.defaultMoveInterval));
            // 连续绘制符号时，每个符号间的时间间隔（ms）
            properties.setProperty("drawSymbolInterval", String.valueOf(globalConfig.drawSymbolInterval));
            // 绘制单个符号的落笔时间（ms）
            properties.setProperty("startDrawDuration", String.valueOf(globalConfig.startDrawDuration));
            // 绘制单个符号的停笔时间（ms）
            properties.setProperty("endDrawDuration", String.valueOf(globalConfig.endDrawDuration));
            // 绘制单个符号的宽度（px）
            properties.setProperty("drawSymbolWidth", String.valueOf(globalConfig.drawSymbolWidth));
            // 图像识别启用多线程
            properties.setProperty("allowOCRMultiThreading", String.valueOf(globalConfig.allowOCRMultiThreading));
            // 保存到文件
            properties.store(fos, "XYKS CONFIG");
        } catch (IOException e) {
            throw new RuntimeException("配置文件保存失败！", e);
        }
    }

    /**
     * 重置全局配置文件到初始状态
     */
    public static void resetGlobalConfig() {
        String config = """
                # 小猿口算全自动挂机答题小助手配置文件
                # 非专业人士请勿手动修改
                                \s
                # 当前题目识别区域
                currentArea=0,0,0,0
                # 下一题目识别区域
                nextArea=0,0,0,0
                # 开始答题判定点（左上角返回的黄色区域）
                point1=0,0,#000000
                # 答题结束判定点（中间领奖励的弹窗按钮黄色）
                point2=0,0,#000000
                # 继续答题操作点（右下角按钮）
                point3=0,0,#000000
                # 开始匹配操作点（继续进入匹配按钮）
                point4=0,0,#000000
                # 作答区域左侧边缘
                point5=0,0,#000000
                # 答题期间页眉蓝色区域的任意点
                point6=0,0,#000000
                # 题目类型
                questionsType=compare20
                # 每轮完成题数
                questionsNum=10
                # 答题失败重试最大次数
                mistakeTryNum=5
                # 中断答题的快捷键
                terminateKeyCombination=10
                # 小猿口算软件内题目间切换动画时长（ms）
                questionAnimationDuration=200
                # 默认的鼠标进行每单位移动时的时间间隔（ms）
                defaultMoveInterval=1
                # 连续绘制符号时，每个符号间的时间间隔（ms）
                drawSymbolInterval=5
                # 绘制单个符号的落笔时间（ms）
                startDrawDuration=10
                # 绘制单个符号的停笔时间（ms）
                endDrawDuration=10
                # 绘制单个符号的宽度（px）
                drawSymbolWidth=50
                # 图像识别启用多线程
                allowOCRMultiThreading=true
                """;
        try (FileWriter fw = new FileWriter("xyks-config.properties")) {
            fw.write(config);
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException("配置文件重置失败", e);
        }
    }

    /**
     * Area 序列化为 String
     *
     * @param area Area
     * @return String
     */
    private static String serializeArea(Area area) {
        return area.x + "," + area.y + "," + area.width + "," + area.height;
    }

    /**
     * String 反序列化 Area
     *
     * @param string String
     * @return Area
     */
    private static Area deserializeArea(String string) {
        String[] split = string.split(",");
        int[] arr = new int[4];
        for (int i = 0; i < 4; i++) {
            arr[i] = Integer.parseInt(split[i]);
        }
        return new Area(arr[0], arr[1], arr[2], arr[3]);
    }

    /**
     * Point 序列化为 String
     *
     * @param point Point
     * @return String
     */
    private static String serializePoint(Point point) {
        return point.x + "," + point.y + "," + Color.colorToHex(point.color);
    }

    /**
     * String 反序列化 Point
     *
     * @param string String
     * @return Point
     */
    private static Point deserializePoint(String string) {
        String[] split = string.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        Color color = Color.hexToColor(split[2]);
        return new Point(x, y, color);
    }

    /**
     * 配置文件内部类
     */
    public static class Config {
        public Area currentArea;
        public Area nextArea;
        public Point point1;
        public Point point2;
        public Point point3;
        public Point point4;
        public Point point5;
        public Point point6;
        public String questionsType;
        public int questionsNum;
        public int mistakeTryNum;
        public String terminateKeyCombination;
        public long questionAnimationDuration;
        public long defaultMoveInterval;
        public long drawSymbolInterval;
        public long startDrawDuration;
        public long endDrawDuration;
        public int drawSymbolWidth;
        public boolean allowOCRMultiThreading;

        @Override
        public String toString() {
            return "Config{" +
                    "currentArea=" + currentArea +
                    ", nextArea=" + nextArea +
                    ", point1=" + point1 +
                    ", point2=" + point2 +
                    ", point3=" + point3 +
                    ", point4=" + point4 +
                    ", point5=" + point5 +
                    ", point6=" + point6 +
                    ", questionsType='" + questionsType + '\'' +
                    ", questionsNum=" + questionsNum +
                    ", mistakeTryNum=" + mistakeTryNum +
                    ", terminateKeyCombination='" + terminateKeyCombination + '\'' +
                    ", defaultMoveInterval=" + defaultMoveInterval +
                    ", drawSymbolInterval=" + drawSymbolInterval +
                    ", startDrawDuration=" + startDrawDuration +
                    ", endDrawDuration=" + endDrawDuration +
                    ", drawSymbolWidth=" + drawSymbolWidth +
                    ", allowOCRMultiThreading=" + allowOCRMultiThreading +
                    '}';
        }
    }
}
