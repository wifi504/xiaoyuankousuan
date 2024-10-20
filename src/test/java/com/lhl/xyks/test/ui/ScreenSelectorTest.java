package com.lhl.xyks.test.ui;

import com.lhl.xyks.ui.ScreenSelector;
import org.junit.Test;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/20_18:50
 */
public class ScreenSelectorTest {
    @Test
    public void testSelectorArea() {
        ScreenSelector selector = new ScreenSelector();
        System.out.println(selector.getSelectorArea());
    }

    @Test
    public void testSelectorPoint() {
        ScreenSelector selector = new ScreenSelector();
        System.out.println(selector.getSelectorPoint());
    }
}
