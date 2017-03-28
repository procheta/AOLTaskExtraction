/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextTiltingExp;

import edu.northwestern.at.morphadorner.corpuslinguistics.textsegmenter.struct.RawText;
import edu.northwestern.at.morphadorner.corpuslinguistics.textsegmenter.texttiling.TextTiling;
import java.util.ArrayList;

/**
 *
 * @author Procheta
 */
public class TxtTlt {
    
    public void method(){
        ArrayList<ArrayList<String>> sentenceList = new ArrayList<ArrayList<String>>();
       ArrayList<String> s1 = new ArrayList<>();
       ArrayList<String> s2 = new ArrayList<>();
       ArrayList<String> s3 = new ArrayList<>();
       
       s1.add("To change this license header");
        s2.add("choose License Headers in Project Properties");
        s3.add("Templates   and open the template in the editor");
        sentenceList.add(s1);
       
        RawText rw = new RawText();
        rw.text = s1;
        
      // TextTiling tlt = new TextTiling("To change this license header, choose License Headers in Project Properties. To change this template file, choose Tools | Templates   and open the template in the editor.","");
    
    }
    
}
