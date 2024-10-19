package com.lhl.xyks.temp;

import com.lhl.xyks.solve.ExpressionOptimizer;
import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.ocr.OCR;
import com.lhl.xyks.utils.ocr.OCRService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/19_21:20
 */
public class ModelTester {

    public static void runTestAll() throws IOException {
        // 统计模型完成所有问题的正确率以及最优表现
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入目录");
        String folder = scanner.next();
        System.out.println("输入题目");
        String question = scanner.next();
        System.out.println("输入范围（多少-多少）");
        String[] rangeStr = scanner.next().split("-");
        int start = Integer.parseInt(rangeStr[0]);
        int end = Integer.parseInt(rangeStr[1]);

        // 准备结果
        ArrayList<ImageResult> imageResults = new ArrayList<>();
        imageResults.add(new ImageResult("xyks", "yuantu"));
        imageResults.add(new ImageResult("xyks", "lashen"));
        imageResults.add(new ImageResult("xyks", "mohu"));
        imageResults.add(new ImageResult("xyks", "ruihua"));

        imageResults.add(new ImageResult("eng", "yuantu"));
        imageResults.add(new ImageResult("eng", "lashen"));
        imageResults.add(new ImageResult("eng", "mohu"));
        imageResults.add(new ImageResult("eng", "ruihua"));

//        imageResults.add(new ImageResult("zh", "yuantu"));
//        imageResults.add(new ImageResult("zh", "lashen"));
//        imageResults.add(new ImageResult("zh", "mohu"));
//        imageResults.add(new ImageResult("zh", "ruihua"));

        imageResults.add(new ImageResult("paddle", "yuantu"));
        imageResults.add(new ImageResult("paddle", "lashen"));
        imageResults.add(new ImageResult("paddle", "mohu"));
        imageResults.add(new ImageResult("paddle", "ruihua"));


        File questionsFolder = new File(folder);
        for (int i = start; i <= end; i++) {
            System.out.println("-------< 进度：" + i + "/" + end + " >---------");
            File file = new File(questionsFolder, question + "_n" + i + ".png");
            BufferedImage image = ImageTools.readImageFromFile(file);


            // 预期结果
            FileReader fr = new FileReader(new File(questionsFolder, question + "_n" + i + ".txt"));
            // 开始读
            String answer = "";
            char[] chars = new char[512];
            int readCount = 0;
            while ((readCount = fr.read(chars)) != -1) {
                answer = new String(chars, 0, readCount);
            }

            // 开始识别
            String finalAnswer = answer;
            imageResults.forEach(imageResult -> checkAnswer(image, finalAnswer, imageResult));
        }

        System.out.println("本轮" + question + "跑分结果：");
        imageResults.forEach(System.out::println);
    }

    static void checkAnswer(BufferedImage image, String answer, ImageResult imageResult) {
        BufferedImage ocrImage = null;
        switch (imageResult.ImageType) {
            case "yuantu" -> {
                ocrImage = image;
            }
            case "lashen" -> {
                ocrImage = ImageTools.resizePNG(image, 1.25, 1);
            }
            case "mohu" -> {
                ocrImage = ImageTools.blurPNG(image, 1.5f);
            }
            case "ruihua" -> {
                ocrImage = ImageTools.sharpenPNG(ImageTools.blurPNG(image, 1.5f), 2f);
            }
        }
        String recognize = OCRService.getModel(imageResult.ModelType).recognize(ocrImage);
        recognize = OCRService.getModel(imageResult.ModelType).optimize(recognize);
        if (recognize.equals(answer)) {
            System.out.println("识别[通过    ]！模型：" + imageResult.ModelType
                    + "；图片类型：" + imageResult.ImageType
                    + "；识别结果：" + recognize);
            imageResult.correct();
        } else {
            System.out.println("识别[    错误]！模型：" + imageResult.ModelType
                    + "；图片类型：" + imageResult.ImageType
                    + "；识别结果：" + recognize
                    + "；预期结果：" + answer);
            imageResult.wrong();
        }
    }


    static String xyks;
    static String eng;
    static String paddle;

    // 配合双重优化器，测试效果
    public static void testSingleQuestion() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入目录");
        String folder = scanner.next();
        String question = "arithmetic";
        System.out.println("输入范围（多少-多少）");
        String[] rangeStr = scanner.next().split("-");
        int start = Integer.parseInt(rangeStr[0]);
        int end = Integer.parseInt(rangeStr[1]);
        File questionsFolder = new File(folder);
        for (int i = start; i <= end; i++) {
            System.out.println("-------< 进度：" + i + "/" + end + " >---------");
            File file = new File(questionsFolder, question + "_n" + i + ".png");
            BufferedImage image = ImageTools.readImageFromFile(file);

            // 预期结果
            FileReader fr = new FileReader(new File(questionsFolder, question + "_n" + i + ".txt"));
            // 开始读
            String answer = "";
            char[] chars = new char[512];
            int readCount = 0;
            while ((readCount = fr.read(chars)) != -1) {
                answer = new String(chars, 0, readCount);
            }

            // 识别题目
            OCR xyksOCR = OCRService.getModel("xyks");
            OCR engOCR = OCRService.getModel("eng");
            OCR paddleOCR = OCRService.getModel("paddle");

            BufferedImage resizePNG = ImageTools.resizePNG(image, 1.25, 1);

            Thread t1 = new Thread(() -> {
                xyks = xyksOCR.optimize(xyksOCR.recognize(resizePNG));
            });
            Thread t2 = new Thread(() -> {
//                eng = engOCR.optimize(engOCR.recognize(image));
            });
            Thread t3 = new Thread(() -> {
                paddle = paddleOCR.optimize(paddleOCR.recognize(resizePNG));
            });
            t1.start();
            t2.start();
            t3.start();
            t1.join();
            t2.join();
            t3.join();

            String result = ExpressionOptimizer.optimizeArithmetic(xyks, paddle);

            String[] split = result.split(":");

            if (split[0].equals(answer)) {
                System.out.println("识别：[通过    ]!  结果：" + split[0] + "；来自模型：" + split[1]);
            } else {
                System.out.println("识别：[    错误]!  结果：" + split[0] + "；预期：" + answer + "；来自模型：" + split[1]);
            }
        }
    }
}
