/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nitdlibrary;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import managers.Config;
import managers.ScreenLock;
/**
 *
 * @author Meena
 */
public class NITDLibrary {

    
    /**
     * @param args the command line arguments
     */
    public NITDLibrary()
    {
        startApplication();
    }
    public static void main(String[] args) {
       // startApplication();
     
    }
    public static void startApplication()
    {
          try {
            Connection library = createConnection();
            
            
            MainScreen startUpWindow  = new MainScreen(library);
            Config.startUpWindow = startUpWindow;
            System.out.println("initiation complete");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
      public static Connection createConnection() throws Exception
      {
          Class.forName(Config.dbDriver);
          Connection library = DriverManager.getConnection(Config.dbConnecionString,Config.dbUserName,Config.dbPassword);
           System.out.println("connection esstablished with database");
           return library;
      }
      /**
       * This method loads config from config.ser
       */
      public static void loadConfig() throws FileNotFoundException, IOException, ClassNotFoundException
      {
          FileInputStream fin = new FileInputStream("config.ser");
          ObjectInputStream in = new ObjectInputStream(fin);
          Config config = (Config)in.readObject();
          in.close();
          config.deSerialize();
      }
}

