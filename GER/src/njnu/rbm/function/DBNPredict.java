package njnu.rbm.function;

import static DeepLearning.utils.sigmoid;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import DeepLearning.HiddenLayerDiscrete;
import DeepLearning.LogisticRegressionDiscrete;

public class DBNPredict {
	
    public HiddenLayerDiscrete[] sigmoid_layers;

    public LogisticRegressionDiscrete log_layer;
    
	
	public void predict(double[] x, double[] y) {
        int n_ins=x.length;
        System.out.println(n_ins);
        int n_layers=sigmoid_layers.length;
        
		double[] layer_input = new double[0];
        // int prev_layer_input_size;
        double[] prev_layer_input = new double[n_ins];
        for(int j=0; j<n_ins; j++) prev_layer_input[j] = x[j];

        double linear_output;


        // layer activation
        for(int i=0; i<n_layers; i++) {
            layer_input = new double[sigmoid_layers[i].n_out];

            for(int k=0; k<sigmoid_layers[i].n_out; k++) {
                linear_output = 0.0;

                for(int j=0; j<sigmoid_layers[i].n_in; j++) {
                    linear_output += sigmoid_layers[i].W[k][j] * prev_layer_input[j];
                }
                linear_output += sigmoid_layers[i].b[k];
                layer_input[k] = sigmoid(linear_output);
            }

            if(i < n_layers-1) {
                prev_layer_input = new double[sigmoid_layers[i].n_out];
                for(int j=0; j<sigmoid_layers[i].n_out; j++) prev_layer_input[j] = layer_input[j];
            }
        }

        for(int i=0; i<log_layer.n_out; i++) {
            y[i] = 0;
            for(int j=0; j<log_layer.n_in; j++) {
                y[i] += log_layer.W[i][j] * layer_input[j];
            }
            y[i] += log_layer.b[i];
        }

        log_layer.softmax(y,log_layer.W.length);
    }
	
	public void LoadDBNModel(String dbnmodelpath){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dbnmodelpath), "UTF-8")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
			String str = null;
			String[] layersnumber = null;
			
			if((str = reader.readLine()) != null){
				sigmoid_layers=new HiddenLayerDiscrete[Integer.valueOf(str)];
			}
			if((str = reader.readLine()) != null){
				layersnumber=str.split("&&");
				
				System.out.println(this.sigmoid_layers.length);
				
				for(int i=0;i<this.sigmoid_layers.length;i++){
					sigmoid_layers[i]=new HiddenLayerDiscrete();
					sigmoid_layers[i].W=new double[Integer.valueOf(layersnumber[i+1])][Integer.valueOf(layersnumber[i])];
					sigmoid_layers[i].b=new double[Integer.valueOf(layersnumber[i+1])];
				}
				log_layer=new LogisticRegressionDiscrete();
				log_layer.W=new double[Integer.valueOf(layersnumber[layersnumber.length-1])][Integer.valueOf(layersnumber[layersnumber.length-2])];
				log_layer.b=new double[Integer.valueOf(layersnumber[layersnumber.length-1])];
			}
			
			for(int i=0;i<sigmoid_layers.length;i++){
				sigmoid_layers[i].n_in=Integer.valueOf(layersnumber[i]);
				sigmoid_layers[i].n_out=Integer.valueOf(layersnumber[i+1]);
				for(int x=0;x<sigmoid_layers[i].W.length;x++){
					for(int y=0;y<sigmoid_layers[i].W[x].length;y++){
						sigmoid_layers[i].W[x][y]=Double.valueOf(reader.readLine());
					}
				}
				for(int z=0;z<sigmoid_layers[i].b.length;z++){
					sigmoid_layers[i].b[z]=Double.valueOf(reader.readLine());
				}
			}
			log_layer.n_in=Integer.valueOf(layersnumber[layersnumber.length-2]);
			log_layer.n_out=Integer.valueOf(layersnumber[layersnumber.length-1]);
			for(int i=0;i<log_layer.W.length;i++){
				for(int j=0;j<log_layer.W[i].length;j++){
					log_layer.W[i][j]=Double.valueOf(reader.readLine());
				}
			}
			for(int i=0;i<log_layer.b.length;i++){
				log_layer.b[i]=Double.valueOf(reader.readLine());
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
	
	public double[][] predicttextreader(String filepath,String w2vmodelfilepath){
		double[][] pt = null;
		
		return pt;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

//		 // test data
//        double[][] test_X = {
//                {1, 1, 0, 0, 0, 0,1},
//                {1, 1, 1, 1, 0, 0,1},
//                {0, 0, 0, 1, 1, 0,1},
//                {0, 0, 1, 1, 1, 0,1},
//        };
//
//        double[][] test_Y = new double[test_X.length][3];
//        
//        String dbnmodelpath="files/DBNModel/testmodel.mod";
//        
//        DBNPredict dbnp=new DBNPredict();
//        
//        dbnp.LoadDBNModel(dbnmodelpath);
//        
//        // test
//        for(int i=0; i<test_X.length; i++) {
//        	dbnp.predict(test_X[i], test_Y[i]);
//            for(int j=0; j<3; j++) {
//                System.out.print(test_Y[i][j] + " ");
//            }
//            System.out.println();
//        }
		
		String[][] resulttext;
		
		String corpuspath="files/predictcorpus/testcorpus.txt";
		String modelpath="files/w2vmodel/zaihai_singlewords_ngram_15_5_1e3_model.mod";
		int d=15;
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
		
		String dbnmodelpath="files/DBNModel/type_single_d_75_windowsize_5_net_-150-_epochs_10000_CDrate_0.1.mod";
		
		DBNPredict dbnp=new DBNPredict();
		
		dbnp.LoadDBNModel(dbnmodelpath);
		
//		for(int i=0; i<predictcorpus.length; i++) {
//    		dbnp.predict(predictcorpus[i], results[i]);
//        	for(int j=0; j<2; j++) {
//         	   System.out.print(results[i][j] + " ");
//       	 	}
//       	 	System.out.println();
//    	}
		
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
