/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managers;
import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;

/**
 * This class is used to get High Resolution Print-outs using Java
 * @author MAYANK
 */
public class PrintUtilities implements Printable {
    private Component componentToBePrinted;

  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }
  
  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }
  
 
 
  /**
   * create printJob and call print(1,2,3);
   */
  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }
  
  /**
   * Most imp method, it scales down frame size to pageformat size and prints
   */
  @Override
  public int print(Graphics g, PageFormat pf, int pageNumber)
                throws PrinterException {
            // TODO Auto-generated method stub
            if (pageNumber > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            // Get the preferred size ofthe component...
            Dimension compSize = componentToBePrinted.getPreferredSize();
            // Make sure we size to the preferred size
            componentToBePrinted.setSize(compSize);
            // Get the the print size
            Dimension printSize = new Dimension();
            printSize.setSize(pf.getImageableWidth(), pf.getImageableHeight());

            // Calculate the scale factor
            double scaleFactor = getScaleFactorToFit(compSize, printSize);
            // Don't want to scale up, only want to scale down
            if (scaleFactor > 1d) {
                scaleFactor = 1d;
            }

            // Calcaulte the scaled size...
            double scaleWidth = compSize.width * scaleFactor;
            double scaleHeight = compSize.height * scaleFactor;

            // Create a clone of the graphics context.  This allows us to manipulate
            // the graphics context without begin worried about what effects
            // it might have once we're finished
            Graphics2D g2 = (Graphics2D) g.create();
            // Calculate the x/y position of the component, this will center
            // the result on the page if it can
            double x = ((pf.getImageableWidth() - scaleWidth) / 2d) + pf.getImageableX();
            double y = ((pf.getImageableHeight() - scaleHeight) / 2d) + pf.getImageableY();
            // Create a new AffineTransformation
            AffineTransform at = new AffineTransform();
            // Translate the offset to out "center" of page
            at.translate(x, y);
            // Set the scaling
            at.scale(scaleFactor, scaleFactor);
            // Apply the transformation
            g2.transform(at);
            // Print the component
            componentToBePrinted.printAll(g2);
            // Dispose of the graphics context, freeing up memory and discarding
            // our changes
            g2.dispose();

            componentToBePrinted.revalidate();
            return Printable.PAGE_EXISTS;
        } 
  /*  @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }
  } */

  public static double getScaleFactorToFit(Dimension original, Dimension toFit) {

        double dScale = 1d;

        if (original != null && toFit != null) {

            double dScaleWidth = getScaleFactor(original.width, toFit.width);
            double dScaleHeight = getScaleFactor(original.height, toFit.height);

            dScale = Math.min(dScaleHeight, dScaleWidth);

        }

        return dScale;

    }
  public static double getScaleFactor(int iMasterSize, int iTargetSize) {

        double dScale = 1;
        if (iMasterSize > iTargetSize) {

            dScale = (double) iTargetSize / (double) iMasterSize;

        } else {

            dScale = (double) iTargetSize / (double) iMasterSize;

        }

        return dScale;

    }
  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
     * @param c
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally.
     * @param c */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}
