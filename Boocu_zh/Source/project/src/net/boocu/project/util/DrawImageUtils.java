package net.boocu.project.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
/**
 * 
 * @author 鲁小翔
 *
 * 2015年7月14日
 */
public class DrawImageUtils {
	public static void resize(File imgFile,String destFile,int width,int height) throws IOException{
		Image img=ImageIO.read(imgFile);
		try {
            BufferedImage _image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            _image.getGraphics().drawImage(img, 0, 0, width, height, null); // 绘制缩小后的图
            FileOutputStream newimageout = new FileOutputStream(destFile); // 输出到文件流
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimageout);
            encoder.encode(_image); // 近JPEG编码
            newimageout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	/**
	 * 生成tiny（60x80）缩略图，源图片地址为sss.jpg,则目标图片为sss-tiny.jpg
	 * @param imgFile
	 * @throws IOException
	 */
	public static void resizeTiny(File imgFile) throws IOException{
		String fileName=imgFile.getName();
		String destFileName=fileName.replace(".", "-tiny.");
		String destFile=imgFile.getPath().replace(fileName, destFileName);
		resize(imgFile, destFile, 60, 80);
	}
	public static void resizeSmall(File imgFile) throws IOException{
		String fileName=imgFile.getName();
		String destFileName=fileName.replace(".", "-small.");
		String destFile=imgFile.getPath().replace(fileName, destFileName);
		resize(imgFile, destFile, 120, 160);
	}
	public static void resizeBig(File imgFile) throws IOException{
		String fileName=imgFile.getName();
		String destFileName=fileName.replace(".", "-big.");
		String destFile=imgFile.getPath().replace(fileName, destFileName);
		resize(imgFile, destFile, 240, 320);
	}
	/**
	 * 生成三种缩略图
	 * @param imgUrl
	 * @throws IOException
	 */
	public static void resizeAll(File imgFile) throws IOException{
		resizeTiny(imgFile);
		resizeSmall(imgFile);
		resizeBig(imgFile);
	}
	
	public static void main(String[] args) throws IOException {
			String imgUrl="C:\\Users\\Public\\Pictures\\Sample Pictures\\cover.png";
			DrawImageUtils.resizeSmall(new File(imgUrl));
	}
}
