package com.lhl.xyks.temp;

import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.Screen;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_20:43
 */
public class GenerateTrainingData {

    public void gen() {
        // 准备模型训练数据
        File folder = new File("training-data");
        /*
            sourcesType: 生成的训练资源来自哪种类型题目

            compare20: 20以内的数比大小
            compare1w: 万以内数比大小
         */
        String sourcesType = "compare20";

        // 截图的区域
        Area area = new Area(1365, 334, 496, 120);

        int begin = 19; // 后缀从几开始
        int end = 100; // 生成到几


        Scanner scanner = new Scanner(System.in);
        Screen screen = Screen.getScreen();

        // 从 gen-tool-config.properties 读取配置
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("gen-tool-config.properties")) {
            properties.load(fis);
            int x = Integer.parseInt(properties.getProperty("x"));
            int y = Integer.parseInt(properties.getProperty("y"));
            int width = Integer.parseInt(properties.getProperty("width"));
            int height = Integer.parseInt(properties.getProperty("height"));
            String type = properties.getProperty("sourcesType");
            int beginNum = Integer.parseInt(properties.getProperty("begin"));
            int endNum = Integer.parseInt(properties.getProperty("end"));
            // System.out.println(x + y + width + height + type + beginNum + endNum);
            area = new Area(x, y, width, height);
            sourcesType = type;
            begin = beginNum;
            end = endNum;
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 开始

        for (int i = begin; i <= end; i++) {
            File image = new File(folder, sourcesType + "_n" + i + ".png");
            File tag = new File(folder, sourcesType + "_n" + i + ".txt");

            System.out.println("输入" + i + "号图片对应内容：");
            String text = scanner.next();
            try (FileWriter fw = new FileWriter(tag)) {
                fw.write(text);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedImage capture = screen.capture(area);
            BufferedImage binary = ImageTools.pngToBinary(capture, 0.5f);
            try {
                ImageTools.saveImageToFile(binary, image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
