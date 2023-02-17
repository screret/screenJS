package screret.screenjs;

public class MenuUtils {

    public static int progress(int max, int current, int width) {
        return max * width / current;
    }
}
