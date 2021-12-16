package ru.kpfu.Arithmetic;

import java.io.IOException;
import java.util.Scanner;

public class Decode {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("Input a directory with coded file and setting ");
        String path = in.next();
        System.out.print("Input a result directory");
        String ans = in.next();
        Ar.decode(path, ans);
    }
}