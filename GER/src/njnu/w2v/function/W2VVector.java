package njnu.w2v.function;

import java.io.IOException;

import com.ansj.vec.Word2VEC;

public class W2VVector {
	
	float[] vector=null;
	Word2VEC w2v= new Word2VEC();
	
	public W2VVector(String modelpath) throws IOException{
		//加载测试
        w2v.loadJavaModel(modelpath) ;

	}
	
	public float[] wordvecter(String word){
		vector=w2v.getWordVector(word);
		
		return vector;
	}
	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

//		String modelfilepath="files/w2vmodel/dlbk_ngram_3_5_1e3_model.mod";
//		String word="北京";
//		W2VVector v=new W2VVector(modelfilepath);
//		float[] temp=v.wordvecter(word);
//		if(v.vector!=null){
//			for(float t:temp){
//				System.out.println(t);
//			}
//		}else{
//			System.out.println("Vector模型无此词汇！");
//		}
		
		
		String modelfilepath="files/w2vmodel/zaihai_singlewords_ngram_15_5_1e3_model.mod";
		String word=" ";
		W2VVector v=new W2VVector(modelfilepath);
		float[] temp=v.wordvecter(word);
		if(v.vector!=null){
			for(float t:temp){
				System.out.println(t);
			}
		}else{
			System.out.println("Vector模型无此词汇！");
		}
		
		
	}

}
