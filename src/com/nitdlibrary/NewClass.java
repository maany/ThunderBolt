/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nitdlibrary;
import java.sql.*;
/**
 *
 * @author Meena
 */
public class NewClass {
            public static void main(String args[])
            {
            Connection conn = null;
           
           try
           {
               String userName = "root"; // db username
               String password = "root123"; // db password
               String url = "jdbc:mysql://localhost/mytest"; //test = db name
               Class.forName ("com.mysql.jdbc.Driver");
               conn = DriverManager.getConnection (url, userName, password);
               System.out.println ("Database connection established");
           
               Statement st = conn.createStatement();
               ResultSet rs = st.executeQuery("select * from sample");
               while(rs.next())
               {
            	   
            	   System.out.print(rs.getString(1)+"\t"+rs.getString(2) +"\t"+rs.getString(3));
            	   System.out.println();
               }
           }
           
           
           
           
           catch (Exception e)
           {
               System.err.println ("Cannot connect to database server");
          
               System.err.println (e.getMessage());
           }
           }
}
