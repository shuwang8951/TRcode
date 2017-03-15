package njnu.rbm.function;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import njnu.utility.Utility;
import njnu.w2v.function.W2VVector;

public class DataCorpusInput {
	
	public static String[] words;
	
	public static double[][] corpus;
	
	
	public DataCorpusInput(String corpuspath,String modelpath,Integer d,int windowsize) throws IOException{
		
		
		String txt="";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(corpuspath), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = null;
			int i=0;
			while ((str = reader.readLine()) != null && str!="") {
				//System.out.println(str);
				if(str.split(" ").length>=2){		//空格和tab
					txt=txt+str.split(" ")[0]+" ";
				}
				System.out.println("读入"+(i++)+"条语料数据");
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
		txt.substring(0,txt.length()-1);
		
		W2VVector v=new W2VVector(modelpath);
		
		words=txt.split(" ");
		
		//System.out.println(words.length);
		
		
		int extend=(windowsize-1)/2;
		
		String[] wordsext=new String[words.length+extend*2];
		
		for(int i=0;i<extend;i++){
			wordsext[i]="。";
		}
		for(int i=0;i<words.length;i++){
			wordsext[i+extend]=words[i];
		}
		for(int i=0;i<extend;i++){
			wordsext[i+extend+words.length]="。";
		}
		
		
		int l=wordsext.length;
		
		System.out.println();
		corpus=new double[l][d];
		for(int i=0;i<l;i++){
			float[] t=v.wordvecter(wordsext[i]);
			for(int j=0;j<d;j++){
				if(t==null){
					t=v.wordvecter("。");
				}
				corpus[i][j]=t[j];
			}
		}
		System.out.println("corpus[0]"+corpus[0].length);
		
	}
	
	public static void corpusbinary(){
		if(corpus!=null){
			for(int i=0;i<corpus.length;i++){
				for(int j=0;j<corpus[i].length;j++){
					if(corpus[i][j]<=0){
						corpus[i][j]=0;
					}else{
						corpus[i][j]=1;
					}				
				}
			}
		}else{
			System.out.println("未读入语料和模型!");
		}
	}
	
	public static double[][] RBMInput(int windowsize){
		double[][] rbminput = null;
		
		int sample;
		int vd;
		if(corpus!=null){				
			
			if(windowsize<corpus.length){
				sample=corpus.length-windowsize+1;
				vd=corpus[0].length*windowsize;
				rbminput=new double[sample][vd];
				
				for(int x=0;x<sample;x++){
					for(int y=x;y<x+windowsize;y++){
						int t=y-x;
						for(int z=0;z<corpus[0].length;z++){
							rbminput[x][t*corpus[0].length+z]=corpus[y][z];
						}
					}
					System.out.println("正在生成"+x+"条样本向量~~~");
				}				
			}else{
				System.out.println("RBM数据输入初始化错误！窗口大小应小于单词量！");
			}
		}else{
			System.out.println("未初始化语料矩阵！");
		}
		
		return rbminput;
	}
	
	
	public static int[][] Vector2RBMInput(String corpuspath,String modelpath,int dimention,int windowsize) throws IOException{
		
		double[][] ri;
		
		corpusbinary();
		ri=RBMInput(windowsize);
		
		int[][] r=new int[ri.length][ri[0].length];
		for(int i=0;i<ri.length;i++){
			for(int j=0;j<ri[i].length;j++){
				r[i][j]=(int) ri[i][j];
			}
		}
		
		return r;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String corpuspath="files/w2vcorpus/rbmtestcorpus.txt";
//		String modelpath="files/w2vmodel/dlbk_ngram_3_5_1e3_model.mod";
//		int d=3;
//		int windowsize=5;
//		
//		DataCorpusInput dci=new DataCorpusInput(corpuspath,modelpath,d,windowsize);
//		
//		int[][] ri=dci.Vector2RBMInput(corpuspath,modelpath,d,windowsize);
		
		String corpuspath="files/originalcorpus/zaihai.txt";
		String modelpath="files/w2vmodel/zaihai_singlewords_ngram_15_5_1e3_model.mod";
		int d=15;
		int windowsize=5;
		
		DataCorpusInput dci=new DataCorpusInput(corpuspath,modelpath,d,windowsize);
		
		int[][] ri=dci.Vector2RBMInput(corpuspath,modelpath,d,windowsize);
		
		
		
		
		
//		DataCorpusInput dci=new DataCorpusInput(corpuspath,modelpath,d);
//		
//		corpusbinary();
//		
//		double[][] ri=RBMInput(5);
		
		
//		for(int i=0;i<dci.corpus.length;i++){
//			for(int j=0;j<dci.corpus[i].length;j++){
//				System.out.print(dci.corpus[i][j]+"		");
//			}
//			System.out.println();
//		}
//		System.out.println(corpus.length+" "+corpus[0].length);
//		for(int i=0;i<ri.length;i++){
//			for(int j=0;j<ri[i].length;j++){
//				System.out.print(ri[i][j]+"    ");
//			}
//			System.out.println();
//		}
		System.out.println(dci.words.length);
		System.out.println(ri.length+" "+ri[0].length);
	}

}
