/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbscanClustering;

/**
 *
 * @author Procheta
 */
public interface DataPoint {
    	public double distance(DataPoint datapoint);
	
	public void setCluster(int id);
	
	public int getCluster();
	
	public int getX();
	
	public int getY();
    
}
