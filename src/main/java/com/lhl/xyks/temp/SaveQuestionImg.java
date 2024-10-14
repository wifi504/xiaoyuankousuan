package com.lhl.xyks.temp;

import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.utils.Screen;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * eng模型以及equ数学公式模型，还有中文模型，对小猿口算的识别都不友好
 * 错误率太高
 * 因此准备自行训练模型
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_18:20
 */
public class SaveQuestionImg {

    public void generateTrainingData() {

    }

    public void getText() {
        Area area = new Area(1365, 334, 496, 120);
        Screen screen = Screen.getScreen();
        screen.captureToFile(area, new File("1.png"));
    }

    public void binary() {
        try {
            // 1. 读取彩色图像1.png
            BufferedImage input = ImageIO.read(new File("1.png"));
            if (input == null) {
                System.out.println("图像加载失败！");
                return;
            }

            // 2. 将输入图像转换为 FImage (浮点灰度图像)
            FImage fImage = ImageUtilities.createFImage(input);

//            // 3. 对图像进行直方图均衡化 (可选，用于增强对比度)
//            fImage.processInplace(new FEqualisation());

            // 4. 手动进行二值化处理
            float threshold = 0.5f;  // 设定一个阈值（0-1的浮点数），0.5 是常用的中间值
            FImage binaryImage = fImage.threshold(threshold);

            // 5. 将二值化结果转换为 BufferedImage 并保存
            BufferedImage binaryOutput = ImageUtilities.createBufferedImage(binaryImage);
            ImageIO.write(binaryOutput, "png", new File("binary_output_openimaj.png"));

            System.out.println("二值化处理完成并保存为 binary_output_openimaj.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
