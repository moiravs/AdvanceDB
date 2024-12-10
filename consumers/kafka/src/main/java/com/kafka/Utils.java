package com.flink;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    static HashSet<String> bannedUser = new HashSet<String>();
    public static void processMessage(String value, String[] bannedWords) throws IOException {
        Map<String, String> valueMap = new ObjectMapper().readValue(value, new TypeReference<Map<String, String>>() {
        });
        if (com.flink.Utils.checkUserIsBan(valueMap.get("user"))){
            System.out.println("\u001B[31m" +
                    valueMap.get("date") + " || " + valueMap.get("user") + ": User Banned"
                    + "\u001B[0m");
            return;
        }
        if (com.flink.Utils.checkWordBan(valueMap.get("text"), bannedWords)) {
            System.out.println("\u001B[31m" +
                    valueMap.get("date") + " || " + valueMap.get("user") + ": Message contains banned word"
                    + "\u001B[0m");
            return;
        }
        System.out.println(valueMap.get("date") + " || " + valueMap.get("user") + ": " + valueMap.get("text"));
    }

    public static boolean checkWordBan(String text, String[] bannedWords) {
        for (String word : bannedWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkUserIsBan(String user){
        return bannedUser.contains(user);
    }
}