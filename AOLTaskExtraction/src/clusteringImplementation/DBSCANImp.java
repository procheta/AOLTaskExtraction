/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clusteringImplementation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

/**
 *
 * @author Procheta
 */
public class DBSCANImp {

    public void cluster() throws FileNotFoundException, IOException {
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(.5, 2);

        FileReader fr = new FileReader(new File("C:/Users/Procheta/Desktop/testFile.txt"));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        List<DoublePoint> points = new ArrayList<DoublePoint>();
        while (line != null) {
            String st[] = line.split("\t");
            String stj[] = st[0].split(",");
            double[] d = new double[200];
            for (int j = 0; j < stj.length; j++) {
                d[j] = Double.parseDouble(stj[j]);
            }
            line = br.readLine();
            points.add(new DoublePoint(d));
        }
        List<Cluster> l = (ArrayList) dbscan.cluster(points);
        System.out.println(l.size());
        for (int i = 0; i < l.size(); i++) {
            Cluster c = (Cluster) l.get(i);
            List<DoublePoint> point = c.getPoints();
            System.out.println(point.size());
            // ArrayList ll = dbscan.getNeighbours();
        }

    }

    public void createArff() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-task-processed.txt"));
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-task-processed.arff"));
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("@Relation AOL");
        bw.newLine();
        bw.write("@attribute Document string");
        bw.newLine();
       bw.write("@attribute class {");
        String line = br.readLine();
       // bw.write("1}");
        HashSet<String> classId = new HashSet<>();
        while (line != null) {
            String st[] = line.split("\t");
            classId.add(st[0] + st[1] + st[2]);
            line = br.readLine();
        }

        for (String s : classId) {
            bw.write(s);
            bw.write(",");
        }
        bw.write("}");
        bw.newLine();
        bw.write("@Data");
        bw.newLine();
        //String line = "";
        fr = new FileReader(new File("C:/Users/Procheta/Documents/ResearchData/AOLTaskData/all-task-processed.txt"));
        br = new BufferedReader(fr);
        line = br.readLine();
        while (line != null) {
            String st[] = line.split("\t");
               //bw.write('"' + st[4] + '"');
            bw.write('"' + st[4] + '"' + "," + st[0] + st[1] + st[2]);
            // bw.write('"' + st[4] + '"' + "," + 1);
            bw.newLine();
            line = br.readLine();
        }
        bw.close();
    }

    public static void main(String[] args) throws IOException {
        DBSCANImp dbi = new DBSCANImp();
        // dbi.cluster();
        dbi.createArff();
    }

}
