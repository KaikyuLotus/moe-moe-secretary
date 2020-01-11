package com.kitsunecode.mms.core.entities;

import com.kitsunecode.mms.core.utils.Util;
import com.kitsunecode.mms.core.settings.Settings;

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
    private static final String FLOAT_SWITCH_SLEEP   = "floating.switchSleep";


    private boolean isSpeaking = false;
    private boolean isJumping  = false;

    private final Secretary parentFrame;

    public SecretaryLabel(ImageIcon icn, Secretary parentFrame) {
        super(icn);
        this.parentFrame = parentFrame;
    }

    public Rectangle getDesiredBounds(int parentWidth, int parentHeight) {
        return new Rectangle(
                0,
                parentHeight - this.getIcon().getIconHeight(),
                this.getIcon().getIconWidth(),
                this.getIcon().getIconHeight());
    }

    public void speak(boolean s) {
        isSpeaking = s;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void startFloating() {

        if (!Settings.get(FLOAT_ENABLED, true)) {
            System.out.println("Floating is disabled");
            return;
        }

        AtomicBoolean raise = new AtomicBoolean(true);

        int stepSleep = Settings.get(FLOAT_STEP_SLEEP, 16);
        int max = Settings.get(FLOAT_PIXEl_RANGE, 300);
        int increment = Settings.get(FLOAT_PIXEL_PER_STEP, 1);
        int swapSleep = Settings.get(FLOAT_SWAP_SLEEP, 100);
        int startY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - parentFrame.getY();
        int minY = startY - max;
        int maxY = startY + max * 2;
        int switchSleep = Settings.get(FLOAT_SWITCH_SLEEP, 10);
        System.out.println(String.format("Min %s Max %s Current %s", minY, maxY, startY));

        final int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        // 60 fps{
        new Thread(() -> {
            while (true) {
                Util.sleep(stepSleep);

                if (isJumping || parentFrame.isDragging()) {
                    continue;
                }
                int position = screenHeight - parentFrame.getY();
                // System.out.println(String.format("%s : %s : %s", startY, position, maxY));

                if (raise.get()) {
                    // Se sta salendo controlla se ha raggiunto il massimo
                    if (position >= maxY) {
                        // Ha raggiunto il massimo quindi inizia a scendere
                        raise.set(false);
                    }
                } else {
                    if (position <= startY) {
                        raise.set(true);
                        Util.sleep(switchSleep);
                    }
                }

                parentFrame.setLocation(parentFrame.getX(), parentFrame.getY() + (raise.get() ? -increment : increment));
            }

        }).start();
    }

    public void jumpAnimation() {
        int jumps = Settings.get(JUMPS_COUNT, 2);
        int stepSleep = Settings.get(JUMP_STEP_SLEEP, 15);
        int step = Settings.get(JUMP_PIXEL_PER_STEP, 5);
        int max = Settings.get(JUMP_PIXEL_RANGE, 40);

        int position = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - parentFrame.getY();

        for (int j = 0; j < jumps; j++) {
            for (int i = 0; i < max / step; i++) {
                parentFrame.setLocation(parentFrame.getX(), parentFrame.getY() - step);
                Util.sleep(stepSleep);
            }

            for (int i = 0; i < max / step; i++) {
                parentFrame.setLocation(parentFrame.getX(), parentFrame.getY() + step);
                Util.sleep(stepSleep);
            }
        }


    }

    public void speakJump() {
        if (!isJumping && Settings.get(JUMP_ON_CLICK, true)) {
            new Thread(() -> {
                isJumping = true;
                jumpAnimation();
                isJumping = false;
            }).start();
        }
    }

    public void waitIdle() {
        int times = 0;
        int dialWait = Settings.get(IDLE_DIALOG_WAIT, 60) * 1000;
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

    // May be useful
    public void onVisible() {

    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        Toolkit.getDefaultToolkit().sync();
        super.paint(g);
    }

    // g.clearRect(0, 0, this.getWidth(), this.getHeight());
    // Toolkit.getDefaultToolkit().sync();
}
