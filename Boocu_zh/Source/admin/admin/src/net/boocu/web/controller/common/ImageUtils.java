package net.boocu.web.controller.common;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageUtils {
        public static final int IMAGE_UNKNOWN = -1;
        public static final int IMAGE_JPEG = 0;
        public static final int IMAGE_PNG = 1;
        public static final int IMAGE_GIF = 2;

      
        /**
         * Resizes an image
         * 
         * @param imgName The image name to resize. Must be the complet path to the file
         * @param type int
         * @param maxWidth The image's max width
         * @param maxHeight The image's max height
         * @return A resized <code>BufferedImage</code>
         */
        public static BufferedImage resizeImage(String imgName, int type, int maxWidth, int maxHeight) throws IOException {
                try {
                        return resizeImage(ImageIO.read(new File(imgName)), type, maxWidth, maxHeight);
                } catch (IOException e) {
                        throw new IOException(e);
                }
        }

        /**
         * Resizes an image.
         * 
         * @param image
         *            The image to resize
         * @param maxWidth
         *            The image's max width
         * @param maxHeight
         *            The image's max height
         * @return A resized <code>BufferedImage</code>
         * @param type
         *            int
         */
        public static BufferedImage resizeImage(BufferedImage image, int type, int maxWidth, int maxHeight) {
                Dimension largestDimension = new Dimension(maxWidth, maxHeight);

                // Original size
                int imageWidth = image.getWidth(null);
                int imageHeight = image.getHeight(null);

                float aspectRation = (float) imageWidth / imageHeight;

                if (imageWidth > maxWidth || imageHeight > maxHeight) {
                        if ((float) largestDimension.width / largestDimension.height > aspectRation) {
                                largestDimension.width = (int) Math.ceil(largestDimension.height * aspectRation);
                        } else {
                                largestDimension.height = (int) Math.ceil(largestDimension.width / aspectRation);
                        }

                        imageWidth = largestDimension.width;
                        imageHeight = largestDimension.height;
                }

                return createHeadlessSmoothBufferedImage(image, type, imageWidth, imageHeight);
        }

        /**
         * Saves an image to the disk.
         * 
         * @param image  The image to save
         * @param toFileName The filename to use
         * @param type The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as JPEG images,
         *  or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
         * @return <code>false</code> if no appropriate writer is found
         * @throws IOException 
         */
        public static boolean saveImage(BufferedImage image, String toFileName, int type) throws IOException {
                try {
                        return ImageIO.write(image, type == IMAGE_JPEG ? "jpg" : "png", new File(toFileName));
                } catch (IOException e) {
                        throw new IOException(e);
                }
        }

        /**
         * Compress and save an image to the disk. Currently this method only supports JPEG images.
         * 
         * @param image The image to save
         * @param toFileName The filename to use
         * @param type The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as JPEG images,
         * or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
         * @throws IOException 
         */
        public static void saveCompressedImage(BufferedImage image, String toFileName, int type) throws IOException {
                try {
                        if (type == IMAGE_PNG)
                                throw new UnsupportedOperationException("PNG compression not implemented");

                        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
                        ImageWriter writer;
                        writer = iter.next();

                        ImageOutputStream ios = ImageIO.createImageOutputStream(new File(toFileName));
                        writer.setOutput(ios);

                        ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());

                        iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        iwparam.setCompressionQuality(0.7F);

                        writer.write(null, new IIOImage(image, null, null), iwparam);

                        ios.flush();
                        writer.dispose();
                        ios.close();
                } catch (IOException e) {
                        throw new IOException(e);
                }
        }

        /**
         * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
         * function on a completely headless system. This especially includes Linux and Unix systems
         * that do not have the X11 libraries installed, which are required for the AWT subsystem to
         * operate. This method uses nearest neighbor approximation, so it's quite fast. Unfortunately,
         * the result is nowhere near as nice looking as the createHeadlessSmoothBufferedImage method.
         * 
         * @param image  The image to convert
         * @param w The desired image width
         * @param h The desired image height
         * @return The converted image
         * @param type int
         */
        public static BufferedImage createHeadlessBufferedImage(BufferedImage image, int type, int width, int height) {
                if (type == ImageUtils.IMAGE_PNG && hasAlpha(image)) {
                        type = BufferedImage.TYPE_INT_ARGB;
                } else {
                        type = BufferedImage.TYPE_INT_RGB;
                }

                BufferedImage bi = new BufferedImage(width, height, type);

                for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                                bi.setRGB(x, y, image.getRGB(x * image.getWidth() / width, y * image.getHeight() / height));
                        }
                }

                return bi;
        }

        /**
         * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
         * function on a completely headless system. This especially includes Linux and Unix systems
         * that do not have the X11 libraries installed, which are required for the AWT subsystem to
         * operate. The resulting image will be smoothly scaled using bilinear filtering.
         * 
         * @param source The image to convert
         * @param w The desired image width
         * @param h The desired image height
         * @return The converted image
         * @param type  int
         */
        public static BufferedImage createHeadlessSmoothBufferedImage(BufferedImage source, int type, int width, int height) {
                if (type == ImageUtils.IMAGE_PNG && hasAlpha(source)) {
                        type = BufferedImage.TYPE_INT_ARGB;
                } else {
                        type = BufferedImage.TYPE_INT_RGB;
                }

                BufferedImage dest = new BufferedImage(width, height, type);

                int sourcex;
                int sourcey;

                double scalex = (double) width / source.getWidth();
                double scaley = (double) height / source.getHeight();

                int x1;
                int y1;

                double xdiff;
                double ydiff;

                int rgb;
                int rgb1;
                int rgb2;

                for (int y = 0; y < height; y++) {
                        sourcey = y * source.getHeight() / dest.getHeight();
                        ydiff = scale(y, scaley) - sourcey;

                        for (int x = 0; x < width; x++) {
                                sourcex = x * source.getWidth() / dest.getWidth();
                                xdiff = scale(x, scalex) - sourcex;

                                x1 = Math.min(source.getWidth() - 1, sourcex + 1);
                                y1 = Math.min(source.getHeight() - 1, sourcey + 1);

                                rgb1 = getRGBInterpolation(source.getRGB(sourcex, sourcey), source.getRGB(x1, sourcey), xdiff);
                                rgb2 = getRGBInterpolation(source.getRGB(sourcex, y1), source.getRGB(x1, y1), xdiff);

                                rgb = getRGBInterpolation(rgb1, rgb2, ydiff);

                                dest.setRGB(x, y, rgb);
                        }
                }

                return dest;
        }

        private static double scale(int point, double scale) {
                return point / scale;
        }

        private static int getRGBInterpolation(int value1, int value2, double distance) {
                int alpha1 = (value1 & 0xFF000000) >>> 24;
                int red1 = (value1 & 0x00FF0000) >> 16;
                int green1 = (value1 & 0x0000FF00) >> 8;
                int blue1 = (value1 & 0x000000FF);

                int alpha2 = (value2 & 0xFF000000) >>> 24;
                int red2 = (value2 & 0x00FF0000) >> 16;
                int green2 = (value2 & 0x0000FF00) >> 8;
                int blue2 = (value2 & 0x000000FF);

                int rgb = ((int) (alpha1 * (1.0 - distance) + alpha2 * distance) << 24)
                                | ((int) (red1 * (1.0 - distance) + red2 * distance) << 16)
                                | ((int) (green1 * (1.0 - distance) + green2 * distance) << 8)
                                | (int) (blue1 * (1.0 - distance) + blue2 * distance);

                return rgb;
        }

        /**
         * Determines if the image has transparent pixels.
         * 
         * @param image The image to check for transparent pixel.s
         * @return <code>true</code> of <code>false</code>, according to the result
         */
        public static boolean hasAlpha(Image image) {
                try {
                        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
                        pg.grabPixels();

                        return pg.getColorModel().hasAlpha();
                } catch (InterruptedException e) {
                        return false;
                }
        }

        /**
        * 图像类型转换 GIF->JPG GIF->PNG PNG->JPG PNG->GIF(X)
        */
        public static void convert(String source, String result) {
                try {
                        File f = new File(source);
                        f.canRead();
                        f.canWrite();
                        BufferedImage src = ImageIO.read(f);
                        ImageIO.write(src, "JPG", new File(result));
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        /**
         * 彩色转为黑白
         *
         * @param source
         * @param result
         */
        public static void gray(String source, String result) {
                try {
                        BufferedImage src = ImageIO.read(new File(source));
                        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                        ColorConvertOp op = new ColorConvertOp(cs, null);
                        src = op.filter(src, null);
                        ImageIO.write(src, "JPEG", new File(result));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }//gray("c:/cc.jpg", "c:/cc2.jpg");

        /**
         * Reads an image from the disk as a BufferedImage - can be case as an Image
         *
         *@param filename the image to be read
         *@return the image object
         */
        public static BufferedImage readImage(String fileName) throws IOException {
                return readImage(new File(fileName));
        }

        public static BufferedImage readImage1(String filePath) {
                try {
                        File source = new File(filePath);
                        String formatName = FileUtils.getExtension(filePath);
                        ImageInputStream iis = ImageIO.createImageInputStream(source);
                        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formatName);
                        final ImageReader reader = readers.next();
                        reader.setInput(iis, true);
                        final ImageReadParam param = reader.getDefaultReadParam();
                        param.setDestinationOffset(new Point(-200, -140));
                        BufferedImage bi = reader.read(0, param);
                        reader.dispose();
                        return bi;
                } catch (IOException ex) {
                        System.err.println("Failed to load image " + filePath);
                        return null;
                }
        }

        /**
         * 从指定文件名读取图像，目前只支持读取以下格式图像：bmp,wbmp,gif,jpge,png。
         *
         * @param file
         *            文件名
         * @return BufferedImage 图像
         * @since 1.0
         *
         *
         *        <pre>
         * BufferedImage image;
         * image = ImageUtils.readImage(new File(&quot;myImage.jpg&quot;));
         * image = ImageUtils.readImage(new File(&quot;myImage.gif&quot;));
         * image = ImageUtils.readImage(new File(&quot;myImage.bmp&quot;));
         * image = ImageUtils.readImage(new File(&quot;myImage.png&quot;));
         * </pre>
         */
        public static BufferedImage readImage(File file) {
                //image
                BufferedImage image = null;
                if (file != null && file.isFile() && file.exists()) {
                        try {
                                image = ImageIO.read(file);
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                // e.printStackTrace();
                        }
                }
                return image;
        }

        /**
         * 从文件流读取图像，目前只支持读取以下格式图像：bmp,wbmp,gif,jpge,png。
         *
         * @param input
         *            输入文件流
         * @return BufferedImage 图像
         * @since 1.0
         */
        public static BufferedImage readImage(InputStream input) {
                BufferedImage image = null;
                if (input != null) {
                        try {
                                image = ImageIO.read(input);
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                // e.printStackTrace();
                        }
                }
                return image;
        }

        /**
         * 从URL读取图像，目前只支持读取以下格式图像：bmp,wbmp,gif,jpge,png。
         *
         * @param url
         * @return BufferedImage 图像
         * @since 1.0
         */
        public static BufferedImage readImage(URL url) {
                BufferedImage image = null;
                if (url != null) {
                        try {
                                image = ImageIO.read(url);
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                // e.printStackTrace();
                        }
                }
                return image;
        }

        /**
         * 获得指定图像的像素宽
         *
         * @param image
         *            图像
         * @return 图像的像素宽
         * @since 1.0
         */
        public static int getWidth(BufferedImage image) {
                return image.getWidth();
        }

        /**
         * 获得指定图像的像素高
         *
         * @param image
         *            图像
         * @return 图像的像素高
         * @since 1.0
         */
        public static int getHeight(BufferedImage image) {
                return image.getHeight();
        }

        /**
         * 按指定格式输出<code>BufferedImage</code>到文件out中，如果没有指定image或formatName或输出文件out,
         * 则do nothing 。目前只支持写入以下格式图像：bmp,wbmp,jpeg,png。
         *
         * @param image
         *            图像
         * @param formatName
         *            格式
         * @param out
         *            输出文件
         * @throws IOException
         * @since 1.0
         * @see com.sitechasia.webx.core.utils.image.ImageConstants.FormatName <pre>
         * ImageUtils.writeImage(imageToSave, ImageConstants.FormatName.BMP, new File(
         *              &quot;newImage.bmp&quot;));
         * ImageUtils.writeImage(imageToSave, ImageConstants.FormatName.JPEG, new File(
         *              &quot;newImage.jpg&quot;));
         * ImageUtils.writeImage(imageToSave, ImageConstants.FormatName.PNG, new File(
         *              &quot;newImage.png&quot;));
         * ImageUtils.writeImage(imageToSave, ImageConstants.FormatName.WBMP, new File(
         *              &quot;newImage.wbmp&quot;));
         * </pre>
         *
         */
        public static void writeImage(BufferedImage image, String formatName, File out) throws IOException {
                if (image != null && formatName != null && !"".equals(formatName) && out != null) {
                        ImageIO.write(image, formatName, out);
                }
        }

        /**
         * 按指定格式输出<code>BufferedImage</code>到out中，如果没有指定image或formatName或输出流out, 则do
         * nothing 。目前只支持写入以下格式图像：bmp,wbmp,jpeg,png。
         *
         * @param image
         *            图像
         * @param formatName
         *            格式
         * @param out
         *            输出流
         * @throws IOException
         * @since 1.0
         * @see com.sitechasia.webx.core.utils.image.ImageConstants.FormatName
         */
        public static void writeImage(BufferedImage image, String formatName, OutputStream out) throws IOException {
                if (image != null && formatName != null && !"".equals(formatName) && out != null) {
                        ImageIO.write(image, formatName, out);
                }
        }
        public static void main(String[] args) {
        	reduceImg("C:\\Users\\Administrator\\Desktop\\aa.jpg","C:\\\\Users\\\\Administrator\\\\Desktop\\\\bb.jpg",1000,1000);
		}
        public static void reduceImg(String imgsrc, String imgdist, int widthdist, int heightdist) {
                try {
                        File srcfile = new File(imgsrc);
                        if (!srcfile.exists()) {
                                return;
                        }
                        Image src = javax.imageio.ImageIO.read(srcfile);
                        
                        //获取原图的大小 进行等比缩放
                        InputStream in = new FileInputStream(srcfile);
                        BufferedImage srcImage = ImageIO.read(in); 
                        int sw = srcImage.getWidth();    
                        int sh = srcImage.getHeight();  
                        
                        //widthdist = sw/2;
                        heightdist = widthdist*sh/sw;
                       
                        in.close(); 
                        //

                        BufferedImage tag = new BufferedImage(widthdist, heightdist, BufferedImage.TYPE_INT_RGB);

                        tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist, Image.SCALE_SMOOTH), 0, 0, null);
                        ///         tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist,  Image.SCALE_AREA_AVERAGING), 0, 0,  null);   

                        FileOutputStream out = new FileOutputStream(imgdist);
                        ImageWriter imgWrier;
                        imgWrier = ImageIO.getImageWritersByFormatName("jpg").next(); 
                        imgWrier.reset();  
                        imgWrier.setOutput(ImageIO.createImageOutputStream(out));  
                        // 调用write方法，就可以向输入流写图片  
                        imgWrier.write(null, new IIOImage(tag, null, null), null);  
                         
                        out.flush();  

//                        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//                        encoder.encode(tag);
                        out.close();

                } catch (IOException ex) {
                        ex.printStackTrace();
                }
        }

        /**   
         * 实现图像的等比缩放   
         * @param source   
         * @param targetW   
         * @param targetH   
         * @return   
         */
        public static BufferedImage resize(BufferedImage source, int targetW, int targetH) {
                // targetW，targetH分别表示目标长和宽    
                int type = source.getType();
                BufferedImage target = null;
                double sx = (double) targetW / source.getWidth();
                double sy = (double) targetH / source.getHeight();
                // 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放    
                // 则将下面的if else语句注释即可    
                if (sx < sy) {
                        sx = sy;
                        targetW = (int) (sx * source.getWidth());
                } else {
                        sy = sx;
                        targetH = (int) (sy * source.getHeight());
                }
                if (type == BufferedImage.TYPE_CUSTOM) { // handmade    
                        ColorModel cm = source.getColorModel();
                        WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
                        boolean alphaPremultiplied = cm.isAlphaPremultiplied();
                        target = new BufferedImage(cm, raster, alphaPremultiplied, null);
                } else
                        target = new BufferedImage(targetW, targetH, type);
                Graphics2D g = target.createGraphics();
                // smoother than exlax:    
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
                g.dispose();
                return target;
        }
        /**   
     * 实现图像的等比缩放和缩放后的截取   
     * @param inFilePath 要截取文件的路径   
     * @param outFilePath 截取后输出的路径   
     * @param width 要截取宽度   
     * @param hight 要截取的高度   
     * @param proportion   
     * @throws Exception   
     */   
        
    public static void saveImageAsJpg(String inFilePath, String outFilePath,    
            int width, int hight, boolean proportion)throws Exception {    
         File file = new File(inFilePath);    
         InputStream in = new FileInputStream(file);    
         File saveFile = new File(outFilePath);    
   
        BufferedImage srcImage = ImageIO.read(in);    
        if (width > 0 || hight > 0) {    
            // 原图的大小    
            int sw = srcImage.getWidth();    
            int sh = srcImage.getHeight();    
            // 如果原图像的大小小于要缩放的图像大小，直接将要缩放的图像复制过去    
            if (sw > width && sh > hight) {    
            	int resize_height = width*sh/sw;
            	hight = resize_height; 
                srcImage = resize(srcImage, width, resize_height);    
            } else {    
                String fileName = saveFile.getName();    
                String formatName = fileName.substring(fileName    
                        .lastIndexOf('.') + 1);    
                ImageIO.write(srcImage, formatName, saveFile);    
                return;    
            }    
        }    
        // 缩放后的图像的宽和高    
        int w = srcImage.getWidth();    
        int h = srcImage.getHeight();    
        // 如果缩放后的图像和要求的图像宽度一样，就对缩放的图像的高度进行截取    
        if (w == width) {    
            // 计算X轴坐标    
            int x = 0;    
            int y = h / 2 - hight / 2;    
            saveSubImage(srcImage, new Rectangle(x, y, width, hight), saveFile);    
        }    
        // 否则如果是缩放后的图像的高度和要求的图像高度一样，就对缩放后的图像的宽度进行截取    
        else if (h == hight) {    
            // 计算X轴坐标    
            int x = w / 2 - width / 2;    
            int y = 0;    
            saveSubImage(srcImage, new Rectangle(x, y, width, hight), saveFile);    
        }    
        in.close();    
    }    
    /**   
     * 实现缩放后的截图   
     * @param image 缩放后的图像   
     * @param subImageBounds 要截取的子图的范围   
     * @param subImageFile 要保存的文件   
     * @throws IOException   
     */   
    private static void saveSubImage(BufferedImage image,    
            Rectangle subImageBounds, File subImageFile) throws IOException {    
//        if (subImageBounds.x < 0 || subImageBounds.y < 0   
//                || subImageBounds.width - subImageBounds.x > image.getWidth()    
//                || subImageBounds.height - subImageBounds.y > image.getHeight()) {    
//            System.out.println("Bad   subimage   bounds");    
//            return;    
//        }    
//        BufferedImage subImage = image.getSubimage(subImageBounds.x,subImageBounds.y, subImageBounds.width, subImageBounds.height);    
//       
        int width = subImageBounds.width;
        int height =  subImageBounds.height;
        
        BufferedImage newImage = new BufferedImage(width,height,image.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(image, 0,0,width,height,null);
        g.dispose();
        
        
        String fileName = subImageFile.getName();    
        String formatName = fileName.substring(fileName.lastIndexOf('.') + 1);    
        ImageIO.write(newImage, formatName, subImageFile);    
    }    

    public static int getHeightForWaterFall(String url,int w){
    	int height = 210;
    	try {
			BufferedImage image = readImage(new URL(url));
			height = image.getHeight()*210/image.getWidth();
			
		} catch (MalformedURLException e) { 
		}
    	
    	
    	return height;
    }
    
//        public static void main(String[] args) throws IOException {
//                reduceImg("E:/image/1.jpg", "E:/image/2.jpg", 60,80 );
//                
////                try {
////					saveImageAsJpg("/Users/longmap/Desktop/girl/299888418507482694.jpg", "/Users/longmap/Desktop/girl/299888418507482694.jpg_11.jpg",60, 80,true);
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//                
////               int width = readImage(new URL("http://img04.taobaocdn.com/bao/uploaded/i4/T1Rp13XaVtXXbUOCA8_101603.jpg")).getWidth();
////               int height = readImage(new URL("http://img04.taobaocdn.com/bao/uploaded/i4/T1Rp13XaVtXXbUOCA8_101603.jpg")).getHeight();
////               System.out.println("width="+width+",height="+height+" "+(height*210/width));
//               // writeImage(readImage1("c:/bb.jpg"), "jpeg", new File("c:/sf.jpg"));
//        }
        
        
        
        
        
       /* public static void main(String[] args)
        {
            if(compressPic("E:/image/1.jpg", "E:/image/3.jpg"))
            {
                System.out.println("压缩成功！"); 
            }
            else
            {
                System.out.println("压缩失败！"); 
            }
        }
        */

        public static boolean compressPic(String srcFilePath, String descFilePath)
        {
            File file = null;
            BufferedImage src = null;
            FileOutputStream out = null;
            ImageWriter imgWrier;
            ImageWriteParam imgWriteParams;

            // 指定写图片的方式为 jpg
            imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
            imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
            // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
            imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
            // 这里指定压缩的程度，参数qality是取值0~1范围内，
            imgWriteParams.setCompressionQuality((float)0.1);
            imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
            ColorModel colorModel = ColorModel.getRGBdefault();
            // 指定压缩时使用的色彩模式
            imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel
                    .createCompatibleSampleModel(16, 16)));

            try
            {
                if(srcFilePath == null || srcFilePath.equals(""))
                {
                    return false;
                }
                else
                {
                    file = new File(srcFilePath);
                    src = ImageIO.read(file);
                    out = new FileOutputStream(descFilePath);

                    imgWrier.reset();
                    // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造
                    imgWrier.setOutput(ImageIO.createImageOutputStream(out));
                    // 调用write方法，就可以向输入流写图片
                    imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);
                    out.flush();
                    out.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        
        
        public static byte[] compressPicByte(byte[] data)
        {
	           
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            
            BufferedImage src = null;
            ByteArrayOutputStream out = null;
            ImageWriter imgWrier;
            ImageWriteParam imgWriteParams;

            // 指定写图片的方式为 jpg
            imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
            imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
            // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
            imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
            // 这里指定压缩的程度，参数qality是取值0~1范围内，
            imgWriteParams.setCompressionQuality((float)0.5/data.length);
                             
            imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
            ColorModel colorModel = ColorModel.getRGBdefault();
            // 指定压缩时使用的色彩模式
            imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel
                    .createCompatibleSampleModel(16, 16)));

            try
            {
                src = ImageIO.read(is);
                out = new ByteArrayOutputStream(data.length);

                imgWrier.reset();
                // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造
                imgWrier.setOutput(ImageIO.createImageOutputStream(out));
                // 调用write方法，就可以向输入流写图片
                imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);
                
                out.flush();
                out.close();
                is.close();
                data = out.toByteArray();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
			return data;
        }
        
}