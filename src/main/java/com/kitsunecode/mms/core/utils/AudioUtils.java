package com.kitsunecode.mms.core.utils;

import com.kitsunecode.mms.core.entities.swing.Secretary;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.DataLine.Info;

import java.io.File;
import java.io.IOException;


public class AudioUtils {

    private Float volume = null;

    public void play(Secretary secretary, String url, int volValue) {

        if (url != null && !"".equals(url)) {

            String fileName = Util.fileFromUrl(url);
            try {
                File audioFile = secretary.getWaifuInterface().downloadFile(url, fileName);
                // Get AudioInputStream from given bytes!
                try (AudioInputStream in = AudioSystem.getAudioInputStream(audioFile)) {
                    AudioFormat baseFormat = in.getFormat();
                    AudioFormat decodedFormat = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            baseFormat.getSampleRate(),
                            16,
                            baseFormat.getChannels(),
                            baseFormat.getChannels() * 2,
                            baseFormat.getSampleRate(),
                            false);
                    // Get AudioInputStream that will be decoded by underlying VorbisSPI
                    AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
                    System.out.println("Playing audio...");
                    rawplay(secretary, decodedFormat, din, volValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        volume = null;
    }

    private void setVolume(SourceDataLine line, int value) {

        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);

        if (volume == null) {
            if (value > 100 || value < 0) {
                System.out.println("Invalid volume value, using 30% as default");
                value = 30;
            }
            // Range / 100 * % + minimum
            volume = (gainControl.getMaximum() - -30.0f) / 100.0f * value + -30.0f;
        }

        gainControl.setValue(volume);
    }

    private void rawplay(Secretary secretary, AudioFormat targetFormat,
                         AudioInputStream din, int volValue) throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];

        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.open(targetFormat);
            line.start();

            setVolume(line, volValue);

            int nBytesRead = 0;
            while (nBytesRead != -1 && secretary.isRunning()) {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1) line.write(data, 0, nBytesRead);

            }

            new Thread(() -> {
                while (line.isOpen()) {
                    if (!secretary.isRunning()) {
                        line.stop();
                        line.close();
                    }
                    Util.sleep(10);
                }
            }).start();

            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        Info info = new Info(SourceDataLine.class, audioFormat);
        return (SourceDataLine) AudioSystem.getLine(info);
    }
}
