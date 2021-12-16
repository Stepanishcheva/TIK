package ru.kpfu.Haffmen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Haffmen {
    public static void encodeMain(String filename, String outputDirectory){
        String text = readFile(filename);
        TreeMap<Character, Integer> chars = countUsages(text);
        Queue<TreeNode> nodesQueue = new PriorityQueue<>(Collections.reverseOrder());
        for(Character c: chars.keySet()) {
            nodesQueue.add(new TreeNode(c, chars.get(c)));
        }
        TreeNode haffmenTree = createHaffmenTree(nodesQueue);

        TreeMap<Character, String> codes = new TreeMap<>();
        for(Character c: chars.keySet()) {
            codes.put(c, haffmenTree.getCharCode(c, ""));
        }
        StringBuilder encodingResult = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            encodingResult.append(codes.get(text.charAt(i)));
        }
        File file = new File((outputDirectory+"\\compressed.haffmen"));
        saveToFile(file, chars, encodingResult.toString());
    }

    public static void decodeMain(String filename, String outputDirectory) throws IOException {
        File file = new File(filename);
        TreeMap<Character, Integer>  chars = new TreeMap<>();
        StringBuilder encoded = new StringBuilder();
        Queue<TreeNode> charsQueue = new PriorityQueue<>(Collections.reverseOrder());

        getFromHaffmenFile(file, chars, encoded);

        for(Character c: chars.keySet()) {
            charsQueue.add(new TreeNode(c, chars.get(c)));
        }
        TreeNode haffmenTree = createHaffmenTree(charsQueue);
        String decodedH = decodeByTree(encoded.toString(), haffmenTree);

        Files.write(Paths.get((outputDirectory+"\\decoded.txt")), decodedH.getBytes());
    }

    private static TreeMap<Character, Integer> countUsages(String text) {
        TreeMap<Character, Integer> freqMap = new TreeMap<>();
        for (int i = 0; i < text.length(); i++) {
            Character c = text.charAt(i);
            Integer count = freqMap.get(c);
            freqMap.put(c, count != null ? count + 1 : 1);
        }
        return freqMap;
    }

    private static TreeNode createHaffmenTree(Queue<TreeNode> codeTreeNodes) {
        while (codeTreeNodes.size() > 1) {
            TreeNode left = codeTreeNodes.poll();
            TreeNode right = codeTreeNodes.poll();
            TreeNode parent = new TreeNode(null, right.weight + left.weight, left, right);
            codeTreeNodes.add(parent);
        }
        return  codeTreeNodes.poll();
    }
    private static String decodeByTree(String encoded, TreeNode tree) {
        StringBuilder decoded = new StringBuilder();
        TreeNode node = tree;
        for (int i = 0; i < encoded.length(); i++) {
            node = encoded.charAt(i) == '0' ? node.left : node.right;
            if (node.content != null) {
                decoded.append(node.content);
                node = tree;
            }
        }
        return decoded.toString();
    }


    // сохранение таблицы частот и сжатой информации в файл
    private static void saveToFile(File output, Map<Character, Integer> frequencies, String bits) {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(output));
            os.writeInt(frequencies.size());
            for (Character character: frequencies.keySet()) {
                os.writeChar(character);
                os.writeInt(frequencies.get(character));
            }
            int compressedSizeBits = bits.length();
            BitArray bitArray = new BitArray(compressedSizeBits);
            for (int i = 0; i < bits.length(); i++) {
                bitArray.set(i, bits.charAt(i) != '0' ? 1 : 0);
            }
            os.writeInt(compressedSizeBits);
            os.write(bitArray.bytes, 0, bitArray.getSizeInBytes());
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // загрузка сжатой информации и таблицы частот из файла
    private static void getFromHaffmenFile(File input, Map<Character, Integer> frequencies, StringBuilder bits) {
        try {
            DataInputStream os = new DataInputStream(new FileInputStream(input));
            int frequencyTableSize = os.readInt();
            for (int i = 0; i < frequencyTableSize; i++) {
                frequencies.put(os.readChar(), os.readInt());
            }
            int dataSizeBits = os.readInt();
            BitArray bitArray = new BitArray(dataSizeBits);
            os.read(bitArray.bytes, 0, bitArray.getSizeInBytes());
            os.close();

            for (int i = 0; i < bitArray.size; i++) {
                bits.append(bitArray.get(i) != 0 ? "1" : 0);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String FileName)
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
