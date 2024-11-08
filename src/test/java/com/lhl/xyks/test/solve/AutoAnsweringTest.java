package com.lhl.xyks.test.solve;

import com.lhl.xyks.solve.*;
import com.lhl.xyks.utils.ConfigParser;
import com.lhl.xyks.utils.Mouse;
import com.lhl.xyks.utils.Screen;
import org.junit.Test;

/**
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/21_17:35
 */
public class AutoAnsweringTest {
    @Test
    public void testExecute() throws InterruptedException {
        ConfigParser.loadGlobalConfig();
        for (int i = 0; i < 5000; i++) {
            Screen.getScreen().waitUntilPointColorLike(ConfigParser.globalConfig.point6, ConfigParser.globalConfig.point6.color, 20 * 1000);
            AutoAnswering.execute(new DivisionOCR(), 10);
            System.out.println("准备下一轮匹配");
            Screen.getScreen().waitUntilPointColorLike(ConfigParser.globalConfig.point2, ConfigParser.globalConfig.point2.color, 10 * 1000);
            Thread.sleep(500);
            Mouse.getMouse().leftClickAt(ConfigParser.globalConfig.point2);
            Screen.getScreen().waitUntilPointColorLike(ConfigParser.globalConfig.point3, ConfigParser.globalConfig.point3.color, 10 * 1000);
            Thread.sleep(500);
            Mouse.getMouse().leftClickAt(ConfigParser.globalConfig.point3);
            Screen.getScreen().waitUntilPointColorLike(ConfigParser.globalConfig.point4, ConfigParser.globalConfig.point4.color, 10 * 1000);
            Thread.sleep(500);
            System.out.println("开始匹配");
            Mouse.getMouse().leftClickAt(ConfigParser.globalConfig.point4);

        }
    }
}
