package core.audio;

import core.adapters.IWaifuAdapter;
import core.utils.Util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;


public class AudioManager {

	private Float volume = null;

	public void play(IWaifuAdapter adapter, String url, int volValue) {

		if (url != null && !url.equals("")) {

			String fileName = Util.fileFromUrl(url);

			try {
				byte[] audio = IWaifuAdapter.downloadFile(adapter, url, fileName);
				// Get AudioInputStream from given bytes!
				try (AudioInputStream in = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audio))) {
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
					rawplay(decodedFormat, din, volValue);
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

	private void rawplay(AudioFormat targetFormat,
						 AudioInputStream din, int volValue) throws IOException, LineUnavailableException {
		byte[]         data = new byte[4096];
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
		DataLine.Info  info = new DataLine.Info(SourceDataLine.class, audioFormat);
		SourceDataLine res  = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}
}
