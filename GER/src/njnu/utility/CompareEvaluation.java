package njnu.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class CompareEvaluation {
	
	String[][] results;
	
	public double Pcrf=0;
	public double Rcrf=0;
	public double Fcrf=0;
	public double PDBN=0;
	public double RDBN=0;
	public double FDBN=0;
	
	public CompareEvaluation(String resultfilepath){
		BufferedReader reader = null;
		String txt="";
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(resultfilepath), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = null;
			while ((str = reader.readLine()) != null && str!="") {
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
		
		String[] lines=txt.split("&&");
		
		results=new String[lines.length][4];
		
		String[] temp=null;
		for(int i=0;i<results.length;i++){
			temp=lines[i].split("		");
			if(temp.length>=4){
				results[i][0]=temp[0];
				results[i][1]=temp[1];
				results[i][2]=temp[2];
				results[i][3]=temp[3];
			}else{
				System.out.println(i+"行数据为空");
			}
		}

	}
	
	public void statistics(){
		
		int lablednumber=0;
		int crfprenum=0;
		int crfcombonum=0;
		int dbnprenum=0;
		int dnmcombonum=0;
		
		for(int i=0;i<results.length;i++){
			if(results[i][1].equals("1")){
				lablednumber++;
				if(results[i][2].equals("1")){
					crfcombonum++;
				}
				if(results[i][3].equals("1")){
					dnmcombonum++;
				}
			}
			if(results[i][2].equals("1")){
				crfprenum++;
			}
			if(results[i][3].equals("1")){
				dbnprenum++;
			}
		}
		
		if(crfprenum!=0){
			Pcrf=(double)crfcombonum/(double)crfprenum*100;
		}
		if(lablednumber!=0){
			Rcrf=(double)crfcombonum/(double)lablednumber*100;
		}
		Fcrf=2*Pcrf*Rcrf/(Pcrf+Rcrf);

		if(dbnprenum!=0){
			PDBN=(double)dnmcombonum/(double)dbnprenum*100;
		}
		if(lablednumber!=0){
			RDBN=(double)dnmcombonum/(double)lablednumber*100;
		}
		FDBN=2*PDBN*RDBN/(PDBN+RDBN);
	
	}
	
	
	public void improve(){
		
		for(int i=3;i<results.length-3;i++){
			if(results[i][3].equals("1")&&results[i-1][3].equals("0")
					&&results[i-2][3].equals("0")&&results[i-3][3].equals("0")
					&&results[i+1][3].equals("0")&&results[i+2][3].equals("0")
					&&results[i+3][3].equals("0")){
				results[i][3]="0";
			}
		}
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String resultfilepath="files/compare/crfs-dbn.txt";
		
		CompareEvaluation ce=new CompareEvaluation(resultfilepath);
		//ce.improve();
		ce.statistics();
		
		System.out.println("CRF-P:"+ce.Pcrf);
		System.out.println("CRF-R:"+ce.Rcrf);
		System.out.println("CRF-F:"+ce.Fcrf);
		System.out.println("DBN-P:"+ce.PDBN);
		System.out.println("DBN-R:"+ce.RDBN);
		System.out.println("DBN-F:"+ce.FDBN);
		

	}

}
