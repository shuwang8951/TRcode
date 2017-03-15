package njnu.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GeoFeature {

	private ArrayList<String> limitedwords=new ArrayList<String>();
	
	private ArrayList<String> deletewords=new ArrayList<String>();
	
	private ArrayList<String> signalwords=new ArrayList<String>();
	
	public GeoFeature(){
		
		String limitedwordspath="files/geofeature/limitedwords.txt";
		
		limitedwords=readlinefile(limitedwordspath);

		String deletewordspath="files/geofeature/deletewords.txt";
		
		deletewords=readlinefile(deletewordspath);
		
		String signalwordspath="files/geofeature/signalwords.txt";
		
		signalwords=readlinefile(signalwordspath);
		
		
	}
	
	public ArrayList<String> readlinefile(String txtpath){
		ArrayList<String> temp = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(txtpath), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = "";
			while ((str = reader.readLine()) != null) {
				temp.add(str);
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
		return temp;
	}
	
	
	
	public String[][] limitedwordImprove(String[][] statisticmatric,String type){
		String[][] im=new String[statisticmatric.length][statisticmatric[0].length];
		int count=0;
		for(int i=0;i<statisticmatric.length;i++){
			for(int j=0;j<statisticmatric[i].length;j++){
				im[i][j]=statisticmatric[i][j];
			}
		}
		
		
		if(type.equals("single")){
			for(int i=0;i<statisticmatric.length;i++){
				
				for(String signalword:signalwords){
					if(statisticmatric[i][0].equals(signalword)){
						im[i][2]=String.valueOf("100");
					}
				}
				
//				if(statisticmatric[i][0].equals("中") &&statisticmatric[i+1][0].equals("国")){
//					im[i][2]=String.valueOf("100");
//					im[i+1][2]=String.valueOf("100");
//				}
				
				String lw="";
				int e=1;
				for(int x=0;x<limitedwords.size();x++){
					lw=limitedwords.get(x);
					if(statisticmatric[i][0].equals(lw.substring(0, 1))){
						for(int m=1;m<lw.length();m++){
							if((statisticmatric[i+m][0].equals(lw.substring(m, m+1)))){
								e++;
							}else{
								break;
							}
						}
						if(e==lw.length()){
							for(int n=0;n<lw.length();n++){
								im[i+n][2]=String.valueOf("100");
							}
						}
						e=1;
					}
					
				}
				
				
			}
			
		}
		if(type.equals("seg")){
			for(int i=0;i<statisticmatric.length;i++){
				if(statisticmatric[i][0].length()>1){
					//限定词
					for(String limitedword:limitedwords){
						if(statisticmatric[i][0].contains(limitedword)){
							im[i][2]=String.valueOf("100");
							count++;
							break;
						}
					}
					//去除词
					for(String deleteword:deletewords){
						if(statisticmatric[i][0].contains(deleteword)){
							im[i][2]=String.valueOf("0");
							break;
						}
					}
				}
			}
		}
		System.out.println("count:"+count);
		
		
		return im;
	}
	
	
	public static void main(String[] args) throws IOException {
		
		GeoFeature gf=new GeoFeature();
		
	}
	
}
