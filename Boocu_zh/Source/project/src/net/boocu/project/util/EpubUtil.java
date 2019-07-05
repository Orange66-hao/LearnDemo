package net.boocu.project.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.boocu.framework.util.JsonUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
/**
 * 
 * @author 鲁小翔
 *
 * 2015年7月19日
 */
public class EpubUtil {
	/**
	 * 读取目录
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readObject(Book book) throws FileNotFoundException, IOException{
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>(); 
		for(TOCReference tocReference:book.getTableOfContents().getTocReferences()){
			Map<String, Object> map=new HashMap<String, Object>();
			add(map, tocReference);
			list.add(map);
		}
    	return JsonUtils.toJson(list);
	}
	/**递归读取目录*/
	public static void add(Map<String, Object> map,TOCReference tocReference){
		map.put("title", tocReference.getTitle());
		map.put("href", tocReference.getResource().getHref());
		List<Map<String, Object>> children=new ArrayList<Map<String,Object>>();
		map.put("children", children);
		if(!tocReference.getChildren().isEmpty()){
			for(TOCReference toc:tocReference.getChildren()){
				Map<String, Object> childMap=new HashMap<String, Object>();
				add(childMap, toc);
				children.add(childMap);
			}
		}
	}
	 /** *//**
     * 定义解压缩zip文件的方法
     * 
     * @param zipFileName
     * @param outputDirectory
     */
    public static void unzip(String zipFileName, String outputDirectory) {
       try {
            ZipInputStream in = new ZipInputStream(new FileInputStream(zipFileName));
           //获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry，
           //当getNextEntry方法的返回值为null，则代表ZipInputStream中没有下一个ZipEntry，
           //输入流读取完成；
            ZipEntry z = in.getNextEntry();
            while (z != null) {
               System.out.println("unziping " + z.getName());
               //创建以zip包文件名为目录名的根目录
               File f = new File(outputDirectory);
               f.mkdir();
               if (z.isDirectory()) {
                   String name = z.getName();
                    name = name.substring(0, name.length() - 1);
                    f = new File(outputDirectory + File.separator + name);
                    f.mkdir();
               }else {
                    f = new File(outputDirectory + File.separator + z.getName());
                    f.createNewFile();
                    FileOutputStream out = new FileOutputStream(f);
                    int b;
                   while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                   out.close();
               }
               z = in.getNextEntry();
           }
           in.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

   public static void main(String[] args) throws FileNotFoundException, IOException {
       // EpubUtil.unzip("D:\\兄弟（上下合集）.epub", "D:\\xd");
	   EpubReader epubReader = new EpubReader();
	   Book book = epubReader.readEpub(new FileInputStream("D:\\兄弟（上下合集）.epub"));
	   System.out.println(EpubUtil.readObject(book));
   }
}
