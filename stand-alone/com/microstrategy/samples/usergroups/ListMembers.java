package com.microstrategy.samples.usergroups;

import java.util.Enumeration;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.*;
import com.microstrategy.web.objects.admin.users.*;
import com.microstrategy.webapi.*;

public class ListMembers {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    // Connectivity for the intelligence server
    String intelligenceServerName = "APS-TSIEBLER-VM";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // Create our I-Server Session
    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
    
    // 
    String groupId = "C1E141DB11D603A2100086B3A5E8F8A4";
    listMembersInGroup(session, groupId);
  }
  
  // This method retrieves a group from the metadata, using the object Id, and lists the first-class children. 
  // These may be users or groups that can be differentiated using the sub type.
  // Note: as this method uses populate() on the group reference, this can be slow in bigger environments. See KB30899 for an alternative.
  public static void listMembersInGroup(WebIServerSession serverSession, String groupId) {
    WebObjectSource wos = serverSession.getFactory().getObjectSource();
    try {
      // retrieve group from metadata
      WebUserGroup group = (WebUserGroup)wos.getObject(groupId, EnumDSSXMLObjectTypes.DssXmlTypeUser, EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup);
      
      // without populate this group is empty. Large groups may take a long time to populate, see KB30899
      group.populate();
      
      WebUserList memberList = group.getMembers();
      System.out.println("children: " + memberList.size());
      
      Enumeration<WebUser> members = memberList.elements();
      
      while(members.hasMoreElements()) {
        WebObjectInfo child = members.nextElement();
        
        switch (child.getSubType()) {
          case EnumDSSXMLObjectSubTypes.DssXmlSubTypeUserGroup:
            WebUserGroup childGroup = (WebUserGroup)child;
            System.out.println("-- Child group: " + childGroup.getDisplayName());
            break;
            
          case EnumDSSXMLObjectSubTypes.DssXmlSubTypeUser:
            WebUser childUser = (WebUser)child;
            System.out.println("-- Child user: " + childUser.getDisplayName());
            break;
            
          default:
            System.out.println("-- Unhandled object subtype: " + child.getSubType());
            break;
        }
      }
      
      System.out.println("reached end.");
      
    } catch (WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
