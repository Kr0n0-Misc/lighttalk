package com.startupeuropesummit.lighttalk.util;

import java.util.HashMap;

/**
 * Created by dmananes on 05/06/16.
 */
public class Translator {

    private static final String HEADER = "001100";

    private HashMap<String, String> englishBinMap;
    private HashMap<String, String> englishBinReverseMap;

    public Translator() {
        englishBinMap = new HashMap();
        englishBinMap.put("a", "000001");
        englishBinMap.put("b", "000010");
        englishBinMap.put("c", "000011");
        englishBinMap.put("d", "000100");
        englishBinMap.put("e", "000101");
        englishBinMap.put("f", "000110");
        englishBinMap.put("g", "000111");
        englishBinMap.put("h", "001000");
        englishBinMap.put("i", "001001");
        englishBinMap.put("j", "001010");
        englishBinMap.put("k", "001011");
        englishBinMap.put("l", "001100");
        englishBinMap.put("m", "001101");
        englishBinMap.put("n", "001110");
        englishBinMap.put("o", "001111");
        englishBinMap.put("p", "010000");
        englishBinMap.put("q", "010001");
        englishBinMap.put("r", "010010");
        englishBinMap.put("s", "010011");
        englishBinMap.put("t", "010100");
        englishBinMap.put("u", "010101");
        englishBinMap.put("v", "010110");
        englishBinMap.put("w", "010111");
        englishBinMap.put("x", "011000");
        englishBinMap.put("y", "011001");
        englishBinMap.put("z", "011010");
        englishBinMap.put("1", "110001");
        englishBinMap.put("2", "110010");
        englishBinMap.put("3", "110011");
        englishBinMap.put("4", "110100");
        englishBinMap.put("5", "110101");
        englishBinMap.put("6", "110110");
        englishBinMap.put("7", "110111");
        englishBinMap.put("8", "111000");
        englishBinMap.put("9", "111001");
        englishBinMap.put("0", "110000");
        englishBinMap.put(".", "101110");
        englishBinMap.put(",", "101100");
        englishBinMap.put("?", "111111");
        englishBinMap.put(" ", "000000");
        englishBinReverseMap = new HashMap();
        englishBinReverseMap.put("000001", "a");
        englishBinReverseMap.put("000010", "b");
        englishBinReverseMap.put("000011", "c");
        englishBinReverseMap.put("000100", "d");
        englishBinReverseMap.put("000101", "e");
        englishBinReverseMap.put("000110", "f");
        englishBinReverseMap.put("000111", "g");
        englishBinReverseMap.put("001000", "h");
        englishBinReverseMap.put("001001", "i");
        englishBinReverseMap.put("001010", "j");
        englishBinReverseMap.put("001011", "k");
        englishBinReverseMap.put("001100", "l");
        englishBinReverseMap.put("001101", "m");
        englishBinReverseMap.put("001110", "n");
        englishBinReverseMap.put("001111", "o");
        englishBinReverseMap.put("010000", "p");
        englishBinReverseMap.put("010001", "q");
        englishBinReverseMap.put("010010", "r");
        englishBinReverseMap.put("010011", "s");
        englishBinReverseMap.put("010100", "t");
        englishBinReverseMap.put("010101", "u");
        englishBinReverseMap.put("010110", "v");
        englishBinReverseMap.put("010111", "w");
        englishBinReverseMap.put("011000", "x");
        englishBinReverseMap.put("011001", "y");
        englishBinReverseMap.put("011010", "z");
        englishBinReverseMap.put("110001", "1");
        englishBinReverseMap.put("110010", "2");
        englishBinReverseMap.put("110011", "3");
        englishBinReverseMap.put("110100", "4");
        englishBinReverseMap.put("110101", "5");
        englishBinReverseMap.put("110110", "6");
        englishBinReverseMap.put("110111", "7");
        englishBinReverseMap.put("111000", "8");
        englishBinReverseMap.put("111001", "9");
        englishBinReverseMap.put("110000", "0");
        englishBinReverseMap.put("101110", ".");
        englishBinReverseMap.put("101100", ",");
        englishBinReverseMap.put("111111", "?");
        englishBinReverseMap.put("000000", " ");
    }

    public String englishToBin(String message) {
        String messageBin = HEADER;

        message = message.toLowerCase();
        for (int i=0; i<message.length(); i++) {
            String actualChar = new String(new char[]{message.charAt(i)});

            messageBin += englishBinMap.get(actualChar);
        }

        return messageBin;
    }

    public String binToEnglish(String message) {
        String messageBin = "";

        message = message.toLowerCase();

        int index = message.indexOf(HEADER);

        if (index != -1) {
            message = message.substring(index);

            for (int i = 0; i < message.length(); i += 6) {
                if (i + 6 < message.length()) {
                    String actualChar = new String(new char[]{message.charAt(i)});
                    actualChar += new String(new char[]{message.charAt(i + 1)});
                    actualChar += new String(new char[]{message.charAt(i + 2)});
                    actualChar += new String(new char[]{message.charAt(i + 3)});
                    actualChar += new String(new char[]{message.charAt(i + 4)});
                    actualChar += new String(new char[]{message.charAt(i + 5)});

                    String charCodified = englishBinReverseMap.get(actualChar);
                    if (charCodified != null && !charCodified.equals("null")) {
                        messageBin += charCodified;
                    }
                }
            }
        }

        return messageBin;
    }

}
