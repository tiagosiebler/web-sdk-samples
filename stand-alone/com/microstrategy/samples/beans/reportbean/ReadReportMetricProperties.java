package com.microstrategy.samples.beans.reportbean;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.beans.*;
import com.microstrategy.web.objects.*;
import com.microstrategy.webapi.*;

public class ReadReportMetricProperties {

  public static void main(String[] args) {
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // Create our I-Server Session
    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    
    // The GUID of the data mart report to use
    String reportID = "F76698D44CC957971487B5A2E82956DF";
    
    listProperties(reportID, session);
  }
  
  public static void listProperties(String reportID, WebIServerSession session) {

    ReportBean rb = (ReportBean)BeanFactory.getInstance().newBean("ReportBean");
    
    // Id of the object we wish to execute
    rb.setObjectID(reportID);
    
    // session used for execution
    rb.setSessionInfo(session);
    
    System.out.println("Loading report");
    
    try {
      rb.collectData();
      
      WebReportInstance ri = rb.getReportInstance();
      WebTemplate template = ri.getTemplate();
      WebTemplateMetrics wtms = template.getTemplateMetrics();
      
      ri.getReportManipulator().applyChanges();

      for (int i = 0;i < wtms.size();i++) {
        WebTemplateMetric wtm = wtms.get(i);
        System.out.println("Metric name: " + wtm.getName());
        
        String categoryName = "VLDB Select";
        String propertyName = "Metric Join Type";
        
        String property = wtm.getTemplateUnitPropertyValue(categoryName, propertyName);
        System.out.println("Metric Join Type value: " + property);
        // default = null = inner
        // inner = "0"
        // outer = "1"
        
        wtm.setTemplateUnitProperty(categoryName, propertyName, "1");
        wtm.setTemplateUnitProperty(categoryName, propertyName, "0");
        
        System.out.println("Metric Join Type value after edit: " + wtm.getTemplateUnitPropertyValue(categoryName, propertyName));
      }
      
      rb.directSave();
      
    } catch (WebBeanException | WebObjectsException | WebReportValidationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
