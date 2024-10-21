package com.lhl.xyks.solve;

import com.lhl.xyks.pk.Pause;
import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.pojo.Color;
import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.pojo.Question;
import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.Mouse;
import com.lhl.xyks.utils.Screen;
import com.lhl.xyks.utils.ocr.OCR;
import com.lhl.xyks.utils.ocr.OCRService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 自动答题器
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/21_15:55
 */
public class AutoAnswering {

    // TODO 这里的参数由参数加载器获取

    // 左上角的返回，需要颜色信息
    static Point point1 = new Point(2519, 121, Color.hexToColor("#FEE305"));
    // 当前题目区域
    static Area currentArea = new Area(2522, 322, 445, 113);
    // 下一题目区域
    static Area nextArea = new Area(2578, 440, 329, 84);


    // 工具
    static Screen screen = Screen.getScreen();
    static Mouse mouse = Mouse.getMouse();
    // 题目缓存
    static ArrayList<Question> questions = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException, IOException {
        // 左上角的返回出现了吗
        screen.waitUntilPointColorLike(point1, point1.color);
        System.out.println("开始答题");
        // 截图
        BufferedImage currentImage = screen.capture(currentArea);
        BufferedImage nextImage = screen.capture(nextArea);
        nextImage = ImageTools.pngToBinary(nextImage, 0.8f);
        ImageTools.saveImageToFile(nextImage, new File("test.png"));
        BufferedImage resizePNG = ImageTools.resizePNG(nextImage, 1.25, 1);
        BufferedImage sharpenPNG = ImageTools.sharpenPNG(ImageTools.blurPNG(nextImage, 1.5f), 2.0f);
        // 识别题目
        Thread t1 = new Thread(() -> {
            // TODO xyks = OCRService.getModel("xyks").optimize(OCRService.getModel("xyks").recognize(resizePNG));
        });
        Thread t2 = new Thread(() -> {
            // TODO paddle = OCRService.getModel("paddle").optimize(OCRService.getModel("paddle").recognize(sharpenPNG));
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // TODO String result = ExpressionOptimizer.optimizeDecimalMultiplication(xyks, paddle);
        // System.out.println(result);
    }

    /**
     * 执行连续的自动答题
     *
     * @param questionOCR 对应类型题目的识别器
     * @param num         题目数量
     */
    public static void execute(QuestionOCR questionOCR, int num) {
        // 准备答题列表
        for (int i = 1; i <= num; i++) {
            questions.add(new Question(i));
        }
        int i = 1; // 当前题号
        // 通过页眉蓝色区域有没有变白判断答题是否结束
        while (true){
            // 完成了一次答题
            // TODO 允许的犯错
        }
    }
}
