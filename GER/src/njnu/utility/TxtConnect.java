package njnu.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TxtConnect {
	
	File[] files;
	
	
	public TxtConnect(String filespath,String outputfilepath){
		
		File f=new File(filespath);
		File opf=new File(outputfilepath);
		
		files=f.listFiles();
		
		String txt="";
		for(File tf:files){
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(tf)); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
				String str = null;
				while ((str = reader.readLine()) != null) {
					str=str.replaceAll("	", " ");
					txt=txt+str+"&&";
				}
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
			System.out.println(tf.getName()+"读取完毕！");
		}
		
		String[] temp=txt.split("&&");
		BufferedWriter fw = null;
		try {
			File file = new File(outputfilepath);
			if(!file.exists()){
				System.out.println("文件不存在！");
			}
			
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			for(int i=0;i<temp.length;i++){
				fw.write(temp[i]);
				fw.newLine();
			}		
			fw.flush(); // 全部写入缓存中的内容
			System.out.println(outputfilepath+"写入完成！");
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
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String filespath="files/originalcorpus/d1-m2";
		String outputfilepath="files/originalcorpus/dlbk_crf_recresults.txt";
		
		TxtConnect tc=new TxtConnect(filespath, outputfilepath);
	}

}
