package com.microstrategy.samples.beans.reportbean;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.samples.util.FileHelper;
import com.microstrategy.utils.cache.CacheException;
import com.microstrategy.utils.config.ConfigurationFilesCache;
import com.microstrategy.web.beans.*;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.transform.*;

public class TransformWithStyle {

  // The styleCatalog.xml can be found in the MicroStrategy Web folder structure
  private static String DEFAULT_STYLE_CATALOG_PATH = "/WEB-INF/xml/styleCatalog.xml";
  
  
  public static void main(String[] args) {
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // The GUID of the report to use
    String reportID = "F76698D44CC957971487B5A2E82956DF";
    String styleName = "ReportDataVisualizationXMLStyle";

    // Where to save the PDF
    String pathToSaveXML = "/Users/tsiebler/Documents/EclipseCodeSavedFiles/reportResults.xml";
    
    // Create our I-Server Session
    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    
    try {
      System.out.println("Starting workflow");
      
      // execute the dataset and render the results using an existing style
      MarkupOutput result = transformReportToStyle(reportID, styleName, session);
      
      System.out.println("Saving export to file");     
      
      // save the results to file
      FileHelper.saveMarkupOutputToFile(result, pathToSaveXML);
      System.out.println("Export complete");

    } catch (WebTransformException | WebBeanException | IllegalArgumentException | TransformDoesNotExistException
        | KeyDoesNotExistException | KeyAlreadyExistsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /*
   * This is an adaptation of KB43407, demonstrating how a ReportBean can be used to execute a report and then render the results using an existing transform.
   */
  public static MarkupOutput transformReportToStyle(String reportID, String styleName, WebIServerSession session) throws WebBeanException, WebTransformException, IllegalArgumentException, TransformDoesNotExistException, KeyDoesNotExistException, KeyAlreadyExistsException {
    WebBeanFactory factory = WebBeanFactory.getInstance();
    
    // Load the report into a ReportBean instance
    ReportBean rb = (ReportBean) factory.newBean("ReportBean");
    rb.setSessionInfo(session);
    rb.setObjectID(reportID);
    rb.setMaxWait(-1);

    // Load the transform definition from the style catalog, using the style name
    StyleCatalog styleCatalog = getStyleCatalog();
    Transform transform = styleCatalog.getTransformByStyle(styleName);
    
    // Add the transform reference to the ReportBean
    TransformInstance ti1 = rb.addTransform(transform);
    
    // Set the style name on the transform instance. Without this, we will see a null pointer exception while we try to render using this transform
    ti1.setKey(styleName);

    // Execute the dataset  
    rb.collectData();
    
    // Render the dataset result using the style & connected transform
    MarkupOutput out = rb.transform();
    
    // System.out.println("out: " + out.getCopyAsString());
    return out;
  }
  

  // The style catalog contains all definitions for styles and associated transforms & parameters
  public static StyleCatalog getStyleCatalog() {
    StyleCatalog styleCatalog = null;
    try {
      styleCatalog = (StyleCatalog) ConfigurationFilesCache.getConfiguration(DEFAULT_STYLE_CATALOG_PATH, StyleCatalogImpl.class);
    } catch (CacheException e) {
      System.out.println("Failed to load style catalog XML");
      e.printStackTrace();
    }
    return styleCatalog;
  }
  

}
