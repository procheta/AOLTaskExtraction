/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clusteringImplementation;

import weka.core.Instances;
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.EM;
import weka.clusterers.ClusterEvaluation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import weka.clusterers.SimpleKMeans;

/**
 *
 * @author Procheta
 */
public class KMeans {

    public void cluster() throws FileNotFoundException, IOException, Exception {

        Instances data;
        data = new Instances(new BufferedReader(new FileReader("C:/Users/Procheta/Desktop/newFile.arff")));

        String[] options = new String[6];
        options[0] = "-t";
        options[1] = "C:/Users/Procheta/Desktop/newFile.arff";
        options[2] = "-N";
        options[3] = "10";
        options[4] = "-c";
        options[5] = "last";
        
        SimpleKMeans   cl;    
        cl   = new SimpleKMeans() ;
        //cl.setOptions(options);
        cl.buildClusterer(data);
        System.out.println(ClusterEvaluation.evaluateClusterer(cl, options));
    }
    
    public static void main(String[] args) throws Exception
    {
    
        KMeans dbm = new KMeans();
        dbm.cluster();
    
    }

}
