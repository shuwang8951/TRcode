package njnu.corpusprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class LabledDataProcess {
	
	public String[][] originalcorpus;
	
	public String[][] segcorpus;
	
	public String[][] wordcorpus;
	
	public LabledDataProcess(String corpuspath){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(corpuspath), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = null;
			ArrayList<String[]> oc=new ArrayList<String[]>();
			while ((str = reader.readLine()) != null) {
				if(str.split(" ").length==2){
					oc.add(str.split(" "));		
				}
			}
			originalcorpus=new String[oc.size()][2];
			for(int i=0;i<oc.size();i++){
				for(int j=0;j<2;j++){
					originalcorpus[i][j]=oc.get(i)[j];
					//System.out.println(originalcorpus[i][j]);
				}
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
		
		//wordcorpus处理
		wordcorpus=new String[originalcorpus.length][originalcorpus[0].length];
		for(int i=0;i<originalcorpus.length;i++){
			wordcorpus[i][0]=originalcorpus[i][0];
			String label=originalcorpus[i][1];
			if(label.endsWith("B")||label.endsWith("I")||label.endsWith("E")||label.endsWith("S")){
				wordcorpus[i][1]=String.valueOf(0);
			}else{
				wordcorpus[i][1]=String.valueOf(1);
			}
		}
		
		//segcorpus处理
		ArrayList<String[]> t=new ArrayList<String[]>();
		String[][] temp=new String[originalcorpus.length][2];
		for(int i=0;i<temp.length;i++){
			temp[i][0]="";
		}
		String str="";
		for(int i=0;i<originalcorpus.length;i++){
			
			if(originalcorpus[i][1].equals("B")||originalcorpus[i][1].equals("I")
					||originalcorpus[i][1].equals("B-LOC")||originalcorpus[i][1].equals("I-LOC")
					||originalcorpus[i][1].equals("B-L")||originalcorpus[i][1].equals("I-L")
					||originalcorpus[i][1].equals("B-A")||originalcorpus[i][1].equals("I-A")
					||originalcorpus[i][1].equals("B-W")||originalcorpus[i][1].equals("I-W")
					||originalcorpus[i][1].equals("B-T")||originalcorpus[i][1].equals("I-T")){
				str=str+originalcorpus[i][0];
			}
			if(originalcorpus[i][1].equals("E")){
				temp[i][0]=str+originalcorpus[i][0];
				temp[i][1]=String.valueOf(0);
				t.add(temp[i]);
				str="";
			}
			if(originalcorpus[i][1].equals("E-LOC")||originalcorpus[i][1].equals("E-L")
					||originalcorpus[i][1].equals("E-A")||originalcorpus[i][1].equals("S-A")
					||originalcorpus[i][1].equals("E-W")||originalcorpus[i][1].equals("E-T")){
				temp[i][0]=str+originalcorpus[i][0];
				temp[i][1]=String.valueOf(1);
				t.add(temp[i]);
				str="";
			}
			if(originalcorpus[i][1].equals("S")){
				temp[i][0]=originalcorpus[i][0];
				temp[i][1]=String.valueOf(0);
				t.add(temp[i]);
			}
		}
		segcorpus=new String[t.size()][2];
		for(int i=0;i<t.size();i++){
			for(int j=0;j<2;j++){
				segcorpus[i][j]=t.get(i)[j];
				//System.out.println(segcorpus[i][j]);
			}
		}
	}
	
	public void writecorpuswordfile(String filepath){
		BufferedWriter fw = null;
		try {
			File file = new File(filepath);

			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			
			for(int i=0;i<wordcorpus.length;i++){
				fw.append(wordcorpus[i][0]+" ");
			}
			
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
	
	public void writecorpussegfile(String filepath){
		BufferedWriter fw = null;
		try {
			File file = new File(filepath);
						
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			
			for(int i=0;i<segcorpus.length;i++){
				fw.append(segcorpus[i][0]+" ");
			}
			
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
	
	public void writecorpussegwithtagsfile(String filepath){
		BufferedWriter fw = null;
		try {
			File file = new File(filepath);
						
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			
			for(int i=0;i<segcorpus.length;i++){
//				for(int j=0;j<segcorpus[i].length;j++){
//					fw.write(segcorpus[i][j]+"  ");
//				}
				fw.write(segcorpus[i][0]+" ");
				if(segcorpus[i][1].equals("1")){
					fw.write("E-LOC");
				}else{
					fw.write("S");
				}
				
				fw.newLine();
			}			
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String corpuspath="files/originalcorpus/dlbk_segwords8000.txt";
		LabledDataProcess ldp=new LabledDataProcess(corpuspath);
		
//		for(int i=0;i<ldp.wordcorpus.length;i++){
//			System.out.println(ldp.wordcorpus[i][1]);
//		}
//		System.out.println("singleword count:"+ldp.wordcorpus.length);
//		System.out.println("segword count:"+ldp.segcorpus.length);
//		
//		ldp.writecorpuswordfile("files/w2vcorpus/zaihai_singelwords.txt");
//		ldp.writecorpussegfile("files/w2vcorpus/zaihai_segwords.txt");
		
		System.out.println("singleword count:"+ldp.wordcorpus.length);
		System.out.println("segword count:"+ldp.segcorpus.length);
		
//		ldp.writecorpuswordfile("files/w2vcorpus/dlbk_singelwords.txt");
//		ldp.writecorpussegfile("files/w2vcorpus/dlbk_segwords.txt");
		
		for(int i=0;i<ldp.wordcorpus.length;i++){
			for(int j=0;j<ldp.wordcorpus[i].length;j++){
				System.out.print(ldp.wordcorpus[i][j]+"  ");
			}
			System.out.println();
		}
		//ldp.writecorpussegwithtagsfile("files/originalcorpus/dlbk_segwords.txt");
		
		
	}

}
