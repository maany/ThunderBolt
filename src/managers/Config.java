/*
    * THIS  CLASS STORES CONFIG INFO LIKE PASSWORDS, MAX ISSUE LIMIT ETC
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managers;

import com.nitdlibrary.MainScreen;
import com.nitdlibrary.NITDLibrary;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 *
 * @author MAYANK
 */
public class Config implements Serializable {
   public static MainScreen startUpWindow; // stores mainscreen instance for current session
    /**
    * Variables related to only this class. non public static.
    */
    public Connection serlibrary;
    static Connection library;
    public static String superPass = "3dsmaxmaya";
    
    
    /**
     * Common Properties for the application are listed below
     */
    public static int studentIssueDuration = 15;
    public static int issueLimitStudent = 2;
    public static int ChARGEPERDAY = 1;
     public  int serstudentIssueDuration = 15;
    public  int serissueLimitStudent = 2;
    public  int serChARGEPERDAY = 1;
    /**
     * DATABASE CONFIG
     * 
     */
    public static String dbDriver = "com.mysql.jdbc.Driver";
    public static String dbConnecionString = "jdbc:mysql://localhost/library";
    public static String dbUserName = "root";
    public static String dbPassword = "root123";
    public  String serdbDriver = "com.mysql.jdbc.Driver";
    public  String serdbConnecionString = "jdbc:mysql://localhost/library";
    public  String serdbUserName = "root";
    public  String serdbPassword = "root123";
    /**
     * Library/Login Details 
    */
    public static String loginName = "admin";
    private static String password = "maany";
    public static String librarianName = "Ma'am";
    public static String librarianEmail ;
    public static String librarianPhone ;
    public static String welcomeScreenPicPath = "welcome_pic.png";
    public  String serloginName = "admin";
    private  String serpassword = "maany";
    public String serlibrarianName = "Ma'am";
    public String serlibrarianEmail ;
    public String serlibrarianPhone ;
    public String serwelcomeScreenPicPath = "welcome_pic.png";
    /**
     * details for 2013 batch for reference
     */
    public static int year2013 = 1;
    public static int sem2013 = 1;
    public int seyear2013 = 1;
    public int sersem2013 = 1;
    /**
    Getters and Setters for private properties
    */
    public static String getPassword()
    {
        return password;
    }
    public static void setPassword(String newPassword)
    {
        password = newPassword;
    }
    /**
     * THIS METHOD CHECKS IF STUDENT IS PASS OUT OR NOT. CHECKS THE DIFFERENCE B/W BTECH AND MTECH
     * @param programme
     * @param year
     * @return 
     */
    public static boolean isPassed(String programme,int year)
    {
        
        boolean isPassed = true;
        if(programme.compareTo("B.Tech")==0 && year<=4)
        {
            isPassed = false;
            System.out.println("condition passed");
        }
        if(programme.compareTo("M.Tech")==0 && year<=2)
            isPassed = false;
        return isPassed;
    }
    /*
    Additional functions
    */
    /**
     * This Function Show a Confirm Delete Dialog and Asks for password. If password matches , returns true.
     * @return 
     */
    public static boolean showDeleteDialog()
    {
       
        JPanel panel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Are You Sure?? Enter Password :");
        JPasswordField passwordField = new JPasswordField(10);
        panel.add(label);
        panel.add(passwordField);
        
        if(JOptionPane.showConfirmDialog(null, panel, "Confirm Delete", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
        {
            System.out.println("Password Entered is " +  passwordField.getText());
            if(passwordField.getText().compareTo(password)==0)
            return true;
            else 
            return false; // default return
        }
        else
            return false;
    }
    
    /**
     * AddStudent.java Config
     */
    public static FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*jpg,*jpeg,*png,*gif only)","jpg","jpeg","png","gif"); // http://docs.oracle.com/javase/6/docs/api/javax/swing/filechooser/FileNameExtensionFilter.html
    public static int MAXCARDS = 2; // modify code in cardsissued ->properties -> model
    
    
    
    public static void main(String args[])
    {
        System.out.println(showDeleteDialog());
    }
    
    /**
     * REPORT GENERATION + FINE CALCULATION METHODS
     * @param accNo
     * @param roll_no 
     * @return array of result sets -> 0 index for bookData and 1 for studentData
     * @throws java.lang.Exception 
     */
    public static ResultSet[] getReportData(int accNo,int roll_no) throws Exception
    {
        ResultSet[] reportData = new ResultSet[3];
        ResultSet bookReportData = null, studentReportData = null;
        library = NITDLibrary.createConnection();
        PreparedStatement queryBooks = library.prepareStatement("select * from books where acc_no = ?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        queryBooks.setInt(1, accNo);
        PreparedStatement queryStudent = library.prepareStatement("select * from student where roll_no = ?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        queryStudent.setInt(1, roll_no);
        bookReportData = queryBooks.executeQuery();
        studentReportData = queryStudent.executeQuery();
        reportData[0] = bookReportData;
        reportData[1] = studentReportData;
        return reportData;
        
    }
    /**
     * Date format changer from dd-mm-yyyy to yyyy-MM-dd . but if input date is in yyyy-MM-dd this does nothing / used by daysExceeded() method
     */
    public static String changeDateFormat(String date)
    {
       Pattern pattern = Pattern.compile("\\d{1,2}-\\d{1,2}-\\d{4}");
        Matcher matcher = pattern.matcher(date);
        if(matcher.matches())
        {
        try {
           System.out.println("Input Date is " + date.toString());
           java.util.Date formattedDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
           DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
           date=format.format(formattedDate);
       } catch (ParseException ex) {
           Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
       }
       System.out.println("formatted date is :" + date);
        }
        
       return date;
        
    }
    /**
     * No of Days Exceeded calculator. Then Fine  = chargePerDay * return value of this method.
     * @param returnDateAttribute
     * @param dueDateAttribute
     * @return int days exceeded
     */
    public static int daysExceeded(String returnDateAttribute,String dueDateAttribute)
    {
        int daysExceeded = 0;
        
        dueDateAttribute=changeDateFormat(dueDateAttribute);
     //   DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      //  LocalDate dueDateFormatted = new LocalDate(dueDateAttribute);
      //  dueDateAttribute = format.format(dueDateFormatted);
        System.out.println("Due Date :" + dueDateAttribute.toString());
        LocalDate dueDate= new LocalDate(dueDateAttribute);
     //   java.util.Date returnDateFormatted = new java.util.Date(returnDateAttribute);
      //  returnDateAttribute = format.format(returnDateFormatted);
        
        if(returnDateAttribute==null  && dueDate.isAfter(new LocalDate(new java.util.Date()))) //book not returned and days not exceeded
        {
            daysExceeded=0;
            System.out.println("book not returned and days not exceeded");
        }
            
        else if (returnDateAttribute==null && dueDate.isBefore(new LocalDate(new java.util.Date()))) // book not returned and days exceeded
        {
            daysExceeded = Days.daysBetween(dueDate, new LocalDate(new java.util.Date())).getDays();
            System.out.println("book not returned anddays not exceeded");
        }
        else //book is returned
        {
            returnDateAttribute=changeDateFormat(returnDateAttribute);
            LocalDate returnDate = new LocalDate(returnDateAttribute); 
        if(dueDate.isAfter(returnDate)) //book retuned and days not exceeded
        {
            daysExceeded = 0;
            System.out.println("book returned and days not exceeded");
        }
        else{
            System.out.println("book returned and days exceeded");
            daysExceeded = Days.daysBetween(dueDate,returnDate).getDays(); // book returned and days exceeded
        }
        }
        return daysExceeded;
    }
    
    public static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    /**
     * Changes date format from dd-mm-yyyy to mysql's yyyy-mm-dd in a result set for specified fields
     * THIS METHOD FAILS BECAUSE WE THE INPUT RESULTSET SELECTS DATA FROM MANY TABLES. HENCE CONCUR UPDATABLE FAILS. IT IS APPLICABLE TO ONLY 1 TABLE.
     * @param rs The resultSet whose date format are to be changed
     * @param fields String Array that stores the name of date fields
     * @return ResultSet rs with modified date formats
     * @throws java.sql.SQLException
     */
    public static TableModel changeResultSetDateFormat(ResultSet rs,TableModel model,Integer[] fields) throws SQLException
    {
        int i=0;
        rs.beforeFirst();
        while(rs.next())
        {
            for(Integer field:fields)
            {
                
                try
                {
                if(model.getValueAt(i,field)!=null || model.getValueAt(i,field).toString().compareTo("")!=0 || model.getValueAt(i,field).toString().length()>3) // so that null date values are not affected by this method
                {
                System.out.println( "value at row " + i + " coloumn "+ field +" :  " + model.getValueAt(i, field));
                LocalDate date = new LocalDate(model.getValueAt(i,field));
                model.setValueAt(date.toString("dd-MM-yyyy"),i,field);
                }
                else
                {
                    continue;
                }
                }catch(Exception e)
                {
                    
                }
            }
            
            i++;
        }
        return model;
    }
    /**
     * MAIL OPTIONS
     */
    public static String sessionEmail = "imptodefeat@gmail.com" ;
    public static String sessionPassword = "3dsmaxmaya";
    public String sersessionEmail = "imptodefeat@gmail.com" ;
    public String sersessionPassword = "3dsmaxmaya";
    public static String issueMessage(String firstName,String rollNo, String title,String acc_no , String issue_date,String due_date)
    {
        String message = 
                "Dear " + firstName + "(Roll No. : " + rollNo + ")" +
                "\nThe following book has been issued to your account at NIT Delhi Library" +
                "\n\nBook Title : " + title +
                "\nBook Accession Number : " + acc_no +
                "\nIssue Date(yyyy-mm-dd) : " + issue_date +
                "\n\nYou have to return the book within " + Config.studentIssueDuration + " days from the Issue Date i.e by " + due_date +
                "\nAfter " + due_date + "you will fined @Rs." + Config.ChARGEPERDAY + " per day until the book is returned." +
                "\n\nIf you did not issue this book, please contact the librarian as soon as possible." +
                "\nRegards" +
                "\nNIT Delhi Library" +
                "\n\nThis is a system generated mail.Do not reply. Contact the library for any other details";
        return message;
    }
    public static String returnMessage(String firstName,String rollNo, String title,String acc_no , String issue_date,String due_date,String return_date)
    {
        LocalDate returnDate = new LocalDate(return_date);
        LocalDate issueDate = new LocalDate(issue_date);
        String message = 
                "Dear " + firstName + " (Roll No. : " + rollNo + ")" +
                "\nThe following book was issued by you at NIT Delhi Library on " + issue_date +
                "\n\nBook Title : " + title +
                "\nBook Accession Number : " + acc_no +
                "\nIssue Date(yyyy-mm-dd) : " + issue_date +
                "\nReturn Date(yyyy-mm-dd) : " + return_date +
                "\nDue Date(yyyy-mm-dd) : " + due_date +
                "\n\nThe book has been returned by you on  " + return_date + " after keeping it for " + Days.daysBetween(issueDate,returnDate).getDays() + " days" +
                "\nAs students are allowed to keep books for " + Config.studentIssueDuration + " days after issue " + 
                "\nNo of days exceeded : " + Config.daysExceeded(return_date, due_date) + 
                "\nRate for calculating fine : Rs." + Config.ChARGEPERDAY + "per day" +
                "\nFine : " + daysExceeded(return_date, due_date)*Config.ChARGEPERDAY +
                "\n\nIf you did not return this book, please contact the librarian as soon as possible." +
                "\nRegards" +
                "\nNIT Delhi Library" +
                "\n\n This is a system generated mail.Do not reply. Contact the library for any other details";
        return message;
    }
   /**
    * this is the constructor . A Config object is created by the ConfigManager and the values are serialized to config.ser
    */
    public Config() throws FileNotFoundException, IOException, URISyntaxException
    {
        this.serChARGEPERDAY=Config.ChARGEPERDAY ;
        this.serdbConnecionString=Config.dbConnecionString;
        this.serdbDriver=Config.dbDriver;
        this.serdbPassword = Config.dbPassword;
        this.serdbUserName=Config.dbUserName;
        this.serissueLimitStudent=Config.issueLimitStudent;
        this.serlibrarianEmail=Config.librarianEmail;
        this.serlibrarianName=Config.librarianName;
        this.serlibrarianPhone =Config.librarianPhone;
        this.serlibrary =Config.library;
        this.serloginName =Config.loginName;
        this.serpassword =Config.password;
        this.sersem2013 =Config.sem2013;
        this.sersessionEmail =Config.sessionEmail;
        this.sersessionPassword =Config.sessionPassword;
        this.serstudentIssueDuration =Config.studentIssueDuration;
        this.serwelcomeScreenPicPath=Config.welcomeScreenPicPath;
        
        /**
         * Serializing
         */
        FileOutputStream f = new FileOutputStream("config.ser");
        ObjectOutputStream out = new ObjectOutputStream(f);
        out.writeObject(this);
        out.close();
        if(JOptionPane.showConfirmDialog(null,"Configurations Saved Successfully. Please Restart Application.\nRestart Now?", "Restart Required", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION)
        {
            startUpWindow.setVisible(false);
            startUpWindow.dispose();
            startUpWindow = null;
            System.out.println("Starting Application");
            new NITDLibrary().main(new String[2]); // contains deserializing
        }
        
    }
   

    public void deSerialize() {
        Config.ChARGEPERDAY =this.serChARGEPERDAY;
        Config.dbConnecionString=this.serdbConnecionString;
        Config.dbDriver=this.serdbDriver;
        Config.dbPassword=this.serdbPassword ;
        Config.dbUserName=this.serdbUserName;
        Config.issueLimitStudent=this.serissueLimitStudent;
        Config.librarianEmail=this.serlibrarianEmail;
        Config.librarianName=this.serlibrarianName;
        Config.librarianPhone=this.serlibrarianPhone ;
        Config.library=this.serlibrary ;
        Config.loginName=this.serloginName;
         Config.password=this.serpassword;
        Config.sem2013=this.sersem2013;
        Config.sessionEmail=this.sersessionEmail;
        Config.sessionPassword=this.sersessionPassword;
        Config.studentIssueDuration=this.serstudentIssueDuration;
        Config.welcomeScreenPicPath=this.serwelcomeScreenPicPath;
    }
}
