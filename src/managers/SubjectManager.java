/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managers;
import com.nitdlibrary.AddBook;
import com.nitdlibrary.EditBook;
import com.nitdlibrary.NITDLibrary;
import com.nitdlibrary.MainScreen;
import com.nitdlibrary.Error;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author MAYANK
 */
public class SubjectManager extends javax.swing.JFrame {

    /**
     * Creates new form SubjectManager
     * ALSO CREATES A MOUSE LISTENER FOR THE TABLE
     * @param defaultDataGetter - query the database for default list
     * @param defaultData - resultSet containing stuff for default data shown in subjects table
     */
    AddBook caller;
    EditBook callerBook;
    int callerFlag=0; // 0 for AddBook caller, 1 for EditBook caller
    Connection library;
    public SubjectManager() {
        try {
            initComponents();
            sendToAddBookButton.setVisible(false); // Coz this constructor call does not come from AddBook form
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        subjectManagerTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
        subjectManagerTableActionPerformed();
        }
        });
            this.library = NITDLibrary.createConnection();
            System.out.print (" for subject manager");
            updateTableModel();
           
        } catch (Exception ex) {
            Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e)
                {
                    dispose();
                }
        });
    }
    /**
     * caller IS ADDBOOK.JAVA KA OBJ THAT CALLS THIS. CALLER KI SEBJECT DETAILS GET UPDATED
     * @param caller 
     */
    public SubjectManager(final AddBook caller) {
        this.caller = caller;
        callerFlag = 0;
        try {
            initComponents();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        subjectManagerTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
        subjectManagerTableActionPerformed();
        }
        });
       
        WindowAdapter adapter = new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                caller.setDefaultsSubjectCombo();
                setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                System.out.println("inside window adarpter anonymous class");
            }
        };
        addWindowListener(adapter);
            this.library = NITDLibrary.createConnection();
            System.out.print (" for subject manager");
            updateTableModel();
           
        } catch (Exception ex) {
            Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * caller IS ADDBOOK.JAVA KA OBJ THAT CALLS THIS. CALLER KI SEBJECT DETAILS GET UPDATED
     * @param caller 
     */
    public SubjectManager(final EditBook caller) {
        this.callerBook = caller;
        callerFlag=1;
        try {
            initComponents();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            sendToAddBookButton.setText("Send to Edit Book Form");

        subjectManagerTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
        subjectManagerTableActionPerformed();
        }
        });
       
        WindowAdapter adapter = new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                caller.setDefaultsSubjectCombo();
                setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                System.out.println("inside window adarpter anonymous class");
            }
        };
        addWindowListener(adapter);
            this.library = NITDLibrary.createConnection();
            System.out.print (" for subject manager");
            updateTableModel();
           
        } catch (Exception ex) {
            Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   /**
    * CALLED BY CONSTRUCTOR
    * WHEN A ROW IS SELECTED, UPDATES THE TEXTFIELDS IN MAKE CHAGES JPANEL
    */
    
    private void subjectManagerTableActionPerformed()
   {
       int row = subjectManagerTable.getSelectedRow();
       changeNameTextField1.setText(subjectManagerTable.getValueAt(row,0).toString());
       changeCodeTextField1.setText(subjectManagerTable.getValueAt(row,1).toString());
       changeClassTextField1.setText(subjectManagerTable.getValueAt(row,2).toString());
       
   }
    
    /**
     * THIS  METHOD UPDATES THE SUNJECT TABLE MODEL I.E USED TO REFRESH AAFTER UPDATES OR CHANGES TO DATABASE
     */

    private void updateTableModel() throws Exception
    {
        Statement defaultDataGetter = library.createStatement();
            ResultSet defaultData = defaultDataGetter.executeQuery("select subject_name,subject_code,subject_class_no from subjects");
            if(defaultData!=null) {
            subjectManagerTable.setModel(MainScreen.populateTable(defaultData));
            }
            else {
                Error.errorDialog("Check Database Connection. No data recieved for defaultData field");
            }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        subjectManagerTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        addNameTextField = new javax.swing.JTextField();
        addCodeTextField = new javax.swing.JTextField();
        addClassTextField = new javax.swing.JTextField();
        addSubjectButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        changeNameTextField1 = new javax.swing.JTextField();
        changeCodeTextField1 = new javax.swing.JTextField();
        changeClassTextField1 = new javax.swing.JTextField();
        makeChangesButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        sendToAddBookButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Subject Manager");

        subjectManagerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject Name", "Subject Code", "Class no"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        subjectManagerTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(subjectManagerTable);
        if (subjectManagerTable.getColumnModel().getColumnCount() > 0) {
            subjectManagerTable.getColumnModel().getColumn(0).setHeaderValue("Subject Name");
            subjectManagerTable.getColumnModel().getColumn(1).setHeaderValue("Subject Code");
            subjectManagerTable.getColumnModel().getColumn(2).setHeaderValue("Class no");
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Add New Subject"));

        jLabel2.setText("Subject Name");

        jLabel3.setText("Subject Code");

        jLabel4.setText("Class No");

        addSubjectButton.setText("ADD to Subjects");
        addSubjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubjectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(addNameTextField))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(addCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(addClassTextField)))
                        .addGap(0, 73, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addSubjectButton)
                .addGap(87, 87, 87))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(addNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(addCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(addClassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(addSubjectButton)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Modify Selected Subject"));

        jLabel5.setText("Subject Name");

        jLabel6.setText("Subject Code");

        jLabel7.setText("Class No");

        makeChangesButton.setText("Make Changes");
        makeChangesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeChangesButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(changeNameTextField1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(changeCodeTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(changeClassTextField1)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(makeChangesButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(changeNameTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(changeCodeTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(changeClassTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(makeChangesButton)
                    .addComponent(deleteButton))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        sendToAddBookButton.setText("Send To Add Book Form");
        sendToAddBookButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendToAddBookButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(277, 277, 277))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(sendToAddBookButton)
                        .addGap(269, 269, 269))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sendToAddBookButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * ADD A NEW SUBJECT TO THE DATABASE AND UPDATE THE TABLE
     * @param evt 
     */
    private void addSubjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubjectButtonActionPerformed
        try {
            PreparedStatement query = library.prepareStatement("insert into subjects(subject_name,subject_code,subject_class_no) values(?,?,?);");
            query.setString(1,addNameTextField.getText());
            query.setString(2,addCodeTextField.getText());
            query.setString(3,addClassTextField.getText());
            System.out.println(query.toString());
            int success = query.executeUpdate();
            if(success==0)
            {
            Error.errorDialog("failed to add a new subject");
            }
            else
            {
             updateTableModel();   
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_addSubjectButtonActionPerformed
/**
 * MODIFY SELECTED ROW.NOTE : rowIndex start form 0
 * ALSO MODIFY THE TABLE BOOKS I.E REFACTORING THE CHANGES
 * @param evt 
 */
    private void makeChangesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeChangesButtonActionPerformed
        System.out.println("make changes button click detected");
       int selectedID = subjectManagerTable.getSelectedRow()+ 1; // the subject_id of selected row
       String subjectString = subjectManagerTable.getValueAt(subjectManagerTable.getSelectedRow(),0).toString();  // the subject_name of selected row
        String subCode = (String) subjectManagerTable.getValueAt(subjectManagerTable.getSelectedRow(),1);
       Statement defaultDataGetter = null;
        try {
            defaultDataGetter = library.createStatement();
            ResultSet defaultData = defaultDataGetter.executeQuery("select subject_id from subjects where subject_code = '" + subCode + "'");
            defaultData.next();
            selectedID = defaultData.getInt("subject_id");
        } catch (SQLException ex) {
            Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        
        System.out.println("SelectedID is " + selectedID + subCode);
        System.out.println("Selected row is :" + subjectManagerTable.getSelectedRow());
        if(selectedID!=0)
        {
          //  System.out.println("Selected row is :" + subjectManagerTable.getSelectedRow());
            try {
                //System.out.println("Selected row is :" + subjectManagerTable.getSelectedRow());
                PreparedStatement query = library.prepareStatement("update subjects set subject_name = ? ,subject_code = ? , subject_class_no = ? where subject_id = ?");
                query.setString(1,changeNameTextField1.getText());
                query.setString(2,changeCodeTextField1.getText());
                query.setString(3,changeClassTextField1.getText());
                query.setInt(4, selectedID);
                //System.out.println("Selected row is :" + subjectManagerTable.getSelectedRow());
                query.executeUpdate();
                updateTableModel();
                System.out.println("Selected row is :" + subjectManagerTable.getSelectedRow());
                /**
                 * REFACTORING THE BOOKS TABLE NOW
                 */
                PreparedStatement booksQuery = library.prepareStatement("update books set subject = ? ,subject_code = ? , classno = ? where subject = ?");
                booksQuery.setString(1,changeNameTextField1.getText());
                booksQuery.setString(2,changeCodeTextField1.getText());
                booksQuery.setString(3,changeClassTextField1.getText());
                System.out.println("Selected row is :" + subjectManagerTable.getSelectedRow());              
                //System.out.println(subjectManagerTable.getValueAt(subjectManagerTable.getSelectedRow(),0));
                booksQuery.setString(4,subjectString);
                booksQuery.executeUpdate();
                JOptionPane.showMessageDialog(null, "Changes Successfully made", "success", JOptionPane.INFORMATION_MESSAGE);
                updateTableModel();
                // re-initaiallizing subject manager values
                changeClassTextField1.setText(null);
                changeCodeTextField1.setText(null);
                changeNameTextField1.setText(null);
            } catch (SQLException ex) {
                Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            

        }
        
    }//GEN-LAST:event_makeChangesButtonActionPerformed

    private void sendToAddBookButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendToAddBookButtonActionPerformed
        if(callerFlag==0)
        {
        caller.setDefaultsSubjectCombo();
        caller.getSubjectCombo().setSelectedItem(subjectManagerTable.getModel().getValueAt(subjectManagerTable.getSelectedRow(),0));
        dispose();
        }
         if(callerFlag==1)
        {
        callerBook.setDefaultsSubjectCombo();
        callerBook.getSubjectCombo().setSelectedItem(subjectManagerTable.getModel().getValueAt(subjectManagerTable.getSelectedRow(),0));
        dispose();
        }
    }//GEN-LAST:event_sendToAddBookButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        boolean cont = false;
        int row = subjectManagerTable.getSelectedRow();
        int executeUpdate =0; int executeUpdate1 =0;
        String sub = subjectManagerTable.getModel().getValueAt(row,0).toString();
        if(JOptionPane.showConfirmDialog(null, "Deleting a Subject will lead data loss.Are you sure you want to Continue?", "Warning", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
            {
            
                /**
                 * Using Config class
                 */
                cont = Config.showDeleteDialog();
                if(cont==true)
                {
                DefaultTableModel model = (DefaultTableModel)subjectManagerTable.getModel();
            try {    
                Statement query = library.createStatement();
                Statement queryBooks = library.createStatement();
                    executeUpdate = query.executeUpdate("delete from subjects where subject_name = '" + sub + "'");
                    executeUpdate1 = queryBooks.executeUpdate("update books set subject = '' where subject = '" + sub + "'");
                    
            } catch (SQLException ex) {
                Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (executeUpdate==1)
                    {
                        JOptionPane.showMessageDialog(null, "1 subject successfully deleted and database is Re-factored","Subject Deleted",JOptionPane.OK_OPTION);
                    }
            else
            {
                Error.errorDialog("Unable to delete Subject");
            }
           /*
            Updating / Refreshing the Table after actions performed
            */
            try {
                updateTableModel();
            } catch (Exception ex) {
                Logger.getLogger(SubjectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            } //cont
                 else
            {
            JOptionPane.showMessageDialog(null, "Wrong Password Entered.","Wrong Password",JOptionPane.OK_OPTION);
            }
            } 
        
    }//GEN-LAST:event_deleteButtonActionPerformed

   
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
            java.util.logging.Logger.getLogger(SubjectManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SubjectManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SubjectManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SubjectManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SubjectManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addClassTextField;
    private javax.swing.JTextField addCodeTextField;
    private javax.swing.JTextField addNameTextField;
    private javax.swing.JButton addSubjectButton;
    private javax.swing.JTextField changeClassTextField1;
    private javax.swing.JTextField changeCodeTextField1;
    private javax.swing.JTextField changeNameTextField1;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton makeChangesButton;
    private javax.swing.JButton sendToAddBookButton;
    private javax.swing.JTable subjectManagerTable;
    // End of variables declaration//GEN-END:variables
}
