package com.microstrategy.samples.objectmanager;

import com.microstrategy.web.objects.*;
import com.microstrategy.webapi.*;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.samples.util.FileHelper;

public class PackageCreation {

  public static WebIServerSession serverSession = null;
  public static WebObjectsFactory factory = null;
  public static WebObjectSource source = null;

  // Hardcoded path where packages are saved
  private static String localFolderPath = "/Users/tsiebler/Documents/EclipseCodeSavedFiles/";

  public static void main(String[] args) {
    System.out.println("Preparing session");
    WebIServerSession session = SessionManager.getSessionWithDetails("APS-TSIEBLER-VM", "MicroStrategy Tutorial", "Administrator", "");
    setupWithSession(session);

    System.out.println("Creating package");
    createSingleObjectPackageWithReport();
  }
  
  public static void setupWithSession(WebIServerSession session) {
    // store session
    serverSession = session;
    
    // the factory is tied to our session
    factory = serverSession.getFactory();
    source = factory.getObjectSource();
  }

  /*
   * KB263411 - create an object manager package with a specific object
   * https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/objects/WebSourceManipulator.html#createObjectDeltaPackage(java.lang.String,%20int,%20int,%20int,%20int)
   * Conflict rules are on the package level, not per object.
   */
  public static byte[] createSingleObjectPackageWithReport(){
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

    byte[] objectPackage = null;
    
    // Create the package in memory
    try {
      objectPackage = createSingleObjectPackageWithOptions(
        objectID,
        objectType,
        conflictDomain,
        conflictObjectParts,
        objectConflictResolutionRule,
        aclConflictResolutionRule
      );
    } catch (IllegalArgumentException | WebObjectsException e) {
      // TODO Auto-generated catch block
      System.out.println("Package creation failed due to exception");
      e.printStackTrace();
      return objectPackage;
    }
    
    // Save the package to a file
    String fileName = "reportPackage";
    String pathToFile = localFolderPath + fileName + ".mmp";

    FileHelper.saveByteArrayToFile(objectPackage, pathToFile);

    System.out.println("Saved package to: " + pathToFile);
    return objectPackage;
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
    
    byte[] objectPackage = null;
    
    // Create the package in memory
    try {
      objectPackage = createMultiObjectPackageWithOptions(
        packageObjectsArray,
        mergeFlagsArray,
        aclConflictResolutionRule
      );
    } catch (IllegalArgumentException | WebObjectsException e) {
      // TODO Auto-generated catch block
      System.out.println("Package creation failed due to exception");
      e.printStackTrace();
      return;
    }

    // Save the package to a file
    String fileName = "multiObjectPackage";
    String pathToFile = localFolderPath + fileName + ".mmp";
    
    FileHelper.saveByteArrayToFile(objectPackage, pathToFile);

    System.out.println("Saved package to: " + pathToFile);
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
    byte[] objectPackage = null;
    try {
      objectPackage = createMultiObjectPackageWithOptions(
        packageObjectsArray,
        conflictDomain,
        conflictObjectParts,
        objectConflictResolutionRule,
        aclConflictResolutionRule
      );
    } catch (IllegalArgumentException | WebObjectsException e) {
      // TODO Auto-generated catch block
      System.out.println("Package creation failed due to exception");
      e.printStackTrace();
      return;
    }
    
    // Save the package to a file
    String fileName = "multiObjectPackage";
    String pathToFile = localFolderPath + fileName + ".mmp";
    FileHelper.saveByteArrayToFile(objectPackage, pathToFile);

    System.out.println("Saved package to: " + pathToFile);
  }
  
  /*
   * Wrapper for creating a object manager package and returning the result in memory as a byte array. Takes objects and delta rules per object.
   */
  public static byte[] createMultiObjectPackageWithOptions(
      WebObjectInfo[] objects,
      ObjectDeltaFlags[] objDeltaFlags,
      int aclConflictResolutionRule
  ) throws IllegalArgumentException, WebObjectsException {
    
    WebSourceManipulator manipulator = source.getSourceManipulator();
    manipulator.setACLConflictRule(aclConflictResolutionRule);

    // Create the package in memory
    return manipulator.createObjectDeltaPackage(objects, objDeltaFlags);
  }
  
  /*
   * Similar to createMultiObjectPackageWithOptions above, but with shared conflict rules for all objects in the array
   */
  public static byte[] createMultiObjectPackageWithOptions(
    WebObjectInfo[] objects,
    int conflictDomain,
    int conflictObjectParts,
    int objectConflictResolutionRule,
    int aclConflictResolutionRule
  ) throws IllegalArgumentException, WebObjectsException {
    
    WebSourceManipulator manipulator = source.getSourceManipulator();
    manipulator.setACLConflictRule(aclConflictResolutionRule);
    
    // Create the package in memory
    return manipulator.createObjectDeltaPackage(
      objects, 
      conflictDomain, 
      conflictObjectParts, 
      objectConflictResolutionRule
    );
  }
  
  /*
   * Convenience wrapper for calling the function to create a package with one object, with object ID.
   */
  public static byte[] createSingleObjectPackageWithOptions(
      String objectID,
      int objectType,
      int conflictDomain,
      int conflictObjectParts,
      int objectConflictResolutionRule,
      int aclConflictResolutionRule
  ) throws IllegalArgumentException, WebObjectsException {
    
    WebSourceManipulator manipulator = source.getSourceManipulator();
    manipulator.setACLConflictRule(aclConflictResolutionRule);

    // Create the package in memory
    return manipulator.createObjectDeltaPackage(
      objectID,
      objectType,
      conflictDomain,
      conflictObjectParts,
      objectConflictResolutionRule
    );
  }

}
