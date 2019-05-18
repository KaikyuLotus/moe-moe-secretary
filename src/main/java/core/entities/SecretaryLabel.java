package core.entities;

import azurlane.utils.Util;
import core.settings.Settings;

import javax.swing.*;
import java.awt.*;

public class SecretaryLabel extends JLabel {

    private static final String TALK_JUMP_COUNT       = "talk.jump.count";
    private static final String TALK_JUMP_STEPS_COUNT = "talk.jump.stepsCount";
    private static final String TALK_JUMP_STEP        = "talk.jump.step";
    private static final String TALK_JUMP_STEP_SLEEP  = "talk.jump.stepSleep";
    private static final String TALK_JUMP_SWAP_SLEEP  = "talk.jump.swapSleep";
    private static final String HIGH_QUALITY          = "ship.highQuality";
    private static final String FLOATING_AMPLITUDE    = "floating.amplitude";
    private static final String FLOATING_STEP         = "floating.step";
    private static final String FLOATING_STEP_SLEEP   = "floating.stepSleep";
    private static final String FLOATING_SWAP_SLEEP   = "floating.swapSleep";
    private static final String IDLE_DIALOG_WAIT      = "dialogs.idle.frequency";

    private boolean isSpeaking = false;
    private boolean isJumping  = false;
    private boolean running    = true;

    public Point getPosition() {
        return getLocation();
    }

    public SecretaryLabel(ImageIcon icn) {
        super(icn);
    }

    public void speak(boolean s) {
        isSpeaking = s;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void startFloating() {
        new Thread(() -> {
            long times = 0;
            int increment = Settings.get(FLOATING_STEP, 1);

            while (running) {

                waitJump();

                if (times > Settings.get(FLOATING_AMPLITUDE, 20)) {
                    if (increment < 0) {

                        setLocation(0, 0);
                    }
                    times = 0;
                    increment *= -1;
                    Util.sleep(Settings.get(FLOATING_SWAP_SLEEP, 700.0f));

                }

                // yPosition += increment;
                setLocation(getX(), getY() + increment);
                Util.sleep(Settings.get(FLOATING_STEP_SLEEP, 40.0f));
                times += 1;
            }
        }).start();
    }

    public void speakJump() {

        if (isJumping) {
            return;
        }

        isJumping = true;
        new Thread(() -> {
            int order = -1;

            for (int i = 0; i < Settings.get(TALK_JUMP_COUNT, 2) * 2; i++) {
                for (int i2 = 0; i2 < Settings.get(TALK_JUMP_STEPS_COUNT, 30); i2++) {
                    setLocation(getX(), getY() + (Settings.get(TALK_JUMP_STEP, 1) * order));
                    Util.sleep(Settings.get(TALK_JUMP_STEP_SLEEP, 5.0f));
                }
                order *= -1;
                Util.sleep(Settings.get(TALK_JUMP_SWAP_SLEEP, 80.0f));
            }

            isJumping = false;
        }).start();
    }

    public void waitIdle() {
        int times = 0;
        int dialWait = Settings.get(IDLE_DIALOG_WAIT, 120000) / 10;
        while (running && times < dialWait) {
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

    public void waitJump() {
        while (isJumping) {
            Util.sleep(10);
        }
    }


    @Override
    public void paint(Graphics g) {
        super.paint(Settings.get(HIGH_QUALITY, true) ? Util.setHighQuality((Graphics2D) g) : g);
    }
}
