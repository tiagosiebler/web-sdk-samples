package com.microstrategy.samples.sessions;

import com.microstrategy.web.objects.*;
import com.microstrategy.webapi.*;

public class SessionManager {
  public static WebIServerSession serverSession = null;

  private static String DEFAULT_SERVER = "APS-TSIEBLER-VM";
  private static String DEFAULT_PROJECT = "MicroStrategy Tutorial";
  private static String DEFAULT_USERNAME = "Administrator";
  private static String DEFAULT_PASSWORD = "";

  private static WebObjectsFactory factory = null;

  public static void main(String[] args) {
    WebIServerSession mySession = getSessionWithDetails("APS-TSIEBLER-VM", "MicroStrategy Tutorial", "Administrator", "");
  }
  
  // this is a basic session tied to a project
  public static WebIServerSession getSession() {
    return getSessionWithDetails(DEFAULT_SERVER, DEFAULT_PROJECT, DEFAULT_USERNAME, DEFAULT_PASSWORD);
  }

  // this is a configuration session, it's not tied to any project
  public static WebIServerSession getSessionWithoutProject() {
    return getSessionWithDetails(DEFAULT_SERVER, "", DEFAULT_USERNAME, DEFAULT_PASSWORD);
  }

  // close the stored session
  public static void closeSession() {
    closeSession(serverSession);
  }
  
  // close the session passed as a parameter
  public static void closeSession(WebIServerSession serverSession) {
    if (!serverSession.isActive()){
      System.out.println("_closeSession - Session already closed");
      return;
    }

    try {
      serverSession.closeSession();
    } catch (WebObjectsException ex) {
      System.out.println("\nError closing session:" + ex.getMessage());
      return;
    }
    System.out.println("_closeSession - Session closed");

    serverSession = null;
  }

  public static WebIServerSession getSessionWithDetails(String serverName,String projectName, String login, String password, int port){
    if (serverSession == null) {
      //create factory object
      factory = WebObjectsFactory.getInstance();
      serverSession = factory.getIServerSession();
      //Set up session properties
      serverSession.setServerName(serverName); //Should be replaced with the name of an Intelligence Server // aps-tsiebler-vm

      if (port != 0) serverSession.setServerPort(port);
      if(projectName != null) serverSession.setProjectName(projectName); //Project where the session should be created

      serverSession.setLogin(login); //User ID
      serverSession.setPassword(password); //Password

      serverSession.setApplicationType(EnumDSSXMLApplicationType.DssXmlApplicationCustomApp);

      //Initialize the session with the above parameters
      try {
        System.out.println("_getSessionWithDetails - Session created with ID: " + serverSession.getSessionID());
      } catch (WebObjectsException ex) {
        System.out.println("_getSessionWithDetails - Error creating session:" + ex.getMessage());
      }
    }
    return serverSession;
  }

  public static WebIServerSession getSessionWithDetails(String serverName,String projectName, String login, String password){
    return getSessionWithDetails(serverName, projectName, login, password, 0);
  }

  /* WORKING SET */
  /*
   * The regular working set is essentially a session-specific array with a finite limit. This array stores references to any ongoing events or executions (not just dataset object).
   * If a new event is executed and the working set is full, the reference to an older event may be lost. When that old event is checked, this may cause a "history list message not found" error.
   */
  public static void updateWorkingSet(int threads){
    serverSession.setRegularWorkingSetSize(threads);
    try {
      serverSession.updateSessionParameters();
    } catch (WebObjectsException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  /*
   * Similar to the regular working set, but specific to WebSearch executions. This caps how many WebSearch instances can run in parallel, before we lose track of ongoing executions.
   * Overflows are also manifested as "history list message not found" errors.
   */
  public static void updateSearchWorkingSet(int threads){
    serverSession.setSearchWorkingSetSize(threads);
    try {
      serverSession.updateSessionParameters();
    } catch (WebObjectsException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

}
