package net.boocu.web.controller.common;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 实现向图片加水印，图片或者文字
 *
 * @author  
 */
public final class WaterMarkUtil {
    public static final int RIGHT_DOWN = 1; // 右下角
    public static final int RIGHT_UP   = 2; // 右上角
    public static final int LEFT_DOWN  = 3; // 左下角
    public static final int LEFT_UP    = 4; // 左上角
    public static final int CENTER     = 5; // 中间

    public WaterMarkUtil() {}

    /**
     * @param waterImg
     *            水印文件
     * @param srcImg
     *            源文件
     * @param outImg
     *            目标文件
     * @param position
     *            水印位置（如WaterMarkUtil.RIGHT_DOWN）
     * @param transparency
     *            水印透明度（0f～1f）
     */
    public final static void waterImage(String waterImg, String srcImg, String outImg,
            int position, float transparency) {
        try {
            File sourceFile = new File(srcImg);
            File waterFile = new File(waterImg);
            Image waterImage = ImageIO.read(waterFile);
            int wideth_biao = waterImage.getWidth(null);
            int height_biao = waterImage.getHeight(null);

            Image sourceImage = ImageIO.read(sourceFile);
            int width = sourceImage.getWidth(null);
            int height = sourceImage.getHeight(null);
            if (width < wideth_biao || height < height_biao) {// 小图片不加水印
                FileOutputStream out = new FileOutputStream(outImg);
                byte[] data = fileToStream(sourceFile);
                BufferedOutputStream fout = null;
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                try {
                    byte[] b = new byte[1024 * 10];
                    fout = new BufferedOutputStream(out);
                    while (in.read(b) > 0) {
                        out.write(b);
                    }
                    out.flush();
                    out.close();
                    in.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                    if (fout != null) {
                        fout.close();
                    }
                }
                return;
            }
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(sourceImage, 0, 0, width, height, null);

            //默认右下角
            int x = width - waterImage.getWidth(null);
            int y = height - waterImage.getHeight(null);
            if (position == WaterMarkUtil.CENTER) {
                x = (width - wideth_biao) / 2;
                y = (height - height_biao) / 2;
            }
            else if (position == WaterMarkUtil.RIGHT_UP) {
                x = width - waterImage.getWidth(null);
                y = 0;
            }
            else if (position == WaterMarkUtil.LEFT_DOWN) {
                x = 0;
                y = height - waterImage.getHeight(null);
            }
            else if (position == WaterMarkUtil.LEFT_UP) {
                x = 0;
                y = 0;
            }

            // 设置图片透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            // 图片右下角添加
            g.drawImage(waterImage, x, y, null);

            g.dispose();
            FileOutputStream out = new FileOutputStream(outImg);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(80f, true);
            encoder.encode(image);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印文字水印图片
     *
     * @param text
     *            水印文字
     * @param srcImg
     *            原图
     * @param outImg
     *            目标图
     * @param fontName
     *            字体名称
     * @param fontStyle
     *            字体风格
     * @param color
     *            字体颜色
     * @param fontSize
     *            字体大小
     * @param position
     *            水印位置（如WaterMarkUtil.RIGHT_DOWN）
     * @param transparency
     *            水印透明度（0f～1f）
     */
    public static void waterText(String text, String srcImg, String outImg, String fontName,
            int fontStyle, Color color, int fontSize, int position, float transparency) {
        try {
            File sourceFile = new File(srcImg);
            Image src = ImageIO.read(sourceFile);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            Font font = new Font(fontName, fontStyle, fontSize);
            FontMetrics metrics = g.getFontMetrics(font);
            int stringWidth = metrics.stringWidth(text);
            int fontHeight = metrics.getHeight();
            g.setFont(font);

            //默认右下角
            int x = width - stringWidth;
            int y = height - fontHeight;
            if (position == WaterMarkUtil.CENTER) {
                x = (width - stringWidth) / 2;
                y = (height - fontHeight) / 2;
            }
            else if (position == WaterMarkUtil.RIGHT_UP) {
                x = width - stringWidth;
                y = 0;
            }
            else if (position == WaterMarkUtil.LEFT_DOWN) {
                x = 0;
                y = height - fontHeight;
            }
            else if (position == WaterMarkUtil.LEFT_UP) {
                x = 0;
                y = 0;
            }

            // 设置透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            g.drawString(text, x, y + fontHeight);

            g.dispose();
            FileOutputStream out = new FileOutputStream(outImg);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(80f, true);
            encoder.encode(image);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * 将文件对象转成字节流
     *
     * @param file
     * @return
     * @throws IOException
     */
    private static byte[] fileToStream(File file) throws IOException {
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int BUFSIZ = 1024;
        byte inbuf[] = new byte[BUFSIZ];
        int n;
        while ((n = input.read(inbuf, 0, BUFSIZ)) != -1)
            output.write(inbuf, 0, n);
        input.close();
        output.close();
        return output.toByteArray();
    }

    public static void main(String[] args) {
        // waterImage("d:/logo.png", "d:/01.jpg", "d:/sss.jpg",WaterMarkUtil.CENTER,0.5f);
        waterText("UCAP.COM.CN", "c:/default.jpg", "c:/default56.jpg", "Dialog", Font.PLAIN, Color.BLACK, 16,
                WaterMarkUtil.RIGHT_DOWN, 1.0f);
    }

}