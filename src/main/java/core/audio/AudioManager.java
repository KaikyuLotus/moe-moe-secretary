package core.audio;


import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;


public class AudioManager {

    private Float volume = null;

    public void play(String url, int volValue) {
        try {

            if (url == null || url.equals("")) {
                return;
            }

            // Get AudioInputStream from given file.
            AudioInputStream in = AudioSystem.getAudioInputStream(new URL(url).openStream());
            AudioInputStream din;

            if (in != null) {
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
                din = AudioSystem.getAudioInputStream(decodedFormat, in);
                // Play now !
                rawplay(decodedFormat, din, volValue);
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        volume = null;
    }

    private void setVolume(SourceDataLine line, int value) {

        FloatControl gainControl =
                (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);

        // 0 : -80 = 100 : 6

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

    private void rawplay(AudioFormat targetFormat,
                         AudioInputStream din, int volValue) throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.start();

            setVolume(line, volValue);

            int nBytesRead = 0;
            while (nBytesRead != -1) {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1) line.write(data, 0, nBytesRead);
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }
}
