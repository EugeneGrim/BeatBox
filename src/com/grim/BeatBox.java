package com.grim;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class BeatBox {
    private JFrame theFrame;
    private Form form;
    private ArrayList<JCheckBox> checkboxList;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    private int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public void buildGui() {
        theFrame = new JFrame("Super BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        form = new Form(this);
        JPanel background = form.getForm();
        theFrame.getContentPane().add(background);
        checkboxList = form.getCheckBoxes();

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setMinimumSize(theFrame.getContentPane().getSize());
        theFrame.setVisible(true);
    }

    private void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        int[] trackList;
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

    public void stopSequencer() {
        sequencer.stop();
    }

    public void clear() {
       sequencer.stop();
       for (int i = 0; i < 256; i++) {
           checkboxList.get(i).setSelected(false);
       }
       sequence.deleteTrack(track);
       track = sequence.createTrack();
    }

    public void upTempo() {
        float tempoFactor = sequencer.getTempoFactor();
        sequencer.setTempoFactor( (float) (tempoFactor * 1.03) );
    }

    public void downTempo() {
        float tempoFactor = sequencer.getTempoFactor();
        sequencer.setTempoFactor( (float) (tempoFactor * 0.97) );
    }

    public void save() {
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

    public void open() {
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

        if (form.isAutoStart()) {
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
