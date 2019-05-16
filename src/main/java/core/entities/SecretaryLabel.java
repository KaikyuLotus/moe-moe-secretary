package core.entities;

import azurlane.utils.Util;
import core.settings.Settings;

import javax.swing.*;
import java.awt.*;

public class SecretaryLabel extends JLabel {

    // Talk
    private static final int   talkJumpsCount     = Settings.get("talk.jump.count", 2);
    private static final int   talkJumpStepsCount = Settings.get("talk.jump.stepsCount", 30);
    private static final int   talkJumpStep       = Settings.get("talk.jump.step", 1);
    private static final float talkJumpStepSleep  = Settings.get("talk.jump.stepSleep", 5.0f);
    private static final float talkJumpSwapSleep  = Settings.get("talk.jump.swapSleep", 80.0f);

    private static final boolean highQuality = Settings.get("ship.highQuality", true);

    private static final int   floatingAmplitude = Settings.get("floating.amplitude", 20);
    private static final int   floatingStep      = Settings.get("floating.step", 1);
    private static final float floatingStepSleep = Settings.get("floating.stepSleep", 40.0f);
    private static final float floatingSwapSleep = Settings.get("floating.swapSleep", 700.0f);

    private static final int idleDialogWait = Settings.get("dialogs.idle.frequency", 120000);

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
            long times     = 0;
            int  increment = floatingStep;

            while (running) {

                waitJump();

                if (times > floatingAmplitude) {
                    if (increment < 0) {

                        setLocation(0,0);
                    }
                    times = 0;
                    increment *= -1;
                    Util.sleep(floatingSwapSleep);

                }

                // yPosition += increment;
                setLocation(getX(), getY() + increment);
                Util.sleep(floatingStepSleep);
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

            for (int i = 0; i < talkJumpsCount * 2; i++) {
                for (int i2 = 0; i2 < talkJumpStepsCount; i2++) {
                    setLocation(getX(), getY() + (talkJumpStep * order));
                    Util.sleep(talkJumpStepSleep);
                }
                order *= -1;
                Util.sleep(talkJumpSwapSleep);
            }

            isJumping = false;
        }).start();
    }

    public void waitIdle() {
        int times    = 0;
        int dialWait = idleDialogWait / 10;
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
        super.paint(highQuality ? Util.setHighQuality((Graphics2D) g) : g);
    }
}
