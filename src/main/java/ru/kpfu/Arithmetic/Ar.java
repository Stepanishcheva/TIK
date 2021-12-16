package ru.kpfu.Arithmetic;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Ar {
    public static Map <Character,Part> encode(String fileName, String out){
        String text = readText(fileName);
        int size = text.length();
        Map<Character, Integer> chars = countUsages(text);
        Map <Character,Part> def = generate(size,chars);
        Part now = new Part(new BigDecimal(Double.toString(0)),new BigDecimal(Double.toString(1.0)));
        char c;
        for (int i=0;i<text.length();i++){
            c= text.charAt(i);
            BigDecimal h =now.low.add((now.high.subtract(now.low)).multiply(def.get(c).getHigh()));
            BigDecimal l =now.low.add((now.high.subtract(now.low)).multiply(def.get(c).getLow()));
            now.setHigh(h);
            now.setLow(l);
        }
        saveToArFile(out, chars, size, now.low.toString());
        return def;

    }
    //Генерирует мапу символ-границы
    public static Map <Character,Part> generate(int textLength, Map<Character, Integer> chars){
        Map <Character,Part> def = new HashMap<>();
        double end =0.0;
        double thisEnd;
        for (char c: chars.keySet()){
            thisEnd=end+chars.get(c)/(1.0*textLength);
            def.put(c, new Part(new BigDecimal(Double.toString(end)),new BigDecimal(Double.toString(thisEnd))));
            end=thisEnd;
        }
        return def;
    }
    public static void decode(String in, String out) throws IOException {

        Map<Character, Integer> chars = new HashMap<>();
        int dataSize;
        String str;
        dataSize= getFromArFileSet(in,chars);
        str=getFromArFile(in);
        str=str.trim();
        BigDecimal code= new BigDecimal(str);
        StringBuilder strb = new StringBuilder();
        Map <Character,Part> def = generate(dataSize,chars);

        for (int i=0;i<dataSize;i++){
            for (char c:def.keySet()) {
                if (((code.compareTo(def.get(c).low))>=0) &&((code.compareTo(def.get(c).high)<0))){
                   strb.append(c);
                   code = (code.subtract(def.get(c).getLow())).divide(def.get(c).getHigh().subtract(def.get(c).getLow()));
                   break;
                }

            }
        }
        Files.write(Paths.get((out+"\\decodedAr.txt")), strb.toString().getBytes());
    }
    private static int getFromArFileSet(String in, Map<Character, Integer> dictionary) {
        try {
            File input = new File(in+"\\setting.arithmetic");
            DataInputStream os = new DataInputStream(new FileInputStream(input));
            int sizeChars = os.readInt();
            for (int i = 0; i < sizeChars; i++) {
                dictionary.put(os.readChar(), os.readInt());
            }
            int dataSize = os.readInt();
            os.close();
            return dataSize;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private static String getFromArFile(String in) {
        String str;
        String inputCode =in+"\\compressed.arithmetic";
        str = readText(inputCode);
        return str;
    }

    private static void saveToArFile(String output, Map<Character, Integer> chars, int size, String code) {
        try {
            File file = new File(output+"\\setting.arithmetic");
            DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
            os.writeInt(chars.size());
            for (Character character: chars.keySet()) {
                os.writeChar(character);
                os.writeInt(chars.get(character));
            }
            os.writeInt(size);
            os.flush();
            os.close();

            try(FileWriter writer = new FileWriter(output+"\\compressed.arithmetic", false))
            {
                writer.write(code);
                writer.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Map<Character, Integer> countUsages(String text) {
        Map<Character, Integer> chars = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            Character c = text.charAt(i);
            Integer count = chars.get(c);
            chars.put(c, count != null ? count + 1 : 1);
        }
        return chars;
    }
    public static class Part{
        BigDecimal low;
        BigDecimal high;

        public Part(BigDecimal low, BigDecimal high) {
            this.low = low;
            this.high = high;
        }

        public BigDecimal getLow() {
            return low;
        }

        public void setLow(BigDecimal low) {
            this.low = low;
        }

        public BigDecimal getHigh() {
            return high;
        }

        public void setHigh(BigDecimal high) {
            this.high = high;
        }

        @Override
        public String toString() {
            return "Part{" +
                    "low=" + low +
                    ", high=" + high +
                    '}';
        }
    }
    public static String readText(String FileName)
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
}
