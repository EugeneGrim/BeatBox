package com.grim;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class form {
    private JPanel background;
    private JPanel nameBox;
    private JPanel buttonBox;
    private JButton start;
    private JButton stop;
    private JCheckBox autoStart;
    private JButton upTempo;
    private JButton downTempo;
    private JButton save;
    private JButton open;
    private JButton clear;
    private JPanel mainPanel;

    public form(BeatBox beatBox) {
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //beatBox.bu
            }
        });
    }

    public JPanel getForm() {
        return background;
    }
}
