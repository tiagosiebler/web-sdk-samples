package com.microstrategy.samples.beans.reportbean;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.samples.util.FileHelper;

import com.microstrategy.web.app.utils.OptionsHelper;
import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.ReportBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.EnumWebReportExecutionModes;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.rw.EnumRWExportOrientation;
import com.microstrategy.webapi.EnumDSSXMLReportObjects;


public class ExportPDF {

  public static void main(String[] args) {
    
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // The GUID of the report to use
    String reportID = "F76698D44CC957971487B5A2E82956DF";

    // Where to save the PDF
    String pathToSavePDF = "/Users/tsiebler/Documents/EclipseCodeSavedFiles/reportOrientationExport.pdf";
    
    // Create our I-Server Session
    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    
    try {
      // Execute the report and generate the export
      System.out.println("Starting export workflow");
      byte[] exportResults = exportReportToPDF(reportID, session);
      
      // Save the export results to file
      System.out.println("Saving export to file");     
      FileHelper.saveByteArrayToFile(exportResults, pathToSavePDF);
      
      System.out.println("Export complete");
    } catch (IllegalArgumentException | WebObjectsException | WebBeanException e) {
      e.printStackTrace();
    }
  }
  
  /*
   * This example defines a custom PDF orientation before exporting a report to PDF.
   */
  public static byte[] exportReportToPDF(String reportID, WebIServerSession serverSession) throws IllegalArgumentException, WebObjectsException, WebBeanException {
    ReportBean rb = (ReportBean)BeanFactory.getInstance().newBean("ReportBean");
    
    // Id of the object we wish to execute
    rb.setObjectID(reportID);
    
    // session used for execution
    rb.setSessionInfo(serverSession);
    
    // type of execution
    rb.setExecutionMode(EnumWebReportExecutionModes.REPORT_MODE_PDF);
    
    /*
     * A change in orientation requires a change in PDF paper height and width. This is required.
     */
    rb.getReportInstance().setProperty(EnumDSSXMLReportObjects.DssXmlReportObjectMainTemplate, OptionsHelper.PDFPropertySetName, OptionsHelper.PaperHeight, "8");
    rb.getReportInstance().setProperty(EnumDSSXMLReportObjects.DssXmlReportObjectMainTemplate, OptionsHelper.PDFPropertySetName, OptionsHelper.PaperWidth, "11.5");

    /*
     * Set PDF orientation settings on the report instance.
     */
    rb.getReportInstance().setProperty(EnumDSSXMLReportObjects.DssXmlReportObjectMainTemplate, OptionsHelper.PDFPropertySetName, OptionsHelper.Orientation, "" + EnumRWExportOrientation.RW_EXPORT_ORIENTATION_LANDSCAPE);
    
    /*
     * Specify that PDF settings are present in this workflow. This is required.
     */
    rb.getReportInstance().setProperty(EnumDSSXMLReportObjects.DssXmlReportObjectMainTemplate, OptionsHelper.PDFPropertySetName, OptionsHelper.ReportPDFSettingsPresent,"1");

    /*
     * Lastly, these changes need to be applied, after being set on the report, else they won't be used in the execution
     */
    rb.setApplyChangesOnCollectData(true); // Indicate we have changes to apply
    rb.collectData(); // Trigger report apply changes and then execution
    
    // return PDF export result as byte array
    return rb.getReportInstance().getPDFData();
  }

}
