package com.lhl.xyks.test.utils;

import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.OCR;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_16:07
 */
public class OCRTest {

    @Test
    public void testRecognizeFormula() throws IOException {
        OCR ocr = new OCR();
        System.out.println(ocr.recognize(ImageTools.readImageFromFile(new File("D:/binary.png"))));
    }

    @Test
    public void testRecognizeImages() throws IOException {
        OCR ocr = new OCR();
        int pass = 0;
        int total = 50;
        for (int i = 1; i <= total; i++) {
            StringBuilder sb = new StringBuilder();
            System.out.println("识别：" + i);
            File file = new File("training-data/integer_addition_subtraction_n" + i + ".png");
            BufferedImage image = ImageTools.readImageFromFile(file);
            String res1 = ocr.recognize(image).trim();
            String res2 = ocr.recognize(ImageTools.resizePNG(image, 1.5, 1)).trim();
            String res3 = ocr.recognize(ImageTools.blurPNG(image, 1.5f)).trim();
            String res4 = ocr.recognize(ImageTools.sharpenPNG(ImageTools.blurPNG(image, 1.5f), 2f)).trim();
            System.out.println("原图结果：" + res1);
            System.out.println("拉伸结果：" + res2);
            System.out.println("模糊结果：" + res3);
            System.out.println("锐化结果：" + res4);

            System.out.print("预期：");
            try (FileReader fr = new FileReader("training-data/integer_addition_subtraction_n" + i + ".txt")) {
                // 开始读
                char[] chars = new char[512];
                int readCount = 0;
                while ((readCount = fr.read(chars)) != -1) {
                    String s = new String(chars, 0, readCount);
                    sb.append(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(sb);

            System.out.print("识别结果：");
            boolean isPass =
                    sb.toString().equals(res1)
                            || sb.toString().equals(res2)
                            || sb.toString().equals(res3)
                            || sb.toString().equals(res4);
            System.out.println(isPass ? "通过" : "错误！");
            if (isPass) pass++;
            System.out.println("----------------------------------");
        }
        System.out.println("测试结束，通过率：" + (double) pass / total);
//        try (FileWriter fr = new FileWriter("training-data/text.txt")) {
//            fr.write(sb.toString());
//            fr.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    @Test
    public void testOCRPass() {
        HashMap<String, Integer> questions = new HashMap<>();
        questions.put("arithmetic", 100);
        questions.put("compare20", 100);
        questions.put("compare100", 100);
        questions.put("compare1w", 100);
        questions.put("decimal_multiplication", 100);
        questions.put("division", 100);
        questions.put("pi_square", 100);

        HashMap<String, Double> score = new HashMap<>();
        questions.keySet().forEach(question -> score.put(question, 0.0));

        ArrayList<Thread> threads = new ArrayList<>();

        questions.forEach((question, num) -> threads.add(scanQuestions(question, num, score)));
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("统计结束，模型信息如下：");
        score.forEach((question, s) -> {
            System.out.print("题目类型：");
            System.out.print(question);
            System.out.println("; 正确率：" + s);
        });
    }

    public String[] doThreadOCR(BufferedImage image) {
        String[] result = new String[4];

        Thread t1 = new Thread(() -> {
            OCR ocr = new OCR();
            result[0] = ocr.recognize(image).trim();
        });
        Thread t2 = new Thread(() -> {
            OCR ocr = new OCR();
            result[1] = ocr.recognize(ImageTools.resizePNG(image, 1.5, 1)).trim();
        });
        Thread t3 = new Thread(() -> {
            OCR ocr = new OCR();
            result[2] = ocr.recognize(ImageTools.blurPNG(image, 1.5f)).trim();
        });
        Thread t4 = new Thread(() -> {
            OCR ocr = new OCR();
            result[3] = ocr.recognize(ImageTools.sharpenPNG(ImageTools.blurPNG(image, 1.5f), 2f)).trim();
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Thread scanQuestions(String question, int num, HashMap<String, Double> score) {
        Thread thread = new Thread(() -> {
            int pass = 0;
            for (int i = 1; i <= num; i++) {
                StringBuilder sb = new StringBuilder();
                System.out.println("识别：" + i);
                File file = new File("training-data/" + question + "_n" + i + ".png");
                BufferedImage image = null;
                try {
                    image = ImageTools.readImageFromFile(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String[] res = doThreadOCR(image);
                System.out.println("原图结果：" + res[0]);
                System.out.println("拉伸结果：" + res[1]);
                System.out.println("模糊结果：" + res[2]);
                System.out.println("锐化结果：" + res[3]);

                System.out.print("预期：");
                try (FileReader fr = new FileReader("training-data/" + question + "_n" + i + ".txt")) {
                    // 开始读
                    char[] chars = new char[512];
                    int readCount = 0;
                    while ((readCount = fr.read(chars)) != -1) {
                        String s = new String(chars, 0, readCount);
                        sb.append(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(sb);

                System.out.print("识别结果：");
                boolean isPass =
                        sb.toString().equals(res[0])
                                || sb.toString().equals(res[1])
                                || sb.toString().equals(res[2])
                                || sb.toString().equals(res[3]);
                System.out.println(isPass ? "通过" : "错误！");
                if (isPass) pass++;
                System.out.println("----------------------------------");
            }
            score.put(question, (double) pass / num);
        });
        thread.start();
        return thread;
    }
}
