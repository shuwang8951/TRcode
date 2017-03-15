package njnu.ger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import njnu.corpusprocess.LabledDataProcess;
import njnu.rbm.function.DBNTrain;
import njnu.rbm.function.DataCorpusInput;

public class GER {
	
	public GER(String corpuspath,String modelpath,int d,int windowsize,
			String resultspath,int epochs,int[] dbnnet,double CDrate) throws IOException{
		
		String corpusname=corpuspath.split("/")[corpuspath.split("/").length-1];
		
		LabledDataProcess ldp=new LabledDataProcess(corpuspath);
		
		DataCorpusInput dci=new DataCorpusInput(corpuspath,modelpath,d,windowsize);
		
		int[][] ri=dci.Vector2RBMInput(corpuspath,modelpath,d,windowsize);
		
		int[][] train_Y = new int[ri.length][2];
		
		for(int i=0;i<train_Y.length;i++){
			//System.out.println(ldp.wordcorpus[i][1]);
			if(ldp.wordcorpus[i][1]!=null){
				train_Y[i][0]=Integer.valueOf(ldp.wordcorpus[i][1]);
			}else{
				train_Y[i][0]=0;
			}			
		}
		for(int i=0;i<train_Y.length;i++){
			if(train_Y[i][0]==0){
				train_Y[i][1]=1;
			}else if(train_Y[i][0]==1){
				train_Y[i][1]=0;
			}
		}
		
//		for(int i=0;i<ldp.wordcorpus.length;i++){
//			for(int j=0;j<ldp.wordcorpus[i].length;j++){
//				System.out.print(ldp.wordcorpus[i][j]+"  ");
//			}
//			System.out.println();
//		}
		
	        
//	    int[] hl={20,8};
	    int[] hl=dbnnet;
	    String net="-";
	    for(int i=0;i<hl.length;i++){
	    	net=net+hl[i]+"-";
	    }
	    
	    long a=System.currentTimeMillis();
	    float traintime;
	    DBNTrain dbnt=new DBNTrain(ri, train_Y, ri.length, ri[0].length, 2, hl, epochs, epochs,CDrate);
	    traintime=(System.currentTimeMillis()-a)/1000f;
	    System.out.println("\r<br>执行耗时 : "+traintime+" 秒 ");
	
	    String dbntype="";
	    if(modelpath.contains("single")){
	    	dbntype="single";
	    }else if(modelpath.contains("seg")){
	    	dbntype="seg";
	    }    
	    
		String dbnmodelpath="files/DBNModel/"+corpusname+"type_"+dbntype+"_d_"+d+"_windowsize_"+windowsize+"_net_"+net+"_epochs_"+epochs+"_CDrate_"+CDrate+".mod";

		dbnt.DBNModelSave(dbnmodelpath, ri[0].length, 2, hl);
		System.out.println("DBN模型生成完毕！");
		
		String testcorpuspath="";
		if(dbntype.equals("single")){
			testcorpuspath="files/predictcorpus/DBNtestcorpus.txt";
		}else if(dbntype.equals("seg")){
			testcorpuspath="files/predictcorpus/DBNtestcorpus_seg.txt";
		} 
		DBNtest dbntest=new DBNtest(testcorpuspath,modelpath,dbnmodelpath,d);
		
		//写出预测结果文件
		BufferedWriter fw = null;
		try {
			File file = new File(resultspath);
			if(!file.exists()){
				System.out.println("文件不存在！");
			}
			
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			fw.write("******************************************");
			fw.newLine();
			fw.write("corpuspath:"+corpuspath);
			fw.newLine();
			fw.write("modelpath:"+modelpath);
			fw.newLine();
			fw.write("d:"+d);
			fw.newLine();
			fw.write("windowsize:"+windowsize);
			fw.newLine();
			fw.write("dbnmodelpath:"+dbnmodelpath);
			fw.newLine();
			fw.write("net:"+net);
			fw.newLine();
			fw.write("epochs:"+epochs);
			fw.newLine();
			fw.write("CDrate:"+CDrate);
			fw.newLine();
			fw.write("traintime:"+traintime);
			fw.newLine();
			for(int i=0;i<dbntest.resulttext.length;i++){
				for(int j=0;j<dbntest.resulttext[i].length;j++){
					fw.write(dbntest.resulttext[i][j]+"	");
				}
				fw.newLine();
			}	
			fw.flush(); // 全部写入缓存中的内容
			System.out.println(resultspath+"写入完成！");
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
	
		/**
		 * 训练语料文件
		 */
//		String corpuspath="files/originalcorpus/dlbk8000single.txt";
		String corpuspath=args[0];
		
		/**
		 * 语言模型（字向量模型）路径
		 */
//		String modelpath="files/w2vmodel/dlbk_singlewords_ngram_15_5_1e3_model.mod";
		String modelpath=args[1];
		
		/**
		 * 向量维度
		 */
//		int d=15;
		int d=Integer.valueOf(args[2]);
		
		/**
		 * 训练窗口大小
		 */
//		int windowsize=5;
		int windowsize=Integer.valueOf(args[3]);
		
		/**
		 * dbn模型输出文件
		 */
//		String dbnmodelpath="files/DBNModel/dlbk_singlewords_ngram_15_5_1e3_50-30-15-8-500-500.mod";
//		String dbnmodelpath=args[4];

		
		/**
		 * 简要预测结果输出文件
		 */
		String resultspath="files/DBNModel/predicresult.txt";
//		String resultspath=args[5];
		
		int epochs=Integer.valueOf(args[4]);
		
		String[] dbnnetread=args[5].split("-");
		int[] dbnnet=new int[dbnnetread.length];
		for(int i=0;i<dbnnet.length;i++){
			dbnnet[i]=Integer.valueOf(dbnnetread[i]);
		}
		
		double CDrate=Double.valueOf(args[6]);
			
		GER ger=new GER(corpuspath, modelpath, d, windowsize, resultspath,epochs,dbnnet,CDrate);
//	
//		/**
//		 * 训练语料文件
//		 */
//		String corpuspath="files/originalcorpus/10fold/1C.txt";
//
//		
//		/**
//		 * 语言模型（字向量模型）路径
//		 */
//		String modelpath="files/w2vmodel/dlbk_singlewords_ngram_15_5_1e3_model.mod";
//		
//		
//		/**
//		 * 向量维度
//		 */
//		int d=15;
//		
//		
//		/**
//		 * 训练窗口大小
//		 */
//		int windowsize=5;
//		
//		/**
//		 * dbn模型输出文件
//		 */
////		String dbnmodelpath="files/DBNModel/dlbk_singlewords_ngram_15_5_1e3_50-30-15-8-500-500.mod";
////		String dbnmodelpath=args[4];
//
//		
//		/**
//		 * 简要预测结果输出文件
//		 */
//		String resultspath="files/DBNModel/predicresult.txt";
////		String resultspath=args[5];
//		
//		int epochs=Integer.valueOf("20");
//		
//		String[] dbnnetread="10".split("-");
//		int[] dbnnet=new int[dbnnetread.length];
//		for(int i=0;i<dbnnet.length;i++){
//			dbnnet[i]=Integer.valueOf(dbnnetread[i]);
//		}
//		
//		double CDrate=Double.valueOf("0.2");
//			
//		GER ger=new GER(corpuspath, modelpath, d, windowsize, resultspath,epochs,dbnnet,CDrate);
//	
	}

}
