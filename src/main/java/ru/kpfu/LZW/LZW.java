package ru.kpfu.LZW;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LZW {


    public static void encode(String in, String out){
        int dictSize = -1;
        String text = readFile(in);
        Map<String,Integer> dictionary = new HashMap<>();
        Map<Character,Integer> baseDictionary = new HashMap<>();
        List<Character> list = new ArrayList<>();
        for (char character :text.toCharArray()) {
            if (!list.contains(character)){
                list.add(character);
            }
        }
        for (int i=0;i< list.size();i++){
            baseDictionary.put(list.get(i),i);
            dictionary.put(Character.toString(list.get(i)),i);
        }
        dictSize = list.size();
        String foundChars ="";
        List<Integer> result = new ArrayList<>();
        for (char character :text.toCharArray()){
            String charsToAdd = foundChars+character;
            if (dictionary.containsKey(charsToAdd)){
                foundChars = charsToAdd;
            }
            else {
                result.add(dictionary.get(foundChars));
                dictionary.put(charsToAdd, dictSize++);
                foundChars = String.valueOf(character);
            }
        }
        if (!foundChars.isEmpty()){
            result.add(dictionary.get(foundChars));
        }
        File file = new File(out+"\\compressed.lzw");
        saveToFile(file,baseDictionary,result);

    }
    private static void saveToFile(File output, Map<Character, Integer> dictionary, List<Integer> codes) {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(output));
            os.writeInt(dictionary.size());
            for (Character character: dictionary.keySet()) {
                os.writeChar(character);
                os.writeInt(dictionary.get(character));
            }
            os.writeInt(codes.size());
            for (int i = 0; i < codes.size(); i++) {
                os.writeInt(codes.get(i));
            }
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void getFromLZWFile(File input, Map<String, Integer> dictionary, List<Integer> list) {
        try {
            DataInputStream os = new DataInputStream(new FileInputStream(input));
            int size = os.readInt();
            for (int i = 0; i < size; i++) {
                dictionary.put(Character.toString(os.readChar()), os.readInt());
            }
            int dataSize = os.readInt();
            for (int i=0;i<dataSize;i++){
                list.add(os.readInt());
            }
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decode(String in, String out) throws IOException {
        File input =new File(in);
        Map<String,Integer > dictionaryOld = new HashMap<>();
        List<Integer> encodedText=new ArrayList<>();
        getFromLZWFile(input,dictionaryOld, encodedText);

        Map<Integer, String > dictionary=new HashMap<>();
        for (Map.Entry<String, Integer> pair: dictionaryOld.entrySet())
        {
            dictionary.put(pair.getValue(), pair.getKey());
        }
        int dictSize= dictionary.size();
        String characters = dictionary.get(encodedText.get(0));
        StringBuilder result = new StringBuilder(characters.toString());
        encodedText.remove(0);

        for (int code: encodedText){
            String entry= dictionary.containsKey(code)
                    ?dictionary.get(code):
                    characters +characters.charAt(0);
            result.append(entry);
            dictionary.put(dictSize++, characters+entry.charAt(0));
            characters=entry;

        }

        Files.write(Paths.get((out+"\\decodedLZW.txt")), result.toString().getBytes());
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