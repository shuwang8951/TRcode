package njnu.ger;

import java.io.IOException;

import njnu.rbm.function.DBNPredict;
import njnu.rbm.function.DataCorpusInput;

public class DBNtest {
	
	public static String[][] resulttext;
	
	public DBNtest(String corpuspath,String modelpath,String dbnmodelpath,int d) throws IOException{
		
		DBNPredict dbnp=new DBNPredict();
		
		
		
//		String corpuspath="files/predictcorpus/DBNtestcorpus.txt";
//		String modelpath="files/w2vmodel/dlbk_singlewords_ngram_15_5_1e3_model.mod";
		
		
		//int d=15;
		
		int windowsize=5;
		
		DataCorpusInput dci=new DataCorpusInput(corpuspath,modelpath,d,windowsize);
		
		resulttext=new String[dci.words.length][2];
		
		for(int i=0;i<dci.words.length;i++){
			resulttext[i][0]=dci.words[i];
		}
		
		int[][] test_X =dci.Vector2RBMInput(corpuspath,modelpath,d,windowsize);
		double[][] predictcorpus=new double[test_X.length][test_X[0].length];
		for(int i=0;i<test_X.length;i++){
			for(int j=0;j<test_X[i].length;j++){
				predictcorpus[i][j]=test_X[i][j];
			}
		}
		
		double[][] results = new double[test_X.length][2];
				
		dbnp.LoadDBNModel(dbnmodelpath);	
		
		for(int i=0; i<predictcorpus.length; i++) {
    		dbnp.predict(predictcorpus[i], results[i]);
    		resulttext[i][1]=String.valueOf(results[i][0]);
       	 	//System.out.println();
    	}
		
		for(int i=0;i<resulttext.length;i++){
			for(int j=0;j<resulttext[i].length;j++){
				System.out.print(resulttext[i][j]+"	");
			}
			System.out.println();
		}
	}
	

}
