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

public class Secretary extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener {

    // Various
    private static final boolean mirrored       = Settings.get("ship.mirrored", false);
    private static final boolean offline        = Settings.get("ship.offline", false);
    private static final boolean welcomeEnabled = Settings.get("ship.welcome.enabled", true);
    private static final int     welcomeDelay   = Settings.get("ship.welcome.delay", 5000);
    private static final int     maxHeight      = Settings.get("ship.height", 800);
    private static final int     shipSkinIndex  = Settings.get("ship.skinIndex", 0);

    // Floating
    private static final boolean floatingEnabled = Settings.get("floating.enabled", true);

    // Voice
    private static final boolean voiceEnabled = Settings.get("voice.enabled", true);
    private static final int     voiceVolume  = Settings.get("voice.volume", 50);

    // Dialogs
    private static final boolean dialogsEnabled        = Settings.get("dialogs.enabled", true);
    private static final boolean dialogsOnClickEnabled = Settings.get("dialogs.onClick", true);
    private static final boolean dialogsOnIdleEnabled  = Settings.get("dialogs.onIdle", true);

    private static final int baloonDurationNoVoice = Settings.get("dialogs.baloon.noVoiceDuration", 3000);

    // Cache
    private static final boolean cacheSaveLocal = Settings.get("dialogs.enabled", true);
    private static final boolean cacheAudios    = Settings.get("dialogs.onClick", true);
    private static final boolean cacheImages    = Settings.get("dialogs.onIdle", true);

    private final AudioManager audioManager = new AudioManager();

    private final Ship                      ship;
    private final Map<String, List<Dialog>> phrases;

    private double startPoint = 0;
    private int    xClickPosition;

    private boolean running;


    private Baloon         baloon;
    private Image          icn;
    private SecretaryLabel secretaryLabel;

    /**
     * Application start point
     *
     * @param shipName The name of the ship to start with
     * @throws Exception Something went wrong while starting the ship
     */
    public Secretary(String shipName) throws Exception {

        ship = Ship.get(shipName);
        phrases = ship.getPhrases();

        swingSetup();

        if (floatingEnabled) {
            secretaryLabel.startFloating();
        }

        if (dialogsOnIdleEnabled) {
            idle();
        }

        if (welcomeEnabled) {
            onLogin(); // Say Hi!
        }

    }

    private void idle() {
        new Thread(() -> {
            Util.sleep(5000); // Wait 5 seconds before starting idle loop
            while (running) {
                secretaryLabel.waitIdle();
                speak(ship.getPhrases().get("Secretary (Idle)"));
                secretaryLabel.waitSpeak();
            }
        }).start();
    }

    private void onLogin() {
        new Thread(() -> {
            Util.sleep(welcomeDelay);
            speak(ship.getPhrases().get("Login"), false);
        }).start();
    }

    private byte[] getShipImage(Ship ship) throws IOException {
        Path imgPath = Paths.get("resources/" + ship.getName() + "_" + shipSkinIndex + ".png");

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
                List<azurlane.entities.Image> set = ship.getImageSizeSet(shipSkinIndex);
                imgData = set.get(set.size() - 1).download();
                fos.write(imgData);
            }
        }

        return imgData;
    }

    private void swingSetup() throws IOException {

        byte[] imgData = getShipImage(ship);

        BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(imgData));
        if (mirrored) {
            buffImage = Util.flipImage(buffImage);
        }

        icn = buffImage.getScaledInstance(-1, maxHeight, Image.SCALE_AREA_AVERAGING);

        setTitle(ship.getName());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        setLayout(new FlowLayout());
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        secretaryLabel = new SecretaryLabel(new ImageIcon(icn));

        setContentPane(secretaryLabel);
        setSize(icn.getWidth(null), icn.getHeight(null) + 100);
        setLocation(0, Util.getYStartPosition(getHeight()));

        baloon = new Baloon(getWidth(), getHeight());
        add(baloon);

        setVisible(true);
        System.out.println("Swing setup done");

        running = true;
    }


    public void speak(List<Dialog> dialogs) {
        speak(dialogs, true);
    }

    public void speak(List<Dialog> dialogs, boolean withJump) {
        if (secretaryLabel.isSpeaking() || !dialogsEnabled) {
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
                if (voiceEnabled) {
                    audioManager.play(dialog.getAudio(), voiceVolume);
                } else {
                    Util.sleep(baloonDurationNoVoice);
                }

                baloon.toggle(false);
                secretaryLabel.speak(false);
            }).start();
        });
    }


    private void onClick() {
        if (dialogsOnClickEnabled) {
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

    // Endregion
}