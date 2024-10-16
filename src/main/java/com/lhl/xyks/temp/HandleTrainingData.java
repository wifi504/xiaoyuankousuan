package com.lhl.xyks.temp;

import com.lhl.xyks.utils.ImageTools;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/16_1:55
 */
public class HandleTrainingData {

    public HandleTrainingData setFolderPath(String folderPath) {
        this.folderPath = folderPath;
        return this;
    }

    // 自动化截图完毕后，会得到若干源文件，我们需要进行一些处理以供训练
    String folderPath = "training-data"; // training-data 目录

    List<String> pngFileNameList;

    {
        File trainingDataFolder = new File(folderPath);
        if (!trainingDataFolder.exists() || !trainingDataFolder.isDirectory()) {
            throw new RuntimeException("指定的路径不是文件夹或文件夹不存在");
        }
        File[] pngFiles = trainingDataFolder.listFiles(((dir, name) -> name.toLowerCase().endsWith(".png")));
        // 判断是否找到任何 .png 文件
        if (pngFiles == null || pngFiles.length == 0) {
            throw new RuntimeException("文件夹中没有找到 .png 图片");
        }
        // 遍历并赋值 pngFileNameList
        pngFileNameList = new ArrayList<>();
        for (File pngFile : pngFiles) {
            String fileName = pngFile.getName();
            String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            pngFileNameList.add(nameWithoutExtension);
        }
        if (pngFileNameList.size() == 0) throw new RuntimeException("pngFileNameList 数量为0！");
    }

    // Step1.
    // 处理文本文档：所有的 + - * / = ? 左右两侧都得有 1 个空格
    // 这样在训练时，总会把一组一组的数字认为是 word 提高准确率
    public void step1() {
        // 比大小的题有可能没有问号，这种得人工加空格
        StringBuilder edit = new StringBuilder();

        // 把每个txt文件拿出来
        pngFileNameList.forEach(name -> {
            System.out.println("<------- " + name + " ------->");
            // 拿到 txt
            File txt = new File(folderPath, name + ".txt");
            // 开始读文件
            StringBuilder text = new StringBuilder();
            try (FileReader fr = new FileReader(txt)) {
                // 开始读
                char[] chars = new char[512];
                int readCount = 0;
                while ((readCount = fr.read(chars)) != -1) {
                    text.append(new String(chars, 0, readCount));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("原始内容：" + text);

            // 找到特殊的比较文件
            if (name.contains("compare") && !text.toString().contains("?")) {
                edit.append(name).append("\n");
            }

            // 遍历读到的字符
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                // 如果是中文字符，变英文的
                if (ch == '？') ch = '?';
                if (ch == '（') ch = '(';
                if (ch == '）') ch = ')';
                // 如果不是最后一个，并且是 + - * / = ? 之一 就两边加空格
                if (i != text.length() - 1 &&
                        (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '?')) {
                    sb.append(' ').append(ch).append(' ');
                } else {
                    // 否则直接追加
                    sb.append(ch);
                }
            }
            // 把额外的空格删掉
            String result = sb.toString().replaceAll("\\s{2,}", " ").trim();
            System.out.println("处理结果：" + result);
            // 覆盖写入原来的文件
            try (FileWriter fw = new FileWriter(txt)) {
                // 开始写
                fw.write(result);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println();
        });

        System.out.println("请人工处理以下项：");
        System.out.println(edit);
    }

    // Step2.
    // 去重，题目很显然是有概率重复的，根据txt内容把重复的删除掉
    public void step2() {
        // 使用 HashSet 来存储唯一的文本内容
        HashSet<String> uniqueContents = new HashSet<>();

        // 存储要删除的文件列表
        List<String> filesToDelete = new ArrayList<>();

        // 遍历每个 txt 文件
        pngFileNameList.forEach(name -> {
            System.out.println("<------- 检查：" + name + " ------->");

            // 拿到 txt 文件
            File txtFile = new File(folderPath, name + ".txt");

            // 读取文件内容
            StringBuilder text = new StringBuilder();
            try (FileReader fr = new FileReader(txtFile)) {
                char[] buffer = new char[512];
                int readCount;
                while ((readCount = fr.read(buffer)) != -1) {
                    text.append(buffer, 0, readCount);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return; // 继续处理下一个文件
            }

            // 检查文本内容是否已存在
            String content = text.toString().trim();
            if (uniqueContents.contains(content)) {
                // 如果已存在，则标记该文件为需要删除
                filesToDelete.add(name);
                System.out.println("找到重复文件：" + name);
            } else {
                // 如果不存在，则添加到唯一集合
                uniqueContents.add(content);
            }

            System.out.println();
        });

        // 删除重复文件
        filesToDelete.forEach(name -> {
            System.out.println("<------- 执行：" + name + " ------->");

            // 删除对应的 png 文件和 txt 文件
            File pngFile = new File(folderPath, name + ".png");
            File txtFile = new File(folderPath, name + ".txt");

            if (pngFile.delete()) {
                System.out.println("删除文件：" + pngFile.getName());
            } else {
                System.out.println("无法删除文件：" + pngFile.getName());
            }

            if (txtFile.delete()) {
                System.out.println("删除文件：" + txtFile.getName());
            } else {
                System.out.println("无法删除文件：" + txtFile.getName());
            }
            System.out.println();
        });

        System.out.println("去重完成，共删除 " + filesToDelete.size() + " 个重复文件。");
    }

    // Step3.
    // 生成 1.25x 1.5x 0.75x 以及 水平1.25x 竖直1.25x 的版本
    // _w1.25h1.25 _w1.5h1.5 _w0.75h0.75 _w1.25h1.0 _w1.0h1.25
    // 所有原始的文件，后缀加上 _w1.0h1.0 （无任何缩放）
    // !!! 经过测试，一个png会衍生出18个来，太爆炸了，放弃 _w0.75h0.75 _w1.0h1.25
    public void step3() {
        // 把每个png文件拿出来
        pngFileNameList.forEach(name -> {
            System.out.println("<------- " + name + " ------->");
            try {
                // 拿到 png
                File png = new File(folderPath, name + ".png");
                BufferedImage image = ImageTools.readImageFromFile(png);
                // 拿到 txt 的 Path 以便创建副本
                Path txtPath = Paths.get(folderPath, name + ".txt");
                // 生成 _w1.25h1.25
                BufferedImage png1 = ImageTools.resizePNG(image, 1.25, 1.25);
                ImageTools.saveImageToFile(png1, new File(folderPath, name + "_w1.25h1.25.png"));
                System.out.println("生成：" + name + "_w1.25h1.25.png");
                Files.copy(txtPath, Paths.get(folderPath, name + "_w1.25h1.25.txt"));
                System.out.println("复制：" + name + "_w1.25h1.25.txt");
                // 生成 _w1.5h1.5
                BufferedImage png2 = ImageTools.resizePNG(image, 1.5, 1.5);
                ImageTools.saveImageToFile(png2, new File(folderPath, name + "_w1.5h1.5.png"));
                System.out.println("生成：" + name + "_w1.5h1.5.png");
                Files.copy(txtPath, Paths.get(folderPath, name + "_w1.5h1.5.txt"));
                System.out.println("复制：" + name + "_w1.5h1.5.txt");
                // 生成 _w0.75h0.75
//                BufferedImage png3 = ImageTools.resizePNG(image, 0.75, 0.75);
//                ImageTools.saveImageToFile(png3, new File(folderPath, name + "_w0.75h0.75.png"));
//                System.out.println("生成：" + name + "_w0.75h0.75.png");
//                Files.copy(txtPath, Paths.get(folderPath, name + "_w0.75h0.75.txt"));
//                System.out.println("复制：" + name + "_w0.75h0.75.txt");
                // 生成 _w1.25h1.0
                BufferedImage png4 = ImageTools.resizePNG(image, 1.25, 1.0);
                ImageTools.saveImageToFile(png4, new File(folderPath, name + "_w1.25h1.0.png"));
                System.out.println("生成：" + name + "_w1.25h1.0.png");
                Files.copy(txtPath, Paths.get(folderPath, name + "_w1.25h1.0.txt"));
                System.out.println("复制：" + name + "_w1.25h1.0.txt");
                // 生成 _w1.0h1.25
//                BufferedImage png5 = ImageTools.resizePNG(image, 1.0, 1.25);
//                ImageTools.saveImageToFile(png5, new File(folderPath, name + "_w1.0h1.25.png"));
//                System.out.println("生成：" + name + "_w1.0h1.25.png");
//                Files.copy(txtPath, Paths.get(folderPath, name + "_w1.0h1.25.txt"));
//                System.out.println("复制：" + name + "_w1.0h1.25.txt");
                // 原文件加后缀
                Files.move(Paths.get(folderPath, name + ".png"),
                        Paths.get(folderPath, name + "_w1.0h1.0.png"));
                System.out.println("重命名：" + name + "_w1.0h1.0.png");
                Files.move(txtPath, Paths.get(folderPath, name + "_w1.0h1.0.txt"));
                System.out.println("重命名：" + name + "_w1.0h1.0.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        });
    }

    // Step4.
    // 生成 模糊(1.5) 版本 再生成 锐化(2) 版本
    // _blur1.5 _sharpen2
    // 所有原始的文件，后缀加上 _normal （无任何滤镜）
    public void step4() {
        // 把每个png文件拿出来
        pngFileNameList.forEach(name -> {
            System.out.println("<------- " + name + " ------->");
            try {
                // 拿到 png
                File png = new File(folderPath, name + ".png");
                BufferedImage image = ImageTools.readImageFromFile(png);
                // 拿到 txt 的 Path 以便创建副本
                Path txtPath = Paths.get(folderPath, name + ".txt");
                // 生成 _blur1.5
                BufferedImage png1 = ImageTools.blurPNG(image, 1.5f);
                ImageTools.saveImageToFile(png1, new File(folderPath, name + "_blur1.5.png"));
                System.out.println("生成：" + name + "_blur1.5.png");
                Files.copy(txtPath, Paths.get(folderPath, name + "_blur1.5.txt"));
                System.out.println("复制：" + name + "_blur1.5.txt");
                // 在 _blur1.5 上生成 _sharpen2
                BufferedImage png2 = ImageTools.sharpenPNG(png1, 2);
                ImageTools.saveImageToFile(png2, new File(folderPath, name + "_blur1.5_sharpen2.png"));
                System.out.println("生成：" + name + "_blur1.5_sharpen2.png");
                Files.copy(txtPath, Paths.get(folderPath, name + "_blur1.5_sharpen2.txt"));
                System.out.println("复制：" + name + "_blur1.5_sharpen2.txt");
                // 原文件加后缀
                Files.move(Paths.get(folderPath, name + ".png"),
                        Paths.get(folderPath, name + "_normal.png"));
                System.out.println("重命名：" + name + "_normal.png");
                Files.move(txtPath, Paths.get(folderPath, name + "_normal.txt"));
                System.out.println("重命名：" + name + "_normal.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        });
    }

    // Step5.
    // Tesseract 5.0 的模型训练，要求 .txt 文件的结尾是 .gt.txt
    public void step5() {
        // 把每个txt文件拿出来
        pngFileNameList.forEach(name -> {
            System.out.println("<------- " + name + " ------->");
            // 拿到 txt 的 Path
            Path txtPath = Paths.get(folderPath, name + ".txt");
            try {
                Files.move(txtPath, Paths.get(folderPath, name + ".gt.txt"));
                System.out.println("重命名：" + name + ".gt.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        });
    }

    // Step6.
    // 在训练模型的时候，为了减少模型的偏差、提高训练的稳定性、防止过拟合、提高模型的泛化能力
    // 打乱所有数据的顺序是一个不错的选择
    // 在正式训练之前，文件名UUID化会导致检查训练数据工作变得很艰难
    // 谨慎执行这一步！
    public void step6() {
        // 遍历每个 png 文件
        pngFileNameList.forEach(name -> {
            System.out.println("<------- 处理：" + name + " ------->");

            // 生成一个新的 UUID 作为文件名
            String newFileName = UUID.randomUUID().toString();

            // 拿到原始的 png 和 txt 文件路径
            Path pngPath = Paths.get(folderPath, name + ".png");
            Path txtPath = Paths.get(folderPath, name + ".gt.txt");

            // 新的文件路径
            Path newPngPath = Paths.get(folderPath, newFileName + ".png");
            Path newTxtPath = Paths.get(folderPath, newFileName + ".gt.txt");

            try {
                // 重命名 png 文件
                Files.move(pngPath, newPngPath);
                System.out.println("重命名：" + name + ".png -> " + newFileName + ".png");

                // 重命名 txt 文件
                Files.move(txtPath, newTxtPath);
                System.out.println("重命名：" + name + ".gt.txt -> " + newFileName + ".gt.txt");
            } catch (IOException e) {
                System.out.println("重命名失败：" + name);
                e.printStackTrace();
            }
        });

        System.out.println("UUID重命名完成。");
    }
}
