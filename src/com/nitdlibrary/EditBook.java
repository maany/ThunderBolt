/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nitdlibrary;

import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import managers.Config;
import managers.LocationManager;
import managers.PrintUtilities;
import managers.SubjectManager;
import net.proteanit.sql.DbUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 *
 * @author MAYANK
 */
public class EditBook extends javax.swing.JFrame {

    
    
    Connection library;
    int acc;
    ResultSet defaults = null,issueResultSet = null,issueFilteredResultSet=null;
    /**
     * Load details from database and display it on controls
     * @param library
     * @param acc 
     */
    public EditBook(Connection library,int acc) {
        this.library = library;
        this.acc =acc;
        initComponents();
        subjectCodeCombo.setVisible(false);
        updateComponents();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        /**
         * setting dispose on close
         */
        addWindowListener(new WindowAdapter(){
            public void windowClosing()
            {
                dispose();
            }
        });
      }
    /**
     * EDIT BOOK SPECIFIC METHOD : THIS TAKES ACC NO AND UPDATES ALL COMPONENTS
     */
    public void updateComponents()
    {
        try {
            /**
             * LOADING INITIAL VALUES
             */
            PreparedStatement getDefault = library.prepareStatement("select * from books where acc_no = ?");
            getDefault.setInt(1, acc);
            defaults = getDefault.executeQuery();
            defaults.next();
            /**
              * Assigning VALues from ResultSet default.
              */
                title.setText(defaults.getString("title"));
                author1.setText(defaults.getString("author"));
                author2.setText(defaults.getString("author2"));
                author3.setText(defaults.getString("author3"));
                isbn.setText(defaults.getString("ISBN"));
                publisher.setText(defaults.getString("publisher"));
                edition.setText(defaults.getString("edition"));
                price.setText((new Float(defaults.getFloat("price")).toString()));
                String[] yearFull = defaults.getString("year").split("[-]");
                year.setText(yearFull[0]);
                pagination.setText((new Integer(defaults.getInt("pagination")).toString()));
                java.util.Date date = new SimpleDateFormat("yyyy-mm-d",Locale.ENGLISH).parse(defaults.getString("acc_date"));
                accDate.setDate(date);
                setDefaultsSubjectCombo();
                subjectCombo.setSelectedItem(defaults.getObject("subject"));
                updateSubjects();
                locationLabel.setText(defaults.getString("location"));
                accNo.setText(defaults.getString("acc_no"));
                int status = defaults.getInt("status");
                switch(status)
                {
                    case 0: availableCombo.setSelectedIndex(2); break;
                        case 2: availableCombo.setSelectedIndex(1); break;
                            case 1: availableCombo.setSelectedIndex(0); break;
                                case 3: availableCombo.setSelectedIndex(3); break;
                }
                exceedLabel.setText("*");
                loadIssueData();
                calculateBookPerformance();
                
        } catch (SQLException ex) {
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   /**
    * This method obtains the book history from database and sets the table model
    */
    public void loadIssueData() throws SQLException
    {
        PreparedStatement query = library.prepareStatement("select issue.issue_id,issue.issuer_id as 'roll_no',student.`First Name`,issue.issue_date,issue.due_date,issue.return_date from issue,student where issue.acc_no = ? and student.roll_no = issue.issuer_id",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        query.setInt(1,acc);
        issueResultSet = query.executeQuery();
        /**
         * Changing date formats in result Set
         */
        Integer[] fields = new Integer[3];
        fields[0] = 3;
        fields[1]= 4;
        fields[2]= 5;
        issueHistoryTable.setModel(DbUtils.resultSetToTableModel(issueResultSet));
        issueResultSet.beforeFirst();
        issueHistoryTable.setModel(Config.changeResultSetDateFormat(issueResultSet,issueHistoryTable.getModel(),fields));
        
    }
    public void calculateBookPerformance() throws SQLException
    {
         issueResultSet.beforeFirst();
        java.util.Date returnDate = null,dueDate = null;
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
                    String tempDate = format.format(new java.util.Date());
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
                Days d = Days.daysBetween(returnDateJoda, dueDateJoda);
                fine+=d.getDays();
            }
            if(issueResultSet.getString("return_date")==null || (issueResultSet.getString("return_date").compareTo("")==0))
            {
                currentIssue++;
            }
            
    }
         /**
         * setting values in Labels
         */
        issued.setText("No of times Issued : " + totalIssued);
        returnedLabel.setText("No of times Returned : " + returned);
        if(fine<0)
            fine=0;
        fineLabel.setText("Total Fine Obtained: " + fine);
        if(currentIssue!=0)
        currentLabel.setText("Currently issued : Yes");
        else
        currentLabel.setText("Currently issued : No");
        if(flag!=0)
            exceedLabel.setText("*OverDue : "+ flag +" student has exceeded due date and has not returned. Assuming he/she retuns today, total fine is being shown.");
                    else
            exceedLabel.setText("*");
   }
    /**
     * load result set for filtered data and apply model to table
     */
    private void loadFilteredIssueData(LocalDate from,LocalDate to) throws SQLException
    {
         
            PreparedStatement query = library.prepareStatement("select issue.issue_id,issue.issuer_id as 'roll_no',student.`First Name`,issue.issue_date,issue.due_date,issue.return_date from issue,student where issue.acc_no = ? and student.roll_no = issue.issuer_id and (issue.issue_date between ? and ?)");
            query.setInt(1,acc);
            query.setString(2,from.toString("yyyy-MM-dd"));
            query.setString(3,to.toString("yyyy-MM-dd"));
            System.out.println(query.toString());
            issueFilteredResultSet = query.executeQuery();
            issueHistoryTable.setModel(DbUtils.resultSetToTableModel(issueFilteredResultSet));
     }
    /**
    * THIS FUNCTION CALCULATES DATA FOR FILTERED RESULTSET AND UPDATES THE LABELS
    * @param from
    * @param to 
    */
    private void calculateFilteredPerformance(LocalDate from,LocalDate to) throws SQLException
   {
        issueFilteredResultSet.beforeFirst();
        java.util.Date returnDate = null,dueDate = null;
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
                    String tempDate = format.format(new java.util.Date());
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
                Days d = Days.daysBetween(returnDateJoda, dueDateJoda);
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
        issuedFiltered.setText("No of times Issued : " + totalIssued);
        returnedFiltered.setText("No of times Returned : " + returned);
        if(fine<0)
            fine=0;
        fineFiltered.setText("Total Fine Obtained : " + fine);
         if(currentIssue!=0)
        currentFiltered.setText("Remained issued : Yes");
        else
        currentFiltered.setText("Remained issued : No");
       // if(flag!=0){
            //exceedLabel.setText("* "+ flag +" books have exceeded due date and are not returned. Assuming they are retuned today, total fine is being shown.");
      //  }
}
    /**
     * RESETS THE LABELS IN FILTERED PANEL AND RELOADS THE ISSUE DATA TABLE WITH COMPLETE ENTRIES
     */
    private void resetFilter()
    {
        
        try {
            loadIssueData();
            calculateBookPerformance();
        } catch (SQLException ex) {
            Logger.getLogger(EditViewStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * reseting the labels
         */
        issuedFiltered.setText("No of times Issued : " );
        returnedFiltered.setText("No of times Returned : " );
        fineFiltered.setText("Total Fine Obtained : " );
        currentFiltered.setText("Currently issued : " );
        exceedLabel.setText("*");
        from.setDate(null);
        to.setDate(null);
    }
    /**
     * SETS THE VALUE OF location string after retrieving value from LocationManger
     * @param location
     */
    public void setLocation(String location)
    {
        locationLabel.setText(location);
    }
    /**
     * RETURNS THE COPY OF subjectCombo to SubjectManager
     */
    public JComboBox getSubjectCombo()
    {
        return subjectCombo;
    }
    /**
     * SETS THE ELEMEMTS ON SUNJECTCOMBO ON FORM LOAD AND ALSO FOR SUBJECT CODE COMBO
     */
    public void setDefaultsSubjectCombo()
    {
        try {
            Statement st = library.createStatement();
            ResultSet subjectNamesResultSet = st.executeQuery("select subject_name,subject_code from subjects");
            DefaultComboBoxModel subjectNames = new DefaultComboBoxModel() {};
            while(subjectNamesResultSet.next())
            {
                subjectNames.addElement(subjectNamesResultSet.getObject("subject_name"));
                subjectCodeCombo.addItem(subjectNamesResultSet.getObject("subject_code"));
            }
            
            subjectCombo.setModel(subjectNames);
        } catch (SQLException ex) {
            Error.errorDialog(ex.toString());
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
    * THIS METHOD CHECKS THE SUBJECTCOMBO VALUE AND UPDATES THE REMAINING SUBJECT FIELDS
    */
    private void updateSubjects()
    {
        try {
            String selelectedSubject = subjectCombo.getSelectedItem().toString();
            
            PreparedStatement query = library.prepareStatement("select subject_code,subject_class_no from subjects where subject_name = ?");
            query.setString(1, selelectedSubject);
            System.out.println(query.toString());
            ResultSet rs = query.executeQuery();
            rs.next();
            subjectCodeLabel.setText(rs.getString("subject_code"));
            subjectCodeCombo.setSelectedItem(rs.getString("subject_code"));
            subjectClassNoLabel.setText(rs.getString("subject_class_no"));
        } catch (SQLException ex) {
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void updateSubjectsViaCode()
    {
        try {
            String selelectedSubject = subjectCodeCombo.getSelectedItem().toString();
            
            PreparedStatement query = library.prepareStatement("select subject_name,subject_class_no from subjects where subject_code = ?");
            query.setString(1, selelectedSubject);
            ResultSet rs = query.executeQuery();
            rs.next();
            subjectCodeLabel.setText(subjectCodeCombo.getSelectedItem().toString());
            subjectCombo.setSelectedItem(rs.getString("subject_name"));
            subjectClassNoLabel.setText(rs.getString("subject_class_no"));
        } catch (SQLException ex) {
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * THIS METHOD MAPS INT VALUES TO MAKE_AVAILABLE_COMBO ITEMS I.ETHE STATUS VALUE TO BE STORED IN BOOKS TABLE
     * @return status which is the mapped integer. 0 -> none 1-> students 2->faculty
     */
    public int statusToIntMapper()
    {
        int status = 0;
        if(availableCombo.getSelectedIndex()==0)
            status = 1;
        else if (availableCombo.getSelectedIndex()==1)
            status = 2;
        else if (availableCombo.getSelectedIndex()==3)
            status = 3;
        return status;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        title = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        author1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        author2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        author3 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        isbn = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        price = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        publisher = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        edition = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        year = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        pagination = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        accDate = new com.toedter.calendar.JDateChooser();
        accNo = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        availableCombo = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        addMultipleButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        subjectCombo = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        subjectCodeLabel = new javax.swing.JLabel();
        subjectClassNoLabel = new javax.swing.JLabel();
        subjectManagerLaunchButton = new javax.swing.JButton();
        subjectCodeCombo = new javax.swing.JComboBox();
        picLabel = new javax.swing.JLabel();
        messageLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        issueHistoryTable = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        from = new com.toedter.calendar.JDateChooser();
        to = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        applyFilterButton = new javax.swing.JButton();
        resetFilter = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        issuedFiltered = new javax.swing.JLabel();
        returnedFiltered = new javax.swing.JLabel();
        fineFiltered = new javax.swing.JLabel();
        currentFiltered = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        issued = new javax.swing.JLabel();
        returnedLabel = new javax.swing.JLabel();
        fineLabel = new javax.swing.JLabel();
        currentLabel = new javax.swing.JLabel();
        exceedLabel = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        prevNavButton = new javax.swing.JButton();
        nextNavButton = new javax.swing.JButton();
        gotoNavButton = new javax.swing.JButton();
        navPrintButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("VIEW/EDIT BOOK");

        addButton.setText("Save Changes");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Title and Author Details"));

        jLabel2.setText("Title");

        title.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleActionPerformed(evt);
            }
        });

        jLabel3.setText("Author1");

        jLabel4.setText("Author2");

        jLabel5.setText("Author3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(author3, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(author2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(author1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(author1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(author2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(author3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "General Info"));

        jLabel9.setText("ISBN");

        jLabel12.setText("Price");

        jLabel10.setText("Publisher");

        jLabel11.setText("Edition");

        jLabel13.setText("Year");

        jLabel14.setText("Pagination");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(isbn, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jLabel10))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(pagination, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE))
                    .addComponent(publisher))
                .addGap(22, 22, 22)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(edition, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(isbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(edition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(pagination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Library related info"));

        jLabel15.setText("Acc date");

        jLabel16.setText("Acc No.");

        accDate.setDateFormatString("yyyy-MM-dd");

        jButton3.setText("Location");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        availableCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Students", "Faculty", "None", "Both" }));

        jLabel18.setText("Available To :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(accDate, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(locationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accNo, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(availableCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(accNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3)
                        .addComponent(availableCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(accDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(locationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        addMultipleButton.setText("Delete");
        addMultipleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMultipleButtonActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Subject Related Info"));

        subjectCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        subjectCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                subjectComboItemStateChanged(evt);
            }
        });
        subjectCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjectComboActionPerformed(evt);
            }
        });

        jLabel6.setText("Subject");

        jLabel7.setText("Subject Code  : ");

        jLabel8.setText("Class No         :");

        subjectClassNoLabel.setText("jLabel19");

        subjectManagerLaunchButton.setText("Manage Subjects");
        subjectManagerLaunchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjectManagerLaunchButtonActionPerformed(evt);
            }
        });

        subjectCodeCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                subjectCodeComboItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(subjectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(subjectManagerLaunchButton)
                                    .addComponent(subjectClassNoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(subjectCodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(subjectCodeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subjectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(subjectCodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(subjectClassNoLabel))
                .addGap(18, 18, 18)
                .addComponent(subjectManagerLaunchButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subjectCodeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        issueHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        issueHistoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                issueHistoryTableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(issueHistoryTable);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setText("BOOK HISTORY");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Add Filters : View issue history for a specific period"));

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

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(from, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(to, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyFilterButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(applyFilterButton)
                        .addComponent(resetFilter))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(from, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(to, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Book Performance : Filtered"));

        issuedFiltered.setText("No of times Issued :");

        returnedFiltered.setText("No of Times Retuned :");

        fineFiltered.setText("Total Fine Obtained:");

        currentFiltered.setText("Remained  issued  :");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(issuedFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnedFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fineFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentFiltered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(issuedFiltered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnedFiltered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fineFiltered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(currentFiltered))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Book Performance : Overall"));

        issued.setText("Total Books Issued :");

        returnedLabel.setText("Total Books Returned :");

        fineLabel.setText("Total Fine :");

        currentLabel.setText("Currently issued book count :");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(issued, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fineLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(issued)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fineLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(currentLabel))
        );

        exceedLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        exceedLabel.setText("*");

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

        navPrintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print.png"))); // NOI18N
        navPrintButton.setFocusable(false);
        navPrintButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navPrintButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        navPrintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                navPrintButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(navPrintButton);

        jLabel20.setText("* double click to open report for selected record");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(235, 235, 235)
                        .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(350, 350, 350)
                        .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(222, 222, 222)
                        .addComponent(addMultipleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124)
                        .addComponent(addButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(349, 349, 349)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(259, 259, 259)
                        .addComponent(jLabel19)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(exceedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addGap(22, 22, 22)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exceedLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addMultipleButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator1)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void titleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_titleActionPerformed

    private void subjectComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_subjectComboItemStateChanged
        updateSubjects();
    }//GEN-LAST:event_subjectComboItemStateChanged

    private void subjectManagerLaunchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subjectManagerLaunchButtonActionPerformed
      new SubjectManager(this).setVisible(true);
    }//GEN-LAST:event_subjectManagerLaunchButtonActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        new LocationManager(this).setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void subjectComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subjectComboActionPerformed
        updateSubjectsViaCode();
    }//GEN-LAST:event_subjectComboActionPerformed

    private void subjectCodeComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_subjectCodeComboItemStateChanged
        
    }//GEN-LAST:event_subjectCodeComboItemStateChanged

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            PreparedStatement query = library.prepareStatement("update books set title = ?,author = ?,author2 = ?,author3 = ?,subject = ?,subject_code = ?,classno = ?,ISBN = ?,publisher = ?,edition = ?,price = ?,year = ?,pagination = ?,location = ?,acc_date = ?,acc_no = ?,status = ?,status_tag = ? where acc_no = ?");
            query.setString(1,title.getText());
            query.setString(2,author1.getText());
            query.setString(3,author2.getText());
            query.setString(4,author3.getText());
            query.setString(5,subjectCombo.getSelectedItem().toString());
            query.setString(6,subjectCodeLabel.getText());
            query.setString(7,subjectClassNoLabel.getText());
            query.setString(8,isbn.getText());
            query.setString(9,publisher.getText());
            query.setString(10,edition.getText());
            query.setFloat(11,Float.parseFloat(price.getText()));
            query.setInt(12, Integer.parseInt(year.getText())); // mysql will convert integer to year as per documentation @  http://dev.mysql.com/doc/refman/5.0/en/year.html
            query.setInt(13, Integer.parseInt(pagination.getText()));
            query.setString(14,locationLabel.getText());
            /**
             * Date to formatted String conversion
             */
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");
            String formattedDate = dateFormat.format(accDate.getDate());
            query.setString(15,formattedDate);
            query.setInt(16,Integer.parseInt(accNo.getText()));
            query.setInt(17,statusToIntMapper());
            if(statusToIntMapper()==3)
            query.setInt(18,1);
            else
                query.setInt(18,0);
            query.setInt(19,acc);
            System.out.println("Edit Book query : " + query.toString());
            int executeUpdate = query.executeUpdate();
            
            /**
             * Refactoring issue database
             */
            PreparedStatement query1 = library.prepareStatement("update issue set acc_no = ? where acc_no=?");
            query1.setInt(1, new Integer(accNo.getText()));
            query1.setInt(2, acc);
            int executeUpdate1 = query1.executeUpdate();
            if(executeUpdate==1)
            {
            picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
            messageLabel.setText(" Book modified and issue history refactored successfully : " + title.getText() );
            /**
             * Auto Increment acc no
             */
            //accNo.setText(new Integer(Integer.parseInt(accNo.getText())+1).toString());
            // JOptionPane.showMessageDialog(null, "Book successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            try {
                picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex1);
            }
            messageLabel.setText(" Error : " + ex.getMessage() );
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception ex)
        {
            try {
                picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex1);
            }
            messageLabel.setText(" Error : " + ex.getMessage() );
        }
    }//GEN-LAST:event_addButtonActionPerformed
    /**
     * Make Multiple entries to the books database using transactions. And increment the AccNo text field
     * @param evt 
     */
    private void addMultipleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMultipleButtonActionPerformed
        int sure = JOptionPane.showConfirmDialog(null,"Are you sure you want to remove this book from library. p.s All issue history will be deleted?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(sure==JOptionPane.YES_OPTION)
        {
            if(Config.showDeleteDialog()==true)
            {
                try {
            
                    PreparedStatement query = library.prepareStatement("delete from books where acc_no = ?");
                    PreparedStatement query1 = library.prepareStatement("delete from issue where acc_no = ?");
                    query.setInt(1,acc);
                    query1.setInt(1,acc);
                    int executeUpdate = query.executeUpdate();query1.executeUpdate();
                    if(executeUpdate!=0)
                        JOptionPane.showMessageDialog(null,"1 record successfully removed and Issue History deleted","Book Deleted",JOptionPane.OK_OPTION);
                    /*
                    EXITING THIS PAGE
                    */    
                    dispose();
                } catch (SQLException ex) {
                    Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Passwords do not match","Wrong Password",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_addMultipleButtonActionPerformed

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

    private void issueHistoryTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_issueHistoryTableMousePressed
        if(evt.getClickCount()==2)
        {
            int issueID = Integer.parseInt(issueHistoryTable.getModel().getValueAt(issueHistoryTable.getSelectedRow(),0).toString());
            new ReportGenerator(issueID).setVisible(true);
        }
    }//GEN-LAST:event_issueHistoryTableMousePressed

    private void prevNavButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevNavButtonActionPerformed

        

        try {
            PreparedStatement st = library.prepareStatement("select * from books where acc_no < ?");
            st.setInt(1, acc);
            defaults = st.executeQuery();
            defaults.last();
           acc = defaults.getInt("acc_no");
           updateComponents();
        } catch (SQLException ex) {
            Error.errorDialog("No Data found");
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_prevNavButtonActionPerformed

    private void nextNavButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextNavButtonActionPerformed
        

        try {
            PreparedStatement st = library.prepareStatement("select * from books where acc_no > ?");
            st.setInt(1, acc);
            defaults = st.executeQuery();
            defaults.first();
            acc = defaults.getInt("acc_no");
            updateComponents();
        } catch (SQLException ex) {
            Error.errorDialog("No Data found");
            Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, ex);

        }

    }//GEN-LAST:event_nextNavButtonActionPerformed

    private void gotoNavButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoNavButtonActionPerformed
        try
        {
        String gotoRollNo =  JOptionPane.showInputDialog(null, "Enter Acc No", "Goto a specific record",JOptionPane.QUESTION_MESSAGE);
        acc = new Integer(gotoRollNo);
        updateComponents();
        }catch(Exception e)
        {
        Error.errorDialog("No Data found :" + e.getMessage());
        Logger.getLogger(EditBook.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_gotoNavButtonActionPerformed

    private void navPrintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_navPrintButtonActionPerformed
        PrintUtilities.printComponent(this);
    }//GEN-LAST:event_navPrintButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser accDate;
    private javax.swing.JTextField accNo;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addMultipleButton;
    private javax.swing.JButton applyFilterButton;
    private javax.swing.JTextField author1;
    private javax.swing.JTextField author2;
    private javax.swing.JTextField author3;
    private javax.swing.JComboBox availableCombo;
    private javax.swing.JLabel currentFiltered;
    private javax.swing.JLabel currentLabel;
    private javax.swing.JTextField edition;
    private javax.swing.JLabel exceedLabel;
    private javax.swing.JLabel fineFiltered;
    private javax.swing.JLabel fineLabel;
    private com.toedter.calendar.JDateChooser from;
    private javax.swing.JButton gotoNavButton;
    private javax.swing.JTextField isbn;
    private javax.swing.JTable issueHistoryTable;
    private javax.swing.JLabel issued;
    private javax.swing.JLabel issuedFiltered;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton navPrintButton;
    private javax.swing.JButton nextNavButton;
    private javax.swing.JTextField pagination;
    private javax.swing.JLabel picLabel;
    private javax.swing.JButton prevNavButton;
    private javax.swing.JTextField price;
    private javax.swing.JTextField publisher;
    private javax.swing.JButton resetFilter;
    private javax.swing.JLabel returnedFiltered;
    private javax.swing.JLabel returnedLabel;
    private javax.swing.JLabel subjectClassNoLabel;
    private javax.swing.JComboBox subjectCodeCombo;
    private javax.swing.JLabel subjectCodeLabel;
    private javax.swing.JComboBox subjectCombo;
    private javax.swing.JButton subjectManagerLaunchButton;
    private javax.swing.JTextField title;
    private com.toedter.calendar.JDateChooser to;
    private javax.swing.JTextField year;
    // End of variables declaration//GEN-END:variables
}
