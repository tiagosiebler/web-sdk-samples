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
    System.out.println("Preparing session");
    serverSession = SessionManager.getSessionWithDetails("APS-TSIEBLER-VM", "MicroStrategy Tutorial", "Administrator", "");

    // the factory is tied to our session
    factory = serverSession.getFactory();
    source = factory.getObjectSource();

    System.out.println("Creating package");
    createSingleObjectPackageWithReport();
  }


  /*
   * KB263411 - create an object manager package with a specific object
   * https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/objects/WebSourceManipulator.html#createObjectDeltaPackage(java.lang.String,%20int,%20int,%20int,%20int)
   * Conflict rules are on the package level, not per object.
   */
  public static void createSingleObjectPackageWithReport(){
    //ID of report
    String objectID = "0BA6017811D5EFDF100080B3A5E8F8A4";

    // Type of object we're sending in
    int objectType = EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition;

    // The level at which conflict rules are checked
    int conflictDomain = EnumDSSXMLConflictDomain.DssXmlConflictAll;
    // Part(s) of object being handled for conflicts
    int conflictObjectParts = EnumDSSXMLObjectFlags.DssXmlObjectDefn;
    // What do we do if there is a conflict with the file(s)
    int objectConflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictReplace;

    // What to do if there is a conflict with the file(s) ACLs
    int aclConflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictReplace;

    // Create the package in memory
    createSingleObjectPackageWithOptions(
      objectID,
      objectType,
      conflictDomain,
      conflictObjectParts,
      objectConflictResolutionRule,
      aclConflictResolutionRule,
      "reportPackage"
    );
  }
  
  /*
   * Similar to createSingleObjectPackageWithReport, but allows for object manager package creation with multiple objects and delta flags per object.
   * Delta flags are created using the ObjectDeltaFlags helper class
   */
  public static void createMultiObjectPackageWithReport(){
    String objectID = "0BA6017811D5EFDF100080B3A5E8F8A4";
    int objectType = EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition;
    
    // Fetch the object definition from the metadata
    WebObjectInfo objectDefinition = null;
    try {
      objectDefinition = source.getObject(objectID, objectType, true);
    } catch (WebObjectsException e) {
      e.printStackTrace();
    }

    // Conflict resolution rules
    int conflictDomain = EnumDSSXMLConflictDomain.DssXmlConflictExplicitObject;
    int conflictObjectParts = EnumDSSXMLObjectFlags.DssXmlObjectDefn;
    int objectConflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictReplace;

    // What to do if there is a conflict with the file(s) ACLs
    int aclConflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictReplace;
    
    // flagDomain = object type, object ID, or folder ID. Can be int or string
    String flagDomain = objectID;
    ObjectDeltaFlags objectMergeRules = new ObjectDeltaFlags(conflictDomain, flagDomain, conflictObjectParts, objectConflictResolutionRule);
    
    // these are the objects to add to the package
    WebObjectInfo[] packageObjectsArray = new WebObjectInfo[] {objectDefinition};
    // these are the merge rules per object in the above array 
    ObjectDeltaFlags[] mergeFlagsArray = new ObjectDeltaFlags[]{objectMergeRules};
    
    // Create the package in memory
    createMultiObjectPackageWithOptions(
      packageObjectsArray,
      mergeFlagsArray,
      aclConflictResolutionRule,
      "multiObjectPackage"
    );
  }
  
  /*
   * Similar to createMultiObjectPackageWithReport, although conflict rules are shared across all objects
   */
  public static void createMultiObjectPackageWithReportSharedRules(){
    String objectID = "0BA6017811D5EFDF100080B3A5E8F8A4";
    int objectType = EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition;
    
    // Fetch the object definition from the metadata
    WebObjectInfo objectDefinition = null;
    try {
      objectDefinition = source.getObject(objectID, objectType, true);
    } catch (WebObjectsException e) {
      e.printStackTrace();
      return;
    }

    // Conflict resolution rules for all objects in package
    int conflictDomain = EnumDSSXMLConflictDomain.DssXmlConflictApplication;
    int conflictObjectParts = EnumDSSXMLObjectFlags.DssXmlObjectDefn;
    int objectConflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictReplace;

    // What to do if there is a conflict with the file(s) ACLs
    int aclConflictResolutionRule = EnumDSSXMLConflictResolution.DssXmlConflictReplace;
    
    // array of objects we want in the package
    WebObjectInfo[] packageObjectsArray = new WebObjectInfo[] {objectDefinition};

    // Create the package in memory
    createMultiObjectPackageWithOptions(
      packageObjectsArray,
      conflictDomain,
      conflictObjectParts,
      objectConflictResolutionRule,
      aclConflictResolutionRule,
      "multiObjectPackage"
    );
  }
  
  /*
   * Wrapper for creating a object manager package and saving the result to file. Takes objects and delta rules per object.
   */
  private static void createMultiObjectPackageWithOptions(
      WebObjectInfo[] objects,
      ObjectDeltaFlags[] objDeltaFlags,
      int aclConflictResolutionRule,
      String fileName
  ) {
    WebSourceManipulator manipulator = source.getSourceManipulator();
    manipulator.setACLConflictRule(aclConflictResolutionRule);

    // Create the package in memory
    byte[] objectPackage = null;
    try {
      objectPackage = manipulator.createObjectDeltaPackage(objects, objDeltaFlags);
    } catch (IllegalArgumentException | WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }

    // Save the package to a file
    String pathToFile = localFolderPath + fileName + ".mmp";
    saveByteArrayToFile(objectPackage, pathToFile);

    System.out.println("package should be ready");
  }
  
  /*
   * Similar to createMultiObjectPackageWithOptions above, but with shared conflict rules for all objects in the array
   */
  private static void createMultiObjectPackageWithOptions(
    WebObjectInfo[] objects,
    int conflictDomain,
    int conflictObjectParts,
    int objectConflictResolutionRule,
    int aclConflictResolutionRule,
    String fileName
  ) {
    WebSourceManipulator manipulator = source.getSourceManipulator();
    manipulator.setACLConflictRule(aclConflictResolutionRule);
    
    // Create the package in memory
    byte[] objectPackage = null;
    try {
      objectPackage = manipulator.createObjectDeltaPackage(objects, conflictDomain, conflictObjectParts, objectConflictResolutionRule);
    } catch (IllegalArgumentException | WebObjectsException e) {
      e.printStackTrace();
      return;
    }
    
    // Save the package to a file
    String pathToFile = localFolderPath + fileName + ".mmp";
    saveByteArrayToFile(objectPackage, pathToFile);
    
    System.out.println("package should be ready");
  }
  
  /*
   * Convenience wrapper for calling the function to create a package with one object, with object ID.
   */
  private static void createSingleObjectPackageWithOptions(
      String objectID,
      int objectType,
      int conflictDomain,
      int conflictObjectParts,
      int objectConflictResolutionRule,
      int aclConflictResolutionRule,
      String fileName
  ) {
    WebSourceManipulator manipulator = source.getSourceManipulator();
    manipulator.setACLConflictRule(aclConflictResolutionRule);

    // Create the package in memory
    byte[] objectPackage = null;
    try {
      objectPackage = manipulator.createObjectDeltaPackage(objectID,
        objectType,
        conflictDomain,
        conflictObjectParts,
        objectConflictResolutionRule);
    } catch (IllegalArgumentException | WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }

    // Save the package to a file
    String pathToFile = localFolderPath + fileName + ".mmp";
    saveByteArrayToFile(objectPackage, pathToFile);

    System.out.println("package should be ready");
  }
  
  /*
   * Helper function to save an in-memory object to file
   */
  private static void saveByteArrayToFile(byte[] byteArray, String targetFilePath) {
    BufferedOutputStream bos = null;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(new File(targetFilePath));
      bos = new BufferedOutputStream(fos);
      bos.write(byteArray);
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
  }

}
