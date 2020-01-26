package com.kitsunecode.mms.core.entities.swing;

import com.kitsunecode.mms.core.utils.Settings;
import com.kitsunecode.mms.core.utils.Util;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecretaryLabel extends JLabel {

    private boolean isSpeaking = false;
    private boolean isJumping = false;

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

        AtomicBoolean raise = new AtomicBoolean(true);

        int stepSleep = Settings.getFloatingStepSleep();
        int max = Settings.getFloatingPixelRange();
        int increment = Settings.getFloatingPixelPerStep();
        // int swapSleep = Settings.getFloatingSwapSleep();
        int startY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - parentFrame.getY();
        int minY = startY - max;
        int maxY = startY + max * 2;
        int switchSleep = Settings.getFloatingSwitchSleep();
        System.out.println(String.format("Min %s Max %s Current %s", minY, maxY, startY));

        final int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        // 60 fps{
        new Thread(() -> {
            while (true) {
                Util.sleep(stepSleep);

                if (isJumping || parentFrame.isDragging() || !parentFrame.isFloatingToggle()) {
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
        int jumps = Settings.getJumpCount();
        int stepSleep = Settings.getJumpSleep();
        int step = Settings.getJumpPixelPerStep();
        int max = Settings.getJumpPixelRange();

        // int position = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - parentFrame.getY();

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
        if (!isJumping && Settings.isJumpOnClick()) {
            new Thread(() -> {
                isJumping = true;
                jumpAnimation();
                isJumping = false;
            }).start();
        }
    }

    public void waitIdle() {
        int times = 0;
        int dialWait = Settings.getDialogsIdleFrequency() / 10;
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
