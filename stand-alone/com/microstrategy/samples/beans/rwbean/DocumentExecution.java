package com.microstrategy.samples.beans.rwbean;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.samples.util.FileHelper;
import com.microstrategy.web.beans.*;
import com.microstrategy.web.objects.*;
import com.microstrategy.web.objects.rw.*;

public class DocumentExecution {

  public static void main(String[] args) {
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // The GUID of the document to use
    String documentID = "BD2CE5814CC6338817737396229F2302";

    // Where to save the PDF
    String pathToSavePDF = "/Users/ddechent/eclipse-workspace/SavedFiles/rwDocumentExport.pdf";

    // Create our I-Server Session
    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    
    try {
      // Execute the report and generate the export
      System.out.println("Starting execution workflow");
      
      byte[] exportResults = addDerivedMetricToDocument(documentID, session);
      
      // Save the export results to file
      System.out.println("Saving export to file");     
      FileHelper.saveByteArrayToFile(exportResults, pathToSavePDF);
      
      System.out.println("Export complete");
      
    } catch (IllegalArgumentException | WebBeanException | WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  // execute a document and return the PDF data
  public static byte[] exportDocumentToPDF(String documentID, WebIServerSession session) throws WebBeanException, WebObjectsException {
    RWBean rwb = (RWBean)BeanFactory.getInstance().newBean("RWBean");

    // ID of the object we wish to execute
    rwb.setObjectID(documentID);
    
    // session used for execution
    rwb.setSessionInfo(session);

    // set execution mode to PDF export
    rwb.setExecutionMode(EnumRWExecutionModes.RW_MODE_PDF);
   
    RWInstance rwInstance = rwb.getRWInstance();        
    System.out.println("Document Name: " + rwInstance.getDefinition().getName());
   
    rwInstance.setAsync(false);
    rwInstance.pollStatus();
    
    return rwInstance.getPDFData();
  }
  
  public static byte[] addDerivedMetricToDocument(String documentID, WebIServerSession session) throws WebBeanException, WebObjectsException {
    RWBean rwb = (RWBean)BeanFactory.getInstance().newBean("RWBean");

    // ID of the object we wish to execute
    rwb.setObjectID(documentID);
    
    // session used for execution
    rwb.setSessionInfo(session);

    // set execution mode to PDF export
    rwb.setExecutionMode(EnumRWExecutionModes.RW_MODE_PDF);
   
    RWInstance rwInstance = rwb.getRWInstance();        
    System.out.println("Document Name: " + rwInstance.getDefinition().getName());
   
    rwInstance.setAsync(false);
    rwInstance.pollStatus();
    
    // From the definition of the document, find all grid/graph elements within
    RWDefinition rwd = rwInstance.getDefinition();
    RWUnitDef[] rwUnits = rwd.findUnitsByType(EnumRWUnitTypes.RWUNIT_GRIDGRAPH);
    
    RWUnitDef rwUnit;
    RWGridGraphDef griddef = null;
   
    // loop through the grid graph elements and print more information for each
    for (int i = 0; i < rwUnits.length; i++) {
      rwUnit = rwUnits[i];
      griddef = (RWGridGraphDef) rwUnit;
      
      //For Debugging
      /*System.out.println("Grid graph getName: " + griddef.getName());
      System.out.println("Grid graph getKey: " + griddef.getKey());
      System.out.println("Grid graph getID: " + griddef.getID());
      */
    }
    
    if (griddef == null) return rwInstance.getPDFData();
 
    //Valid formulas include those than can be used to Create Derived Metrics within MicroStrategy Web / Developer
    String formula = "Avg(Profit)";
    int position = 0;
    String objectAlias = "My Derived Metric";
    
    
    RWManipulation rwm = rwInstance.getRWManipulator();
    
    // Just use the last grid/graph element key that we found looping through the RWGridGraphDef griddef
    rwm.addDerivedMetricToTemplate(griddef.getKey(), formula, position, objectAlias);
    //(String key, String formula, int position, String name)
    // https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/objects/rw/RWManipulation.html#addDerivedMetricToTemplate(java.lang.String,%20java.lang.String,%20int,%20java.lang.String)

    rwInstance.pollStatus(); 
    return rwInstance.getPDFData();
  }

}