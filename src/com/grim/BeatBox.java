package com.grim;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class BeatBox {
    private JFrame theFrame;
    private JCheckBox autoStart;
    private ArrayList<JCheckBox> checkboxList;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    private String[] instrumentsName = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal",
            "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom",
            "High Agogo", "Open Hi Conga"};

    private int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public void buildGui() {
        theFrame = new JFrame("Super BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        BorderLayout layout = new BorderLayout();
//        JPanel background = new JPanel(layout);
//        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        checkboxList = new ArrayList<>();
//        JPanel buttonBox = new JPanel();
//        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
//
//        JButton start = new JButton("Start");
//        start.addActionListener(new MyStartListener());
//        buttonBox.add(start);
//
//        autoStart = new JCheckBox("Auto start");
//        autoStart.setSelected(true);
//        buttonBox.add(autoStart);
//
//        JButton stop = new JButton("Stop");
//        stop.addActionListener(new MyStopListener());
//        buttonBox.add(stop);
//
//        JButton upTempo = new JButton("Tempo Up");
//        upTempo.addActionListener(new MyUpTempoListener());
//        buttonBox.add(upTempo);
//
//        JButton downTempo = new JButton("Tempo Down");
//        downTempo.addActionListener(new MyDownTempoListener());
//        buttonBox.add(downTempo);
//
//        JButton save = new JButton("Save...");
//        save.addActionListener(new MySaveListener());
//        buttonBox.add(save);
//
//        JButton open = new JButton("Open...");
//        open.addActionListener(new MyOpenListener());
//        buttonBox.add(open);
//
//        JButton clear = new JButton("Clear");
//        clear.addActionListener(new MyClearListener());
//        buttonBox.add(clear);
//
//        Box nameBox = new Box(BoxLayout.Y_AXIS);
//        for (int i = 0; i < 16; i++) {
//            nameBox.add(new Label(instrumentsName[i]));
//        }
//
//        background.add(BorderLayout.EAST, buttonBox);
//        background.add(BorderLayout.WEST, nameBox);
//
//        theFrame.getContentPane().add(background);
//
//        GridLayout grid = new GridLayout(16, 16);
//        grid.setVgap(1);
//        grid.setHgap(2);
//        JPanel mainPanel = new JPanel(grid);
//
//        background.add(BorderLayout.CENTER, mainPanel);
//
//        MyCheckboxClickedtListener cheboxClicked = new MyCheckboxClickedtListener();
//
//        for (int i = 0; i < 256; i++) {
//            JCheckBox c = new JCheckBox();
//            c.setSelected(false);
//            c.addActionListener(cheboxClicked);
//            checkboxList.add(c);
//            mainPanel.add(c);
//        }

        form ff = new form(this);
        JPanel background = ff.getForm();
        theFrame.getContentPane().add(background);

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    private void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();;
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildTrackAndStart() {
        int[] trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            trackList = new int[16];
            int key = instruments[i];
            for (int j = 0; j < 16; j++) {
                JCheckBox jc = checkboxList.get(j + (16*i));
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }

            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }

        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyCheckboxClickedtListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            if (autoStart.isSelected()) {
                buildTrackAndStart();
            }
        }
    }

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    }

    public class MyClearListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
            for (int i = 0; i < 256; i++) {
                checkboxList.get(i).setSelected(false);
            }
            sequence.deleteTrack(track);
            track = sequence.createTrack();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor( (float) (tempoFactor * 1.03) );
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor( (float) (tempoFactor * 0.97) );
        }
    }

    public class MySaveListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox check = checkboxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }

            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(theFrame);
            try {
                FileOutputStream fileStream = new FileOutputStream(fileSave.getSelectedFile());
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MyOpenListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            boolean[] checkboxState = new boolean[256];
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showSaveDialog(theFrame);

            try {
                FileInputStream fileStream = new FileInputStream(fileOpen.getSelectedFile());
                ObjectInputStream is = new ObjectInputStream(fileStream);
                checkboxState = (boolean[]) is.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 256; i++) {
                JCheckBox check = checkboxList.get(i);
                if (checkboxState[i]) {
                    check.setSelected(true);
                } else {
                    check.setSelected(false);
                }
            }

            sequencer.stop();
            buildTrackAndStart();
        }
    }

    private void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];
            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i+1));
            }
        }
    }

    private MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }
}
