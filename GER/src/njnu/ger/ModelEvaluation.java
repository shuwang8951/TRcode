package njnu.ger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import njnu.rbm.function.DBNPredict;
import njnu.rbm.function.DataCorpusInput;
import njnu.utility.GeoFeature;

public class ModelEvaluation {
	
	public String txt="";
	
	public String tags="";
	
	public String[][] statisticmatric;
	
	public static String[][] resulttext;
	
	public double[][] PCmatric=new double[100][3];
	
	public int lablednumber=0;
	
	public int predicnumber=0;
	
	public int subnumber=0;
	
	public String predictcorpus;
	public String modelpath;
	public String dbnmodelpath;
	
	public double MaxFmeasure=0;
	public double MaxP=0;
	public double MaxR=0;
	
	public int max=0;
	
	public String modeltype="";
	
	public String wordd="";
	
	
	public ModelEvaluation(String predictcorpus){
		

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(predictcorpus), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = null;
			int i=0;
			while ((str = reader.readLine()) != null && str!="") {
				//System.out.println(str);
				if(str.split(" ").length>=2){		//注意不同文件下 空格和tab键
					txt=txt+str.split(" ")[0]+" ";
					tags=tags+str.split(" ")[1]+" ";
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
		txt=txt.substring(0,txt.length()-1);
		tags=tags.substring(0,tags.length()-1);
		
		String[] txtlist=txt.split(" ");
		String[] tagslist=tags.split(" ");
		
		statisticmatric=new String[txtlist.length][3];
		
		for(int i=0;i<statisticmatric.length;i++){
			statisticmatric[i][0]=txtlist[i];
			if(tagslist[i].split("-").length==2){
				statisticmatric[i][1]=String.valueOf(1);
			}else{
				statisticmatric[i][1]=String.valueOf(0);
			}			
		}
	
	}
	
	
	public void predict(String predictcorpus,String modelpath,String dbnmodelpath,Integer d,Integer windowsize) throws IOException{
		
		this.predictcorpus=predictcorpus;
		this.modelpath=modelpath;
		this.dbnmodelpath=dbnmodelpath;
		
		this.wordd=String.valueOf(d);
		
		DBNPredict dbnp=new DBNPredict();
			
//		String corpuspath="files/predictcorpus/DBNtestcorpus.txt";
//		String modelpath="files/w2vmodel/dlbk_singlewords_ngram_15_5_1e3_model.mod";
//		int d=15;
//		int windowsize=5;
		
		DataCorpusInput dci=new DataCorpusInput(predictcorpus,modelpath,d,windowsize);
		
		resulttext=new String[dci.words.length][2];
		
		for(int i=0;i<dci.words.length;i++){
			resulttext[i][0]=dci.words[i];
		}
		
		int[][] test_X =dci.Vector2RBMInput(predictcorpus,modelpath,d,windowsize);
		double[][] predictcorpusmatric=new double[test_X.length][test_X[0].length];
		for(int i=0;i<test_X.length;i++){
			for(int j=0;j<test_X[i].length;j++){
				predictcorpusmatric[i][j]=test_X[i][j];
			}
		}
		
		double[][] results = new double[test_X.length][2];
				
		dbnp.LoadDBNModel(dbnmodelpath);	
		
		for(int i=0; i<predictcorpusmatric.length; i++) {
    		dbnp.predict(predictcorpusmatric[i], results[i]);
    		resulttext[i][1]=String.valueOf(results[i][0]);
       	 	//System.out.println();
    	}
		
		for(int i=0;i<resulttext.length;i++){
			statisticmatric[i][2]=String.valueOf(Double.valueOf(resulttext[i][1])*100);
		}
		
		for(int i=0;i<statisticmatric.length;i++){
			if(statisticmatric[i][0].equals("，")||statisticmatric[i][0].equals("。")
					||statisticmatric[i][0].equals("！")||statisticmatric[i][0].equals("、")
					||statisticmatric[i][0].equals("；")||statisticmatric[i][0].equals("“")
					||statisticmatric[i][0].equals("”")||statisticmatric[i][0].equals("？")){
				statisticmatric[i][1]="0";
				statisticmatric[i][2]="0";
			}
		}
		
		
	}
	
	public void statistics(String gfs){
		
		double p=0;
		double c=0;
		
		String[] predicbinary=new String[statisticmatric.length];
		
		if(gfs.equals("true")){
			GeoFeature gf=new GeoFeature();
			String[][] im = null;
			if(dbnmodelpath.contains("seg")){
				im=gf.limitedwordImprove(statisticmatric, "seg");
			}
			if(dbnmodelpath.contains("single")){
				im=gf.limitedwordImprove(statisticmatric, "single");
			}
			
			for(int i=0;i<statisticmatric.length;i++){
				for(int j=0;j<statisticmatric[i].length;j++){
					statisticmatric[i][j]=im[i][j];
					System.out.print(statisticmatric[i][j]+"	");
				}
				System.out.println();
			}
		}
		
		
		if(dbnmodelpath.contains("seg")){
			modeltype="seg";
		}
		if(dbnmodelpath.contains("single")){
			modeltype="single";
		}
		
		
		for(int h=0;h<PCmatric.length;h++){
			for(int i=0;i<statisticmatric.length;i++){
				if(Double.valueOf(statisticmatric[i][2])>h ){
					predicbinary[i]=String.valueOf(1);
				}else{
					predicbinary[i]=String.valueOf(0);
				}
			}
			
			
			for(int i=0;i<statisticmatric.length;i++){
				if(statisticmatric[i][1].equals("1")){
					lablednumber++;
				}
				if(predicbinary[i].equals("1")){
					predicnumber++;
				}
				if(statisticmatric[i][1].equals("1") && predicbinary[i].equals("1")){
					subnumber++;
				}
			}
			
			if(predicnumber!=0){
				p=(double)subnumber/(double)predicnumber*100;
			}else{
				p=0;
			}
			if(lablednumber!=0){
				c=(double)subnumber/(double)lablednumber*100;
			}			
			
			PCmatric[h][0]=p;
			PCmatric[h][1]=c;
			PCmatric[h][2]=2*p*c/(p+c);
			
			lablednumber=0;
			predicnumber=0;
			subnumber=0;		
		}
		
		MaxFmeasure=PCmatric[0][2];
		
		for(int i=0;i<PCmatric.length;i++){
			if(PCmatric[i][2]>MaxFmeasure){
				MaxFmeasure=PCmatric[i][2];
				max=i;
			}
		}
		MaxP=PCmatric[max][0];
		MaxR=PCmatric[max][1];
		
		
	}
	
	public void writemodelpredictfile(String filepath){
		BufferedWriter fw = null;
		try {
			File file = new File(filepath);
			if(!file.exists()){
				System.out.println("文件不存在！");
			}
			
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			fw.newLine();		
			fw.write("*******************************************");
			fw.newLine();
			fw.write("predictcorpus:"+predictcorpus);
			fw.newLine();
			fw.write("modelpath:"+modelpath);
			fw.newLine();
			fw.write("dbnmodelpath:"+dbnmodelpath);
			fw.newLine();
			
//			for(int i=0;i<PCmatric.length;i++){
//				fw.write(String.valueOf(i));
//				fw.write("	");		
//			}
//			fw.newLine();
//			for(int i=0;i<PCmatric.length;i++){
//				fw.write(String.format("%1$.2f", PCmatric[i][0])+"/"+String.format("%1$.2f", PCmatric[i][1])+"/"+String.format("%1$.2f", PCmatric[i][2]));
//				fw.write("	");		
//			}
			fw.write( MaxP+"/"+MaxR+"/"+MaxFmeasure);
			fw.newLine();
			fw.write("-----------F-measure:"+MaxFmeasure+"----------------");
			fw.newLine();
			fw.write(modeltype+"	"+wordd+"	"+wordd+"	");
			fw.newLine();
//			for(int i=0;i<statisticmatric.length;i++){
//				if(statisticmatric[i][1].equals("1")||statisticmatric[i][2].equals("1")){
//					for(int j=0;j<statisticmatric[i].length;j++){
//						fw.write(statisticmatric[i][j]+"		");
//					}
//					fw.newLine();
//				}
//			}
//			fw.newLine();
			
			
		
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
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String predictcorpus="files/originalcorpus/10fold/1P.txt";
//		
//		String modelpath="files/w2vmodel/dlbk_singlewords_ngram_15_5_1e3_model.mod";
//		
//		String dbnmodelpath="files/DBNModel/type_single_d_15_windowsize_5_net_-10-_epochs_20_CDrate_0.2.mod";
//
//		int d=Integer.valueOf("15");		
//		
//		int windowsize=Integer.valueOf("5");			
//		
//		String gf="false";
		
		String predictcorpus=args[0];
		
		String modelpath=args[1];
		
		String dbnmodelpath=args[2];
		
		int d=Integer.valueOf(args[3]);		
		
		int windowsize=Integer.valueOf(args[4]);	
		
		String gf=args[5];
		
		
		String filepath="files/predictcorpus/modelstatistics.txt";
		
		ModelEvaluation me=new ModelEvaluation(predictcorpus);
		
		System.out.println(me.txt);
		System.out.println(me.tags);
		
		me.predict(predictcorpus, modelpath, dbnmodelpath,d,windowsize);
		
		for(int i=0;i<me.statisticmatric.length;i++){
			for(int j=0;j<me.statisticmatric[i].length;j++){
				System.out.print(me.statisticmatric[i][j]+"		");
			}
			System.out.println();
		}
		
		
		
		me.statistics(gf);
		
		me.writemodelpredictfile(filepath);
		
		
	}

}
