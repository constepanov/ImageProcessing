import color.Util;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        int[][] array = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        System.out.println(Arrays.toString(Util.flatten(array)));
    }
}
