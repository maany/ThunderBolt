/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nitdlibrary;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import managers.Config;
import managers.LocationManager;
import managers.MailManager;
import managers.PrintUtilities;
import managers.Register;
import managers.SubjectManager;
import net.proteanit.sql.DbUtils;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.jpa.QueryHintsHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 *
 * @author Meena
 */
public class MainScreen extends javax.swing.JFrame {

    int acc = 1; // Stores the accession number from searchResultsTable on mouseClick .
    int issueBookFlag=0,issueStudentFlag=0; // if book is available or student can issue books then flag value is incremented. flag values are checked before issue is executed
   
    /**
     * Creates new form MainScreen
     * @param books
     */
       
    
       public MainScreen(Connection books) {
           this.books = books;
        Config.startUpWindow = this;
        PrintStream out = null;
        //redirecting output to output.txt
        /*try {
            out = new PrintStream(new FileOutputStream("output_home.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddStudent.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(out);*/
        initComponents();
        setVisible(true);
        likeCombo.setSelected(true);
        ascendingRadio.setSelected(true);
        try {
            welcomeScreenLabel.setIcon(new ImageIcon(ImageIO.read(new File(Config.welcomeScreenPicPath))));
        } catch (IOException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        searchBookPanel.setVisible(false);
        IssuePanel.setVisible(false);
        returnPanel.setVisible(false);
        overduesPanel.setVisible(false);
        searchStudentPanel.setVisible(false);
        DateTime dt = new DateTime();
        String day = null;
        switch(dt.getDayOfWeek())
        {
            case 1 : day = "Monday"; break ;
                case 2 : day = "Tuesday";break;
                    case 3 : day = "Wednesday";break;
                        case 4 : day = "Thursday";break;
                            case 5 : day = "Friday";break;
                                case 6 : day = "Saturday";break;
                                    case 7 : day = "Sunday";break;
                                        
        }
        date.setText(day + ", " + dt.toString("MMM d,yyyy") );
        welcomeName.setText("Welcome " + Config.librarianName);
        /**
        * SETTING PROGRAM FORM WINDOW SIZE AS SCREEN SIZE
        */
        //TODO code not working.. check 
       /* double screenWidth; 
        double screenHeight;
        Toolkit toolkit = Toolkit.getDefaultToolkit(); // get default OS toolkit
        Dimension winDim = toolkit.getScreenSize(); // extract dimension of screen
        screenWidth = winDim.getWidth(); // TODO check if required
        screenHeight = winDim.getHeight();
        System.out.println(screenWidth + " " + screenHeight);
        setPreferredSize(winDim); // set dimension of screen
        */
    }
     /**
      * THIS METHOD MAINTAINS AN ARRAY OF JPanels in quick access . and sets visible only one of them.
      */
       private void quickAccessSetVisibile(JPanel p)
       {
           JPanel[] panelList = new JPanel[5];
           panelList[0] = searchBookPanel;
           panelList[1] = IssuePanel;
           panelList[2] = returnPanel;
           panelList[3] = overduesPanel;
           panelList[4] = searchStudentPanel;
           for(JPanel temp : panelList)
           {
               if(temp==p)
                   p.setVisible(true);
               else
                   temp.setVisible(false);
           }
       }
       
       /**
       * RETURNS NO OF ROWS AS '# RESULTS FOUND'
       * @param booksTable
       * @return
       * @throws SQLException 
       */
      private String metaSearchResult(ResultSet booksTable) throws SQLException
      {
       int rowCount=0;
       if(booksTable.first()==true)
        rowCount=1;
       while(booksTable.next())
       {
           rowCount++;
       }
       if(rowCount==1)
           return rowCount + "Result found";
       else if (rowCount==0)
       {
           return "0 Results found.";
       }
       else
            return rowCount + " Results found";
      }
       /**
        * SET DEFAULT VALUES FOR CONTROLS
        */ 
      private void defaults ()
       {
           likeCombo.setSelected(true);
           ascendingRadio.setSelected(true);
           
       }
     /**
      * TRYING TO FIX THE VECTOR BUG. CURRENTLY USING RS2XML. FIX LATER
      * @param books
      * @return 
      */ 
      public static TableModel populateTableWithoutVector(ResultSet books)
      {
           
               DefaultTableModel data = new DefaultTableModel();
               try {
               ResultSetMetaData meta = books.getMetaData();
               int noOfColumns = meta.getColumnCount();
               Object [] rowData = new Object[noOfColumns];
               books.beforeFirst();
               while(books.next())
               {
                   for(int i=0;i<noOfColumns;i++)
                   {
                       rowData[i] = books.getObject(i);
                   }
                   data.addRow(rowData);
               }

           } catch (SQLException ex) {
               Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
           }
                              return data;
      }
      /**
       * FINDS NO OF COLOUMNS AND DATA AND CREATES A TABLEMODEL FROM RESULTSET
       * @param booksTable
       * @return
       * @throws Exception 
       */
       
     public static TableModel populateTable (ResultSet booksTable) throws Exception
       {
        return DbUtils.resultSetToTableModel(booksTable);
       /*      ResultSetMetaData md = booksTable.getMetaData();
       Vector<String> columnNames = new Vector<String>();
       int noOfColumns = md.getColumnCount();
       //columnNames
       for(int i=1;i<=noOfColumns;i++)
       {
           columnNames.add(md.getColumnName(i));
       }
       int currentRow = 0;
      
       // data
       Vector<Vector<Object>> data = new Vector<Vector<Object>>();
       Vector<Object> rowData = new Vector<Object>();
       while(booksTable.next())
       {
          for(int i=1;i<=noOfColumns;i++)
          {
              rowData.add(booksTable.getObject(i));
              
          }
          data.add(rowData);
       }
           
       TableModel tableModel = new DefaultTableModel(data,columnNames);
       return tableModel; */ 
       } 
       /**
        * returns a 2 coloumn table with first column having key and other having value
        * @param booksTable
        * @param columnsBooksTable
        * @return
        * @throws Exception 
        */
       private TableModel generatePivot(ResultSet booksTable,ResultSet columnsBooksTable) throws Exception
       {
           Vector<Object> modelColumnNames = new Vector<Object>();
           Vector<Object> columnNames = new Vector<Object>();
           columnsBooksTable.beforeFirst();
           while(columnsBooksTable.next())
           {
               columnNames.add(columnsBooksTable.getObject(1));
           }
           modelColumnNames.add("Key");
           modelColumnNames.add("Value");
           
           Vector<Object> rowData = new Vector<Object>();
           Vector<Vector<Object>> data = new Vector<Vector<Object>>();
           booksTable.first();
           
           for(int i=0;i<booksTable.getMetaData().getColumnCount();i++)
           {
               
               System.out.println("Iteration " + i + " :" + rowData +"\n DATA :" + data);
               rowData.clear();
               rowData.add(columnNames.elementAt(i));
               rowData.add(booksTable.getObject(i+1));
               data.addElement(rowData);
               System.out.println("The retrieved for " + columnNames.elementAt(i) + " is " + booksTable.getObject(i+1));
               System.out.println("The value added to Model is " + data.elementAt(i));
               
               System.out.println("now data is " + data);
           }
           /*for(int i=0;i<data.size();i++)
           {
               for(int j=0;j<2;j++)
               {
                   System.out.print(data.elementAt(i).elementAt(j));
               }
               System.out.println();
           } */
           TableModel model = new DefaultTableModel(data,modelColumnNames);
           return model;
       }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        search_SearchForButtonGroup = new javax.swing.ButtonGroup();
        search_TypeButtonGroup = new javax.swing.ButtonGroup();
        search_SortButtonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        searchQuickAccess = new javax.swing.JButton();
        overduesQuickAccess = new javax.swing.JButton();
        returnQuickAccess = new javax.swing.JButton();
        issueQuickAccess = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jPanel2 = new javax.swing.JPanel();
        date = new javax.swing.JLabel();
        welcomeName = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        addBookButton = new javax.swing.JButton();
        searchBookManagement = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        searchStudentPanel = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        ssSearchInCombo = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        ssKeyWord = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        ssProgramme = new javax.swing.JComboBox();
        ssYear = new javax.swing.JSpinner();
        ssBranch = new javax.swing.JComboBox();
        jLabel50 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        ssDOBFrom = new com.toedter.calendar.JDateChooser();
        ssDOBTo = new com.toedter.calendar.JDateChooser();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        ssCategory = new javax.swing.JComboBox();
        searchStudentButton = new javax.swing.JButton();
        ssExact = new javax.swing.JCheckBox();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        ssTable = new javax.swing.JTable();
        jLabel57 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        ssNoOfResults = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        ssPic = new javax.swing.JLabel();
        ssEmail = new javax.swing.JTextField();
        ssPhone = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        ssAddress = new javax.swing.JTextArea();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        overduesPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        overduesTable = new javax.swing.JTable();
        overduesShowBook = new javax.swing.JButton();
        overduesShowStudent = new javax.swing.JButton();
        overduesShowReport = new javax.swing.JButton();
        overduesFineLabel = new javax.swing.JLabel();
        returnPanel = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        returnAccNo = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        returnTitle = new javax.swing.JTextField();
        returnAuthor = new javax.swing.JTextField();
        returnPublisher = new javax.swing.JTextField();
        returnSubject = new javax.swing.JTextField();
        returnEdition = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        returnName = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        returnProgramme = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        returnBranch = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        returnYear = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        returnCardNumbers = new javax.swing.JTextField();
        returnPic = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        returnEmail = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        returnIssueDate = new javax.swing.JTextField();
        returnDueDate = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        returnExceedSpinner = new javax.swing.JSpinner();
        returnFine = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        returnRollNo = new javax.swing.JTextField();
        returnSearch = new javax.swing.JButton();
        returnReturn = new javax.swing.JButton();
        returnReport = new javax.swing.JButton();
        returnMail = new javax.swing.JButton();
        returnStatusPic = new javax.swing.JLabel();
        returnStatusMessage = new javax.swing.JLabel();
        IssuePanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        issueIssue = new javax.swing.JButton();
        issuePic = new javax.swing.JLabel();
        issueMessage = new javax.swing.JLabel();
        issueNewIssue = new javax.swing.JButton();
        issueShowReport = new javax.swing.JButton();
        issueMailStudent = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        issueAcc = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        issueAuthor = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        issueTitle = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        issueSubject = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        issuePublisher = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        issueEdition = new javax.swing.JTextField();
        issueBookStatusPic = new javax.swing.JLabel();
        issueBookStatusMessage = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        issueRollNo = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        issueStudentName = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        issueStudentBranch = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        issueStudentYear = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        issueStudentCardNumbers = new javax.swing.JTextField();
        issueStudentIssueCountSpinner = new javax.swing.JSpinner();
        issueStudentPic = new javax.swing.JLabel();
        issueStudentMessagePic = new javax.swing.JLabel();
        issueStudentMessage = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        issueStudentProgramme = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        issueStudentEmail = new javax.swing.JTextField();
        searchBookPanel = new javax.swing.JPanel();
        searchResultsPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        searchResultsTable = new javax.swing.JTable();
        resultMDString = new javax.swing.JLabel();
        showDetailsButton = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        searchQueryPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        keywordTextField = new javax.swing.JTextField();
        inCombo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        likeCombo = new javax.swing.JRadioButton();
        exactCombo = new javax.swing.JRadioButton();
        orderByCombo = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ascendingRadio = new javax.swing.JRadioButton();
        descendingRadio = new javax.swing.JRadioButton();
        groupByCombo = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        welcomeScreen = new javax.swing.JLayeredPane();
        welcomeScreenLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        searchStudentMemberManagement = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        lock = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NIT Delhi Library");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Quick Access Panel", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 10))); // NOI18N

        searchQuickAccess.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        searchQuickAccess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/search_icon.png"))); // NOI18N
        searchQuickAccess.setText("CATALOG");
        searchQuickAccess.setFocusable(false);
        searchQuickAccess.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchQuickAccess.setMaximumSize(new java.awt.Dimension(100, 100));
        searchQuickAccess.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        searchQuickAccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchQuickAccessActionPerformed(evt);
            }
        });

        overduesQuickAccess.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        overduesQuickAccess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/overdues_icon1.png"))); // NOI18N
        overduesQuickAccess.setText("OVERDUES");
        overduesQuickAccess.setFocusable(false);
        overduesQuickAccess.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        overduesQuickAccess.setMaximumSize(new java.awt.Dimension(100, 100));
        overduesQuickAccess.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        overduesQuickAccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overduesQuickAccessActionPerformed(evt);
            }
        });

        returnQuickAccess.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        returnQuickAccess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/return_book_icon.png"))); // NOI18N
        returnQuickAccess.setText("RETURN");
        returnQuickAccess.setFocusable(false);
        returnQuickAccess.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        returnQuickAccess.setMaximumSize(new java.awt.Dimension(100, 100));
        returnQuickAccess.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        returnQuickAccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnQuickAccessActionPerformed(evt);
            }
        });

        issueQuickAccess.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        issueQuickAccess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/issue_book_icon1.png"))); // NOI18N
        issueQuickAccess.setText("ISSUE");
        issueQuickAccess.setFocusable(false);
        issueQuickAccess.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        issueQuickAccess.setMargin(new java.awt.Insets(0, 0, 0, 0));
        issueQuickAccess.setMaximumSize(new java.awt.Dimension(100, 100));
        issueQuickAccess.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        issueQuickAccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueQuickAccessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(issueQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(returnQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(overduesQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(searchQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(issueQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(overduesQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(returnQuickAccess, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "NIT Delhi Library", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 10))); // NOI18N
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        date.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        date.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        welcomeName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeName.setText("jLabel59");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(welcomeName, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(welcomeName)
                .addGap(8, 8, 8)
                .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Book Management", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 10))); // NOI18N

        addBookButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        addBookButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/book_add_icon.png"))); // NOI18N
        addBookButton.setText("ADD");
        addBookButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addBookButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addBookButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookButtonActionPerformed(evt);
            }
        });

        searchBookManagement.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        searchBookManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/book_history_icon.png"))); // NOI18N
        searchBookManagement.setText("CATALOG");
        searchBookManagement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchBookManagement.setMargin(new java.awt.Insets(0, 0, 0, 0));
        searchBookManagement.setMaximumSize(new java.awt.Dimension(100, 100));
        searchBookManagement.setMinimumSize(new java.awt.Dimension(100, 100));
        searchBookManagement.setPreferredSize(new java.awt.Dimension(53, 69));
        searchBookManagement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        searchBookManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBookManagementActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/book_edit_icon.png"))); // NOI18N
        jButton9.setText("VIEW/EDIT");
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addBookButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(searchBookManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(searchBookManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 74, Short.MAX_VALUE)
                        .addComponent(addBookButton, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)))
                .addContainerGap())
        );

        searchStudentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Student Search "));

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel54.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel54.setText("STEP 1 : Search Query");

        ssSearchInCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Name", "Fathers Name", "Roll No" }));
        ssSearchInCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ssSearchInComboItemStateChanged(evt);
            }
        });

        jLabel48.setText("Search in :");

        jLabel47.setText("Keyword :");

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filters"));

        ssProgramme.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "B.Tech", "M.Tech" }));

        ssBranch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any", "CSE", "ECE", "EEE" }));

        jLabel50.setText("Year :");

        jLabel49.setText("Programme :");

        jLabel51.setText("Branch :");

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "DOB range"));

        jLabel52.setText("From :");

        jLabel53.setText("To :");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ssDOBFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel53)
                .addGap(18, 18, 18)
                .addComponent(ssDOBTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel53)
                    .addComponent(ssDOBFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ssDOBTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel58.setText("Category :");

        ssCategory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any", "GEN", "OBC", "SC", "ST" }));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel50))
                            .addComponent(jLabel49))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ssProgramme, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ssYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addGap(18, 18, 18)
                                .addComponent(ssBranch, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ssCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ssProgramme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ssBranch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel51))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ssYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50)
                    .addComponent(jLabel58)
                    .addComponent(ssCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        searchStudentButton.setText("Search");
        searchStudentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchStudentButtonActionPerformed(evt);
            }
        });

        ssExact.setText("Exact");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(122, 122, 122))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel48))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel16Layout.createSequentialGroup()
                                        .addComponent(ssKeyWord, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(31, 31, 31)
                                        .addComponent(ssExact))
                                    .addComponent(ssSearchInCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addComponent(searchStudentButton)
                        .addGap(180, 180, 180))))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(ssSearchInCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ssKeyWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ssExact))
                .addGap(8, 8, 8)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchStudentButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ssTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll No", "First Name", "Branch", "Year"
            }
        ));
        ssTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ssTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ssTableMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(ssTable);

        jLabel57.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel57.setText("Step 2 : Search Results");

        jLabel55.setText("* single click to show details on right side");

        jLabel56.setText("** double click for detailed student report");

        ssNoOfResults.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel17Layout.createSequentialGroup()
                            .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(133, 133, 133)))
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel57)
                        .addGap(151, 151, 151))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addComponent(ssNoOfResults, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125))))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ssNoOfResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel56)
                .addGap(47, 47, 47))
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ssPic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/profile_pic.jpg"))); // NOI18N
        ssPic.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        ssAddress.setColumns(20);
        ssAddress.setRows(5);
        jScrollPane5.setViewportView(ssAddress);

        jLabel60.setText("E-Mail :");

        jLabel61.setText("Phone :");

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel62.setText("Mailing Address");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                            .addComponent(jLabel61)
                            .addGap(18, 18, 18)
                            .addComponent(ssPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                            .addComponent(ssPic)
                            .addGap(30, 30, 30))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                            .addComponent(jLabel60)
                            .addGap(18, 18, 18)
                            .addComponent(ssEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))))
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(ssPic)
                .addGap(20, 20, 20)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ssEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ssPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61))
                .addGap(18, 18, 18)
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout searchStudentPanelLayout = new javax.swing.GroupLayout(searchStudentPanel);
        searchStudentPanel.setLayout(searchStudentPanelLayout);
        searchStudentPanelLayout.setHorizontalGroup(
            searchStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchStudentPanelLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(41, 41, 41)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );
        searchStudentPanelLayout.setVerticalGroup(
            searchStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchStudentPanelLayout.createSequentialGroup()
                .addGroup(searchStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        overduesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "OverDues"));

        overduesTable.setModel(new javax.swing.table.DefaultTableModel(
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
        overduesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                overduesTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(overduesTable);

        overduesShowBook.setText("Show Book");
        overduesShowBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overduesShowBookActionPerformed(evt);
            }
        });

        overduesShowStudent.setText("Show Student");
        overduesShowStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overduesShowStudentActionPerformed(evt);
            }
        });

        overduesShowReport.setText("Show Report");
        overduesShowReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overduesShowReportActionPerformed(evt);
            }
        });

        overduesFineLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        overduesFineLabel.setText("FINE :");

        javax.swing.GroupLayout overduesPanelLayout = new javax.swing.GroupLayout(overduesPanel);
        overduesPanel.setLayout(overduesPanelLayout);
        overduesPanelLayout.setHorizontalGroup(
            overduesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overduesPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1005, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(overduesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(overduesShowStudent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(overduesShowBook, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(overduesShowReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(overduesFineLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(44, 44, 44))
        );
        overduesPanelLayout.setVerticalGroup(
            overduesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overduesPanelLayout.createSequentialGroup()
                .addGroup(overduesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(overduesPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(overduesPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(overduesFineLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(overduesShowBook, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(overduesShowStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(overduesShowReport, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        returnPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Return"));

        jLabel27.setText("Enter Accession Number:");

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Book Details"));

        jLabel28.setText("Title :");

        jLabel30.setText("Author :");

        jLabel31.setText("Publisher :");

        jLabel32.setText("Subject :");

        jLabel33.setText("Edition :");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(returnTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(18, 18, 18)
                        .addComponent(returnAuthor))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(returnPublisher))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(returnEdition))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(returnSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(returnTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(returnAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(returnPublisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(returnSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(returnEdition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Student Details"));

        jLabel35.setText("Name :");

        jLabel36.setText("Programme :");

        jLabel38.setText("Branch :");

        jLabel39.setText("Year :");

        jLabel40.setText("Card Number :");

        jLabel45.setText("E-Mail ID :");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                        .addComponent(jLabel38)
                                        .addGap(186, 186, 186))
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addComponent(jLabel39)
                                        .addGap(73, 73, 73)))
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel40)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(returnYear, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(returnCardNumbers)
                                        .addComponent(returnEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                                    .addGap(25, 25, 25)))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel35))
                                .addGap(14, 14, 14)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addComponent(returnName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(returnBranch, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                                            .addComponent(returnProgramme))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addComponent(returnPic, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(returnPic, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(returnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel36)
                            .addComponent(returnProgramme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(returnBranch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39)
                            .addComponent(returnYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel40)
                            .addComponent(returnCardNumbers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(returnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jLabel37))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Mini Report"));

        jLabel34.setText("Issue Date :");

        jLabel41.setText("Due Date :");

        jLabel43.setText("No of Days Exceeded :");

        jLabel44.setText("Fine :");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                            .addComponent(jLabel34)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(returnIssueDate))
                        .addGroup(jPanel13Layout.createSequentialGroup()
                            .addComponent(jLabel41)
                            .addGap(18, 18, 18)
                            .addComponent(returnDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                            .addComponent(jLabel44)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(returnFine, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                            .addComponent(jLabel43)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(returnExceedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(returnIssueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(returnDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(returnExceedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(returnFine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel42.setText("Enter Roll_no :");

        returnSearch.setText("Search");
        returnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnSearchActionPerformed(evt);
            }
        });

        returnReturn.setText("Return");
        returnReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnReturnActionPerformed(evt);
            }
        });

        returnReport.setText("Report");
        returnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnReportActionPerformed(evt);
            }
        });

        returnMail.setText("Send Mail ");
        returnMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnMailActionPerformed(evt);
            }
        });

        returnStatusMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout returnPanelLayout = new javax.swing.GroupLayout(returnPanel);
        returnPanel.setLayout(returnPanelLayout);
        returnPanelLayout.setHorizontalGroup(
            returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnAccNo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnRollNo, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(returnSearch)
                .addGap(322, 322, 322))
            .addGroup(returnPanelLayout.createSequentialGroup()
                .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(returnPanelLayout.createSequentialGroup()
                        .addGap(405, 405, 405)
                        .addComponent(returnReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(returnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(returnMail)
                        .addGap(33, 33, 33)
                        .addComponent(returnStatusPic, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(returnPanelLayout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(returnStatusMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 896, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        returnPanelLayout.setVerticalGroup(
            returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnPanelLayout.createSequentialGroup()
                .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(returnAccNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(returnRollNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(returnSearch))
                .addGap(27, 27, 27)
                .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(returnReturn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnMail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnStatusPic, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(returnStatusMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE))
        );

        IssuePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "ISSUE"));

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("Step 3 : Issue");

        issueIssue.setText("Issue");
        issueIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueIssueActionPerformed(evt);
            }
        });

        issueMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        issueNewIssue.setText("New Issue ");
        issueNewIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueNewIssueActionPerformed(evt);
            }
        });

        issueShowReport.setText("Show Report");
        issueShowReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueShowReportActionPerformed(evt);
            }
        });

        issueMailStudent.setText("Mail Report");
        issueMailStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueMailStudentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(issueMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(issueNewIssue)
                        .addGap(18, 18, 18)
                        .addComponent(issueShowReport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(issueMailStudent)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(issuePic, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(issueIssue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(issueIssue, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(issuePic, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(issueMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(issueShowReport, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(issueMailStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(issueNewIssue, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Step 1 : Book Details");

        issueAcc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                issueAccFocusLost(evt);
            }
        });

        jLabel15.setText("Enter Acc. no :");

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Book Details"));

        jLabel17.setText("Author :");

        jLabel16.setText("Title : ");

        jLabel18.setText("Subject :");

        jLabel19.setText("Publisher :");

        jLabel20.setText("Edition :");

        issueBookStatusMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(issueTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addComponent(jLabel19)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(issuePublisher))
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addComponent(jLabel18)
                                    .addGap(18, 18, 18)
                                    .addComponent(issueSubject))
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(issueAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                    .addComponent(jLabel20)
                                    .addGap(18, 18, 18)
                                    .addComponent(issueEdition))))
                        .addGap(0, 50, Short.MAX_VALUE))
                    .addComponent(issueBookStatusMessage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(139, 139, 139)
                .addComponent(issueBookStatusPic, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(issueTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(issueAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(issueSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(issuePublisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(issueEdition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(issueBookStatusPic, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(issueBookStatusMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(jLabel10))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(issueAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(issueAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("Step 2 : Student Details");

        jLabel21.setText("Enter Roll No.");

        issueRollNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                issueRollNoFocusLost(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Student Details"));

        jLabel22.setText("Name :");

        jLabel23.setText("Branch :");

        jLabel24.setText("Year :");

        jLabel25.setText("No of books currently issued :");

        jLabel26.setText("Card Numbers :");

        issueStudentPic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/profile_pic.jpg"))); // NOI18N
        issueStudentPic.setText("jLabel27");
        issueStudentPic.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        issueStudentMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel29.setText("Programme :");

        jLabel46.setText("E-Mail :");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(issueStudentMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(issueStudentName, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel25)
                                    .addGap(18, 18, 18)
                                    .addComponent(issueStudentIssueCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel23)
                                        .addComponent(jLabel24))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(issueStudentYear, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(issueStudentBranch, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel26)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(issueStudentCardNumbers, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel46))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(issueStudentProgramme)
                                    .addComponent(issueStudentEmail))))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(issueStudentPic, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(issueStudentMessagePic, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48))))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(issueStudentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(issueStudentBranch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(issueStudentYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(issueStudentCardNumbers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(issueStudentIssueCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(issueStudentPic, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(issueStudentMessagePic, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(issueStudentProgramme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel46)
                            .addComponent(issueStudentEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(issueStudentMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(106, 106, 106))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(issueRollNo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(issueRollNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout IssuePanelLayout = new javax.swing.GroupLayout(IssuePanel);
        IssuePanel.setLayout(IssuePanelLayout);
        IssuePanelLayout.setHorizontalGroup(
            IssuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IssuePanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        IssuePanelLayout.setVerticalGroup(
            IssuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, IssuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(IssuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        searchBookPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Book Search"));

        searchResultsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Step 2 : Search Results");

        searchResultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "S.no", "Title ", "Author", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        searchResultsTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        searchResultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchResultsTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchResultsTableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(searchResultsTable);

        resultMDString.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultMDString.setText("0 Results Found");

        showDetailsButton.setText("Show Details");
        showDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showDetailsButtonActionPerformed(evt);
            }
        });

        jButton15.setText("Print");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchResultsPanelLayout = new javax.swing.GroupLayout(searchResultsPanel);
        searchResultsPanel.setLayout(searchResultsPanelLayout);
        searchResultsPanelLayout.setHorizontalGroup(
            searchResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchResultsPanelLayout.createSequentialGroup()
                .addGroup(searchResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchResultsPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1))
                    .addGroup(searchResultsPanelLayout.createSequentialGroup()
                        .addGroup(searchResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(searchResultsPanelLayout.createSequentialGroup()
                                .addGap(353, 353, 353)
                                .addGroup(searchResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(resultMDString, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(searchResultsPanelLayout.createSequentialGroup()
                                .addGap(295, 295, 295)
                                .addComponent(showDetailsButton)
                                .addGap(80, 80, 80)
                                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 341, Short.MAX_VALUE)))
                .addContainerGap())
        );
        searchResultsPanelLayout.setVerticalGroup(
            searchResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchResultsPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultMDString)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(searchResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showDetailsButton)
                    .addComponent(jButton15))
                .addGap(27, 27, 27))
        );

        searchQueryPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setText("Keyword(s) :");

        inCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "title", "acc_no", "author", "publisher", "classno", "subject", "location", "date" }));

        jLabel3.setText("In :");

        jLabel4.setText("Type :");

        search_TypeButtonGroup.add(likeCombo);
        likeCombo.setText("Like");

        search_TypeButtonGroup.add(exactCombo);
        exactCombo.setText("Exact");

        orderByCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "author", "acc_no", "title", "subject", "publisher", "year" }));

        jLabel5.setText("Order by :");

        jLabel6.setText("Sort by :");

        search_SortButtonGroup.add(ascendingRadio);
        ascendingRadio.setText("Ascending");

        search_SortButtonGroup.add(descendingRadio);
        descendingRadio.setText("Descending");

        groupByCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "author", "subject", "publisher", "year" }));

        jLabel7.setText("Group by :");

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Step 1 : Search Query");

        javax.swing.GroupLayout searchQueryPanelLayout = new javax.swing.GroupLayout(searchQueryPanel);
        searchQueryPanel.setLayout(searchQueryPanelLayout);
        searchQueryPanelLayout.setHorizontalGroup(
            searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchQueryPanelLayout.createSequentialGroup()
                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchQueryPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(searchQueryPanelLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(searchQueryPanelLayout.createSequentialGroup()
                                        .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(orderByCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 76, Short.MAX_VALUE)
                                            .addComponent(likeCombo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(ascendingRadio, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGap(18, 18, 18)
                                        .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(descendingRadio)
                                            .addComponent(exactCombo)))
                                    .addComponent(groupByCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(searchQueryPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(inCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(keywordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(searchQueryPanelLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel8)))
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchQueryPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(searchButton)
                .addGap(137, 137, 137))
        );
        searchQueryPanelLayout.setVerticalGroup(
            searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchQueryPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keywordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(likeCombo)
                    .addComponent(exactCombo))
                .addGap(18, 18, 18)
                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(orderByCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ascendingRadio)
                    .addComponent(descendingRadio))
                .addGap(18, 18, 18)
                .addGroup(searchQueryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(groupByCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchButton)
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout searchBookPanelLayout = new javax.swing.GroupLayout(searchBookPanel);
        searchBookPanel.setLayout(searchBookPanelLayout);
        searchBookPanelLayout.setHorizontalGroup(
            searchBookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBookPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchQueryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(searchResultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        searchBookPanelLayout.setVerticalGroup(
            searchBookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBookPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchBookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchResultsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchQueryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        welcomeScreenLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/welcome_pic.png"))); // NOI18N

        javax.swing.GroupLayout welcomeScreenLayout = new javax.swing.GroupLayout(welcomeScreen);
        welcomeScreen.setLayout(welcomeScreenLayout);
        welcomeScreenLayout.setHorizontalGroup(
            welcomeScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomeScreenLayout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addComponent(welcomeScreenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 996, Short.MAX_VALUE)
                .addGap(134, 134, 134))
        );
        welcomeScreenLayout.setVerticalGroup(
            welcomeScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomeScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomeScreenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        welcomeScreen.setLayer(welcomeScreenLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addComponent(searchStudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 118, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(IssuePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 97, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(returnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 105, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(overduesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 105, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(searchBookPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(welcomeScreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addComponent(searchStudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(IssuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(393, 393, 393)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(returnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 365, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(overduesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 369, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(searchBookPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 372, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(welcomeScreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(380, Short.MAX_VALUE)))
        );
        jLayeredPane1.setLayer(searchStudentPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(overduesPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(returnPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(IssuePanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(searchBookPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(welcomeScreen, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Member Management", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 10))); // NOI18N

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/member-add-icon.png"))); // NOI18N
        jButton5.setText("ADD");
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/book_edit_icon.png"))); // NOI18N
        jButton6.setText("VIEW/EDIT");
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        searchStudentMemberManagement.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        searchStudentMemberManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/member-view-icon1.png"))); // NOI18N
        searchStudentMemberManagement.setText("SEARCH");
        searchStudentMemberManagement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchStudentMemberManagement.setMargin(new java.awt.Insets(0, 0, 0, 0));
        searchStudentMemberManagement.setMaximumSize(new java.awt.Dimension(100, 100));
        searchStudentMemberManagement.setMinimumSize(new java.awt.Dimension(100, 100));
        searchStudentMemberManagement.setPreferredSize(new java.awt.Dimension(53, 69));
        searchStudentMemberManagement.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        searchStudentMemberManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchStudentMemberManagementActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(searchStudentMemberManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(searchStudentMemberManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Advanced Options/Help"));

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/main_location.png"))); // NOI18N
        jButton2.setText("Location");
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/main_subjects.png"))); // NOI18N
        jButton3.setText("Subjects");
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/main_report.png"))); // NOI18N
        jButton4.setText("Reports");
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/main_datasource.png"))); // NOI18N
        jButton7.setText("DB BackDoor");
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/main_register.png"))); // NOI18N
        jButton8.setText("Daily Log");
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/help_icon.png"))); // NOI18N
        jButton11.setText("HELP");
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/help_about_icon.png"))); // NOI18N
        jButton12.setText("ABOUT");
        jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nit/d/icons/help_logout_icon.png"))); // NOI18N
        jButton13.setText("Exit");
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton13.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton13.setMaximumSize(new java.awt.Dimension(100, 100));
        jButton13.setMinimumSize(new java.awt.Dimension(100, 100));
        jButton13.setPreferredSize(new java.awt.Dimension(53, 69));
        jButton13.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/main_settings.png"))); // NOI18N
        jButton1.setText("Settings");
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/lock.png"))); // NOI18N
        lock.setText("Lock");
        lock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(130, 130, 130)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(490, 490, 490))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(146, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void returnQuickAccessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnQuickAccessActionPerformed
        quickAccessSetVisibile(returnPanel);
    }//GEN-LAST:event_returnQuickAccessActionPerformed

    private void issueQuickAccessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueQuickAccessActionPerformed
        quickAccessSetVisibile(IssuePanel);
        IssuePanel.setVisible(true);
        
    }//GEN-LAST:event_issueQuickAccessActionPerformed
/**
 * IF KEYWORD  IS LEFT EMPTY THEN ENTIRE DATABASE IS LOOKED UP..FAILES IS EXACT COMBO IS SELECTED.
 * @param evt 
 */
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        
        try{
        String queryString = "SELECT acc_no,title,author,publisher,subject,status FROM books WHERE ";
        queryString += inCombo.getSelectedItem().toString();
        if(likeCombo.isSelected()==true)
            queryString += " LIKE ? ";//GROUP BY " + groupByCombo.getSelectedItem().toString() + " ORDER BY " + orderByCombo.getSelectedItem().toString() ;
        else if (exactCombo.isSelected()==true)
            queryString += " = ? ";//GROUP BY " + groupByCombo.getSelectedItem().toString() + " ORDER BY " + orderByCombo.getSelectedItem().toString() ;
       if(groupByCombo.getSelectedItem().toString().compareTo("none")==0)
       {
           queryString += " ORDER BY " + orderByCombo.getSelectedItem().toString();
       }
       else
       {
           queryString += "GROUP BY " + groupByCombo.getSelectedItem().toString() + " ORDER BY " + orderByCombo.getSelectedItem().toString() ; 
       }
        if (ascendingRadio.isSelected()==true)
            queryString += " ASC";
        else if (descendingRadio.isSelected()==true)
            queryString +=" DESC";
        PreparedStatement query = books.prepareStatement(queryString,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
        if(keywordTextField.getText().length()!=0)
        {
        if(likeCombo.isSelected()==true)
            query.setString(1,"%"+keywordTextField.getText()+"%");
        else if (exactCombo.isSelected()==true)
            query.setString(1,keywordTextField.getText());
        }
        else // IF KEYWORD  IS LEFT EMPTY THEN ENTIRE DATABASE IS LOOKED UP..FAILES IS EXACT COMBO IS SELECTED.
        {
            query.setString(1,"%");
        }
       System.out.println(query.toString());// TODO remove
        booksTable = query.executeQuery();
        /**
         * mapping status to available
         */
        booksTable.beforeFirst();
        
        searchResultsTable.setModel(populateTable(booksTable));
        int i=0,st;
        booksTable.beforeFirst();
        while(booksTable.next())
        {
            st = new Integer(searchResultsTable.getModel().getValueAt(i,5).toString());
            System.out.println("st : " + st);
            if(st!=0)
            {
                searchResultsTable.setValueAt("Available", i, 5);
            }
            else
            {
                 searchResultsTable.setValueAt("Not Available", i, 5);
            }
            i++;
        }
        resultMDString.setText(metaSearchResult(booksTable));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void addBookButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBookButtonActionPerformed
         new AddBook(books).setVisible(true);
        
        
    }//GEN-LAST:event_addBookButtonActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        new EditBook(books,acc).setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        new AddStudent(books).setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        new EditViewStudent(books,130233).setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void showDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDetailsButtonActionPerformed
        acc = Integer.parseInt(searchResultsTable.getModel().getValueAt(searchResultsTable.getSelectedRow(),0).toString());
        new EditBook(books,acc).setVisible(true);
        /* try {
            TableModel model;
            model = searchResultsTable.getModel();
            int selectedRow = searchResultsTable.getSelectedRow();
            System.out.println(selectedRow);
            String selectedTitle = (String)model.getValueAt(selectedRow,1);
            System.out.println(selectedTitle);
            //preparing query for selected title
            PreparedStatement query = books.prepareStatement("SELECT * FROM books WHERE title = ?");
            query.setString(1, selectedTitle);
            detailsBooksTable = query.executeQuery();
            Statement columnRetrieval = books.createStatement();
            columnsBooksTable = columnRetrieval.executeQuery("SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='library' AND `TABLE_NAME`='books'");
            detailsBooksTable.first();
            System.out.println("Start");
            for(int i=1;i<=detailsBooksTable.getMetaData().getColumnCount();i++)
            System.out.println(detailsBooksTable.getObject(i));
            System.out.println("Start 2");
            columnsBooksTable.first();
            System.out.println(columnsBooksTable.getMetaData().getColumnCount());
            for(int i=1;i<=columnsBooksTable.getMetaData().getColumnCount();i++)
            System.out.println(columnsBooksTable.getObject(i));
           // detailsTable.setModel(generatePivot(detailsBooksTable,columnsBooksTable));

        } catch (Exception ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }//GEN-LAST:event_showDetailsButtonActionPerformed

    private void searchResultsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchResultsTableMouseClicked
        acc = Integer.parseInt(searchResultsTable.getModel().getValueAt(searchResultsTable.getSelectedRow(),0).toString());
        System.out.println("Selected acc no is :" + acc);

    }//GEN-LAST:event_searchResultsTableMouseClicked
/**
 * ISSUE : this method loads book details into step 1
 * @param evt 
 */
    private void issueAccFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_issueAccFocusLost
        ResultSet issueBookResultSet = null;
        try {
            PreparedStatement query = books.prepareStatement("select title,author,publisher,edition,status,subject from books where acc_no = ?");
            query.setInt(1,new Integer(issueAcc.getText()));
            issueBookResultSet = query.executeQuery();
            issueBookResultSet.first();
        } catch (SQLException ex) {
            issueBookStatusMessage.setText("no book found with acc_no : " + issueAcc.getText());
            try {
                issueBookStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            /**
             * Assigning values from result set
             */
            issueTitle.setText(issueBookResultSet.getString("title"));
            issueAuthor.setText(issueBookResultSet.getString("author"));
            issuePublisher.setText(issueBookResultSet.getString("publisher"));
            issueEdition.setText(issueBookResultSet.getString("edition"));
            issueSubject.setText(issueBookResultSet.getString("subject"));
            /*
            checking status
            */
            int issueStatus = issueBookResultSet.getInt("status");
            if(issueStatus==0)
            {
                issueBookStatusMessage.setText("This book is not availavle. See history for acc_no : " + issueAcc.getText());
            try {
                issueBookStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex1);
            }
            }
            else if(issueStatus==2)
            {
                issueBookStatusMessage.setText("This book is not available to students");
            try {
                issueBookStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex1);
            }
                
            }
            else if (issueStatus==1 || issueStatus==3)
            {
                issueBookFlag=1;
                 issueBookStatusMessage.setText("This book is available for issue");
                try {
                issueBookStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex1);
            }
                
            }
            System.out.println(issueBookFlag);    
            
        } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_issueAccFocusLost
/**
 * This method scales the students image
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
    /**
 * ISSUE: Student : this function grabs student details from db and updates fields
 * @param evt 
 */
    private void issueRollNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_issueRollNoFocusLost
        ResultSet issueStudentResultSet = null;
        try {
            PreparedStatement query = books.prepareStatement("select `First Name`,`Branch`,`Year`,`Card Numbers`,`Programme`,`pic_path`,`current_issued_no`,`E-Mail` from student where roll_no = ?");
            query.setInt(1, new Integer(issueRollNo.getText()));
            issueStudentResultSet = query.executeQuery();
            issueStudentResultSet.next();
        } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            /**
             * Filling values
             */
            issueStudentName.setText(issueStudentResultSet.getString("First Name"));
            issueStudentYear.setText(issueStudentResultSet.getString("Year"));
            issueStudentBranch.setText(issueStudentResultSet.getString("Branch"));
            issueStudentCardNumbers.setText(issueStudentResultSet.getString("Card Numbers"));
            issueStudentProgramme.setText(issueStudentResultSet.getString("Programme"));
            issueStudentEmail.setText(issueStudentResultSet.getString("E-Mail"));
            issueStudentIssueCountSpinner.setValue(issueStudentResultSet.getInt("current_issued_no"));
            Image image = ImageIO.read(new File(issueStudentResultSet.getString("pic_path")));
            issueStudentPic.setIcon(new ImageIcon(getScaledImage(image,168,130)));
            if(Config.isPassed(issueStudentResultSet.getString("Programme"), issueStudentResultSet.getInt("Year")))
                issueStudentYear.setText("Passed Out");
            if(issueStudentResultSet.getInt("current_issued_no")<Config.issueLimitStudent && Config.isPassed(issueStudentResultSet.getString("Programme"), issueStudentResultSet.getInt("Year"))==false)
            {
                issueStudentMessage.setText("Student can issue books");
                issueStudentMessagePic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
                issueStudentFlag++;
            }
            else{
                issueStudentMessage.setText("Student cannot issue books");
                issueStudentMessagePic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            }
        } catch (SQLException ex) {
            issueStudentMessage.setText("some details not found in database");
            try {
                issueStudentMessagePic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_issueRollNoFocusLost

    private void issueIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueIssueActionPerformed
        // Validations
        System.out.println("issueflag : " + issueBookFlag + "student :" + issueStudentFlag);
        if(issueBookFlag==1 && issueStudentFlag==1)
        {
            //reset flags
            issueBookFlag=0; issueStudentFlag=0;
            try {
                // make entries into databases. see lakshya's register second last page for details
                books.setAutoCommit(false);
                PreparedStatement queryBooks = books.prepareStatement("update books set status = 0 where acc_no = ?");
                queryBooks.setInt(1, new Integer(issueAcc.getText()));
                int executeUpdate = queryBooks.executeUpdate();
                // student database changes
                PreparedStatement queryStudent = books.prepareStatement("update student set `books issued` = concat(`books issued`,?),`current_issued_no` = `current_issued_no` + ? where roll_no = ?");
                queryStudent.setString(1,issueAcc.getText()+",");
                queryStudent.setInt(2,1);
                queryStudent.setInt(3,new Integer(issueRollNo.getText()));
                int executeUpdate1 = queryStudent.executeUpdate();
                // issue database update. 
                PreparedStatement queryIssue = books.prepareStatement("insert into issue(acc_no,issuer_id,issue_date,due_date) values(?,?,?,?)");
                queryIssue.setInt(1,new Integer(issueAcc.getText()));
                queryIssue.setInt(2,new Integer(issueRollNo.getText()));
                LocalDate today = new LocalDate(new java.util.Date());
                LocalDate dueLocalDate = new LocalDate(today.plusDays(Config.studentIssueDuration));
                queryIssue.setString(3,today.toString("yyyy-MM-dd"));
                queryIssue.setString(4,dueLocalDate.toString("yyyy-MM-dd"));
                int executeUpdate2 = queryIssue.executeUpdate();
                if(executeUpdate==1 && executeUpdate1==1 && executeUpdate2==1)
                {
                    issueMessage.setText("Book issued successfully");
                    issuePic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
                    books.commit();
                    books.setAutoCommit(true);
                }
                
            } catch (SQLException ex) {
                issueMessage.setText(ex.getMessage());
                try {
                    issuePic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
                } catch (IOException ex1) {
                    Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            issueMessage.setText("Book cannot be issued. Please re-check details on the left side");
            try {
                issuePic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_issueIssueActionPerformed
/**
 * loads data into returnPanel. IF BOOK HAS BEEN RETURNED, NO RE RETURN. Sets returnFlag = 1 if return is possible
 * @param evt 
 */
    private void returnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnSearchActionPerformed
        
        try {
            System.out.println();
            
            returnStatusMessage.setText("");
            returnStatusPic.setIcon(null);
            PreparedStatement query = books.prepareStatement("select * from issue where issuer_id = ? and acc_no = ?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            query.setInt(1,new Integer(returnRollNo.getText()));
            query.setInt(2,new Integer(returnAccNo.getText()));
            issueRS = query.executeQuery();
            /**
             * Moving cursor location to last record in result set 
             * FIXING : if a student issues same book twice, the latest issue will be displayed. 
             */
            issueRS.last();
            /**
             * IF BOOK HAS ALREADY BEEN RETURNED, SHOW NOTHING
             */
            if(issueRS.getString("return_date")!=null )//|| issueRS.getString("return_date").compareTo("")!=0)
            {
                returnStatusMessage.setText("Book Has Already Been retuned by the student on :" + issueRS.getString("return_date"));
                returnStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            }
            else
            {
                returnFlag = 1;
            }
            ResultSet[] reportData = Config.getReportData(issueRS.getInt("acc_no"),issueRS.getInt("issuer_id"));
            bookRS = reportData[0];
            studentRS = reportData[1];
            bookRS.first();
            studentRS.first();
            
            /**
             * Now All three ResultSets have been initialized. Filling Fields
             */
            returnTitle.setText(bookRS.getString("title"));
            returnAuthor.setText(bookRS.getString("author"));
            returnPublisher.setText(bookRS.getString("publisher"));
            returnEdition.setText(bookRS.getString("edition"));
            returnSubject.setText(bookRS.getString("subject"));
            
            returnName.setText(studentRS.getString("First Name"));
            returnProgramme.setText(studentRS.getString("Programme"));
            returnEmail.setText(studentRS.getString("E-Mail"));
            returnBranch.setText(studentRS.getString("Branch"));
            returnYear.setText(studentRS.getString("Year"));
            returnCardNumbers.setText(studentRS.getString("Card Numbers"));
            Image image = ImageIO.read(new File(studentRS.getString("pic_path")));
            returnPic.setIcon(new ImageIcon(getScaledImage(image,132,132)));
            LocalDate issueDate = new LocalDate(issueRS.getString("issue_date"));
            LocalDate dueDate = new LocalDate(issueRS.getString("due_date"));
            /*
            DATE FORMAT MAPPING
            */
            returnIssueDate.setText(issueDate.toString("dd-MM-yyyy"));
            returnDueDate.setText(dueDate.toString("dd-MM-yyyy"));
            returnExceedSpinner.setValue(Config.daysExceeded(issueRS.getString("return_date"),issueRS.getString("due_date")));
            Integer fine = Config.daysExceeded(issueRS.getString("return_date"), issueRS.getString("due_date"))*Config.ChARGEPERDAY;
            returnFine.setText(fine.toString());
            
        } catch (SQLException ex) {
            returnStatusMessage.setText(ex.getMessage());
            try {
                returnStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_returnSearchActionPerformed

    private void returnReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnReturnActionPerformed
        if(returnFlag==0)
        {
            returnStatusMessage.setText("This book cannot be returned as it is already returned.");
            try {
                returnStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            returnFlag = 0;
            try {
                if(bookRS.getInt("status_tag")==1)
                bookRS.updateInt("status",3);
                else if(bookRS.getInt("status_tag")==0)
                    bookRS.updateInt("status",1);
                bookRS.updateRow();
            
                int currentIssueCount = studentRS.getInt("current_issued_no")-1;
                studentRS.updateInt("current_issued_no",currentIssueCount);
                studentRS.updateRow();
                
                issueRS.updateString("return_date",(new LocalDate(new java.util.Date())).toString("yyyy-MM-dd"));
                issueRS.updateRow();
                
                returnStatusMessage.setText("Book returned successfully");
                returnStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_success.png"))));
            } catch (SQLException ex) {
                returnStatusMessage.setText(ex.getMessage());
            try {
                returnStatusPic.setIcon(new ImageIcon(ImageIO.read(new File("add_book_failure.png"))));
            } catch (IOException ex1) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            
        }
    }//GEN-LAST:event_returnReturnActionPerformed

    private void overduesQuickAccessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overduesQuickAccessActionPerformed
        quickAccessSetVisibile(overduesPanel);
        overduesShowBook.setEnabled(false);
        overduesShowReport.setEnabled(false);
        overduesShowStudent.setEnabled(false);
        /**
         * loading values in overduesTable
         */
        try {
            Statement overDueQuery = books.createStatement();
            overdueResultSet=overDueQuery.executeQuery("select issue.issue_id,books.title,student.`First Name`,student.`Last Name`,student.roll_no,books.acc_no,issue.issue_date,issue.due_date,issue.return_date from books,student,issue where ((issue.return_date is null and issue.due_date< curdate()) or issue.return_date>due_date)and books.acc_no = issue.acc_no and student.roll_no = issue.issuer_id order by issue.due_date desc;"); 
            //see query from imp_queries.sql
           overduesTable.setModel(DbUtils.resultSetToTableModel(overdueResultSet));
           Integer[] fields = new Integer[3];
           fields[0]=6;
           fields[1]=7;
           fields[2]=8;
           overdueResultSet.beforeFirst();
           overduesTable.setModel(Config.changeResultSetDateFormat(overdueResultSet,overduesTable.getModel(), fields));
        } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_overduesQuickAccessActionPerformed

    private void searchQuickAccessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchQuickAccessActionPerformed
        quickAccessSetVisibile(searchBookPanel);
    }//GEN-LAST:event_searchQuickAccessActionPerformed

    private void returnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnReportActionPerformed
        new ReportGenerator(new Integer(returnAccNo.getText()),new Integer(returnRollNo.getText())).setVisible(true);
    }//GEN-LAST:event_returnReportActionPerformed

    private void overduesShowBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overduesShowBookActionPerformed
        int acc = new Integer(overduesTable.getModel().getValueAt(overduesTable.getSelectedRow(),5).toString());
        new EditBook(books,acc).setVisible(true);
    }//GEN-LAST:event_overduesShowBookActionPerformed

    private void overduesShowStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overduesShowStudentActionPerformed
       int roll = new Integer(overduesTable.getModel().getValueAt(overduesTable.getSelectedRow(),4).toString());
       new EditViewStudent(books,roll);
    }//GEN-LAST:event_overduesShowStudentActionPerformed

    private void overduesShowReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overduesShowReportActionPerformed
        int issue_id = new Integer(overduesTable.getModel().getValueAt(overduesTable.getSelectedRow(),0).toString());
        new ReportGenerator(issue_id).setVisible(true);
    }//GEN-LAST:event_overduesShowReportActionPerformed

    private void overduesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_overduesTableMouseClicked
        overduesShowBook.setEnabled(true);
        overduesShowReport.setEnabled(true);
        overduesShowStudent.setEnabled(true);
        int fine = 0;
        
        if(overduesTable.getModel().getValueAt(0,8)==null || overduesTable.getModel().getValueAt(overduesTable.getSelectedRow(),8).toString().isEmpty())
        {
            
            System.out.println("inside overduesTableMouseClicked()");
            fine = Config.daysExceeded(null,overduesTable.getModel().getValueAt(overduesTable.getSelectedRow(),7).toString())*Config.ChARGEPERDAY;
            overduesFineLabel.setText("FINE : " + new Integer(fine).toString());
        }
            else
            {
                
                
                fine = Config.daysExceeded(overduesTable.getModel().getValueAt(overduesTable.getSelectedRow(),8).toString(),overduesTable.getModel().getValueAt(overduesTable.getSelectedRow(),7).toString());
                overduesFineLabel.setText("FINE : "+new Integer(fine).toString());
            }
    }//GEN-LAST:event_overduesTableMouseClicked

    private void issueShowReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueShowReportActionPerformed
        new ReportGenerator(Integer.parseInt(issueAcc.getText()),Integer.parseInt(issueRollNo.getText())).setVisible(true);
    }//GEN-LAST:event_issueShowReportActionPerformed

    private void issueMailStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueMailStudentActionPerformed
        try {
            PreparedStatement query = books.prepareStatement("select issue_date from issue where issuer_id = ? and acc_no= ?");
            query.setInt(2, Integer.parseInt(issueAcc.getText()));
            query.setInt(1, Integer.parseInt(issueRollNo.getText()));
            ResultSet rs = query.executeQuery();
            rs.first();
            LocalDate issue_date = new LocalDate(rs.getString("issue_date"));
            String issueMessage = Config.issueMessage(issueStudentName.getText(),issueRollNo.getText(),issueTitle.getText(),issueAcc.getText(),issue_date.toString("yyyy-MM-dd"),issue_date.plusDays(Config.studentIssueDuration).toString("yyyy-MM-dd"));
            MailManager.sendMail(Config.sessionEmail,issueStudentEmail.getText(),issueMessage, 0);
        } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_issueMailStudentActionPerformed

    private void returnMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnMailActionPerformed
        try {
            issueRS.first();
            String return_date = issueRS.getString("return_date");
            
            String returnMessage = Config.returnMessage(returnName.getText(),returnRollNo.getText(),returnTitle.getText(),returnAccNo.getText(),returnIssueDate.getText(),returnDueDate.getText(),return_date);
            MailManager.sendMail(Config.sessionEmail,returnEmail.getText(),returnMessage, 1);
        } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_returnMailActionPerformed
/**
 * STUDENT SEARCH: This method creates a query to search student table in database, poplulates the Step 2 table.
 * If keyword is left empty then entire table is looked up.
 * @param evt 
 */
    private void searchStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchStudentButtonActionPerformed
        try {
            /**
             * Reseting ssPic and other details
             */
            ssEmail.setText(null);
            ssPhone.setText(null);
            ssAddress.setText(null);
            ssPic.setIcon(new ImageIcon(ImageIO.read(new File("profile_pic.jpg"))));
        } catch (IOException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        String query = "select `roll_no`,`First Name`,`Branch`,`Year` from student where ";
        /**
         * ssSearchInCombo to query mapping
         */
         if(ssKeyWord.getText().length()!=0) // FOR FIXING BUG#21 see excel busgs and fixes doc. If keyword is left empty then entire table is looked up.
        {
        switch (ssSearchInCombo.getSelectedItem().toString())
        {
            case "Name" : query +="CONCAT(`First Name`,' ',`Middle Name`,' ',`Last Name`) " ;ssExact.setEnabled(true); break;
            case "Fathers Name" : query+="`Fathers Name` "; ssExact.setEnabled(true); break;
            case "Roll No" : query +="roll_no " ; ssExact.setEnabled(false); break;
        }
       /**
        * like or exact for String and Integer(roll no)
        */
       if(ssSearchInCombo.getSelectedIndex()<2)
       {
        if(ssExact.isSelected())
        {
             query +="= ";
             query+="'"+ssKeyWord.getText() + "' ";
        }
        else
        {
            query +="like ";
            query+="'%"+ssKeyWord.getText() + "%' ";
        }
       }
       else // roll no is selected
       {
            query +="= ";
            query+=ssKeyWord.getText() + " ";
        
       }
      }else
         {
             query += "`First Name` like '%' ";
         }
        /**
         * Programme,Branch,Category
         */
        String prog = ssProgramme.getSelectedItem().toString();
        String branch = ssBranch.getSelectedItem().toString();
        Integer year = (Integer)ssYear.getModel().getValue();
        String category = ssCategory.getSelectedItem().toString();
        if (prog.compareTo("Any")!=0)
            query+="and Programme = '" + ssProgramme.getSelectedItem().toString() + "' ";
        if(branch.compareTo("Any")!=0)
            query+="and Branch = '" + branch + "' ";
        if(category.compareTo("Any")!=0)
            query+="and Category = '" + category + "' ";
        /**
         * DOB
         */
        LocalDate from = new LocalDate(ssDOBFrom.getDate());
        LocalDate to = new LocalDate(ssDOBTo.getDate());
        if(ssDOBFrom.getDate()!=null) //from is not null
        {
            if(ssDOBTo.getDate()!=null) // from and to are not null
            {
                if(ssDOBFrom.getDate().before(ssDOBTo.getDate())) // from is before to
                    query+="and `Date Of Birth` between '" + from.toString("yyyy-MM-dd") + "' and '" + to.toString("yyyy-MM-dd") + "' ";
                else
                    Error.errorDialog("Interchange DOB range");
            }
            else // from is not null but to is null
            {
                query+="and `Date Of Birth` >= '" + from.toString("yyyy-MM-dd") + "' and '" + to.toString("yyyy-MM-dd") + "' ";
            }
        }
        /**
         * year
         */
         if(year==1 || year==2 || year==3 || year ==4)
         {
            query+="and Year = ?";
        
        try {
            PreparedStatement ssQuery = books.prepareStatement(query);
            ssQuery.setInt(1,year);
            System.out.println(ssQuery.toString());
            searchStudent = ssQuery.executeQuery();
          } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
          }
         }
         else // year is not 1,2,3,4
         {
             try {
            PreparedStatement ssQuery = books.prepareStatement(query);
            System.out.println(ssQuery.toString());
            searchStudent = ssQuery.executeQuery();
            
          } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
          }
         }
        
 
         /**
          * Apply resultSet to table
          */
         ssTable.setModel(DbUtils.resultSetToTableModel(searchStudent));
        try {
            /**
             * Calculating no of results found
             */
            searchStudent.beforeFirst();
            int count=0;
            while(searchStudent.next())
                count ++;
            ssNoOfResults.setText( count +" Results Found");
        } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_searchStudentButtonActionPerformed

    private void ssSearchInComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ssSearchInComboItemStateChanged
        switch (ssSearchInCombo.getSelectedItem().toString())
        {
            case "Name" : ssExact.setEnabled(true); break;
            case  "Fathers Name": ssExact.setEnabled(true); break;
            case "Roll No": ssExact.setEnabled(false);ssExact.setSelected(true); break;
        }
    }//GEN-LAST:event_ssSearchInComboItemStateChanged

    private void ssTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ssTableMouseClicked
        try {
            Integer roll = (Integer)ssTable.getModel().getValueAt(ssTable.getSelectedRow(),0);
            Statement st = books.createStatement();
           ResultSet rs =  st.executeQuery("select * from student where roll_no=" + roll);
           rs.first();
           String path = rs.getString("pic_path");
           File f = new File(path);
           Image img = ImageIO.read(f);
           Image scaledImg = getScaledImage(img,140,140);
           ssPic.setIcon(new ImageIcon(scaledImg));
           ssEmail.setText(rs.getString("E-Mail"));
           ssPhone.setText(rs.getString("Contact Number"));
           ssAddress.setText(rs.getString("Mailing Address"));
        } catch (SQLException | IOException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ssTableMouseClicked

    private void ssTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ssTableMousePressed
        Integer roll = (Integer)ssTable.getModel().getValueAt(ssTable.getSelectedRow(),0);
        if(evt.getClickCount()==2)
        {
            new EditViewStudent(books,roll).setVisible(true);
        }   
         
    }//GEN-LAST:event_ssTableMousePressed

    private void searchBookManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBookManagementActionPerformed
        quickAccessSetVisibile(searchBookPanel);
    }//GEN-LAST:event_searchBookManagementActionPerformed

    private void searchStudentMemberManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchStudentMemberManagementActionPerformed
        quickAccessSetVisibile(searchStudentPanel);
    }//GEN-LAST:event_searchStudentMemberManagementActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        new SubjectManager().setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        new Register().setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new LocationManager().setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        JPanel panel = new JPanel();
        JLabel passwordLabel = new JLabel("Enter Super-Admin/Developer password");
        JPasswordField pass = new JPasswordField(10);
        panel.add(passwordLabel);
        panel.add(pass);
        panel.setLayout(new FlowLayout());
        if(JOptionPane.showConfirmDialog(null, panel, "Confirm Delete", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
        {
        if(pass.getText().compareTo(Config.superPass)==0)
        new managers.DataSourceBackDoor().setVisible(true);
        else
            JOptionPane.showMessageDialog(null,"Wrong Password","Password error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            Statement query = books.createStatement();
            ResultSet executeQuery = query.executeQuery("select issue_id from issue");
            executeQuery.next();
            new ReportGenerator(executeQuery.getInt("issue_id")).setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        JOptionPane.showMessageDialog(null,"This feature will be available soon","Help",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        JOptionPane.showMessageDialog(null,"This feature will be available soon","About",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new managers.ConfigManager().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void searchResultsTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchResultsTableMousePressed
        acc = Integer.parseInt(searchResultsTable.getModel().getValueAt(searchResultsTable.getSelectedRow(),0).toString());
        if(evt.getClickCount()==2)
        {
            new EditBook(books,acc).setVisible(true);
        }
    }//GEN-LAST:event_searchResultsTableMousePressed
/**
 * this method resets the issue fields. Only changes the roll no and acc no to null and throws exception for no data found
 * @param evt 
 */
    private void issueNewIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueNewIssueActionPerformed
        issueAcc.setText(null);
        issueRollNo.setText(null);
            
        issueTitle.setText(null);
        issueAuthor.setText(null);
        issuePublisher.setText(null);
        issueEdition.setText(null);
        issueSubject.setText(null);
        
        issueStudentName.setText(null);
        issueStudentYear.setText(null);
        issueStudentBranch.setText(null);
        issueStudentCardNumbers.setText(null);
            issueStudentProgramme.setText(null);
            issueStudentEmail.setText(null);
            issueStudentIssueCountSpinner.setValue(new Integer(0));
            
            // resetting images and labels
            issueBookStatusMessage.setText(null);
            issueStudentMessage.setText(null);
            issueMessage.setText(null);
            issueBookStatusPic.setIcon(null);
            issueStudentMessagePic.setIcon(null);
            issuePic.setIcon(null);
            issueStudentPic.setIcon(null);
            issueStudentPic.setText(null);
    }//GEN-LAST:event_issueNewIssueActionPerformed

    private void lockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockActionPerformed
        Config.startUpWindow.setVisible(false); 
        new managers.ScreenLock().setVisible(true);
    }//GEN-LAST:event_lockActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        PrintUtilities.printComponent(searchBookPanel);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
JPanel[] panelList = new JPanel[5];
           panelList[0] = searchBookPanel;
           panelList[1] = IssuePanel;
           panelList[2] = returnPanel;
           panelList[3] = overduesPanel;
           panelList[4] = searchStudentPanel;
           for(JPanel temp : panelList)
           {
               temp.setVisible(false);
           }        
    }//GEN-LAST:event_jPanel2MouseClicked

    /**
     * @param args the command line arguments
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel IssuePanel;
    private javax.swing.JButton addBookButton;
    private javax.swing.JRadioButton ascendingRadio;
    private javax.swing.JLabel date;
    private javax.swing.JRadioButton descendingRadio;
    private javax.swing.JRadioButton exactCombo;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox groupByCombo;
    private javax.swing.JComboBox inCombo;
    private javax.swing.JTextField issueAcc;
    private javax.swing.JTextField issueAuthor;
    private javax.swing.JLabel issueBookStatusMessage;
    private javax.swing.JLabel issueBookStatusPic;
    private javax.swing.JTextField issueEdition;
    private javax.swing.JButton issueIssue;
    private javax.swing.JButton issueMailStudent;
    private javax.swing.JLabel issueMessage;
    private javax.swing.JButton issueNewIssue;
    private javax.swing.JLabel issuePic;
    private javax.swing.JTextField issuePublisher;
    private javax.swing.JButton issueQuickAccess;
    private javax.swing.JTextField issueRollNo;
    private javax.swing.JButton issueShowReport;
    private javax.swing.JTextField issueStudentBranch;
    private javax.swing.JTextField issueStudentCardNumbers;
    private javax.swing.JTextField issueStudentEmail;
    private javax.swing.JSpinner issueStudentIssueCountSpinner;
    private javax.swing.JLabel issueStudentMessage;
    private javax.swing.JLabel issueStudentMessagePic;
    private javax.swing.JTextField issueStudentName;
    private javax.swing.JLabel issueStudentPic;
    private javax.swing.JTextField issueStudentProgramme;
    private javax.swing.JTextField issueStudentYear;
    private javax.swing.JTextField issueSubject;
    private javax.swing.JTextField issueTitle;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField keywordTextField;
    private javax.swing.JRadioButton likeCombo;
    private javax.swing.JButton lock;
    private javax.swing.JComboBox orderByCombo;
    private javax.swing.JLabel overduesFineLabel;
    private javax.swing.JPanel overduesPanel;
    private javax.swing.JButton overduesQuickAccess;
    private javax.swing.JButton overduesShowBook;
    private javax.swing.JButton overduesShowReport;
    private javax.swing.JButton overduesShowStudent;
    private javax.swing.JTable overduesTable;
    private javax.swing.JLabel resultMDString;
    private javax.swing.JTextField returnAccNo;
    private javax.swing.JTextField returnAuthor;
    private javax.swing.JTextField returnBranch;
    private javax.swing.JTextField returnCardNumbers;
    private javax.swing.JTextField returnDueDate;
    private javax.swing.JTextField returnEdition;
    private javax.swing.JTextField returnEmail;
    private javax.swing.JSpinner returnExceedSpinner;
    private javax.swing.JTextField returnFine;
    private javax.swing.JTextField returnIssueDate;
    private javax.swing.JButton returnMail;
    private javax.swing.JTextField returnName;
    private javax.swing.JPanel returnPanel;
    private javax.swing.JLabel returnPic;
    private javax.swing.JTextField returnProgramme;
    private javax.swing.JTextField returnPublisher;
    private javax.swing.JButton returnQuickAccess;
    private javax.swing.JButton returnReport;
    private javax.swing.JButton returnReturn;
    private javax.swing.JTextField returnRollNo;
    private javax.swing.JButton returnSearch;
    private javax.swing.JLabel returnStatusMessage;
    private javax.swing.JLabel returnStatusPic;
    private javax.swing.JTextField returnSubject;
    private javax.swing.JTextField returnTitle;
    private javax.swing.JTextField returnYear;
    private javax.swing.JButton searchBookManagement;
    private javax.swing.JPanel searchBookPanel;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchQueryPanel;
    private javax.swing.JButton searchQuickAccess;
    private javax.swing.JPanel searchResultsPanel;
    private javax.swing.JTable searchResultsTable;
    private javax.swing.JButton searchStudentButton;
    private javax.swing.JButton searchStudentMemberManagement;
    private javax.swing.JPanel searchStudentPanel;
    private javax.swing.ButtonGroup search_SearchForButtonGroup;
    private javax.swing.ButtonGroup search_SortButtonGroup;
    private javax.swing.ButtonGroup search_TypeButtonGroup;
    private javax.swing.JButton showDetailsButton;
    private javax.swing.JTextArea ssAddress;
    private javax.swing.JComboBox ssBranch;
    private javax.swing.JComboBox ssCategory;
    private com.toedter.calendar.JDateChooser ssDOBFrom;
    private com.toedter.calendar.JDateChooser ssDOBTo;
    private javax.swing.JTextField ssEmail;
    private javax.swing.JCheckBox ssExact;
    private javax.swing.JTextField ssKeyWord;
    private javax.swing.JLabel ssNoOfResults;
    private javax.swing.JTextField ssPhone;
    private javax.swing.JLabel ssPic;
    private javax.swing.JComboBox ssProgramme;
    private javax.swing.JComboBox ssSearchInCombo;
    private javax.swing.JTable ssTable;
    private javax.swing.JSpinner ssYear;
    private javax.swing.JLabel welcomeName;
    private javax.swing.JLayeredPane welcomeScreen;
    private javax.swing.JLabel welcomeScreenLabel;
    // End of variables declaration//GEN-END:variables
     Connection books,issue,members;
     ResultSet booksTable,detailsBooksTable,columnsBooksTable;      
     ResultSet issueRS,bookRS,studentRS; int returnFlag=0; 
     ResultSet searchStudent;
     ResultSet overdueResultSet;
     DefaultTableModel booksTableModel ;
     LocalDate today = new LocalDate(new java.util.Date());
     String title,author1,status;
}