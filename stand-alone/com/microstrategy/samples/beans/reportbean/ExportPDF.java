package com.microstrategy.samples.beans.reportbean;

import java.io.FileOutputStream;
import java.io.IOException;

import com.microstrategy.samples.sessions.SessionManager;
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
    
    WebIServerSession session = SessionManager.getSessionWithDetails("APS-TSIEBLER-VM", "MicroStrategy Tutorial", "Administrator", "");
    
    String reportID = "F76698D44CC957971487B5A2E82956DF";
    try {
      System.out.println("Starting export workflow");
      // run the export workflow
      byte[] exportResults = exportReportToPDF(reportID, session);
      System.out.println("Saving export to file");

      // save the results to file
      FileOutputStream fos = new FileOutputStream("/Users/tsiebler/Documents/EclipseCodeSavedFiles/reportOrientationExport.pdf");
      fos.write(exportResults);
      fos.close();
      
      System.out.println("Export complete");
      
    } catch (IllegalArgumentException | WebObjectsException | WebBeanException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  public static byte[] exportReportToPDF(String reportID, WebIServerSession serverSession) throws IllegalArgumentException, WebObjectsException, WebBeanException {
    ReportBean rb = (ReportBean)BeanFactory.getInstance().newBean("ReportBean");
    
    // Id of the object we wish to execute
    rb.setObjectID(reportID);
    
    // session used for execution
    rb.setSessionInfo(serverSession);
    
    // type of execution
    rb.setExecutionMode(EnumWebReportExecutionModes.REPORT_MODE_PDF);
    
    /*
     * Set PDF orientation settings on the report instance, prior to the export itself
     */
    rb.getReportInstance().setProperty(EnumDSSXMLReportObjects.DssXmlReportObjectMainTemplate, OptionsHelper.PDFPropertySetName, OptionsHelper.Orientation, "" + EnumRWExportOrientation.RW_EXPORT_ORIENTATION_LANDSCAPE);
    rb.getReportInstance().setProperty(EnumDSSXMLReportObjects.DssXmlReportObjectMainTemplate, OptionsHelper.PDFPropertySetName, OptionsHelper.ReportPDFSettingsPresent,"1");

    /*
     * These changes need to be applied, after being set on the report, else they won't be used in the execution
     */
    rb.setApplyChangesOnCollectData(true); // Indicate we have changes to apply
    rb.collectData(); // Trigger report apply changes and then execution
    
    // return PDF export result as byte array
    return rb.getReportInstance().getPDFData();
  }

}
