package com.kitsunecode.mms.core.entities.swing;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.Settings;
import com.kitsunecode.mms.core.entities.WaifuData;
import com.kitsunecode.mms.core.entities.audio.Audio;
import com.kitsunecode.mms.core.entities.audio.AudioPlayer;
import com.kitsunecode.mms.core.utils.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Secretary extends JFrame implements MouseListener, MouseMotionListener,
        MouseWheelListener, KeyListener, WindowListener {

    private final AudioPlayer audioPlayer = new AudioPlayer();

    private final IWaifuAdapter waifuInterface;

    private boolean floatingToggle;

    private int xClickPosition;
    private int yClickPosition;
    private int dragDiffX = 0;
    private int dragDiffY = 0;

    private boolean running;
    private boolean alwaysOnTop;
    private boolean mirrored;

    private int skinIndex;

    private int leftOffset;

    private Baloon baloon;
    private SecretaryLabel secretaryLabel;

    private BufferedImage buffImage;

    private boolean isManual = false;
    private boolean isDragging = false;

    private Audio currentAudio;

    /**
     * Application start point
     *
     * @param waifu Your initialized waifu
     * @throws Exception Something went wrong while starting the ship
     */
    public Secretary(IWaifuAdapter waifu) throws Exception {

        waifuInterface = waifu;
        WaifuData data = waifu.getWaifuData();
        skinIndex = data.getSkinIndex();
        alwaysOnTop = data.isAlwaysOnTop();
        floatingToggle = data.isFloatingEnabled();
        mirrored = data.isMirrored();

        swingSetup();

        secretaryLabel.startFloating();

        if (Settings.isDialogsOnIdle()) {
            idle();
        }

        if (Settings.isWaifuWelcomeEnabled()) {
            onLogin(); // Say Hi!
        }

    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        super.paint(g);
        if (!isManual) {
            new Thread(() -> {
                BufferedImage image = getScreenShot(this);
                Area area = Util.getOutline(image, 0);
                setShape(area);
            }).start();
        }
    }

    public BufferedImage getScreenShot(Component component) {
        BufferedImage image = new BufferedImage(
                component.getWidth(),
                component.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        setShape(null);
        isManual = true;
        Graphics graphics = image.getGraphics();
        if (graphics != null) {
            component.printAll(graphics);
        }
        isManual = false;

        // Debug screenshot
        // try {
        //     ImageIO.write(image, "PNG", Paths.get("resources", "screen.png").toFile());
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        return image;
    }

    private void idle() {
        new Thread(() -> {
            Util.sleep(5000); // Wait 5 seconds before starting idle loop
            while (running) {
                secretaryLabel.waitIdle();
                speak(waifuInterface.getDialogs(waifuInterface.onIdleEventKey()), null);
                secretaryLabel.waitSpeak();
            }
        }).start();
    }

    private void onLogin() {
        new Thread(() -> {
            Util.sleep(Math.max(Settings.getWaifuWelcomeDelay(), 1000));
            speak(waifuInterface.getDialogs(waifuInterface.onLoginEventKey()), null);
        }).start();
    }

    private Image loadSkin(int index) throws IOException {
        int updatedIndex = index;
        if (updatedIndex < 0) {
            updatedIndex = waifuInterface.getSkinCount() - 1;
        } else if (updatedIndex >= waifuInterface.getSkinCount()) {
            updatedIndex = 0;
        }

        skinIndex = updatedIndex;

        byte[] imgData = Util.getShipImage(waifuInterface, updatedIndex);

        buffImage = ImageIO.read(new ByteArrayInputStream(imgData));
        if (mirrored) {
            buffImage = Util.flipImage(buffImage);
        }

        Image i = buffImage;
        if (Settings.getWaifuHeight() != 0) {
            i = buffImage.getScaledInstance(-1, Settings.getWaifuHeight(), Image.SCALE_AREA_AVERAGING);
        }

        int width = Math.max(Settings.getBaloonWidth(), i.getWidth(null));
        leftOffset = (int) ((width - i.getWidth(null)) / 2f);

        setSize(width, i.getHeight(null));
        setLocation(leftOffset + getX(), Util.getYStartPosition(i.getHeight(null)));

        return i;
    }

    public void flipSkin() {
        System.out.println("Flipping skin");
        buffImage = Util.flipImage(Util.toBufferedImage((ImageIcon) secretaryLabel.getIcon()));
        secretaryLabel.setIcon(new ImageIcon(buffImage));
        mirrored = !mirrored;
    }

    public final int getStartY() {
        int y = Util.getScreenSize().height - getHeight();
        String settingPos = Settings.getWaifuStartY().toLowerCase(Locale.ENGLISH);
        if (!"auto".equals(settingPos) && settingPos.matches("-?\\d+")) {
            y += Integer.parseInt(settingPos);
        }

        return y;
    }

    private void swingSetup() throws IOException {
        setTitle(waifuInterface.getShowableName());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        addWindowListener(this);

        setLayout(null);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        ImageIcon icn = new ImageIcon(loadSkin(skinIndex));
        secretaryLabel = new SecretaryLabel(icn, this);
        secretaryLabel.setBounds(secretaryLabel.getDesiredBounds(leftOffset, icn.getIconWidth(), icn.getIconHeight()));

        baloon = new Baloon(getWidth(), getHeight());
        baloon.setBounds(baloon.getDesiredSize(leftOffset, icn.getIconWidth(), icn.getIconHeight()));

        add(baloon);
        add(secretaryLabel);

        URL ico = this.getClass().getResource("/icon.png");
        if (ico != null) {
            setIconImage(new ImageIcon(ico).getImage());
        }

        setAlwaysOnTop(alwaysOnTop);
        setLocation(waifuInterface.getWaifuData().getPosition(), getStartY());
        setType(Util.isWindows() ? Type.UTILITY : Type.POPUP);

        setVisible(true);

        secretaryLabel.onVisible();

        System.out.println("Swing setup done");
        running = true;
    }

    public void toggleAlwaysOnTop() {
        alwaysOnTop = !alwaysOnTop;
        setAlwaysOnTop(alwaysOnTop);
        System.out.println("Always on top: " + alwaysOnTop);
    }

    public void toggleFloating() {
        floatingToggle = !floatingToggle;
        System.out.println("Floating: " + floatingToggle);
    }

    public void reloadSkin() throws IOException {
        if (waifuInterface.getSkinCount() == 1) return;
        secretaryLabel.setIcon(new ImageIcon(loadSkin(skinIndex)));
        secretaryLabel.setBounds(secretaryLabel.getDesiredBounds(
                leftOffset,
                secretaryLabel.getIcon().getIconWidth(),
                secretaryLabel.getIcon().getIconHeight())
        );
        baloon.setBounds(baloon.getDesiredSize(
                leftOffset,
                secretaryLabel.getIcon().getIconWidth(),
                secretaryLabel.getIcon().getIconHeight())
        );
        setLocation(getX(), getStartY());
    }

    private void speakStateChanged(boolean speaking) {
        if (!speaking) {
            baloon.toggle(false);
        }
        secretaryLabel.speak(speaking);
    }

    private void speakNoVoice() {
        new Thread(() -> {
            speakStateChanged(true);
            Util.sleep(Settings.getDialogsBaloonNoVoiceDuration());
            speakStateChanged(false);
        }).start();
    }

    private void internalSpeak(Dialog dialog, Runnable optionalCallback) {
        String text = Util.parseDialog(dialog.getDialog());
        if (!"".equals(text)) {
            baloon.toggle(true);
            baloon.setText(Settings.getBaloonFormatString().replace("[[text]]", text + "<br>&zwnj;"));
        }

        if (!Settings.isVoiceEnabled() || dialog.getAudio() == null || "".equals(dialog.getAudio())) {
            speakNoVoice();
            return;
        }

        String fileName = Util.fileFromUrl(dialog.getAudio());
        File audioFile = waifuInterface.downloadFile(dialog.getAudio(), fileName);

        Audio audio = new Audio(audioFile, Settings.getVoiceVolume()) {
            @Override
            public void onStart() {
                speakStateChanged(true);
            }

            @Override
            public void onFinish() {
                speakStateChanged(false);
                currentAudio = null;
                if (optionalCallback != null) {
                    optionalCallback.run();
                }
            }
        };
        currentAudio = audio;
        audioPlayer.play(audio);
    }

    public void speak(List<Dialog> dialogs, Runnable optionalCallback) {

        if (secretaryLabel.isSpeaking() || !Settings.isDialogsEnabled() || dialogs.isEmpty()) {
            if (optionalCallback != null) {
                optionalCallback.run();
            }
            return;
        }

        Dialog dialog = dialogs.get(new Random().nextInt(dialogs.size()));
        internalSpeak(dialog, optionalCallback);
    }

    public boolean isDragging() {
        return isDragging;
    }

    private void internalClose() {
        try {
            this.running = false;
            waifuInterface.getWaifuData().setPosition(getX());
            waifuInterface.getWaifuData().setAlwaysOnTop(alwaysOnTop);
            waifuInterface.getWaifuData().setFloatingEnabled(floatingToggle);
            waifuInterface.getWaifuData().setMirrored(mirrored);
            waifuInterface.getWaifuData().setSkinIndex(skinIndex);
            waifuInterface.saveDataToFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    public void close() {
        boolean wasSpeaking = currentAudio != null && currentAudio.isPlaying();

        List<Dialog> logoutDialogs = waifuInterface.getDialogs(waifuInterface.onLogoutEventKey());

        if (logoutDialogs.size() == 0 || !Settings.isLogoutDialogEnabled()) {
            if (wasSpeaking) {
                currentAudio.stop();
            }
            internalClose();
            return;
        }

        if (wasSpeaking) {
            currentAudio.addCloseAction(() -> speak(logoutDialogs, this::internalClose));
            currentAudio.stop();
            return;
        }

        speak(logoutDialogs, this::internalClose);
    }

    private void onClick() {
        if (Settings.isDialogsOnClick()) {
            speak(waifuInterface.getDialogs(waifuInterface.onTouchEventKey()), null);
        }
    }

    // Region: Swing Mouse Events
    @Override
    public void mousePressed(MouseEvent e) {
        xClickPosition = e.getXOnScreen();
        yClickPosition = e.getYOnScreen();
        dragDiffX = xClickPosition - getX();
        dragDiffY = yClickPosition - getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging = false;
        if (e.getButton() == 2) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            return;
        }
        if (xClickPosition == e.getXOnScreen() && yClickPosition == e.getYOnScreen()) {
            secretaryLabel.speakJump();
            onClick();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        isDragging = true;
        setLocation(e.getXOnScreen() - dragDiffX, Settings.isWaifuYDragEnabled() ? e.getYOnScreen() - dragDiffY : getY());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        try {
            switch (Character.toLowerCase(e.getKeyChar())) {
                case 'k':
                    skinIndex++;
                    reloadSkin();
                    break;
                case 'j':
                    skinIndex--;
                    reloadSkin();
                    break;
                case 't':
                    toggleAlwaysOnTop();
                    break;
                case 'f':
                    toggleFloating();
                    break;
                case 's':
                    flipSkin();
                    break;
                default:
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 2) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // No action
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // No action
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action
    }

    @Override
    public void windowOpened(WindowEvent e) {
        long waifuUptime = waifuInterface.getUptime();
        long waifuStartTimeSeconds = (waifuUptime / 1000000);
        long waifuStartTimeMillis = waifuUptime - waifuStartTimeSeconds;
        System.out.println("Secretary up and running in " + waifuStartTimeSeconds + "." + waifuStartTimeMillis + " seconds");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        close();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // No action
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // No action
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // No action
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (Util.isWindows()) {
            setOpacity(Settings.getWaifuActiveOpacity() / 100.0f);
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (Util.isWindows()) {
            setOpacity(Settings.getWaifuInactiveOpacity() / 100.0f);
        }
    }

    // Endregion

    public boolean isFloatingToggle() {
        return floatingToggle;
    }
}