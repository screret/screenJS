package screret.screenjs;

public class MenuUtils {

    public static int progress(int max, int current, int length) {
        return max != 0 && current != 0 ? current * length / max : 0;
    }
}
