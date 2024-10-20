package com.lhl.xyks.test.utils;

import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.ui.ScreenSelector;
import com.lhl.xyks.utils.ConfigParser;
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
        ScreenSelector screenSelector = new ScreenSelector();
        ConfigParser.globalConfig.currentArea = screenSelector.getSelectorArea();
        ConfigParser.saveGlobalConfig();
        System.out.println("重新加载");
        ConfigParser.loadGlobalConfig();
        System.out.println(ConfigParser.globalConfig);
        System.out.println(ConfigParser.globalConfig.currentArea);

    }

    @Test
    public void testResetConfig() {
        ConfigParser.resetGlobalConfig();
    }
}
