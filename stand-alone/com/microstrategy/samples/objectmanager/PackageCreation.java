package com.microstrategy.samples.objectmanager;

import java.io.*;

import com.microstrategy.web.objects.*;
import com.microstrategy.webapi.*;

import com.microstrategy.samples.sessions.SessionManager;

public class PackageCreation {

  public static WebIServerSession serverSession = null;
  public static WebObjectsFactory factory = null;
  public static WebObjectSource source = null;

  private static String localFolderPath = "/Users/tsiebler/Documents/EclipseCodeSavedFiles/";

  public static void main(String[] args) {
    // TODO Auto-generated method stub

    System.out.println("Preparing session");
    serverSession = SessionManager.getSessionWithDetails("APS-TSIEBLER-VM", "MicroStrategy Tutorial", "Administrator", "");

    // the factory is tied to our session
    factory = serverSession.getFactory();
    source = factory.getObjectSource();

    System.out.println("Creating package");
    createPackageWithReport();
  }

  /*
   * KB263411 - create an object manager package with a specific object
   * https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/objects/WebSourceManipulator.html#createObjectDeltaPackage(java.lang.String,%20int,%20int,%20int,%20int)
   */
  public static void createPackageWithReport(){
    //ID of report 
    String objectID = "0BA6017811D5EFDF100080B3A5E8F8A4";

    // Type of object we're sending in
    int objectType = EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition;

    // The level at which conflict rules are checked
    int conflictDomain = EnumDSSXMLConflictDomain.DssXmlConflictApplication;
    // Part(s) of object being handled for conflicts
    int conflictObjectParts = EnumDSSXMLObjectFlags.DssXmlObjectDefn;
    // What do we do if there is a conflict with the file(s)
    int conflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictExisting;

    // What to do if there is a conflict with the file(s) ACLs
    int aclConflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictReplace;
    WebSourceManipulator manipulator = source.getSourceManipulator(); 
    manipulator.setACLConflictRule(aclConflictResolutionRule);

    // Create the package in memory
    byte[] objectPackage = null;
    try {
      objectPackage = manipulator.createObjectDeltaPackage(objectID, 
          objectType,
          conflictDomain, 
          conflictObjectParts, 
          conflictResolutionRule);
    } catch (IllegalArgumentException | WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }

    // Save the package to a file
    BufferedOutputStream bos = null; 
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(new File(localFolderPath + "reportPackage.mmp"));
      bos = new BufferedOutputStream(fos); 
      bos.write(objectPackage);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

    } finally {
      try {
        bos.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    System.out.println("package should be ready");

  }

}
