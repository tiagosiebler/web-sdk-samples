package com.microstrategy.samples.sessions;

import com.microstrategy.utils.serialization.EnumWebPersistableState;
import com.microstrategy.web.objects.WebIServerSession;

public class SessionState {
  private static String DEFAULT_SERVER = "APS-TSIEBLER-VM";
  private static String DEFAULT_PROJECT = "MicroStrategy Tutorial";
  private static String DEFAULT_USERNAME = "Administrator";
  private static String DEFAULT_PASSWORD = "";
  
  /*
   * Simple demonstration on saving a session as a "session state" string, which can be used with other requests (e.g the task API or URL API).
   */
  public static void main(String[] args) {
    // create a session in any way
    WebIServerSession exampleSession = SessionManager.getSessionWithDetails(DEFAULT_SERVER, DEFAULT_PROJECT, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    
    // save the session state as a string
    String sessionStateString = getSessionState(exampleSession);
    
    System.out.println("session state: " + sessionStateString);
  }
  
  // https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/utils/serialization/Persistable.html#saveState(int)    
  public static String getSessionState(WebIServerSession session) {    
    return session.saveState(EnumWebPersistableState.MINIMAL_STATE_INFO);
  }

}
