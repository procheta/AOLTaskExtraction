/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoltaskextraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Procheta
 */
public class AOLTaskExtraction {

    public void prepareTaskFile(String groundTruthFile, String taskFile) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(groundTruthFile));
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(new File(taskFile));
        BufferedWriter bw = new BufferedWriter(fw);
        String line = br.readLine();
        while (line != null) {
            String st[] = line.split("\t");
            
            
            String stArray[] = st[4].split("\\.");
            for(int i = 0; i < stArray.length - 1;i++)
                bw.write(stArray[i] + " ");
            
            bw.write(stArray[stArray.length -1] + "\t");
             
            String taskId = st[0] + st[1] + st[2];

            bw.write(taskId);
            bw.newLine();

            line = br.readLine();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        AOLTaskExtraction aolt = new  AOLTaskExtraction();
        //aolt.prepareTaskFile("C:/Users/Procheta/Downloads/aol-task-ground-truth.tar/aol-task-ground-truth/all-tasks.txt", "C:/Users/Procheta/Desktop/all-tasks.txt");
    }

}
