package com.lhl.xyks;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.lhl.xyks.solve.AutoAnswering;
import com.lhl.xyks.solve.Compare20OCR;
import com.lhl.xyks.solve.DivisionOCR;
import com.lhl.xyks.ui.GlobalKeyListener;
import com.lhl.xyks.ui.GlobalKeyListener.Key;
import com.lhl.xyks.ui.Home;
import com.lhl.xyks.utils.ConfigParser;

/**
 * @author WIFI连接超时
 * @version 2.0
 * Create Time: 2024/10/14_0:50
 */
public class Main {
    public static void main(String[] args) {
//        new Home().launch();
//        GlobalKeyListener.initialize();
//
//        System.out.println("等待Ctrl+F10");
//        GlobalKeyListener.waitKeyTrigger(Key.CTRL, Key.F10);
//        System.out.println("监听到Ctrl+F10");
//        System.out.println("等待Ctrl+Alt+Shift+F9");
//        GlobalKeyListener.waitKeyTrigger(Key.CTRL, Key.ALT, Key.SHIFT, Key.F9);
//        System.out.println("监听到Ctrl+Alt+Shift+F9");
//        GlobalKeyListener.shutdown();
        ConfigParser.loadGlobalConfig();
        AutoAnswering.execute(new DivisionOCR(), 10);
    }
}