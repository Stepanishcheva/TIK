package ru.kpfu.LZW;

import ru.kpfu.Haffmen.Haffmen;

import java.io.IOException;
import java.util.Scanner;

public class Encode {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("Input a file path ");
        String path = in.next();
        System.out.print("Input a result directory");
        String ans = in.next();
        LZW.encode(path, ans);
    }
}
