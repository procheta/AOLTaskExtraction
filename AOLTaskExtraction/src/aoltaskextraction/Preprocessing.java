/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoltaskextraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Procheta
 */
public class Preprocessing {
    
    public void prepareARFFFile(String inputFile, String outputFile) throws IOException
    {
    
        FileWriter fw = new FileWriter(new File(outputFile));
        BufferedWriter bw = new BufferedWriter(fw);
        
        bw.write("@RELATION cluster");
        bw.newLine();
        for(int i = 0; i < 200; i++)
        {
            bw.write("@ATTRIBUTE ");
            bw.write("column" + i);
            bw.write("\tREAL");
            bw.newLine();
        }
        bw.write("@ATTRIBUTE class\t {");
        for(int i = 0; i < 406; i++)
            bw.write("class_"+i+",");
        
        bw.write("class_"+"406}");
        bw.newLine();
        bw.write("@DATA");
        bw.newLine();
        
        FileReader fr = new FileReader(new File(inputFile));
        BufferedReader br = new BufferedReader(fr);
        
        HashMap<String,Integer> classList = new HashMap<String,Integer>();
        
        String line = br.readLine();
        int count = 0;
        while(line != null)
        {
            String st[] = line.split("\t");
            if(!classList.containsKey(st[1]))
                classList.put(st[1], count++);
            Integer it = classList.get(st[1]);
            bw.write(st[0]+ "class_"+ it.toString() );
            bw.newLine();        
            line = br.readLine();
        }
         bw.close();
         System.out.println(count);
    
    }
    
    public void prepareClutoFile(String inputFile, String outputFile) throws IOException
    {
    
        FileWriter fw = new FileWriter(new File(outputFile));
        BufferedWriter bw = new BufferedWriter(fw);
        
        
        
        FileReader fr = new FileReader(new File(inputFile));
        BufferedReader br = new BufferedReader(fr);
        
        HashMap<String,Integer> classList = new HashMap<String,Integer>();
        
        String line = br.readLine();
        int count = 0;
        bw.write("997 200");
        bw.newLine();
        while(line != null)
        {
            String st[] = line.split("\t");
            String stj[] = st[0].split(",");
            for(int j = 0; j < stj.length - 1;j++)
                bw.write(stj[j] + " ");
            bw.write(stj[st.length - 1]);
            bw.newLine();      
            line = br.readLine();
        }
         bw.close();
         //System.out.println(count);
    
    }
    
    public static void main(String[] args) throws IOException
    {
        Preprocessing pr = new Preprocessing();
       // pr.prepareARFFFile("C:/Users/Procheta/Desktop/testFile.txt", "C:/Users/Procheta/Desktop/newFile.arff");
        pr.prepareClutoFile("C:/Users/Procheta/Desktop/testFile.txt", "C:/Users/Procheta/Desktop/matrix.mat");
    
        
    }
    
}
