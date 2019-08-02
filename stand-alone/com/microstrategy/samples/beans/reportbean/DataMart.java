package com.microstrategy.samples.beans.reportbean;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.ReportBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.webapi.EnumDSSXMLExecutionFlags;

public class DataMart {

  public static void main(String[] args) {
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // Create our I-Server Session
    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    
    // The GUID of the data mart report to use
    String reportID = "94FA0D694A2AE80676D21384BEF54BF8";
    
    executeDataMart(reportID, session);
  }
  
  // Simple example to execute a data mart report, without any prompt requirements
  public static void executeDataMart(String reportID, WebIServerSession session) {
    ReportBean rb = (ReportBean)BeanFactory.getInstance().newBean("ReportBean");
    
    // Id of the object we wish to execute
    rb.setObjectID(reportID);
    
    // session used for execution
    rb.setSessionInfo(session);
    
    System.out.println("Executing Data Mart Report");
    
    // sets the execution flag to indicate that datamart should be executed
    rb.setExecutionFlags(EnumDSSXMLExecutionFlags.DssXmlExecutionGenerateDatamart); 
    
    try {
      rb.collectData();
      
      /*
       * 3 = successful
       * 6 = waiting for user input
       * https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/beans/EnumRequestStatus.html
       */
      System.out.println("Execution complete! XML status: " + rb.getXMLStatus());
      
    } catch (WebBeanException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
