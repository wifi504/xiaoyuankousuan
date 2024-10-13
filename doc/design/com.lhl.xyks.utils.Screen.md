class Screen 屏幕工具类
一、成员变量
Screen screen
null，单例（对应有个单例的Robot）

二、方法
《静态》
Screen getScreen()
返回一个单例的Screen对象（对应有个单例的Robot）

《屏幕截图》
BufferedImage capture(Area area)
获取指定区域的屏幕截图

《基于屏幕信息的线程管理》
boolean isPointColorLike(Point point, Color color)
指定点的颜色跟这个颜色相似吗？

void waitUntilPointColorLike(Point point, Color color)
永久阻塞当前线程，直到指定点颜色与该颜色相似

void waitUntilPointColorLike(Point point, Color color, long timeout)
超时阻塞当前线程，直到指定点颜色与该颜色相似，如果达到超时时间颜色也没有相似，取消阻塞

void waitWhilePointColorLike(Point point, Color color)
永久阻塞当前线程，直到指定点颜色与该颜色不再相似

void waitWhilePointColorLike(Point point, Color color, long timeout)
超时阻塞当前线程，直到指定点颜色与该颜色不再相似，如果达到超时时间颜色也仍然相似，取消阻塞