public class Test {
    public Test(){
    }

    public static int sum(int var0) {
        int var2 = 0;
        for (int var1 = 1; var1 <= var0; var1++) {
            var2 = var2 + var1;
        }
        return var2;
    }
    public static void main(String[] var0){
        int var2 = 100;
        System.out.println(sum(var2));
    }
}
