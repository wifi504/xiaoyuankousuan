package com.lhl.xyks.ui;

import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.utils.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * 屏幕坐标选择器
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/20_18:40
 */
public class ScreenSelector {
    private Area selectedArea;

    /**
     * 使用鼠标框选一个区域，得到 Area
     * 使用方式类似于截图工具
     *
     * @return Area
     */
    public Area getSelectorArea() {
        Screen screen = Screen.getScreen();
        BufferedImage capture = screen.capture(screen.getFullScreenArea());

        // 创建并显示全屏截图窗口
        ScreenImageWindow screenImageWindow = new ScreenImageWindow(capture);
        screenImageWindow.setVisible(true);

        // 创建并显示选择器窗口
        SelectorWindow selectorWindow = new SelectorWindow(screenImageWindow);
        selectorWindow.setVisible(true);

        // 阻塞，直到选择完成，返回区域
        synchronized (this) {
            try {
                wait();  // 等待选择完成
            } catch (InterruptedException ignore) {
            }
        }

        // 返回选择的区域
        return selectedArea;
    }

    // 全屏显示当前屏幕截图的窗口
    static class ScreenImageWindow extends JWindow {
        private final BufferedImage screenshot;

        public ScreenImageWindow(BufferedImage screenshot) {
            this.screenshot = screenshot;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(0, 0, screenSize.width, screenSize.height);
            setAlwaysOnTop(true);  // 保持窗口在最顶层
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (screenshot != null) {
                g.drawImage(screenshot, 0, 0, this);
            }
        }
    }

    // 监听鼠标事件，实时绘制选择框和辅助线的窗口
    class SelectorWindow extends JWindow {
        private Point startPoint;
        private final Rectangle selectedRect;
        private Point mousePoint;  // 当前鼠标位置

        public SelectorWindow(JWindow screenImageWindow) {
            startPoint = new Point();
            selectedRect = new Rectangle();
            mousePoint = new Point();

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(0, 0, screenSize.width, screenSize.height);
            setAlwaysOnTop(true);  // 保持窗口在最顶层

            // 半透明背景
            setBackground(new Color(0, 0, 0, 50));

            // 监听鼠标按下、拖动和释放事件
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();  // 记录起点
                    selectedRect.setBounds(startPoint.x, startPoint.y, 0, 0);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    updateSelectedRect(e.getPoint());
                    selectedArea = new Area(selectedRect.x, selectedRect.y, selectedRect.width, selectedRect.height);  // 构造选择的区域

                    dispose();
                    screenImageWindow.dispose();
                    synchronized (ScreenSelector.this) {
                        ScreenSelector.this.notify();  // 通知等待中的线程
                    }
                }
            });

            // 监听鼠标拖动和移动事件
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    updateSelectedRect(e.getPoint());
                    mousePoint = e.getPoint();  // 更新鼠标位置以保持辅助线移动
                    repaint();  // 重新绘制
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePoint = e.getPoint();  // 更新鼠标位置
                    repaint();  // 重新绘制辅助线
                }
            });
        }

        // 更新选中的矩形区域
        private void updateSelectedRect(Point endPoint) {
            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y);
            int width = Math.abs(startPoint.x - endPoint.x);
            int height = Math.abs(startPoint.y - endPoint.y);
            selectedRect.setBounds(x, y, width, height);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制鼠标辅助线
            g2d.setColor(Color.GREEN);
            g2d.drawLine(mousePoint.x, 0, mousePoint.x, getHeight());  // 竖线
            g2d.drawLine(0, mousePoint.y, getWidth(), mousePoint.y);  // 横线

            // 绘制选择的矩形区域
            if (selectedRect.width > 0 && selectedRect.height > 0) {
                drawSelectionRect(g2d, selectedRect);  // 使用提取的函数绘制矩形
            }
        }

        // 提取出来的绘制矩形的函数
        private void drawSelectionRect(Graphics2D g2d, Rectangle rect) {
            g2d.setColor(new Color(255, 0, 0, 20));  // 填充
            g2d.fill(rect);
            g2d.setColor(Color.RED);  // 红色边框
            g2d.draw(rect);
        }
    }
}
