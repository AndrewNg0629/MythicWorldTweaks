package online.andrew2007.mythic;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                try {
                    int s1 = scanner.nextInt();
                    String s2;
                    if (s1 < 100000) {
                        s2 = String.valueOf(s1);
                    } else if (s1 < 1000000) {
                        s2 = processDouble((double) s1 / 1000) + "k";
                    } else if (s1 < 1000000000) {
                        s2 = processDouble((double) s1 / 1000000) + "M";
                    } else {
                        s2 = processDouble((double) s1 / 1000000000) + "G";
                    }
                    System.out.println(s2);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
    public static String processDouble(double num) {
        String string = String.valueOf(num).substring(0, 3);
        if (string.endsWith(".")) {
            string = string.substring(0, 2);
        }
        return string;
    }
}
