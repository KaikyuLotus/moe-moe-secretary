package com.kitsunecode.mms.core.entities.audio;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.File;


public class AudioPlayer {

    public void play(Audio audio) {
        new Thread(() -> playThread(audio)).start();
    }

    private void playThread(Audio audio) {
        File audioFile = audio.getFile();

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
            try (AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in)) {

                Info info = new Info(SourceDataLine.class, decodedFormat);

                try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                    // Start
                    line.open(decodedFormat);
                    line.start();

                    FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);

                    float volume = (gainControl.getMaximum() - -30.0f) / 100.0f * audio.getVolume() + -30.0f;
                    gainControl.setValue(volume);

                    audio.start();

                    byte[] data = new byte[128];
                    int nBytesRead = 0;
                    while (nBytesRead != -1 && audio.isPlaying()) {
                        nBytesRead = din.read(data, 0, data.length);
                        if (nBytesRead != -1) line.write(data, 0, nBytesRead);
                    }

                    // Stop
                    line.drain();
                    line.stop();
                    audio.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            audio.stop();
        }
    }

}
