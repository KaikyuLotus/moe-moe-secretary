package core.entities;

import core.utils.Util;
import core.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecretaryLabel extends JLabel {

    private static final String HIGH_QUALITY         = "waifu.highQuality";
    private static final String FLOAT_ENABLED        = "floating.enabled";
    private static final String FLOAT_PIXEL_PER_STEP = "floating.pixelPerStep";
    private static final String FLOAT_PIXEl_RANGE    = "floating.pixelRange";
    private static final String FLOAT_STEP_SLEEP     = "floating.stepSleep";
    private static final String FLOAT_SWAP_SLEEP     = "floating.swapSleep";
    private static final String IDLE_DIALOG_WAIT     = "dialogs.idle.frequency";
    private static final String JUMP_ON_CLICK        = "jump.onClick";
    private static final String JUMPS_COUNT          = "jump.count";
    private static final String JUMP_PIXEL_PER_STEP  = "jump.pixelPerStep";
    private static final String JUMP_STEP_SLEEP      = "jump.stepSleep";
    private static final String JUMP_PIXEL_RANGE     = "jump.pixelRange";


    private boolean isSpeaking = false;
    private boolean isJumping  = false;

    public SecretaryLabel(ImageIcon icn) {
        super(icn);
    }

    public Rectangle getDesiredBounds(int parentWidth, int parentHeight) {
        return new Rectangle(
                0,
                getStartY(parentWidth, parentHeight),
                this.getIcon().getIconWidth(),
                this.getIcon().getIconHeight());
    }

    public void speak(boolean s) {
        isSpeaking = s;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public int getStartY(int parentWidth, int parentHeight) {
        int y = 0;
        String settingPos = Settings.get("waifu.startY", "auto").toLowerCase();
        if (settingPos.equals("auto")) {
            y = parentHeight - this.getIcon().getIconHeight();
        } else if (settingPos.matches("-?\\d+")) {
            y = Integer.parseInt(settingPos);
        }
        return y;
    }

    public void startFloating() {

        if (!Settings.get(FLOAT_ENABLED, true)) {
            System.out.println("Floating is disabled");
            return;
        }

        AtomicBoolean raise = new AtomicBoolean(true);

        int stepSleep = Settings.get(FLOAT_STEP_SLEEP, 16);
        int max = Settings.get(FLOAT_PIXEl_RANGE, 30);
        int increment = Settings.get(FLOAT_PIXEL_PER_STEP, 1);
        int swapSleep = Settings.get(FLOAT_SWAP_SLEEP, 100);
        int defaultY = getY();

        // 60 fps{
        new Timer(stepSleep, (event) -> {
            if (!isJumping) {
                if (getY() >= defaultY + max || getY() <= defaultY - max) {
                    raise.set(!raise.get());
                }
                setLocation(getX(), getY() + (raise.get() ? increment : -increment));
            }
        }).start();
    }

    public void jumpAnimation() {
        int jumps = Settings.get(JUMPS_COUNT, 2);
        int stepSleep = Settings.get(JUMP_STEP_SLEEP, 15);
        int step = Settings.get(JUMP_PIXEL_PER_STEP, 5);
        int max = Settings.get(JUMP_PIXEL_RANGE, 40);

        for (int j = 0; j < jumps; j++) {
            for (int i = 0; i < max / step; i++) {
                setLocation(getX(), getY() - step);
                Util.sleep(stepSleep);
            }

            for (int i = 0; i < max / step; i++) {
                setLocation(getX(), getY() + step);
                Util.sleep(stepSleep);
            }
        }


    }

    public void speakJump() {
        if (!isJumping && Settings.get(JUMP_ON_CLICK, true)) {
            new Thread(() -> {
                isJumping = true;
                jumpAnimation();
                // Util.sleep((long) Settings.get(JUMP_DURATION, 1500));
                isJumping = false;
            }).start();
        }
    }

    public void waitIdle() {
        int times = 0;
        int dialWait = Settings.get(IDLE_DIALOG_WAIT, 120000) / 10;
        while (times < dialWait) {
            for (int i = 0; i < dialWait; i++) {
                Util.sleep(10);
                if (isJumping || isSpeaking) {
                    times = 0;
                } else {
                    times++;
                }
            }

        }
    }

    public void waitSpeak() {
        while (isSpeaking) {
            Util.sleep(10);
        }
    }

    // May be usefull
    public void onVisible() {

    }

    @Override
    public void paint(Graphics g) {
        super.paint(Settings.get(HIGH_QUALITY, true) ? Util.setHighQuality((Graphics2D) g) : g);
    }
}
