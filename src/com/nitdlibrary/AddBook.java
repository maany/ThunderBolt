/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nitdlibrary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import managers.LocationManager;
import managers.SubjectManager;

/**
 *
 * @author MAYANK
 */
public class AddBook extends javax.swing.JFrame {

    /**
     * Creates new form AddBook
     */
    
    Connection library;
    
    public AddBook(Connection library) {
        setVisible(true);
        this.library = library;
        //redirect output to output.txt
        /*PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("output_addbook.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(out);*/
        System.out.println("Inside Add Book. connection set");
        initComponents();
        
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setDefaultsSubjectCombo();
        updateSubjects();
        subjectCodeCombo.setVisible(false);
        
        
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
            Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
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
        else if(availableCombo.getSelectedIndex()==3)
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

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("ADD NEW BOOK TO LIBRARY");

        addButton.setText("ADD");
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
                        .addComponent(pagination, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
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

        accDate.setDateFormatString("dd-MM-yyyy");

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
                .addGap(17, 17, 17)
                .addComponent(jButton3)
                .addGap(3, 3, 3)
                .addComponent(locationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accNo, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(availableCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(locationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        );

        addMultipleButton.setText("ADD Multiple");
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
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(subjectCodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(subjectCodeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(21, 21, 21))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(subjectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(subjectCodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(subjectCodeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(subjectClassNoLabel))
                .addGap(18, 18, 18)
                .addComponent(subjectManagerLaunchButton)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE)))
                .addContainerGap(31, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(235, 235, 235)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(messageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addMultipleButton)
                                .addGap(52, 52, 52)
                                .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(301, 301, 301)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(185, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(picLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            PreparedStatement query = library.prepareStatement("insert into books(title,author,author2,author3,subject,subject_code,classno,ISBN,publisher,edition,price,year,pagination,location,acc_date,acc_no,status,status_tag) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
            System.out.println("Add Book query : " + query.toString());
            int executeUpdate = query.executeUpdate();
            if(executeUpdate==1)
            {
            picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
            messageLabel.setText(" Book added successfully : " + title.getText() );
            /**
             * Auto Increment acc no
             */
            accNo.setText(new Integer(Integer.parseInt(accNo.getText())+1).toString());
            // JOptionPane.showMessageDialog(null, "Book successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            try {
                picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex1);
            }
            messageLabel.setText(" Error : " + ex.getMessage() );
            Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception ex)
        {
            try {
                picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex1);
            }
            messageLabel.setText(" Error : " + ex.getMessage() );
        }
    }//GEN-LAST:event_addButtonActionPerformed
    /**
     * Make Multiple entries to the books database using transactions. And increment the AccNo text field
     * @param evt 
     */
    private void addMultipleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMultipleButtonActionPerformed
       
        /**
        * Date to formatted String conversion
        */
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");
        String formattedDate = dateFormat.format(accDate.getDate());
        /**
         * set autoCommit to false
         */
        try {
            library.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * check if starting accno is not null.
         */
        int i=0;
        int count =0;
        int acc = Integer.parseInt(accNo.getText());
        if(accNo.getText()==null)
         {
            JOptionPane.showMessageDialog(null, "Please fill Accession Number(Acc No.) for the first book", "Warning", JOptionPane.WARNING_MESSAGE);
         }
        else
        {
            String index = JOptionPane.showInputDialog(null, "Enter nunber of books to add", "Add Multiple Books ", JOptionPane.QUESTION_MESSAGE);
            
                try {
                    for(i=0;i<Integer.parseInt(index);i++)
                    {
                    PreparedStatement query = library.prepareStatement("insert into books(title,author,author2,author3,subject,subject_code,classno,ISBN,publisher,edition,price,year,pagination,location,acc_date,acc_no,status,status_tag) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
                    query.setString(15,formattedDate);
                    query.setInt(16,acc+i);
                    query.setInt(17,statusToIntMapper());
                    if(statusToIntMapper()==3)
                        query.setInt(18,1);
                    else
                        query.setInt(18,0);
                    System.out.println("Add Book query : " + query.toString()); 
                    int executeUpdate = query.executeUpdate();
                    if(executeUpdate==1)
                        count ++ ;
                    } 
                   /**
                    * Commiting Transaction
                    * Loop Over 
                    */
                    library.commit();
                    library.setAutoCommit(true);
                
                if(count==Integer.parseInt(index))
                    {
                    try {
                        picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
                    } catch (IOException ex) {
                        Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
                    }
                         messageLabel.setText(index + " Books added successfully from acc_no :" + acc + " to acc_no :" + (acc + Integer.parseInt(index) -1));
                         accNo.setText((new Integer(acc + Integer.parseInt(index)).toString()));
                    }
                    else if(count!=0)
                            {
                try {
                    picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
                } catch (IOException ex) {
                    Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
                }
                         messageLabel.setText("Only " + count + " Books added successfully" );
                         accNo.setText((new Integer(acc+ Integer.parseInt(index)+1).toString()));
                            }
            
                }catch (SQLException ex) {
                    try {
                        picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
                    } catch (IOException ex1) {
                        Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                         messageLabel.setText(ex.getMessage());
                    //Error.errorDialog(ex.getMessage());
                    Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                     try {
                        picLabel.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
                    } catch (IOException ex1) {
                        Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                         messageLabel.setText(ex.getMessage());
                    Logger.getLogger(AddBook.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        
    }//GEN-LAST:event_addMultipleButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser accDate;
    private javax.swing.JTextField accNo;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addMultipleButton;
    private javax.swing.JTextField author1;
    private javax.swing.JTextField author2;
    private javax.swing.JTextField author3;
    private javax.swing.JComboBox availableCombo;
    private javax.swing.JTextField edition;
    private javax.swing.JTextField isbn;
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
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JTextField pagination;
    private javax.swing.JLabel picLabel;
    private javax.swing.JTextField price;
    private javax.swing.JTextField publisher;
    private javax.swing.JLabel subjectClassNoLabel;
    private javax.swing.JComboBox subjectCodeCombo;
    private javax.swing.JLabel subjectCodeLabel;
    private javax.swing.JComboBox subjectCombo;
    private javax.swing.JButton subjectManagerLaunchButton;
    private javax.swing.JTextField title;
    private javax.swing.JTextField year;
    // End of variables declaration//GEN-END:variables
}
