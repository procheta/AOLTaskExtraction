/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbscanClustering;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Procheta
 */
public class Cluster {
        public List points;
	public DataPoint centroid;
	public int id;
	
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList();
		this.centroid = null;
	}
 
	public List getPoints() {
		return points;
	}
	
	public void addPoint(DataPoint point) {
		points.add(point);
		point.setCluster(id);
	}
 
	public void setPoints(List points) {
		this.points = points;
	}
 
	public DataPoint getCentroid() {
		return centroid;
	}
 
	public void setCentroid(DataPoint centroid) {
		this.centroid = centroid;
	}
 
	public int getId() {
		return id;
	}
	
	public void clear() {
		points.clear();
	}
	
	public void plotCluster() {
		System.out.println("[Cluster: " + id+"]");
		System.out.println("[Centroid: " + centroid + "]");
		System.out.println("[Points: \n");
		for(Object p : points) {
			System.out.println(p);
		}
		System.out.println("]");
	}
    
}
