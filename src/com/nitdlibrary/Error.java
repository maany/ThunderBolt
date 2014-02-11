/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// paste this in catch blocks - > Error.errorDialog(ex.toString());
package com.nitdlibrary;

import javax.swing.JOptionPane;

/**
 *
 * @author MAYANK
 */
public class Error {
    
    public static void errorDialog(String error)
    {
        JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main (String args[])
    {
        errorDialog("message");
    }
}