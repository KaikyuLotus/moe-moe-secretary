package core.entities;

import azurlane.endpoints.Ship;
import azurlane.entities.Dialog;
import azurlane.utils.Util;
import core.audio.AudioManager;
import core.settings.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Secretary extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private static final String MAX_HEIGHT               = "ship.height";
    private static final String MIRRORED                 = "ship.mirrored";
    private static final String OFFLINE                  = "ship.offline";
    private static final String WELCOME_ENABLED          = "ship.welcome.enabled";
    private static final String WELCOME_DELAY            = "ship.welcome.delay";
    private static final String FLOAT_ENABLED            = "floating.enabled";
    private static final String VOICE_ENABLED            = "voice.enabled";
    private static final String VOICE_VOLUME             = "voice.volume";
    private static final String DIALOGS_ENABLED          = "dialogs.enabled";
    private static final String DIALOGS_ON_CLICK_ENABLED = "dialogs.onClick";
    private static final String DIALOGS_ON_IDLE_ENABLED  = "dialogs.onIdle";
    private static final String BALOON_DURATION_NO_VOICE = "dialogs.baloon.noVoiceDuration";
    private static final String CACHE_SAVE_LOCAL         = "dialogs.enabled";
    private static final String CACHE_AUDIOS             = "dialogs.onClick";
    private static final String CACHE_IMAGES             = "dialogs.onIdle";

    private final AudioManager audioManager = new AudioManager();

    private final Ship                      ship;
    private final Map<String, List<Dialog>> phrases;

    private double startPoint = 0;
    private int    xClickPosition;

    private boolean running;

    private int skinIndex = 0;

    private Baloon         baloon;
    private SecretaryLabel secretaryLabel;

    /**
     * Application start point
     *
     * @param shipName The name of the ship to start with
     * @throws Exception Something went wrong while starting the ship
     */
    public Secretary(String shipName) throws Exception {

        ship = new Ship(shipName);
        phrases = ship.getDialogs();

        swingSetup();

        if (Settings.get(FLOAT_ENABLED, true)) {
            secretaryLabel.startFloating();
        }

        if (Settings.get(DIALOGS_ON_IDLE_ENABLED, true)) {
            idle();
        }

        if (Settings.get(WELCOME_ENABLED, true)) {
            onLogin(); // Say Hi!
        }

    }

    private void idle() {
        new Thread(() -> {
            Util.sleep(5000); // Wait 5 seconds before starting idle loop
            while (running) {
                secretaryLabel.waitIdle();
                speak(ship.getDialogs().get("Secretary (Idle)"));
                secretaryLabel.waitSpeak();
            }
        }).start();
    }

    private void onLogin() {
        new Thread(() -> {
            Util.sleep(Settings.get(WELCOME_DELAY, 5000));
            speak(ship.getDialogs().get("Login"), false);
        }).start();
    }

    private byte[] getShipImage(Ship ship, int skinIndex) throws IOException {
        Path imgPath = Paths.get("resources/" + ship.getName() + "_" + skinIndex + ".png");

        byte[] imgData;

        if (Files.exists(imgPath)) {
            System.out.println("Ship already downloaded");
            imgData = Files.readAllBytes(imgPath);
        } else {
            System.out.println("Ship image not in memory, download and saving...");
            File file = imgPath.toFile();
            if (!file.createNewFile()) {
                throw new IOException("Cannot save temp img...");
            }
            try (FileOutputStream fos = new FileOutputStream(imgPath.toFile().getAbsolutePath())) {
                List<azurlane.entities.Image> set = ship.getImageSizeSet(skinIndex);
                imgData = set.get(set.size() - 1).download();
                fos.write(imgData);
            }
        }

        return imgData;
    }


    private Image loadSkin(int index) throws IOException {

        if (index < 0) {
            index = ship.getSkinCount() - 1;
            skinIndex = index;
        } else if (index == ship.getSkinCount()) {
            skinIndex = 0;
            index = 0;
        }

        byte[] imgData = getShipImage(ship, index);

        BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(imgData));
        if (Settings.get(MIRRORED, false)) {
            buffImage = Util.flipImage(buffImage);
        }

        Image i = buffImage.getScaledInstance(-1, Settings.get(MAX_HEIGHT, 800), Image.SCALE_AREA_AVERAGING);

        setSize(i.getWidth(null), i.getHeight(null) + 100);
        setLocation(0, Util.getYStartPosition(getHeight()));
        return i;
    }

    private void swingSetup() throws IOException {


        setTitle(ship.getName());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        setLayout(new FlowLayout());
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        secretaryLabel = new SecretaryLabel(new ImageIcon(loadSkin(Settings.get("ship.skinIndex", 0))));

        setContentPane(secretaryLabel);

        baloon = new Baloon(getWidth(), getHeight());
        add(baloon);

        setVisible(true);
        System.out.println("Swing setup done");

        running = true;
    }

    public void setAlwaysOnTop() {

    }

    public void reloadSkin() throws IOException {
        secretaryLabel.setIcon(new ImageIcon(loadSkin(skinIndex)));
    }

    public void speak(List<Dialog> dialogs) {
        speak(dialogs, true);
    }

    public void speak(List<Dialog> dialogs, boolean withJump) {
        if (secretaryLabel.isSpeaking() || !Settings.get(DIALOGS_ENABLED, true) || !running) {
            return;
        }
        Random rand = new Random();

        Dialog dialog = dialogs.get(rand.nextInt(dialogs.size()));

        SwingUtilities.invokeLater(() -> {
            secretaryLabel.speak(true);

            baloon.setText("<html><div style='text-align: center';><p style='padding-left: 16px; padding-right: 16px;'><br>" + dialog.getDialog() + "</p></div></html>");
            //baloon.setText("Oh");
            baloon.toggle(true);
            new Thread(() -> {
                if (Settings.get(VOICE_ENABLED, true)) {
                    audioManager.play(dialog.getAudio(), Settings.get(VOICE_VOLUME, 50));
                } else {
                    Util.sleep(Settings.get(BALOON_DURATION_NO_VOICE, 3000));
                }

                baloon.toggle(false);
                secretaryLabel.speak(false);
            }).start();
        });
    }


    public void close() {
        this.running = false;
    }

    private void onClick() {
        if (Settings.get(DIALOGS_ON_CLICK_ENABLED, true)) {
            speak(phrases.get("Secretary (Touch)"));
        }
    }

    // Region: Swing Mouse Events
    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getXOnScreen();
        xClickPosition = e.getXOnScreen();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        startPoint = e.getXOnScreen();
        if (xClickPosition == e.getXOnScreen()) {
            secretaryLabel.speakJump();
            onClick();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        setLocation((int) getLocation().getX() + e.getXOnScreen() - (int) startPoint, getY());
        startPoint = e.getXOnScreen();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

        try {

            System.out.println("Key: " + e.getKeyChar());

            switch (e.getKeyChar()) {
                case 'k':
                    skinIndex++;
                    reloadSkin();
                    break;
                case 'j':
                    skinIndex--;
                    reloadSkin();
                    break;
                case 't':

                    break;
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    // Endregion
}