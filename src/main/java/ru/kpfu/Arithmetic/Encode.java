package ru.kpfu.Arithmetic;

import java.io.IOException;
import java.util.Scanner;

public class Encode {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("Input a file path ");
        String path = in.next();
        System.out.print("Input a result directory");
        String ans = in.next();
        Ar.encode(path, ans);
    }
}