package filter;

public class ConvolutionMask {
    public static final double[][] laplacianMask = {
            {0, -1, 0},
            {-1, 4, -1},
            {0, -1, 0}
    };

    public static double[][] highBoostMask(double alpha) {
        return new double[][] {
                {0, -1, 0},
                {-1, alpha + 4, -1},
                {0, -1, 0}
        };
    }
}
