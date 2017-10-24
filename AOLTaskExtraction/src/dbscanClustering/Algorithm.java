/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbscanClustering;

import java.util.List;

/**
 *
 * @author Procheta
 */
public interface Algorithm {

    public void setPoints(List points);

    public void cluster();

}
