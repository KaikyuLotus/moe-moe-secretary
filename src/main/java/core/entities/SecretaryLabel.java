package core.entities;

import core.utils.Util;
import core.settings.Settings;

import javax.swing.*;
import java.awt.*;

public class SecretaryLabel extends JLabel {

	private static final String HIGH_QUALITY         = "waifu.highQuality";
	private static final String FLOATING_AMPLITUDE   = "floating.amplitude";
	private static final String IDLE_DIALOG_WAIT     = "dialogs.idle.frequency";
	private static final String JUMP_DURATION        = "talk.jump.duration";
	private static final String SPEED_FACTOR         = "floating.speed";
	private static final String JUMP_TIME_MULTIPLIER = "floating.jumpSpeedMultiplier";

	private boolean isSpeaking = false;
	private boolean isJumping  = false;

	private long lastTime        = System.currentTimeMillis();
	private long currentFakeTime = 0;
	private int  startY          = 0;

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
		int    y          = 0;
		String settingPos = Settings.get("waifu.startY", "auto").toLowerCase();
		if (settingPos.equals("auto")) {
			y = parentHeight - this.getIcon().getIconHeight();
		} else if (settingPos.matches("-?\\d+")) {
			y = Integer.parseInt(settingPos);
		}
		startY = y;
		return y;
	}

	public void startFloating() {
		float  floatingAplitude   = Settings.get(FLOATING_AMPLITUDE, 30.0f);
		double jumpTimeMultiplier = Settings.get(JUMP_TIME_MULTIPLIER, 5.0);
		double speedFactor        = Settings.get(SPEED_FACTOR, 15) / 10000.0;

		// 60 fps{
		new Timer(16, (event) -> {
			currentFakeTime += (System.currentTimeMillis() - lastTime) * (isJumping ? jumpTimeMultiplier : 1.0);
			lastTime = System.currentTimeMillis();
			setLocation(getX(), (int) (startY + ((Util.sin(currentFakeTime, speedFactor) * floatingAplitude))));
		}).start();
	}

	public void speakJump() {
		if (!isJumping) {
			new Thread(() -> {
				isJumping = true;
				Util.sleep(Settings.get(JUMP_DURATION, 1.5f));
				isJumping = false;
			}).start();
		}
	}

	public void waitIdle() {
		int times    = 0;
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
