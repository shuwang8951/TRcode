package DeepLearning;

import java.util.Random;
import static DeepLearning.utils.*;

public class DBN {
    public int N;
    public int n_ins;
    public int[] hidden_layer_sizes;
    public int n_outs;
    public int n_layers;
    public HiddenLayerDiscrete[] sigmoid_layers;
    public RBM[] rbm_layers;
    public LogisticRegressionDiscrete log_layer;
    public Random rng;


    /**
     * 
     * @param N 训练集个数
     * @param n_ins 输入层元素个数，也是维度
     * @param hidden_layer_sizes 每个隐含层的节点个数
     * @param n_outs 输出层元素个数
     * @param n_layers 隐含层层数
     * @param rng 初始随机矩阵
     */
    public DBN(int N, int n_ins, int[] hidden_layer_sizes, int n_outs, int n_layers, Random rng) {
        int input_size;

        this.N = N;
        this.n_ins = n_ins;
        this.hidden_layer_sizes = hidden_layer_sizes;
        this.n_outs = n_outs;
        this.n_layers = n_layers;

        this.sigmoid_layers = new HiddenLayerDiscrete[n_layers];
        this.rbm_layers = new RBM[n_layers];

        if(rng == null)	this.rng = new Random(1234);
        else this.rng = rng;

        // construct multi-layer
        for(int i=0; i<this.n_layers; i++) {
        	//输入层节点数判断（首次输入为输入数据节点数）
        	if(i == 0) {
            	input_size = this.n_ins;						
            } else {
                input_size = this.hidden_layer_sizes[i-1];
            }

            // construct sigmoid_layer
            this.sigmoid_layers[i] = new HiddenLayerDiscrete(this.N, input_size, this.hidden_layer_sizes[i], null, null, rng);

            // construct rbm_layer
            this.rbm_layers[i] = new RBM(this.N, input_size, this.hidden_layer_sizes[i], this.sigmoid_layers[i].W, this.sigmoid_layers[i].b, null, rng);
        }

        // layer for output using Logistic Regression
        this.log_layer = new LogisticRegressionDiscrete(this.N, this.hidden_layer_sizes[this.n_layers-1], this.n_outs);
    }

    /**
     * 
     * @param train_X 训练数据
     * @param lr CD中下降梯度值一般取0.1
     * @param k CD中取样次数一般为1
     * @param epochs 训练步数
     */
    public void pretrain(int[][] train_X, double lr, int k, int epochs) {
        int[] layer_input = new int[0];
        int prev_layer_input_size;
        int[] prev_layer_input;

        for(int i=0; i<n_layers; i++) {  // layer-wise
            for(int epoch=0; epoch<epochs; epoch++) {  // training epochs
                for(int n=0; n<N; n++) {  // input x1...xN
                    // layer input
                    for(int l=0; l<=i; l++) {

                        if(l == 0) {
                            layer_input = new int[n_ins];
                            for(int j=0; j<n_ins; j++) layer_input[j] = train_X[n][j];
                        } else {
                            if(l == 1) prev_layer_input_size = n_ins;
                            else prev_layer_input_size = hidden_layer_sizes[l-2];

                            prev_layer_input = new int[prev_layer_input_size];
                            for(int j=0; j<prev_layer_input_size; j++) prev_layer_input[j] = layer_input[j];

                            layer_input = new int[hidden_layer_sizes[l-1]];

                            sigmoid_layers[l-1].sample_h_given_v(prev_layer_input, layer_input);
                        }
                    }

                    rbm_layers[i].contrastive_divergence(layer_input, lr, k);
                }
                System.out.println("第"+(i+1)+"隐含层：第"+epoch+"次迭代完成~");
            }
            System.out.println("第"+(i+1)+"隐含层训练完毕！");
        }
    }

    /**
     * 
     * @param train_X 训练数据
     * @param train_Y 期望输出
     * @param lr CD下降梯度
     * @param epochs 微调步数
     */
    public void finetune(int[][] train_X, int[][] train_Y, double lr, int epochs) {
        int[] layer_input = new int[0];
        // int prev_layer_input_size;
        int[] prev_layer_input = new int[0];

        for(int epoch=0; epoch<epochs; epoch++) {
            for(int n=0; n<N; n++) {

                // layer input
                for(int i=0; i<n_layers; i++) {
                    if(i == 0) {
                        prev_layer_input = new int[n_ins];
                        for(int j=0; j<n_ins; j++) prev_layer_input[j] = train_X[n][j];
                    } else {
                        prev_layer_input = new int[hidden_layer_sizes[i-1]];
                        for(int j=0; j<hidden_layer_sizes[i-1]; j++) prev_layer_input[j] = layer_input[j];
                    }

                    layer_input = new int[hidden_layer_sizes[i]];
                    sigmoid_layers[i].sample_h_given_v(prev_layer_input, layer_input);
                }

                log_layer.train(layer_input, train_Y[n], lr);
            }
            System.out.println("第"+epoch+"次微调完成~");
            // lr *= 0.95;
        }
    }

    public void predict(int[] x, double[] y) {
    	System.out.println(x.length);
    	System.out.println(sigmoid_layers.length);
    	System.out.println(n_layers);
    	
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

        log_layer.softmax(y);
    }

    private static void test_dbn() {
        Random rng = new Random(123);

        double pretrain_lr = 0.5;
        int pretraining_epochs = 800;
        int k = 1;
        double finetune_lr = 0.1;
        int finetune_epochs = 800;

        int train_N = 6;
        int test_N = 4;
        int n_ins = 7;
        int n_outs = 2;
        /**
         * 每个隐含层节点数
         */
        int[] hidden_layer_sizes = {20,15,7,4};
        /**
         * RBM层数
         */
        int n_layers = hidden_layer_sizes.length;

        System.out.println(n_layers);
        
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
                {1, 0},
                {1, 0},
                {1, 0},
                {0, 1},
                {0, 1},
                {0, 1},
        };


        // construct DNN.DBN
        DBN dbn = new DBN(train_N, n_ins, hidden_layer_sizes, n_outs, n_layers, rng);

        // pretrain
        dbn.pretrain(train_X, pretrain_lr, k, pretraining_epochs);

        // finetune
        dbn.finetune(train_X, train_Y, finetune_lr, finetune_epochs);
        
        System.out.println(dbn.sigmoid_layers[0].W[0][0]);

        // test data
        int[][] test_X = {
                {1, 1, 0, 0, 0, 0,1},
                {1, 1, 1, 1, 0, 0,1},
                {0, 0, 0, 1, 1, 0,1},
                {0, 0, 1, 1, 1, 0,1},
        };

        double[][] test_Y = new double[test_N][n_outs];

        // test
        for(int i=0; i<test_N; i++) {
            dbn.predict(test_X[i], test_Y[i]);
            for(int j=0; j<n_outs; j++) {
                System.out.print(test_Y[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        test_dbn();
    }
}
