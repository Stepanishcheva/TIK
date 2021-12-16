package ru.kpfu.Hamming;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HammingCoder {
    public static String stringToBits(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length << 1];
        CharBuffer cBuffer = ByteBuffer.wrap(b).asCharBuffer();
        for(int i = 0; i < buffer.length; i++) {
            cBuffer.put(buffer[i]);
        }
        StringBuilder sb= new StringBuilder();
        for (int i=0; i<b.length;i++){
            sb.append(String.format("%8s", Integer.toBinaryString(b[i])).replace(' ', '0'));
        }
        return sb.toString();

    }

    public static String bitsToString(String textInBits) {
        byte[] bytes = new byte[textInBits.length()/8];
        String part;
        for(int i=0;i<(textInBits.length()/8);i++){
            part = textInBits.substring(i*8,(i+1)*8);
            bytes[i]= (byte)(int)Integer.valueOf(part, 2);
        }
        CharBuffer cBuffer = ByteBuffer.wrap(bytes).asCharBuffer();
        return cBuffer.toString();
    }

    public static void encodeText(String source, String resultDirectory) {
        String text = readFile(source);
        String bits = stringToBits(text);
        saveToFile(resultDirectory + "\\encoded.hamming",encode(bits));

    }
    public static void decodeText(String source, String resultDirectory) throws IOException {
        String text = getFromHammingFile(source);
        String ans;
        ans=decode(text);
        String res = bitsToString(ans);
        saveToFile(resultDirectory + "\\decodedHamming.txt", res);

    }
    private static String encode(String in) {
        int charBlock = 4;//количество исходных символов в блоке
        int resBlockLength = 7;
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < in.length() / charBlock; i++) {
            String s2= in.substring(4*i, (i+1)*4);
            char[] c = s2.toCharArray();
            int[] start=new int[4];
            for (int k=0;k<4;k++){
                start[k] = c[k]-'0';
            }
            int[] resBlock = new int[resBlockLength];
            resBlock[0] =(start[0]+start[1]+start[3])%2;
            resBlock[1] =(start[0]+start[2]+start[3])%2;
            resBlock[2] = start[0];
            resBlock[3] =(start[1]+start[2]+start[3])%2;
            resBlock[4] = start[1];
            resBlock[5] = start[2];
            resBlock[6] = start[3];
            for (int k=0;k<7;k++){
                res.append(resBlock[k]);
            }
        }
        return res.toString();
    }

    private static String decode(String in) {
        int charBlock = 7;//количество исходных символов в блоке
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < in.length() / charBlock; i++) {
            String s2 = in.substring(7 * i, (i + 1) * 7);
            char[] c = s2.toCharArray();
            int[] start = new int[7];
            for (int k = 0; k < 7; k++) {
                start[k] = c[k] - '0';
            }
            int p1 = (start[2] + start[4] + start[6]) % 2;
            int p2 = (start[2] + start[5] + start[6]) % 2;
            int p3 = (start[5] + start[4] + start[6]) % 2;
            if (p1 == start[0]) {
                if ((p2 != start[1]) && (p3 != start[3])) {
                    start[5] = (start[5] == 1) ? 0 : 1;
                }
            } else {
                if (p2 == start[1]) {
                    if (p3 != start[3]) {
                        start[4] = (start[4] == 1) ? 0 : 1;
                    }
                }else {
                    if  (p3 == start[3]) {
                        start[2] = (start[2] == 1) ? 0 : 1;
                    } else {
                        start[6] = (start[6] == 1) ? 0 : 1;
                    }
                }
            }
            res.append(start[2]);
            res.append(start[4]);
            res.append(start[5]);
            res.append(start[6]);
        }
        return res.toString();
    }
    private static String readFile(String FileName)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            BufferedReader input = new BufferedReader(new FileReader( new File(FileName).getAbsoluteFile()));
            try{
                String s;
                while ((s=input.readLine())!=null) {
                    sb.append(s);
                    sb.append("\n");
                }
            }
            finally {
                input.close();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    private static void saveToFile(String file,String bits) {
        try(FileWriter writer = new FileWriter(file, false))
        {
            writer.write(bits);
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    private static String getFromHammingFile(String input) throws IOException {
        return new String(Files.readAllBytes(Paths.get(input)));
    }
}
