package njnu.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Utility {

		// 读文件
		public static String readTxt(String path) {
			String txt="";
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
				String str = null;
				while ((str = reader.readLine()) != null) {
					txt=txt+str;
				}
				return txt;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return txt;
		}	
		
		
		// 写文件
				public static void writerTxt(String filepath,String content) {
					BufferedWriter fw = null;
					try {
						File file = new File(filepath);
						if(!file.exists()){
							System.out.println("文件不存在！");
						}
						
						fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
						fw.append(content);
						
						fw.flush(); // 全部写入缓存中的内容
						System.out.println(filepath+"写入完成！");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (fw != null) {
							try {
								fw.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
					}
				}		
}
