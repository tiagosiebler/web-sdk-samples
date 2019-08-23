package com.microstrategy.samples.beans.rwbean;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.samples.util.FileHelper;
import com.microstrategy.web.beans.*;
import com.microstrategy.web.objects.*;
import com.microstrategy.web.objects.rw.*;

public class DocumentSelectors {

  /*
   * This sample executes a document and changes a number of attribute element selectors in bulk. The results are saved to PDF for easy validation.
   */
  public static void main(String[] args) {
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // The GUID of the document to use : this one has 5 attribute element drop-down selectors
    String documentID = "5459E73D46BD5FD1F3DF899395632878";
    
    // Export the results to a PDF at this absolute path.
    String pathToSavePDF = "/Users/tsiebler/Desktop/eclipseFiles/documentWithSelectors.pdf";

    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    try {
      // Execute the report and generate the export
      System.out.println("Starting execution workflow");
      byte[] exportResults = changeSelectorsForDocument(documentID, session);
      
      // Save the export results to file
      FileHelper.saveByteArrayToFile(exportResults, pathToSavePDF);
      System.out.println("Saved PDF to file: " + pathToSavePDF);
      
    } catch (IllegalArgumentException | WebBeanException | WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /*
   * - Fetch a document using objectID and session
   * - loop through selectors and print name & key per selector
   * - change selection for each selector, to second element in list
   * - apply changes in bulk (pollStatus())
   * - export results to PDF
   */
  public static byte[] changeSelectorsForDocument(String documentID, WebIServerSession session) throws WebBeanException, WebObjectsException {
    RWBean rwb = (RWBean)BeanFactory.getInstance().newBean("RWBean");
    // ID of the object we wish to execute
    rwb.setObjectID(documentID);
    rwb.setSessionInfo(session);
    
    // set execution mode to PDF export
    rwb.setExecutionMode(EnumRWExecutionModes.RW_MODE_PDF);
   
    RWInstance rwInstance = rwb.getRWInstance();        
    System.out.println("Document Name: " + rwInstance.getDefinition().getName());
    rwInstance.setAsync(false);
    rwInstance.pollStatus();
    
    // From the definition of the document, find all grid/graph elements within
    RWDefinition rwd = rwInstance.getDefinition();
    RWManipulation rwm = rwInstance.getRWManipulator();
    
    // To get a list of attribute elements, we need to get data from the document, which means a first execution
    RWData data = rwInstance.getData();
    
    // Fetch all selector-type objects from this document
    RWUnitDef[] units = rwd.findUnitsByType(EnumRWUnitTypes.RWUNIT_CONTROL);
    
    System.out.println("-> Applying selections...");
    
    boolean setAllElements = false;
    if (setAllElements) {
      System.out.println("Setting all elements in the selector, except the first (since that's 'all'");
    } else {
      System.out.println("Setting only the second element in the selector");
    }
    
    for (int i = 0; i < units.length; i++) {
      // get the current selector
      RWControlDef unit = (RWControlDef)units[i];
      
      // name and key for current selector
      String selectorName = unit.getName();
      String selectorKey = unit.getKey();
      //System.out.println("selectorName: " + selectorName);
      //System.out.println("selectorKey: " + selectorKey);
      
      // Get the selector from the dataset
      List selectorsInDataset = data.findUnits(selectorKey);
      RWSelectorControlObject selectorInDataset = (RWSelectorControlObject) selectorsInDataset.get(0);
      
      // Get a list of elements for this selector in the dataset
      RWGroupByElements selectorElements = selectorInDataset.getElements();
      //System.out.println("-> elements in selector: " + selectorElements.size());

      // set the selector changes for this selector
      Vector listOfSelectionsForSelector = new Vector();
            
      if (setAllElements) {
        // If desired, loop through individual attribute elements in this element selector
        for (int y = 1; y < selectorElements.size();y++) {
          RWGroupByElement currentElement = selectorElements.get(y);
          //System.out.println("--> selectorElementName: " + currentElement.getDisplayName());
          //System.out.println("--> selectorElement.isCurrent(): " + currentElement.isCurrent());
          
          RWGroupByElement element = selectorElements.get(y);        
          listOfSelectionsForSelector.add(element.getID()); 
        }
      } else {
        RWGroupByElement secondElementInSelector = selectorElements.get(1);
        listOfSelectionsForSelector.add(secondElementInSelector.getID()); 
      }

      // we want to queue our selections together before applying them in one bulk operation
      boolean applyChangesImmediately = false;
      
      Date startDate = new Date();
      rwm.setCurrentControlElements(selectorKey, listOfSelectionsForSelector, applyChangesImmediately);
      Date endDate = new Date();
      
      long diffMs = endDate.getTime() - startDate.getTime();
      long diffSecs = diffMs / 1000 % 60;
      long diffMins = diffMs / (60 * 1000) % 60;
      
      System.out.println("--> " + selectorName + " : " + listOfSelectionsForSelector.size() + "/" + selectorElements.size() + " elements changed : time taken ------> " + diffMins + ":" + diffSecs + "." + diffMs);
    }
    
    System.out.println("-> finished applying selector changes, executing dataset");

    // Request messageId and execution status
    rwInstance.pollStatusOnly();
    
    System.out.println("-> returning PDF export");
    
    // return the PDF export of the result
    return rwInstance.getPDFData();
  }

}