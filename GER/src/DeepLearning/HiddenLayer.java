package DeepLearning;

import java.util.Random;
import static DeepLearning.utils.*;

public class HiddenLayer {
    public int N;
    public int n_in;
    public int n_out;
    public double[][] W;
    public double[] b;
    public Random rng;
//    public DoubleFunction<Double> activation;
//    public DoubleFunction<Double> dactivation;
    public String activation="";
    public String dactivation="";

    public HiddenLayer(int N, int n_in, int n_out, double[][] W, double[] b, Random rng, String activation) {
        this.N = N;
        this.n_in = n_in;
        this.n_out = n_out;

        if (rng == null) this.rng = new Random(1234);
        else this.rng = rng;

        if (W == null) {
            this.W = new double[n_out][n_in];
            double a = 1.0 / this.n_in;

            for(int i=0; i<n_out; i++) {
                for(int j=0; j<n_in; j++) {
                    this.W[i][j] = uniform(-a, a, rng);
                }
            }
        } else {
            this.W = W;
        }

        if (b == null) this.b = new double[n_out];
        else this.b = b;

        if (activation == "sigmoid" || activation == null) {
            this.activation = "sigmoid";
            this.dactivation = "dsigmoid";

        } else if (activation == "tanh") {
            this.activation = "tanh";
            this.dactivation = "dtanh";
        } else if (activation == "ReLU") {
            this.activation = "ReLU";
            this.dactivation = "dReLU";
        } else {
            throw new IllegalArgumentException("activation function not supported");
        }

    }

    public HiddenLayer() {
		// TODO Auto-generated constructor stub
    	super();
	}

	public double output(double[] input, double[] w, double b) {
        double linear_output = 0.0;
        for(int j=0; j<n_in; j++) {
            linear_output += w[j] * input[j];
        }
        linear_output += b;

        double result;
        if(activation=="sigmoid"){
        	result=sigmoid(linear_output);
        }else if(activation=="tanh"){
        	result=tanh(linear_output);
        }else if(activation=="ReLU"){
        	result=ReLU(linear_output);
        }else{
        	throw new IllegalArgumentException("activation function not supported");
        }
        
        return result;
    }


    public void forward(double[] input, double[] output) {
        for(int i=0; i<n_out; i++) {
            output[i] = this.output(input, W[i], b[i]);
        }
    }

    public void backward(double[] input, double[] dy, double[] prev_layer_input, double[] prev_layer_dy, double[][] prev_layer_W, double lr) {
        if(dy == null) dy = new double[n_out];

        int prev_n_in = n_out;
        int prev_n_out = prev_layer_dy.length;

        for(int i=0; i<prev_n_in; i++) {
            dy[i] = 0;
            for(int j=0; j<prev_n_out; j++) {
                dy[i] += prev_layer_dy[j] * prev_layer_W[j][i];
            }

            double result;
            if(dactivation=="dsigmoid"){
            	result=dsigmoid(prev_layer_input[i]);
            }else if(dactivation=="dtanh"){
            	result=dtanh(prev_layer_input[i]);
            }else if(dactivation=="dReLU"){
            	result=dReLU(prev_layer_input[i]);
            }else{
            	throw new IllegalArgumentException("dactivation function not supported");
            }    
            
            dy[i] *= result;
        }

        for(int i=0; i<n_out; i++) {
            for(int j=0; j<n_in; j++) {
                W[i][j] += lr * dy[i] * input[j] / N;
            }
            b[i] += lr * dy[i] / N;
        }
    }

    public int[] dropout(int size, double p, Random rng) {
        int[] mask = new int[size];

        for(int i=0; i<size; i++) {
            mask[i] = binomial(1, p, rng);
        }

        return mask;
    }
}
