import java.io.File;

import net.boocu.framework.util.FileUtils;


public class Test {

	public static void main(String[] args) {
		

		File file = new File("/Users/apple/Documents/workspaceLuna/Source");
		
		test(file);
		
	}
	
	public static void test(File file){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f : files){
				test(f);
			}
		}else{
			String path = file.getParentFile().getPath();
			
			String name = file.getName();
			String nameo = name.substring(0,name.lastIndexOf("."));
			
			String ext = name.substring(name.lastIndexOf("."));
			if(ext.equalsIgnoreCase(".ftl")){
				try {
					FileUtils.copy(file, new File(path+"/"+nameo+".html"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				FileUtils.deleteFile(file);
			}
			System.out.println(nameo);
			 
		}
	}

}
