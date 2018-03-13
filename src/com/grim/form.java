package com.grim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

    private BeatBox beatBox;

    private ArrayList<JCheckBox> checkboxList = new ArrayList<>();

    public form(BeatBox beatBox) {
        this.beatBox = beatBox;

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel.setLayout(grid);

        MyCheckboxClickedtListener cheboxClicked = new MyCheckboxClickedtListener();

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            c.addActionListener(cheboxClicked);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                beatBox.buildTrackAndStart();
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                beatBox.stopSequencer();
            }
        });

        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                beatBox.clear();
            }
        });

        upTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                beatBox.upTempo();
            }
        });

        downTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                beatBox.downTempo();
            }
        });

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                beatBox.open();
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                beatBox.save();
            }
        });
    }

    public class MyCheckboxClickedtListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            if (autoStart.isSelected()) {
                beatBox.buildTrackAndStart();
            }
        }
    }

    public ArrayList<JCheckBox> getCheckBoxes() {
        return checkboxList;
    }

    public JPanel getForm() {
        return background;
    }
}
