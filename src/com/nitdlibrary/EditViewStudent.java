/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nitdlibrary;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import managers.Config;
import managers.PrintUtilities;
import net.proteanit.sql.DbUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 *
 * @author MAYANK
 */
public class EditViewStudent extends javax.swing.JFrame {
    Connection library;
    String picLoc = null;
    ResultSet loadViewResultSet ;
    ResultSet issueResultSet,issueFilteredResultSet;
    int currentRollNo ;
    /**
     * Creates new form EDITVIEWStudent
     * @param library
     */
    public EditViewStudent(Connection library,int currentRollNo) {
        setVisible(true);
        initComponents();
       setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.library = library ;
        this.currentRollNo = currentRollNo;
         try {
            loadViewData(new Integer(currentRollNo));
        } catch (SQLException ex) {
            successFailure(false, ex.getMessage());
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        addWindowListener(new WindowAdapter(){
            public void windowClosing()
            {
                setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                dispose();
                
            }
        });
    }
    /**
     * THIS METHOD CONTROLS THE SUCCESS AND FAILURE LABELS
     */
    private void successFailure(boolean test,String message)
    {
        if(test==true)
        {
            try {
                statusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
            } catch (IOException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
            statusMessage.setText(message);
        }else{
            try {
                statusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
            statusMessage.setText(message);
        }
    }
    /**
     * THIS METHOD MAPS category from loadviewResultSet info
     */
    private void categoryMap() throws SQLException
    {
        switch(loadViewResultSet.getString("Category"))
        {
            case "GEN" : category.setSelectedIndex(0); break;
            case "OBC" : category.setSelectedIndex(1); break;
            case "SC" : category.setSelectedIndex(2); break;
            case "ST" : category.setSelectedIndex(3); break;
        }
    }
    /**
     * THIS METHOD LOADS VIEW DATA INTO FIELDS AFTER RECIEVING ROLL NO. ALSO CHECKS IF STUDENT IS PASSED OUT OR NOT
     * @param rollNoForLoad
     */
    public void loadViewData(int rollNoForLoad) throws SQLException
    {
        currentRollNo = rollNoForLoad;
        try {
            PreparedStatement query = library.prepareStatement("select * from student where roll_no = ?");
            query.setInt(1,rollNoForLoad);
            System.out.println(query.toString());
            loadViewResultSet = query.executeQuery();
            loadViewResultSet.next();
        } catch (SQLException ex) {
            successFailure(false,ex.getMessage());
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * RESULTSET OBTAINED, UPDATING FIELDS
         */
        fname.setText(loadViewResultSet.getString("First Name"));
        mname.setText(loadViewResultSet.getString("Middle Name"));
        lname.setText(loadViewResultSet.getString("Last Name"));
        fathername.setText(loadViewResultSet.getString("Fathers Name"));
        categoryMap();
        if(loadViewResultSet.getString("Gender").compareTo("Male")==0)
        male.setSelected(true);
        else
        female.setSelected(true);
        
        primary.setText(loadViewResultSet.getString("Primary Address"));
        mailing.setText(loadViewResultSet.getString("Mailing Address"));
        contactnumber.setText(loadViewResultSet.getString("Contact Number"));
        email.setText(loadViewResultSet.getString("E-Mail"));
        rollno.setText(loadViewResultSet.getString("roll_no"));
        cardno.setText(loadViewResultSet.getString("Card Numbers"));
        /**
         * CHECKING IF STUDENT IS PASSED OUT
         */
        String programme = loadViewResultSet.getString("Programme");
        if(Config.isPassed(programme,loadViewResultSet.getInt("Year")))
        {
        year.setVisible(false);
        sem.setVisible(false);
        year.setValue(loadViewResultSet.getInt("Year"));
        sem.setValue(loadViewResultSet.getInt("Semester"));
        yearLabel.setVisible(false);
        semLabel.setVisible(false);
         /**
         * take care of passing year based on programme
         */
        
             if(programme.compareTo("B.Tech")==0)
              {
               int passingYear = (new LocalDate(new Date()).getYear()-loadViewResultSet.getInt("Year")+4);
                passOutLabel.setText("Passed out in " + passingYear);
              }
             else
              {
             int passingYear = (new LocalDate(new Date()).getYear()-loadViewResultSet.getInt("Year")+2);
             passOutLabel.setText("Passed out in " + passingYear); 
              }
        }
        else
        {
        
        year.setValue(loadViewResultSet.getInt("Year")); // so that when changes are saved data is not lost
        sem.setValue(loadViewResultSet.getInt("Semester"));
        passOutLabel.setText("Pass Out : No");
        }
        cardsissued.setValue(loadViewResultSet.getInt("Cards Issued"));
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-mm-d",Locale.ENGLISH).parse(loadViewResultSet.getString("Date Of Birth"));
        } catch (ParseException ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        dob.setDate(date);
        Image image = null;
        try {
            image = ImageIO.read(new File(loadViewResultSet.getString("pic_path")));
            
        } catch (IOException ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        Image scaledImage = getScaledImage(image,168,130);
        picLabel.setIcon(new ImageIcon(scaledImage));
        picLoc = loadViewResultSet.getString("pic_path");
        
        loadIssueData();
        calculateStudentPerformance();
    }
  /**
   * This Method loads data from issue table and sets the issue history table model
   */
    private void loadIssueData()
    {
        try {
            PreparedStatement query = library.prepareStatement("select issue.issue_id,issue.acc_no,books.title,books.author,issue.issue_date,issue.return_date,issue.due_date from issue,books where issue.issuer_id = ? and books.acc_no = issue.acc_no");
            query.setInt(1,currentRollNo);
            issueResultSet = query.executeQuery();
            issueHistoryTable.setModel(DbUtils.resultSetToTableModel(issueResultSet));
            issueResultSet.beforeFirst();
            Integer[] fields = new Integer[3];
            fields[0]=4;
            fields[1]=5;
            fields[2]=6;
            issueHistoryTable.setModel(Config.changeResultSetDateFormat(issueResultSet,issueHistoryTable.getModel(), fields));
        } catch (SQLException ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
     * load result set for filtered data and apply model to table
     */
    private void loadFilteredIssueData(LocalDate from,LocalDate to) throws SQLException
    {
         
            PreparedStatement query = library.prepareStatement("select issue.issue_id,issue.acc_no,books.title,books.author,issue.issue_date,issue.return_date,issue.due_date from issue,books where issue.issuer_id = ? and books.acc_no = issue.acc_no and (issue.issue_date between ? and ?)");
            query.setInt(1,currentRollNo);
            query.setString(2,from.toString("yyyy-MM-dd"));
            query.setString(3,to.toString("yyyy-MM-dd"));
            System.out.println(query.toString());
            issueFilteredResultSet = query.executeQuery();
            issueHistoryTable.setModel(DbUtils.resultSetToTableModel(issueFilteredResultSet));
     }
    /**
     * THIS FUNCTION CALCULATES STUDENT PERFORMCE AND UPDATES FIELDS
     */
    private void calculateStudentPerformance() throws SQLException
    {
        issueResultSet.beforeFirst();
        Date returnDate = null,dueDate = null;
        LocalDate returnDateJoda = null,dueDateJoda = null;
        int totalIssued = 0,returned = 0,fine=0,currentIssue =0;
        int flag=0; //incremented when today is > due date and return_date  is null. it means that some books are not returned and fine calc is shown wrt today
        while(issueResultSet.next())
        {
            totalIssued++;
            if(issueResultSet.getString("return_date")!=null)
                returned ++ ;
            
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
            
            try {
                dueDate = format.parse(issueResultSet.getString("due_date"));
                /**
                 * IF BOOK HAS NOT BEEN RETURNED AND TODAY>DUEDATE .. FINE TO BE PAID IS SHOWN
                 */
                if(issueResultSet.getString("return_date")!=null && (issueResultSet.getString("return_date").compareTo("")!=0)) 
                {
                    returnDate = format.parse(issueResultSet.getString("return_date"));
                }
                else 
                {
                    String tempDate = format.format(new Date());
                    returnDate = format.parse(tempDate);
                    if(dueDate.before(returnDate)) // i.e due date before today and book is not returned.
                        flag++;
                }
                
                returnDateJoda = new LocalDate(returnDate);
                dueDateJoda = new LocalDate(dueDate);
            } catch (ParseException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("DUE DATE : " + dueDateJoda.toString() + " RETURN DATE : " + returnDateJoda.toString());
            if(dueDate.before(returnDate))
            {
                Days d = Days.daysBetween(dueDateJoda, returnDateJoda);
                fine+=d.getDays();
                System.out.println("Calculting fine");
            }
            if(issueResultSet.getString("return_date")==null || (issueResultSet.getString("return_date").compareTo("")==0))
            {
                currentIssue++;
            }
            
        }
        /**
         * setting values in Labels
         */
        issued.setText("Total Books Issued : " + totalIssued);
        returnedLabel.setText("Total Books Returned : " + returned);
        if(fine<0)
            fine=0;
        fineLabel.setText("Total Fine : " + fine);
        
        currentLabel.setText("Currently issued book count : " + currentIssue);
        if(flag!=0)
            exceedLabel.setText("* "+ flag +" books have exceeded due date and are not returned. Assuming they are retuned today, total fine is being shown.");
    }
   /**
    * THIS FUNCTION CALCULATES DATA FOR FILTERED RESULTSET AND UPDATES THE LABELS
    * @param from
    * @param to 
    */
    private void calculateFilteredPerformance(LocalDate from,LocalDate to) throws SQLException
   {
        issueFilteredResultSet.beforeFirst();
        Date returnDate = null,dueDate = null;
        LocalDate returnDateJoda = null,dueDateJoda = null;
        int totalIssued = 0,returned = 0,fine=0,currentIssue =0;
        int flag=0; //incremented when today is > due date and return_date  is null. it means that some books are not returned and fine calc is shown wrt today
        while(issueFilteredResultSet.next())
        {
            totalIssued++;
            if(issueFilteredResultSet.getString("return_date")!=null)
                returned ++ ;
            
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
            
            try {
                dueDate = format.parse(issueFilteredResultSet.getString("due_date"));
                /**
                 * IF BOOK HAS NOT BEEN RETURNED AND TODAY>DUEDATE .. FINE TO BE PAID IS SHOWN
                 */
                if(issueFilteredResultSet.getString("return_date")!=null && (issueFilteredResultSet.getString("return_date").compareTo("")!=0))
                {
                    returnDate = format.parse(issueFilteredResultSet.getString("return_date"));
                }
                else 
                {
                    String tempDate = format.format(new Date());
                    returnDate = format.parse(tempDate);
                    if(dueDate.before(returnDate)) // i.e due date before today and book is not returned.
                        flag++;
                }
                
                returnDateJoda = new LocalDate(returnDate);
                dueDateJoda = new LocalDate(dueDate);
            } catch (ParseException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(dueDate.before(returnDate))
            {
                Days d = Days.daysBetween(dueDateJoda, returnDateJoda);
                fine+=d.getDays();
                
            }
            if(issueFilteredResultSet.getString("return_date")==null || (issueFilteredResultSet.getString("return_date").compareTo("")==0))
            {
                currentIssue++;
            }
        }
        /**
         * setting values in Labels
         */
        issuedFiltered.setText("Total Books Issued : " + totalIssued);
        returnedFiltered.setText("Total Books Returned : " + returned);
        if(fine<0)
            fine=0;
        fineFiltered.setText("Total Fine : " + fine);
        currentFiltered.setText("Currently issued book count : " + currentIssue);
        if(flag!=0){
            //exceedLabel.setText("* "+ flag +" books have exceeded due date and are not returned. Assuming they are retuned today, total fine is being shown.");
        }
}
    /**
     * RESETS THE LABELS IN FILTERED PANEL AND RELOADS THE ISSUE DATA TABLE WITH COMPLETE ENTRIES
     */
    private void resetFilter()
    {
        loadIssueData();
        exceedLabel.setText("*");
        try {
            calculateStudentPerformance();
        } catch (SQLException ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * reseting the labels
         */
        issuedFiltered.setText("Total Books Issued : " );
        returnedFiltered.setText("Total Books Returned : " );
        fineFiltered.setText("Total Fine : " );
        currentFiltered.setText("Currently issued book count : " );
        from.setDate(null);
        to.setDate(null);
    }
    /**
     * This Method Resized the Image
     * @param srcImg
     * @param w
     * @param h
     * @return 
     */
    private Image getScaledImage(Image srcImg, int w, int h){
    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = resizedImg.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.drawImage(srcImg, 0, 0, w, h, null);
    g2.dispose();
    return resizedImg;
}
    public static BufferedImage resize(BufferedImage image, int width, int height) {
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
    Graphics2D g2d = (Graphics2D) bi.createGraphics();
    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
    g2d.drawImage(image, 0, 0, width, height, null);
    g2d.dispose();
    return bi;
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gender = new javax.swing.ButtonGroup();
        picLabel = new javax.swing.JLabel();
        uploadButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        fname = new javax.swing.JTextField();
        mname = new javax.swing.JTextField();
        lname = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        male = new javax.swing.JRadioButton();
        female = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        fathername = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        dob = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        category = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        primary = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mailing = new javax.swing.JTextArea();
        contactnumber = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        email = new javax.swing.JTextField();
        sameAsPrimary = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        yearLabel = new javax.swing.JLabel();
        year = new javax.swing.JSpinner();
        sem = new javax.swing.JSpinner();
        semLabel = new javax.swing.JLabel();
        rollno = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        cardno = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        cardsissued = new javax.swing.JSpinner();
        programme = new javax.swing.JComboBox();
        branch = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        passOutLabel = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        prevNavButton = new javax.swing.JButton();
        nextNavButton = new javax.swing.JButton();
        gotoNavButton = new javax.swing.JButton();
        navPrintStudent = new javax.swing.JButton();
        addStudentButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane4 = new javax.swing.JScrollPane();
        issueHistoryTable = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        from = new com.toedter.calendar.JDateChooser();
        to = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        applyFilterButton = new javax.swing.JButton();
        resetFilter = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        issuedFiltered = new javax.swing.JLabel();
        returnedFiltered = new javax.swing.JLabel();
        fineFiltered = new javax.swing.JLabel();
        currentFiltered = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        issued = new javax.swing.JLabel();
        returnedLabel = new javax.swing.JLabel();
        fineLabel = new javax.swing.JLabel();
        currentLabel = new javax.swing.JLabel();
        statusPic = new javax.swing.JLabel();
        statusMessage = new javax.swing.JLabel();
        deleteStudent = new javax.swing.JButton();
        exceedLabel = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        picLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/profile_pic.jpg"))); // NOI18N
        picLabel.setText("jLabel1");
        picLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        uploadButton.setText("Upload");
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("EDIT/VIEW STUDENT");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basic Information"));

        jLabel3.setText("First Name ");

        jLabel4.setText("Middle Name");

        gender.add(male);
        male.setSelected(true);
        male.setText("Male");

        gender.add(female);
        female.setText("Female");

        jLabel7.setText("Gender");

        jLabel9.setText("Father's Name");

        dob.setDateFormatString("dd-MM-yyyy");

        jLabel10.setText("Date Of Birth");

        jLabel11.setText("Last Name");

        jLabel15.setText("Category");

        category.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GEN", "OBC", "SC", "ST" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addComponent(male)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(female))))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fname, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(30, 30, 30)
                            .addComponent(mname, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addGap(21, 21, 21))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(30, 30, 30)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(fathername, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                                .addComponent(lname))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dob, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(fname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(fathername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(male)
                    .addComponent(female))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(dob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Contact Information"));

        primary.setColumns(20);
        primary.setRows(5);
        jScrollPane1.setViewportView(primary);

        jLabel8.setText("Primary Address ");

        jLabel12.setText("Mailing Address");

        mailing.setColumns(20);
        mailing.setRows(5);
        jScrollPane2.setViewportView(mailing);

        jLabel13.setText("Contact Number");

        jLabel14.setText("Email ID ");

        sameAsPrimary.setText("Same as Primary");
        sameAsPrimary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sameAsPrimaryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(contactnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(92, 92, 92)
                                .addComponent(jLabel8)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(sameAsPrimary))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(90, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(sameAsPrimary)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(contactnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Student Information"));

        jLabel2.setText("Programme");

        jLabel5.setText("Branch");

        yearLabel.setText("Year");

        year.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4, 1));

        sem.setModel(new javax.swing.SpinnerNumberModel(1, 1, 8, 1));

        semLabel.setText("Semester");

        jLabel17.setText("Roll No");

        jLabel18.setText("Card Nos *");

        jLabel19.setText("No of Cards Issued");

        cardsissued.setModel(new SpinnerNumberModel(2,0,Config.MAXCARDS,1));

        programme.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "B.Tech", "M.Tech" }));
        programme.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                programmeItemStateChanged(evt);
            }
        });

        branch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CSE", "ECE", "EEE" }));

        jLabel22.setText("* Separate using ',' (comma)");

        passOutLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        passOutLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        passOutLabel.setText("Pass Out : No");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(yearLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(12, 12, 12))
                            .addComponent(semLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(branch, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(programme, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 4, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cardsissued, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel17)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(rollno, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                                .addGap(87, 87, 87))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cardno)
                                .addGap(19, 19, 19))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(passOutLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(rollno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(programme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel18)
                    .addComponent(cardno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(branch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cardsissued, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(sem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(semLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passOutLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar1.setToolTipText("Navigation Bar");
        jToolBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jToolBar1.setName("Navigation Bar"); // NOI18N

        prevNavButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/navigation_previous.png"))); // NOI18N
        prevNavButton.setFocusable(false);
        prevNavButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevNavButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        prevNavButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevNavButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(prevNavButton);

        nextNavButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/navigation_next.png"))); // NOI18N
        nextNavButton.setFocusable(false);
        nextNavButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextNavButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextNavButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextNavButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(nextNavButton);

        gotoNavButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/navigation_goto.png"))); // NOI18N
        gotoNavButton.setFocusable(false);
        gotoNavButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gotoNavButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gotoNavButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoNavButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(gotoNavButton);

        navPrintStudent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print.png"))); // NOI18N
        navPrintStudent.setFocusable(false);
        navPrintStudent.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navPrintStudent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        navPrintStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                navPrintStudentActionPerformed(evt);
            }
        });
        jToolBar1.add(navPrintStudent);

        addStudentButton.setText("Save Changes");
        addStudentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStudentButtonActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        issueHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Issue_Id", "acc_no", "Book Title", "Author", "Issue Date", "Return Date", "Due Date"
            }
        ));
        issueHistoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                issueHistoryTableMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(issueHistoryTable);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setText("ISSUE HISTORY");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Add Filters : View issue history for a specific period"));

        from.setDateFormatString("dd-MM-yyyy");

        to.setDateFormatString("dd-MM-yyyy");

        jLabel21.setText("From :");

        jLabel23.setText("To:");

        applyFilterButton.setText("Apply Filter");
        applyFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyFilterButtonActionPerformed(evt);
            }
        });

        resetFilter.setText("Reset");
        resetFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(from, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addGap(18, 18, 18)
                .addComponent(to, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyFilterButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(applyFilterButton)
                        .addComponent(resetFilter))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(from, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(to, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Student Performance : Filtered"));

        issuedFiltered.setText("Total Books Issued :");

        returnedFiltered.setText("Total Books Returned :");

        fineFiltered.setText("Total Fine :");

        currentFiltered.setText("Currently issued book count :");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(issuedFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnedFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fineFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(issuedFiltered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnedFiltered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fineFiltered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(currentFiltered))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Student Performance : Overall"));

        issued.setText("Total Books Issued :");

        returnedLabel.setText("Total Books Returned :");

        fineLabel.setText("Total Fine :");

        currentLabel.setText("Currently issued book count :");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(issued, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fineLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(issued)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fineLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(currentLabel))
        );

        statusMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        deleteStudent.setText("Delete");
        deleteStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteStudentActionPerformed(evt);
            }
        });

        exceedLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        exceedLabel.setText("*");

        jLabel24.setText("* double click to open report for selected record");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(321, 321, 321)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(32, 32, 32)
                                                .addComponent(uploadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(220, 220, 220)
                                        .addComponent(addStudentButton)
                                        .addGap(27, 27, 27)
                                        .addComponent(statusPic, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(deleteStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(14, 14, 14))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(statusMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 731, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(215, 215, 215)
                                .addComponent(jLabel20))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(exceedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(202, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel24)
                        .addGap(80, 80, 80))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(picLabel)
                                .addGap(23, 23, 23)
                                .addComponent(uploadButton))
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addStudentButton, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(deleteStudent, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(statusPic, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(statusMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exceedLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        jPanel5.getAccessibleContext().setAccessibleName("Student Performance during Filter Period");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadButtonActionPerformed
        JFileChooser pic = new JFileChooser();
        pic.setFileFilter(Config.filter);
        int returnVal = pic.showDialog(null,"Choose Pic");
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            try {
                Image image,scaledImage;
                File f = pic.getSelectedFile();
                image = ImageIO.read(f);
                scaledImage = getScaledImage(image,168,130);
                picLabel.setIcon(new ImageIcon(scaledImage));
                this.picLoc = f.getAbsolutePath();
            } catch (IOException ex) {
                Error.errorDialog("Cannot upload pic");
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_uploadButtonActionPerformed
    /**
     * THIS FUNCTION RESETS FIELDS AFTER ADD QUERY IS EXECUTED. DOES NOT CHANGE JSpinners
     */
    private void reset()
    {
        fname.setText("");
        mname.setText("");
        lname.setText("");
        fathername.setText("");
        fname.setText("");
        category.setSelectedIndex(0);
        male.setSelected(true);
        primary.setText("");
        mailing.setText("");
        contactnumber.setText("");
        email.setText("");
        rollno.setText("");
        cardno.setText("");
        /**
         * this is to undo is next student is not passed out
         */
        year.setVisible(true);
        sem.setVisible(true);
        yearLabel.setVisible(true);
        semLabel.setVisible(true);
        passOutLabel.setText("Pass Out : No");
        
        statusPic.setIcon(null);
        statusMessage.setText("");
        exceedLabel.setText("*");
        
         try {
                picLabel.setIcon(new ImageIcon(ImageIO.read(new File("profile_pic.jpg"))));
            } catch (IOException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    /**
     * If Btech is selected give 4 yrs, if Mtech 2 yrs fromthe Combo Box
     * @param evt 
     */
    private void programmeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_programmeItemStateChanged
        int selected = programme.getSelectedIndex();
        switch(selected)
        {
            case 0 : year.setModel(new SpinnerNumberModel(1,1,4,1));
                     sem.setModel(new SpinnerNumberModel(1,1,8,1));
                break;
            case 1 : year.setModel(new SpinnerNumberModel(1,1,2,1));
                     sem.setModel(new SpinnerNumberModel(1,1,4,1));
                break;
        }
    }//GEN-LAST:event_programmeItemStateChanged
    public void addStudent() throws SQLException, ParseException, IOException,Exception 
    {
        PreparedStatement query = library.prepareStatement("update student set `roll_no` = ?,`First Name` = ?,`Middle Name` = ?,`Last Name` = ?,`Fathers Name` = ?,`Gender` = ?,`Date Of Birth` = ?,`Category` = ?,`Primary Address` = ?,`Mailing Address` = ?,`Contact Number` = ?,`E-Mail` = ?,`Programme` = ?,`Branch` = ?,`Year` = ?,`Semester` = ?,`Card Numbers` = ?,`Cards Issued` = ?,`pic_path` = ? where roll_no = ?");
        query.setInt(1,Integer.parseInt(rollno.getText()));
        query.setString(2,fname.getText());
        query.setString(3,mname.getText());
        query.setString(4,lname.getText());
        query.setString(5,fathername.getText());
        if(female.isSelected())
        query.setString(6,"Female");
        else
            query.setString(6,"Male");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        String date = dateFormat.format(dob.getDate());
        query.setString(7,date);
        query.setString(8,category.getSelectedItem().toString());
        query.setString(9,primary.getText());
        query.setString(10,mailing.getText());
        query.setString(11,contactnumber.getText());
        String[] mail = email.getText().split("[@.]+");
        try
        {
        if(mail[2]==null)
        {
            statusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            statusMessage.setText("wrong email");
        }
        
        else
        {
            query.setString(12,email.getText());
            query.setString(13,programme.getSelectedItem().toString());
            query.setString(14,branch.getSelectedItem().toString());
            query.setInt(15,Integer.parseInt(year.getModel().getValue().toString()));
            query.setInt(16,Integer.parseInt(sem.getModel().getValue().toString()));
            query.setString(17,cardno.getText()); //TODO add validation
            query.setInt(18,Integer.parseInt(cardsissued.getModel().getValue().toString()));
            query.setString(19,this.picLoc);
            query.setInt(20,currentRollNo);
            System.out.println("Add Student query" + query.toString());
            int update = query.executeUpdate();
            PreparedStatement query1 = library.prepareStatement("update issue set issuer_id = ? where issuer_id=?");
            query1.setInt(1, new Integer(rollno.getText()));
            query1.setInt(2, currentRollNo);
            int executeUpdate1 = query1.executeUpdate();
            if(update==1)
            {
                statusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
                statusMessage.setText("Student " + fname.getText() + " updated successfully and issue history refactored");
            }
        }
        }catch(Exception e)
        {
            e.printStackTrace();
            statusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            statusMessage.setText(e.getMessage());
        }
        /**
             * Refactoring issue database
             */
            
    }
    /**
     * DELETE STUDENT PERMANENTLY FROM DATABASE. ISSUE AND STUDENT TABLES ARE AFFECTED
     */
    private void deleteStudent()
    {
        int sure = JOptionPane.showConfirmDialog(null,"Are you sure you want to remove this Student permanently ?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(sure==JOptionPane.YES_OPTION)
        {
            if(Config.showDeleteDialog()==true)
            {
                try {
            
                    PreparedStatement query = library.prepareStatement("delete from student where roll_no = ?");
                    PreparedStatement query1 = library.prepareStatement("delete from issue where issuer_id = ?");
                    query.setInt(1,currentRollNo);
                    query1.setInt(1, currentRollNo);
                    int executeUpdate = query.executeUpdate();
                    int executeUpdate1 = query1.executeUpdate();
                    if(executeUpdate!=0)
                        JOptionPane.showMessageDialog(null,"1 Student successfully removed and issue history refactored.Loading next student","Student Deleted",JOptionPane.OK_OPTION);
                    /*
                    Load next student
                    */    
                    nextNavButtonActionPerformed(null);
                } catch (SQLException ex) {
                    Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Passwords do not match","Wrong Password",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * REGEX matcher
     * 
     * @param cardNoString
     * @param cardNoSpinner
     * @return finalString with incremented card nos.
     */
    public String regexMatch(String cardNoString,int cardNoSpinner)
    {
        /**
         * max no of cards han can be issued = 10 as array limit is 10
         */
        Integer[] incDigit = new Integer[10] ;
        String finalString = "";
        Pattern digitPattern = Pattern.compile("(\\d{1,})");
        Matcher digitMatcher = digitPattern.matcher(cardNoString);
       /*
        INCREMENTING THE FOUND INTEGERS
        */
        for(int i=0;i<cardNoSpinner;i++)
        {
             /*
            movies cursor to first find
            */
            if(digitMatcher.find()){
                System.out.println("ye lo"+ digitMatcher.group(0));
            incDigit[i]=(new Integer(digitMatcher.group(0))+cardNoSpinner); }
        }
        Pattern nonDigitPattern = Pattern.compile("(\\D{1,})");
        Matcher nonDigitMatcher = nonDigitPattern.matcher(cardNoString);
       /*
        MAKING FINAL STRING WITH INCREMENTED VALUES
        */
        for(int i=0;i<cardNoSpinner;i++)
        {
            if(nonDigitMatcher.find())
            {
                
                finalString += nonDigitMatcher.group(i) + incDigit[i].toString();
                System.out.println( "finalString : "+ finalString );
            }
        }
        return finalString;
    }
    private void addStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStudentButtonActionPerformed
       
        try {
            addStudent();
        } catch (SQLException ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            try {
                statusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex1);
            }
            statusMessage.setText(ex.getMessage());
        } catch (ParseException | IOException ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            try {
                statusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex1);
            }
            statusMessage.setText(ex.getMessage());
        }
        
        /**
         * SMART ADD FEATURES
         */
       /* if(smartAdd.isSelected())
        {
            int pro = programme.getSelectedIndex();
            int bra = branch.getSelectedIndex();
            int yea = Integer.parseInt(year.getModel().getValue().toString());
            int seme = Integer.parseInt(sem.getModel().getValue().toString());
            int rol = Integer.parseInt(rollno.getText())+1; 
            int ciss = Integer.parseInt(cardsissued.getModel().getValue().toString());
            String cnos = cardno.getText();
            try {
                picLabel.setIcon(new ImageIcon(ImageIO.read(new File("profile_pic.jpg"))));
            } catch (IOException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
             addWindowListener(new WindowAdapter(){
            public void windowClosing()
            {
                setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                dispose();
            }
            });
    
            programme.setSelectedIndex(pro);
            branch.setSelectedIndex(bra);
            year.setValue(yea);
            sem.setValue(seme);
            rollno.setText(new Integer(rol).toString());
            cardsissued.setValue(ciss);
            cardno.setText(regexMatch(cnos,ciss));
        }
        else
        {
            //reset();
            try {
                picLabel.setIcon(new ImageIcon(ImageIO.read(new File("profile_pic.jpg"))));
            } catch (IOException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
            addWindowListener(new WindowAdapter(){
            public void windowClosing()
            {
               setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                dispose();
                
            }
            });
    
        } */
    }//GEN-LAST:event_addStudentButtonActionPerformed

    private void sameAsPrimaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sameAsPrimaryActionPerformed
        mailing.setText(primary.getText());
    }//GEN-LAST:event_sameAsPrimaryActionPerformed

    private void prevNavButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevNavButtonActionPerformed
        
            reset();
           
          try {
            PreparedStatement st = library.prepareStatement("select * from student where roll_no < ?");
            st.setInt(1, currentRollNo);
            loadViewResultSet = st.executeQuery();
            loadViewResultSet.last();
            currentRollNo = loadViewResultSet.getInt("roll_no");
            loadViewData(currentRollNo);
        } catch (SQLException ex) {
            successFailure(false,"No Data found");
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }//GEN-LAST:event_prevNavButtonActionPerformed

    private void nextNavButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextNavButtonActionPerformed
        reset();
           
           
           
            try {
                PreparedStatement st = library.prepareStatement("select * from student where roll_no > ?");
                st.setInt(1, currentRollNo);
                loadViewResultSet = st.executeQuery();
                loadViewResultSet.first();
                currentRollNo = loadViewResultSet.getInt("roll_no");
                loadViewData(currentRollNo);
            } catch (SQLException ex) {
                successFailure(false,"No Data found");
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            
           }
            
    }//GEN-LAST:event_nextNavButtonActionPerformed

    private void gotoNavButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoNavButtonActionPerformed
        reset();
          
            
        String gotoRollNo =  JOptionPane.showInputDialog(null, "Enter Roll No", "Goto a specific record",JOptionPane.QUESTION_MESSAGE);
        try {
            loadViewData(new Integer(gotoRollNo));
        } catch (SQLException ex) {
            successFailure(false,"No Data found");
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_gotoNavButtonActionPerformed

    private void applyFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyFilterButtonActionPerformed
        if(from.getDate().after(to.getDate()))
            JOptionPane.showMessageDialog(null,"The 'to' date should be after the 'from' date");
        else
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
            String fromDate = format.format(from.getDate());
            String toDate = format.format(to.getDate());
            LocalDate fromDateJoda = new LocalDate(fromDate);
            LocalDate toDateJoda = new LocalDate(toDate);
            try {
                loadFilteredIssueData(fromDateJoda, toDateJoda);
                calculateFilteredPerformance(fromDateJoda,toDateJoda);
            } catch (SQLException ex) {
                Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_applyFilterButtonActionPerformed

    private void resetFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetFilterActionPerformed
        resetFilter();
    }//GEN-LAST:event_resetFilterActionPerformed

    private void deleteStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteStudentActionPerformed
        deleteStudent();
    }//GEN-LAST:event_deleteStudentActionPerformed

    private void issueHistoryTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_issueHistoryTableMousePressed
        if(evt.getClickCount()==2)
        {
            new ReportGenerator(new Integer(issueHistoryTable.getModel().getValueAt(issueHistoryTable.getSelectedRow(), 0).toString())).setVisible(true);
        }
    }//GEN-LAST:event_issueHistoryTableMousePressed

    private void navPrintStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_navPrintStudentActionPerformed
        PrintUtilities.printComponent(this);
    }//GEN-LAST:event_navPrintStudentActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditViewStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditViewStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditViewStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditViewStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new EditViewStudent().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addStudentButton;
    private javax.swing.JButton applyFilterButton;
    private javax.swing.JComboBox branch;
    private javax.swing.JTextField cardno;
    private javax.swing.JSpinner cardsissued;
    private javax.swing.JComboBox category;
    private javax.swing.JTextField contactnumber;
    private javax.swing.JLabel currentFiltered;
    private javax.swing.JLabel currentLabel;
    private javax.swing.JButton deleteStudent;
    private com.toedter.calendar.JDateChooser dob;
    private javax.swing.JTextField email;
    private javax.swing.JLabel exceedLabel;
    private javax.swing.JTextField fathername;
    private javax.swing.JRadioButton female;
    private javax.swing.JLabel fineFiltered;
    private javax.swing.JLabel fineLabel;
    private javax.swing.JTextField fname;
    private com.toedter.calendar.JDateChooser from;
    private javax.swing.ButtonGroup gender;
    private javax.swing.JButton gotoNavButton;
    private javax.swing.JTable issueHistoryTable;
    private javax.swing.JLabel issued;
    private javax.swing.JLabel issuedFiltered;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField lname;
    private javax.swing.JTextArea mailing;
    private javax.swing.JRadioButton male;
    private javax.swing.JTextField mname;
    private javax.swing.JButton navPrintStudent;
    private javax.swing.JButton nextNavButton;
    private javax.swing.JLabel passOutLabel;
    private javax.swing.JLabel picLabel;
    private javax.swing.JButton prevNavButton;
    private javax.swing.JTextArea primary;
    private javax.swing.JComboBox programme;
    private javax.swing.JButton resetFilter;
    private javax.swing.JLabel returnedFiltered;
    private javax.swing.JLabel returnedLabel;
    private javax.swing.JTextField rollno;
    private javax.swing.JButton sameAsPrimary;
    private javax.swing.JSpinner sem;
    private javax.swing.JLabel semLabel;
    private javax.swing.JLabel statusMessage;
    private javax.swing.JLabel statusPic;
    private com.toedter.calendar.JDateChooser to;
    private javax.swing.JButton uploadButton;
    private javax.swing.JSpinner year;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables
}
