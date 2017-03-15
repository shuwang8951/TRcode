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

import njnu.ger.ModelEvaluation;

public class CompareCRF {
	
	String crfrectxtpath="";
	
	String crfprocesstxtpath="";
	
	public String[][] statisticmatric;
	
	public int max=0;
	
	public String[] crfbinary;
	
	public String[][] result;

	public CompareCRF(String crfrecpath,String crfprocesspath) {
		// TODO Auto-generated constructor stub
		this.crfprocesstxtpath=crfprocesspath;
		this.crfrectxtpath=crfrecpath;
	}
	
	public void loadbinary(){
		String path=crfprocesstxtpath;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = null;
			
			while ((str = reader.readLine()) != null && str!="") {
				//txt=txt+str;
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
	}
	
	public void crftxtbinaryprocess(){
		BufferedReader reader = null;
		BufferedWriter fw = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.crfrectxtpath), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			
			File file = new File(crfprocesstxtpath);
			fw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			
			
			String str = null;
			String[] eachline=null;
			while ((str = reader.readLine()) != null) {
	
				if(!str.equals("")){
					eachline=str.split(" ");
					for(int i=1;i<eachline.length;i++){
						if(eachline[i].split("-").length>1){
							eachline[i]=String.valueOf(1);
						}else{
							eachline[i]=String.valueOf(0);
						}
					}
					
					for(int j=0;j<eachline.length;j++){
						fw.write(eachline[j]);
						if(j!=eachline.length-1){
							fw.write(" ");
						}					
					}
					fw.newLine();
					fw.flush();	
				}			
			}
			System.out.println(crfprocesstxtpath+"写入完成！");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getMatrix(String predictcorpus,String modelpath,String dbnmodelpath,Integer d,Integer windowsize) throws IOException{
		ModelEvaluation me=new ModelEvaluation(predictcorpus);
		me.predict(predictcorpus, modelpath, dbnmodelpath, d, windowsize);
		me.statistics("false");
		this.statisticmatric=me.statisticmatric;
		this.max=me.max;
		
		String txt="";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(predictcorpus), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = null;
			while ((str = reader.readLine()) != null && str!="") {
				String[] t=str.split(" ");
				if(t.length>1){
					str=str.split(" ")[2];
					if(str.split("-").length>1){
						str="1";
					}else{
						str="0";
					}
					txt=txt+str+"&&";
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
		
		crfbinary=txt.split("&&");
		
		System.out.println(crfbinary.length+"/"+statisticmatric.length);
		
		result=new String[statisticmatric.length][4];
		
		for(int i=0;i<result.length;i++){
			result[i][0]=statisticmatric[i][0];
			result[i][1]=statisticmatric[i][1];
			result[i][2]=crfbinary[i];
			if(Double.valueOf(statisticmatric[i][2])>max){
				result[i][3]="1";
			}else{
				result[i][3]="0";
			}
			//result[i][3]=statisticmatric[i][2];			
		}		
	}
	
	
	public void writeResult(String resultfilepath){
		BufferedWriter fw = null;
		try {
			File file = new File(resultfilepath);
			if(!file.exists()){
				System.out.println("文件不存在！");
			}
			
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultfilepath, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			for(int i=0;i<result.length;i++){
				for(int j=0;j<result[i].length;j++){
					fw.write(result[i][j]+"		");
//					System.out.print(result[i][j]+"		");
				}
				fw.newLine();
//				System.out.println();
			}
			
			
			fw.flush(); // 全部写入缓存中的内容
			System.out.println(resultfilepath+"写入完成！");
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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String crfrecpath="files/originalcorpus/half3.txt";
		
		String crfprocesspath="files/originalcorpus/dlbk_crf_bi.txt";
		
		CompareCRF cc=new CompareCRF(crfrecpath,crfprocesspath);

		//cc.crftxtbinaryprocess();
		
		String modelpath="files/w2vmodel/dlbk_singlewords_ngram_100_5_1e3_model.mod";
		
		String dbnmodelpath="files/DBNModel/type_single_d_100_windowsize_5_net_-600-_epochs_15000_CDrate_0.2.mod";
		
		int d=100;
		
		int windowsize=5;
		
		cc.getMatrix(crfrecpath, modelpath, dbnmodelpath, d, windowsize);
		
		String resultfilepath="files/compare/crfs-dbn_all.txt";
		
		cc.writeResult(resultfilepath);
		
//		for(int i=0;i<cc.statisticmatric.length;i++){
//			if(Double.valueOf(cc.statisticmatric[i][2])>cc.max){
//				cc.statisticmatric[i][2]="1";
//			}else{
//				cc.statisticmatric[i][2]="0";
//			}
//			
//			for(int j=0;j<cc.statisticmatric[i].length;j++){
//				System.out.print(cc.statisticmatric[i][j]+"	");
//			}
//			System.out.println();
//		}
		
		
//		for(int i=0;i<cc.result.length;i++){
//			for(int j=0;j<cc.result[i].length;j++){
//				System.out.print(cc.result[i][j]+"	");
//			}
//			System.out.println();
//		}
		
		
	}

}
