package demo.verification_code_demo.util;
import demo.verification_code_demo.bean.VerificationCodePlace;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class VerificationCodeAdapter {
    /**
     * 源文件宽度
     */
    private static int ORI_WIDTH = 300;
    /**
     * 源文件高度
     */
    private static int ORI_HEIGHT = 150;
    /**
     * 模板图宽度
     */
    private static int CUT_WIDTH = 50;
    /**
     * 模板图高度
     */
    private static int CUT_HEIGHT = 50;
    /**
     * 抠图凸起圆心
     */
    private static int circleR = 5;
    /**
     * 抠图内部矩形填充大小
     */
    private static int RECTANGLE_PADDING = 8;
    /**
     * 抠图的边框宽度
     */
    private static int SLIDER_IMG_OUT_PADDING = 1;

    // 生成拼图样式
    private static int[][] getBlockData(){
        int[][] data = new int[CUT_WIDTH][CUT_HEIGHT];
        Random random = new Random();
        //(x-a)²+(y-b)²=r²
        //x中心位置左右5像素随机
        double x1 = RECTANGLE_PADDING + (CUT_WIDTH - 2 * RECTANGLE_PADDING) / 2.0 - 5 + random.nextInt(10);
        //y 矩形上边界半径-1像素移动
        double y1_top = RECTANGLE_PADDING - random.nextInt(3);
        double y1_bottom = CUT_HEIGHT - RECTANGLE_PADDING + random.nextInt(3);
        double y1 = random.nextInt(2) == 1 ? y1_top : y1_bottom;


        double x2_right = CUT_WIDTH - RECTANGLE_PADDING - circleR + random.nextInt(2 * circleR - 4);
        double x2_left = RECTANGLE_PADDING + circleR - 2 - random.nextInt(2 * circleR - 4);
        double x2 = random.nextInt(2) == 1 ? x2_right : x2_left;
        double y2 = RECTANGLE_PADDING + (CUT_HEIGHT - 2 * RECTANGLE_PADDING) / 2.0 - 4 + random.nextInt(10);

        double po = Math.pow(circleR, 2);
        for (int i = 0; i < CUT_WIDTH; i++) {
            for (int j = 0; j < CUT_HEIGHT; j++) {
                //矩形区域
                boolean fill;
                if ((i >= RECTANGLE_PADDING && i < CUT_WIDTH - RECTANGLE_PADDING)
                        && (j >= RECTANGLE_PADDING && j < CUT_HEIGHT - RECTANGLE_PADDING)) {
                    data[i][j] = 1;
                    fill = true;
                } else {
                    data[i][j] = 0;
                    fill = false;
                }
                //凸出区域
                double d3 = Math.pow(i - x1, 2) + Math.pow(j - y1, 2);
                if (d3 < po) {
                    data[i][j] = 1;
                } else {
                    if (!fill) {
                        data[i][j] = 0;
                    }
                }
                //凹进区域
                double d4 = Math.pow(i - x2, 2) + Math.pow(j - y2, 2);
                if (d4 < po) {
                    data[i][j] = 0;
                }
            }
        }
        //边界阴影
        for (int i = 0; i < CUT_WIDTH; i++) {
            for (int j = 0; j < CUT_HEIGHT; j++) {
                //四个正方形边角处理
                for (int k = 1; k <= SLIDER_IMG_OUT_PADDING; k++) {
                    //左上、右上
                    if (i >= RECTANGLE_PADDING - k && i < RECTANGLE_PADDING
                            && ((j >= RECTANGLE_PADDING - k && j < RECTANGLE_PADDING)
                            || (j >= CUT_HEIGHT - RECTANGLE_PADDING - k && j < CUT_HEIGHT - RECTANGLE_PADDING +1))) {
                        data[i][j] = 2;
                    }

                    //左下、右下
                    if (i >= CUT_WIDTH - RECTANGLE_PADDING + k - 1 && i < CUT_WIDTH - RECTANGLE_PADDING + 1) {
                        for (int n = 1; n <= SLIDER_IMG_OUT_PADDING; n++) {
                            if (((j >= RECTANGLE_PADDING - n && j < RECTANGLE_PADDING)
                                    || (j >= CUT_HEIGHT - RECTANGLE_PADDING - n && j <= CUT_HEIGHT - RECTANGLE_PADDING ))) {
                                data[i][j] = 2;
                            }
                        }
                    }
                }

                if (data[i][j] == 1 && j - SLIDER_IMG_OUT_PADDING > 0 && data[i][j - SLIDER_IMG_OUT_PADDING] == 0) {
                    data[i][j - SLIDER_IMG_OUT_PADDING] = 2;
                }
                if (data[i][j] == 1 && j + SLIDER_IMG_OUT_PADDING > 0 && j + SLIDER_IMG_OUT_PADDING < CUT_HEIGHT && data[i][j + SLIDER_IMG_OUT_PADDING] == 0) {
                    data[i][j + SLIDER_IMG_OUT_PADDING] = 2;
                }
                if (data[i][j] == 1 && i - SLIDER_IMG_OUT_PADDING > 0 && data[i - SLIDER_IMG_OUT_PADDING][j] == 0) {
                    data[i - SLIDER_IMG_OUT_PADDING][j] = 2;
                }
                if (data[i][j] == 1 && i + SLIDER_IMG_OUT_PADDING > 0 && i + SLIDER_IMG_OUT_PADDING < CUT_WIDTH && data[i + SLIDER_IMG_OUT_PADDING][j] == 0) {
                    data[i + SLIDER_IMG_OUT_PADDING][j] = 2;
                }
            }
        }
        return data;
    }

    // 抠出拼图
    private static void cutImgByTemplate(BufferedImage oriImage, BufferedImage targetImage, int[][] blockImage, int x, int y) {
        for (int i = 0; i < CUT_WIDTH; i++) {
            for (int j = 0; j < CUT_HEIGHT; j++) {
                int _x = x + i;
                int _y = y + j;
                int rgbFlg = blockImage[i][j];
                int rgb_ori = oriImage.getRGB(_x, _y);
                // 原图中对应位置变色处理
                if (rgbFlg == 1) {
                    //抠图上复制对应颜色值
                    targetImage.setRGB(i,j, rgb_ori);
                    //原图对应位置颜色变化
                    oriImage.setRGB(_x, _y, Color.LIGHT_GRAY.getRGB());
                } else if (rgbFlg == 2) {
                    targetImage.setRGB(i, j, Color.WHITE.getRGB());
                    oriImage.setRGB(_x, _y, Color.GRAY.getRGB());
                }else if(rgbFlg == 0){
                    //int alpha = 0;
                    targetImage.setRGB(i, j, rgb_ori & 0x00ffffff);
                }
            }

        }
    }

    // 获取图片
    private static BufferedImage getBufferedImage(String path) throws IOException{
        File file = new File(path);
        System.out.println(file.getAbsolutePath());
        if(file.isFile()){
            return ImageIO.read(file);
        }
        return null;
    }

    // 存放图片
    private static void writeImg(BufferedImage image, String file) throws Exception {
        byte[] imagedata = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image,"png",bos);
        imagedata = bos.toByteArray();
        File outFile = new File(file);
        System.out.println(outFile.getAbsolutePath());
        FileOutputStream out = new FileOutputStream(outFile);
        out.write(imagedata);
        out.close();
    }

    // 处理存放
    private static VerificationCodePlace cutAndSave(String imgName, String path, int [][] data, String headPath) throws Exception {
        VerificationCodePlace vcPlace =
                new VerificationCodePlace("sample_after.png", "sample_after_mark.png", 112, 50);

        // 进行图片处理
        BufferedImage originImage = getBufferedImage(path);
        if(originImage!=null) {
            int locationX = CUT_WIDTH + new Random().nextInt(originImage.getWidth() - CUT_WIDTH * 3);
            int locationY = CUT_HEIGHT + new Random().nextInt(originImage.getHeight() - CUT_HEIGHT) / 2;
            BufferedImage markImage = new BufferedImage(CUT_WIDTH, CUT_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
            cutImgByTemplate(originImage, markImage, data, locationX, locationY);

            String name = imgName.substring(0, imgName.indexOf('.'));

            // 考虑图片覆盖,简单设置四位随机数
            int r = (int)Math.round(Math.random() * 8999) + 1000;
            String afterName = name + "_after" + r + ".png";
            String markName = name + "_after_mark" + r + ".png";
            writeImg(originImage, headPath + afterName);
            writeImg(markImage, headPath + markName);
            vcPlace = new VerificationCodePlace(afterName, markName, locationX, locationY);
        }

        return vcPlace;
    }

    // 获取文件夹下所有文件名
    private static ArrayList<String> getFileNamesFromDic(String dicPath){
        File dic = new File(dicPath);
        ArrayList<String> imageFileNames = new ArrayList<String>();
        File[] dicFileList = dic.listFiles();
        for(File f: dicFileList){
            imageFileNames.add(f.getName());
        }
        return imageFileNames;
    }

    // 总流程，随机获取图片并处理，将拼图和对应图片存放至after_img
    // 出错则返回sample
    // headPath为存放生成图片的文件夹地址
    public static VerificationCodePlace getRandomVerificationCodePlace(String headPath) {
        VerificationCodePlace vcPlace = new VerificationCodePlace("sample_after.png", "sample_mark_after.png", 112, 50);

        // 从文件夹中读取所有待选择文件
        String directoryPath = "src/main/resources/static/image";
        ArrayList<String> imageFileNames = getFileNamesFromDic(directoryPath);

        // 随机获取
        int r = (int)Math.round(Math.random() * (imageFileNames.size() - 1));
        String imgName = imageFileNames.get(r);
        String path = "src/main/resources/static/image/" + imgName;
        int[][] data = VerificationCodeAdapter.getBlockData();

        // 进行图片处理
        try {
            vcPlace = cutAndSave(imgName, path, data, headPath);
        } catch (Exception e) {
            e.printStackTrace();
            return vcPlace;
        }

        return vcPlace;
    }

    // 删除after中的图片文件
    public static String deleteAfterImage(String headPath){
        boolean successDelete = true;
        int sum = 0;
        float fileSize = 0;
        String directoryPath = headPath;
        File dic = new File(directoryPath);
        File[] dicFileList = dic.listFiles();
        if(dicFileList != null) {
            for (File f : dicFileList) {
                if (!f.getName().equals("sample_after.png") && !f.getName().equals("sample_after_mark.png")) {
                    long fLength = f.length();
                    successDelete = f.delete();
                    if(!successDelete)
                        break;
                    sum ++;
                    fileSize += fLength;

                }
            }
        }
        float fileSizeInMB = fileSize / 1024 / 1024;
        if(!successDelete){
            String tip = "拼图文件删除中出现错误，请到" + directoryPath + "中进行查看";
            System.out.println(tip);
            return tip;
        }else{
            String tip = "拼图文件删除成功，删除文件数量为" + sum + ",文件总大小为" + fileSizeInMB + "MB";
            System.out.println(tip);
            return tip;
        }
    }

}
