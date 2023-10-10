package nz.ac.wgtn.swen225.lc.app;

import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.IllegalMove;
import nz.ac.wgtn.swen225.lc.domain.tiles.Tile;
import nz.ac.wgtn.swen225.lc.recorder.Recorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * RecorderPanel is a JPanel that provides recording and playback controls for the Larry Croft's Adventures game.
 * It allows the player to record and replay their actions in the game.
 */
public class RecorderPanel extends JPanel {

    private JButton recordButton;
    private JButton loadButton;
    private JButton stepButton;
    private JButton autoReplayButton;
    private JSlider replaySpeedSlider;

    /**
     * ArrayList to store recorded game moves.
     */
    public static ArrayList<Move> moves;

    /**
     * A boolean flag to indicate whether recording is in progress.
     */
    public static boolean recording = false;
    private boolean recordingIndicatorVisible = false; // Flag to control the visibility of the recording indicator
    private Timer recordingIndicatorTimer; // Timer for the recording indicator
    public static int time;
    public static int count = 0;
    File file = null;
    public static App app;
    int chapX;
    int chapY;
    int chapTreasures;
    int chapInitLevel;
    int boardTreasureCount;
    int timeLeft;
    Tile[][] board;
    //speed of auto replay
    int speed;

    /**
     * Constructs a RecorderPanel and initializes its components.
     */
    public RecorderPanel(App app) {
        this.app = app;
        initializeComponents();
        addComponentsToPanel();
    }

    /**
     * Initializes the UI components of the RecorderPanel.
     */
    private void initializeComponents() {
        this.setPreferredSize(new Dimension(300, 300));
        this.setBackground(new Color(174, 119, 100));
        recordButton = createSimpleButton("Record");
        loadButton = createSimpleButton("Load Recorded Game");
        stepButton = createSimpleButton("Step by Step");
        autoReplayButton = createSimpleButton("Auto Replay");

        // Add action listeners to buttons
        recordButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                recording = !recording;
                if (recording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });

        // Add action listeners for the other buttons
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement load recorded game logic here
                // sets up an interface to choose a load file
                JFileChooser fc = new JFileChooser("LarryCroftsAdventures/Saves");
                fc.setDialogTitle("Choose a saved game");
                int retVal = fc.showOpenDialog(null);

                if (retVal == JFileChooser.APPROVE_OPTION){
                    file = fc.getSelectedFile();
                }
                //getting the moves out of the loaded file
                moves = new Recorder().loadSave(file,app);

            }
        });

        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement step-by-step logic here
                if (file == null){
                    JOptionPane.showMessageDialog(null,
                            "You need to load a file first!",
                            "File not chosen!",
                            JOptionPane.PLAIN_MESSAGE);
                } else {
                    app.repaint();
                    if (!moves.isEmpty()) {
                        try {
                            new Recorder().step(moves.remove(0));
                        } catch (IllegalMove ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else{
                        JOptionPane.showMessageDialog(null,
                                "All moves have been shown!",
                                "Replay finished!",
                                JOptionPane.PLAIN_MESSAGE);
                    }

                }
            }
        });

        autoReplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement auto replay logic here
                if (file == null){
                    JOptionPane.showMessageDialog(null,
                            "You need to load a file first!",
                            "File not chosen!",
                            JOptionPane.PLAIN_MESSAGE);
                } else{
                    //timer to be able to go through each action at a certain speed
                    recordingIndicatorTimer = new Timer();
                    recordingIndicatorTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                if (!moves.isEmpty()) {
                                    new Recorder().step(moves.remove(0));
                                } else{
                                    JOptionPane.showMessageDialog(null,
                                            "All moves have been shown!",
                                            "Replay finished!",
                                            JOptionPane.PLAIN_MESSAGE);
                                    recordingIndicatorTimer.cancel();
                                }
                            } catch (IllegalMove ex) {
                                throw new RuntimeException(ex);
                            }
                            repaint();
                        }
                    }, 0, ((speed*200)/2)+200);
                }
            }
        });

        addSlider();

    }

    /**
     * adds JSlider to panel
     */
    private void addSlider(){
        replaySpeedSlider = new JSlider(JSlider.HORIZONTAL, 0, 3, 1) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw the title "Replay Speed" above the slider
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.setColor(Color.BLACK);
                String title = "Replay Speed";
                int titleWidth = g.getFontMetrics().stringWidth(title);
                int x = (getWidth() - titleWidth) / 2;
                int y = 40;
                g.drawString(title, x, y);
            }
        };
        replaySpeedSlider.setBackground(Color.WHITE);


        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        replaySpeedSlider.setBorder(compound);
        replaySpeedSlider.setBorder(new RoundedBorder(20));

        replaySpeedSlider.setMajorTickSpacing(1);
        replaySpeedSlider.setMinorTickSpacing(1);
        replaySpeedSlider.setPaintTicks(true);
        replaySpeedSlider.setPaintLabels(true);


        replaySpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                speed = replaySpeedSlider.getValue();
                // Use the 'speed' value to adjust the game's replay speed
                // Implement your replay speed adjustment logic here

            }
        });



    }

    /**
     * Adds UI components to the panel.
     */
    private void addComponentsToPanel() {
        setLayout(new GridLayout(5, 1, 10, 10));
        add(recordButton);
        add(loadButton);
        add(stepButton);
        add(autoReplayButton);
        add(replaySpeedSlider);
    }


    /**
     * Creates a simple JButton with specified text and appearance.
     *
     * @param text The text to display on the button.
     * @return A JButton with the specified text and appearance.
     */
    private static JButton createSimpleButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        button.setBorder(compound);
        button.setBorder(new RoundedBorder(20)); //10 is the radius
        return button;
    }

    // Helper method to start recording and show recording indicator
    private void startRecording() {
        recordButton.setText("Stop Recording");
        count++;
        Chap chap = app.getBoard().getChap();
        chapX = chap.getX();
        chapY = chap.getY();
        chapTreasures = chap.getPlayerTreasureCount();
        boardTreasureCount = app.getBoard().getBoardTreasureCount();
        chapInitLevel = app.getBoard().getLevel();
        timeLeft = app.getBoard().getTime();
        board = app.getBoard().getTiles();

        moves = new ArrayList<>();

        recordingIndicatorTimer = new Timer();

    }

    // Helper method to stop recording and hide recording indicator
    private void stopRecording() {
        recordButton.setText("Record");
        recordingIndicatorTimer.cancel();
        recordingIndicatorVisible = false;
        repaint();

        Recorder recorder = new Recorder(moves, chapX, chapY, chapTreasures,
                boardTreasureCount, chapInitLevel, timeLeft, board);

        try {
            recorder.saveRecorder(count);
            JOptionPane.showMessageDialog(null, "Game saved successfully!", "Save success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        moves.clear();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (recording && recordingIndicatorVisible) {
            g.setColor(Color.RED);
            int radius = 10;
            int x = getWidth() / 2 - radius;
            int y = getHeight() / 2 - radius;
            g.fillOval(0, 0, radius * 2, radius * 2);
        }
    }

    private static class RoundedBorder implements Border {

        private int radius;


        RoundedBorder(int radius) {
            this.radius = radius;
        }


        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }


        public boolean isBorderOpaque() {
            return true;
        }


        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }

    }
    public int getFileCount() {
        return count;
    }

    public ArrayList<Move> getMovesList() {
        return moves;
    }
}
