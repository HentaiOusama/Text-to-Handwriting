public class TestClass {
    public static void main(String[] args) {
        int min = 0;
        int max = 0;
        for (int i = 0; i < 150; i++) {
            int num = (int) ((Math.random() * 8) - 4);
            if (min > num) {
                min = num;
            }
            if (max < num) {
                max = num;
            }
        }
        System.out.println("Min : " + min + ", Max : " + max);
    }
}
