# class Mouse 鼠标工具类

## 一、成员变量

Mouse mouse 
null，单例（对应有个单例的Robot）

long defaultMoveInterval
1，默认的鼠标进行每单位移动时的时间间隔（ms）

long drawSymbolInterval
10，连续绘制符号时，每个符号间的时间间隔（ms）

// 关于落笔与停笔时间：防止因为鼠标按下瞬间开始移动和移动到终点瞬间就松开时，引起的因画板程序反应不过来导致的丢失轨迹的问题

long startDrawDuration
10，绘制单个符号的落笔时间（ms）

long endDrawDuration
10，绘制单个符号的停笔时间（ms）

HashMap<Character, ArrayList<Point>> symbolMap
存放每个绘制符号的点轨迹，只有实心点才会绘制出痕迹，空心点会让鼠标修改落笔位置，这个Map会在初始化Mouse实例时根据"symbol-mapper.json"自动生成，符号映射文件中的坐标是相对坐标，在一个宽100高200的范围内，依次存放每个笔画起止点



## 二、方法

《静态》
Mouse getMouse() 
返回一个单例的Mouse对象，可以执行一系列鼠标动作

《移动相关》
Point getCurrentPoint()
返回鼠标当前所在的绝对坐标点

void setDefaultMoveInterval(long interval)
设置默认鼠标单位移动间隔（ms）

void moveTo(Point p)
鼠标立刻移动到点p

void smoothMoveTo(Point p)
鼠标以默认间隔平滑移动到点p

void smoothMoveTo(Point p, long interval)
鼠标以指定间隔平滑移动到点p

《按键相关》
void leftPress()
void leftRelease()
void rightPress()
void rightRelease()
左右键的按下和松开

void leftClick()
void rightClick()
左右键的点击

《符号绘制》
void setDrawSymbolInterval(long interval)
设置每个符号间的时间间隔

void setStartDrawDuration(long duration)
设置落笔时间

void setEndDrawDuration(long duration)
设置停笔时间

void drawSymbol(char symbol)
在当前鼠标位置绘制指定的符号

void drawSymbol(char symbol, Point p)
在指定鼠标位置（p点）绘制指定的符号

void drawSymbols(String symbols)
在当前鼠标位置开始横向绘制一系列符号

void drawSymbols(String symbols, Point p)
在指定鼠标位置（p点）开始横向绘制一系列符号