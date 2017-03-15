package njnu.rbm.function;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import DeepLearning.DBN;
import DeepLearning.HiddenLayerDiscrete;
import DeepLearning.LogisticRegressionDiscrete;
import DeepLearning.RBM;

public class DBNTrain {
	
    public HiddenLayerDiscrete[] sigmoid_layers;

    public LogisticRegressionDiscrete log_layer;
	
    /**
     * 
     * @param train_X 训练数据
     * @param train_Y 期望数据
     * @param train_N 样本数
     * @param n_ins 输入层节点数
     * @param n_outs 输出层节点数
     * @param hidden_layer_sizes 每个隐含层节点数
     * @param pretraining_epochs 训练周期
     * @param finetune_epochs 微调周期
     */
	public DBNTrain(int[][] train_X,int[][] train_Y,int train_N,int n_ins,int n_outs
			,int[] hidden_layer_sizes,int pretraining_epochs,int finetune_epochs,double CDrate){
		Random rng = new Random(123);
		
		 // construct DNN.DBN
        DBN dbn = new DBN(train_N, n_ins, hidden_layer_sizes, n_outs, hidden_layer_sizes.length, rng);

        // pretrain
        dbn.pretrain(train_X, CDrate, 1, pretraining_epochs);

        // finetune
        dbn.finetune(train_X, train_Y, CDrate, finetune_epochs);
        
        sigmoid_layers=dbn.sigmoid_layers;
        log_layer=dbn.log_layer;
		
	}
	
	public void DBNModelSave(String dbnmodelpath,int n_ins,int n_outs,int[] hidden_layer_sizes){
		BufferedWriter fw = null;
		try {
			File file = new File(dbnmodelpath);
			if(file.exists()){
				file.delete();
				file = new File(dbnmodelpath);
			}
			
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常

			fw.write(Integer.toString(sigmoid_layers.length));
			fw.newLine();
			fw.write(Integer.toString(n_ins));
			fw.write("&&");
			for(int hnumber:hidden_layer_sizes){
				fw.write(Integer.toString(hnumber));
				fw.write("&&");
			}
			fw.write(Integer.toString(n_outs));
			fw.newLine();
			
			for(int i=0;i<sigmoid_layers.length;i++){
				for(int x=0;x<sigmoid_layers[i].W.length;x++){
					for(int y=0;y<sigmoid_layers[i].W[x].length;y++){
						fw.write(String.valueOf(sigmoid_layers[i].W[x][y]));
						fw.newLine();
					}
				}
				for(int z=0;z<sigmoid_layers[i].b.length;z++){
					fw.write(String.valueOf(sigmoid_layers[i].b[z]));
					fw.newLine();
				}
			}
			
			for(int i=0;i<log_layer.W.length;i++){
				for(int j=0;j<log_layer.W[i].length;j++){
					fw.write(String.valueOf(log_layer.W[i][j]));
					fw.newLine();
				}
			}		
			for(int i=0;i<log_layer.b.length;i++){
				fw.write(String.valueOf(log_layer.b[i]));
				fw.newLine();
			}
			
			
			fw.flush(); // 全部写入缓存中的内容
			System.out.println(dbnmodelpath+"写入完成！");
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// training data
        int[][] train_X = {
                {1, 1, 1, 0, 0, 0,1},
                {1, 0, 1, 0, 0, 0,1},
                {1, 1, 1, 0, 0, 0,1},
                {0, 0, 1, 1, 1, 0,1},
                {0, 0, 1, 1, 0, 0,1},
                {0, 0, 1, 1, 1, 0,1}
        };

        int[][] train_Y = {
                {1, 0,0},
                {1, 0,0},
                {1, 0,0},
                {0, 1,0},
                {0, 1,0},
                {0, 1,0},
        };
        
        int[] hl={20,15,7,4};
		
		DBNTrain dbnt=new DBNTrain(train_X, train_Y, 6, 7, 3, hl, 1000, 1000,0.1);
		
		String dbnmodelpath="files/DBNModel/testmodel.mod";
		
		
		dbnt.DBNModelSave(dbnmodelpath, 7, 3, hl);
		
	}

}
