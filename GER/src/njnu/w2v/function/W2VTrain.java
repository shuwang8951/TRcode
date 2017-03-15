package njnu.w2v.function;

import java.io.File;
import java.io.IOException;

import com.ansj.vec.Learn;

public class W2VTrain {
	
	/**
	 * 
	 * @param corpusfilepath w2v训练语料文件，格式为空格间隔词汇或汉字
	 * @param isCbow n-gram算法填false，CBOW算法填true
	 * @param layerSize 向量维度
	 * @param window 训练时选用上下文的开窗大小
	 * @param alpha 通用为0.025
	 * @param sample 取样大小1e-3至1e-5之间
	 * @throws IOException
	 */
	public W2VTrain(String corpusfilepath,String modelfilepath,Boolean isCbow, Integer layerSize, Integer window, Double alpha, Double sample) throws IOException{
		File file=new File(corpusfilepath);

		if(file.exists()){
			//进行分词训练
			
	        Learn lean = new Learn(false, layerSize, window, alpha,sample) ;

	        lean.learnFile(file) ;

	        lean.saveModel(new File(modelfilepath)) ;

		}else{
			System.out.println("语料文件在"+corpusfilepath+"不存在！");
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/**
		 * 汉字共有约90000，2^16,常用汉字2500-7000，2^12-13
		 */
		
//		String corpusfilepath="files/w2vcorpus/segtxt.txt";
//		String modelfilepath="files/w2vmodel/dlbk_ngram_3_5_1e3_model.mod";
//		
//		W2VTrain t= new W2VTrain(corpusfilepath,modelfilepath,false, 3, 5, 0.025,1e-3);
		
//		String corpusfilepath="files/w2vcorpus/zaihai_segwords.txt";
//		String modelfilepath="files/w2vmodel/zaihai_segwords_ngram_15_5_1e3_model.mod";
//		
//		long a=System.currentTimeMillis();
//		W2VTrain t= new W2VTrain(corpusfilepath,modelfilepath,false, 15, 5, 0.025,1e-3);
//		System.out.println("\r<br>执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
//		System.out.println();
//		
//		String corpusfilepath1="files/w2vcorpus/zaihai_singelwords.txt";
//		String modelfilepath1="files/w2vmodel/zaihai_singlewords_ngram_15_5_1e3_model.mod";
//		a=System.currentTimeMillis();
//		W2VTrain t1= new W2VTrain(corpusfilepath1,modelfilepath1,false, 15, 5, 0.025,1e-3);
//		System.out.println("\r<br>执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
//		System.out.println();
		
		String corpusfilepath="files/w2vcorpus/dlbk_segwords.txt";
		String modelfilepath="files/w2vmodel/dlbk_segwords_ngram_75_5_1e3_model.mod";
		
		long a=System.currentTimeMillis();
		W2VTrain t= new W2VTrain(corpusfilepath,modelfilepath,false, 75, 5, 0.025,1e-3);
		System.out.println("\r<br>执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
		System.out.println();
		
		String corpusfilepath1="files/w2vcorpus/dlbk_singelwords.txt";
		String modelfilepath1="files/w2vmodel/dlbk_singlewords_ngram_75_5_1e3_model.mod";
		a=System.currentTimeMillis();
		W2VTrain t1= new W2VTrain(corpusfilepath1,modelfilepath1,false, 75, 5, 0.025,1e-3);
		System.out.println("\r<br>执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
		System.out.println();
		
		
		
	}

}
