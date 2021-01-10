public class Tests {














    public static void main(String[] args) {

        double test = (((Math.atan2(59, 90) / 2) / Math.PI) * 360 + 360) % 360;
        double test2 = Math.toDegrees(Math.atan2(59, 90));

        System.out.println(test);
        System.out.println(test2);
    }
}
