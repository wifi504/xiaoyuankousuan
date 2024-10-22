package com.lhl.xyks.test.utils;

import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.ui.GlobalKeyListener;
import com.lhl.xyks.ui.GlobalKeyListener.Key;
import com.lhl.xyks.ui.ScreenSelector;
import com.lhl.xyks.utils.ConfigParser;
import com.lhl.xyks.utils.Screen;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_2:29
 */
public class ConfigParserTest {
    @Test
    public void testLoadSymbolMap() throws IOException {
        HashMap<Character, ArrayList<Point>> hashMap = ConfigParser.loadSymbolMap();
        System.out.println(hashMap);
    }

    @Test
    public void testLoadGlobalConfig() throws InterruptedException {
        ConfigParser.loadGlobalConfig();
        System.out.println(ConfigParser.globalConfig);
        System.out.println(ConfigParser.globalConfig.point6);

    }

    @Test
    public void testResetConfig() {
        ConfigParser.resetGlobalConfig();
    }

    @Test
    public void testSetConfig() {
        GlobalKeyListener.initialize();
        ConfigParser.loadGlobalConfig();
        ScreenSelector selector = new ScreenSelector();
        System.out.println("选择点：作答区域左侧边缘");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        ConfigParser.globalConfig.point5 = selector.getSelectorPoint();
        System.out.println("选择点：答题期间页眉蓝色区域的任意点");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        ConfigParser.globalConfig.point6 = selector.getSelectorPoint();
        System.out.println("选择区域：当前题目识别区域");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        ConfigParser.globalConfig.currentArea = selector.getSelectorArea();
        System.out.println("选择区域：下一题目识别区域");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        ConfigParser.globalConfig.nextArea = selector.getSelectorArea();
        ConfigParser.saveGlobalConfig();
        GlobalKeyListener.shutdown();
    }
}
