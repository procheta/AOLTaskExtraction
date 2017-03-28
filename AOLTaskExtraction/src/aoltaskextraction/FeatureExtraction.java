/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoltaskextraction;

import java.util.ArrayList;

/**
 *
 * @author Procheta
 */
public class FeatureExtraction {

    public String findCharNgram(String str, int n) {
        String word[] = str.split(" ");

        ArrayList charArray = new ArrayList<Character>();
        for (int i = 0; i < word.length; i++) {
            char cArray[] = word[i].toCharArray();
            for (int j = 0; j < cArray.length; j++) {
                charArray.add(cArray[j]);
            }

        }

        ArrayList ngram = new ArrayList<String>();
        for (int i = 0; i < charArray.size() - 3; i++) {
            String token = charArray.get(i).toString() + charArray.get(i + 1).toString() + charArray.get(i + 2).toString();
            ngram.add(token);
        }

        return null;
    }

}
