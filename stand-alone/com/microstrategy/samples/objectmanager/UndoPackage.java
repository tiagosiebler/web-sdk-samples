package com.microstrategy.samples.objectmanager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.samples.util.FileHelper;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebSourceManipulator;
import com.microstrategy.webapi.EnumDSSXMLConflictResolution;

public class UndoPackage {
  private static String localFolderPath = "/Users/tsiebler/Documents/EclipseCodeSavedFiles/";

  public static void main(String[] args) {
    System.out.println("Preparing session");
    
    WebIServerSession session = SessionManager.getSessionWithDetails("APS-TSIEBLER-VM", "MicroStrategy Tutorial", "Administrator", "");
    PackageCreation.setupWithSession(session);
    
    createSimpleUndoPackage();
  }
  
  public static void createSimpleUndoPackage() {
    // wrapped logic to create a simple package containing a report
    byte[] packageWithReport = PackageCreation.createSingleObjectPackageWithReport();
    if (packageWithReport == null) {
      System.out.println("Cannot create undo package if main package creation failed");
      return;
    }
    
    // now that we have a package, generate an undo package
    String fileName = "undoReportPackage";
    createUndoPackageForPackage(packageWithReport, fileName);
  }
  
  public static void createUndoPackageForPackage(byte[] inputPackage, String fileName) {
    WebSourceManipulator manipulator = PackageCreation.source.getSourceManipulator();
    manipulator.setACLConflictRule(EnumDSSXMLConflictResolution.DssXmlConflictReplace);

    InputStream inputPackageStream = new ByteArrayInputStream(inputPackage);
    
    byte[] undoPackage = null;
    try {
      undoPackage = manipulator.createUndoPackageClientToClient(inputPackageStream, inputPackage.length);
    } catch (WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    
    // save the undo package to file
    String pathToFile = localFolderPath + fileName + ".mmp";
    System.out.println("Saved undo package to: " + pathToFile);
    FileHelper.saveByteArrayToFile(undoPackage, pathToFile);
  }
  

}
