package com.lhl.xyks.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 全局键盘监听器
 */
public class GlobalKeyListener implements NativeKeyListener {

    private static boolean isRegistered = false;  // 判断是否已经注册监听器
    private static boolean captureActive = false; // 控制是否捕获按键组合
    private static final Set<Integer> requiredKeys = new HashSet<>();
    private final Set<Integer> pressedKeys = new HashSet<>();

    // 单例模式，确保只有一个实例
    private static final GlobalKeyListener instance = new GlobalKeyListener();

    // 用于阻塞和唤醒线程的锁
    private final Object lock = new Object();

    private GlobalKeyListener() {
        disableNativeHookLogging();
    }

    public static GlobalKeyListener getInstance() {
        return instance;
    }

    /**
     * 初始化全局键盘监听器（生命周期是JVM，可以在需要时进行此操作，但要在退出程序进行关闭）
     */
    public static void initialize() {
        if (!isRegistered) {
            try {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(getInstance());
                isRegistered = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭全局键盘监听器（生命周期是JVM，在整个程序结束进行此操作）
     */
    public static void shutdown() {
        if (isRegistered) {
            try {
                GlobalScreen.removeNativeKeyListener(getInstance());
                GlobalScreen.unregisterNativeHook();
                isRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 设置当前捕获的按键组合
    public static void waitKeyTrigger(Key... keys) {
        requiredKeys.clear();
        for (Key key : keys) {
            requiredKeys.add(key.getKeyCode());
        }
        captureActive = true;  // 激活捕获

        // 阻塞当前线程，直到按键组合被触发
        synchronized (getInstance().lock) {
            try {
                getInstance().lock.wait();  // 阻塞线程，等待唤醒
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (captureActive) {
            pressedKeys.add(e.getKeyCode());
            if (pressedKeys.containsAll(requiredKeys)) {
                captureActive = false;  // 捕获到组合键后，停用捕获
                synchronized (lock) {
                    lock.notify();  // 唤醒等待的线程
                }
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    private void disableNativeHookLogging() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    // 内部类 Key
    public static class Key {
        public static final Key CTRL = new Key(NativeKeyEvent.VC_CONTROL);
        public static final Key ALT = new Key(NativeKeyEvent.VC_ALT);
        public static final Key SHIFT = new Key(NativeKeyEvent.VC_SHIFT);
        public static final Key F9 = new Key(NativeKeyEvent.VC_F9);
        public static final Key F10 = new Key(NativeKeyEvent.VC_F10);
        public static final Key F11 = new Key(NativeKeyEvent.VC_F11);
        public static final Key F12 = new Key(NativeKeyEvent.VC_F12);

        private final int keyCode;

        private Key(int keyCode) {
            this.keyCode = keyCode;
        }

        public int getKeyCode() {
            return keyCode;
        }
    }
}
