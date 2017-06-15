package fast3d.util;


import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * a very simple wrapper around the core-audio-functionalities of java.sampled
 * 
 * @author Tim Trense
 *
 */
public class SimpleAudio {

	/**
	 * 
	 * @return information about all available mixers on the system
	 */
	public static Mixer.Info[] getMixers() {
		return AudioSystem.getMixerInfo();
	}

	/**
	 * requests the mixer from the system
	 * 
	 * @param info
	 *            information what a mixer has to be requested
	 * @return the mixer that is requested or null if it is not available
	 */
	public static Mixer getMixer(final Mixer.Info info) {
		try {
			return AudioSystem.getMixer(info);
		} catch (final Throwable t) {
			return null;
		}
	}

	/**
	 * requests the first mixer available in the system
	 * 
	 * @return the first mixer that the system provides information for or null
	 *         if something failed
	 */
	public static Mixer getPrimaryMixer() {
		try {
			return getMixer(getMixers()[0]);
		} catch (final Throwable t) {
			return null;
		}
	}

	/**
	 * @return the audio-out-line for the primary mixer
	 */
	public static SourceDataLine playAudio() {
		return playAudio(getPrimaryMixer());
	}

	/**
	 * requests a line to play raw audio data on from the mixer
	 * 
	 * @param mixer
	 *            the line-combining audio device
	 * @return a line to play audio on or null if that is not possible with the
	 *         given mixer
	 */
	public static SourceDataLine playAudio(final Mixer mixer) {
		try {
			return (SourceDataLine) mixer.getLine(new Line.Info(SourceDataLine.class));
		} catch (final IllegalArgumentException iae) {
			return null;
		} catch (final NullPointerException npe) {
			return null;
		} catch (LineUnavailableException e) {
			return null;
		}
	}

	/**
	 * requests a line, to record raw audio data from, from the mixer
	 * 
	 * @param mixer
	 *            the line-combining audio device
	 * @return a raw-audio-line or null if that is not possible with the given
	 *         mixer
	 */
	public static TargetDataLine recordAudio(final Mixer mixer) {
		try {
			return (TargetDataLine) mixer.getLine(new Line.Info(TargetDataLine.class));
		} catch (final IllegalArgumentException iae) {
			return null;
		} catch (final NullPointerException npe) {
			return null;
		} catch (LineUnavailableException e) {
			return null;
		}
	}

	/**
	 * opens the file and reads the header
	 * 
	 * @param f
	 *            the file to get audio data from
	 * @return a stream of raw audio data encoded like described in the files
	 *         header or null if anything failed
	 */
	public static AudioInputStream openFileToPlay(final File f) {
		try {
			return AudioSystem.getAudioInputStream(f);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * a wrapper around openFileToPlay(File)
	 * 
	 * @see #openFileToPlay(File)
	 * @param filename
	 *            the name of the file to get audio data from
	 * @return a stream of raw audio data encoded like described in the files
	 *         header or null if anything failed
	 */
	public static AudioInputStream openFileToPlay(final String filename) {
		return openFileToPlay(new File(filename));
	}

	/**
	 * a wrapper around audioPlayer(String)<br>
	 * because the result-thread will do various I/O interrupt() will not work
	 * 
	 * @see #audioPlayer(String)
	 * @param filename
	 *            the name of the file to play
	 * @return a already started thread playing the file
	 */
	public static Thread playAudio(final String filename) {
		final Thread t = audioPlayer(filename);
		t.start();
		return t;
	}

	/**
	 * a wrapper around audioPlayer(File,double,double)<br>
	 * skip at begin and end will be 0.0 seconds<br>
	 * because the result-thread will do various I/O interrupt() will not work
	 * 
	 * @see #audioPlayer(File,double,double)
	 * @param filename
	 *            the name of the file to play
	 * @return a not started thread playing the file
	 */
	public static Thread audioPlayer(final String filename) {
		return audioPlayer(new File(filename), 0, 0);
	}

	/**
	 * returns a thread doing the I/O for passing the data from a
	 * AudioInputStream upon the given file to a sourceDataLine for playback on the primary mixer<br>
	 * because the result-thread will do various I/O interrupt() will not work
	 * 
	 * @param f the file to play
	 * @param secondsSkipStart how many seconds (not milliseconds) to skip at the begin of the audio-data
	 * @param secondsSkipEnd how many seconds (not milliseconds) to skip at the end of the audio-data
	 * @return a not started thread playing the file
	 */
	public static Thread audioPlayer(final File f, final double secondsSkipStart,
			final double secondsSkipEnd) {
		final AudioInputStream ais = openFileToPlay(f);
		final SourceDataLine sdl = playAudio();

		final int bufferSize = (int) (ais.getFormat().getSampleRate()
				* ais.getFormat().getSampleSizeInBits() / 8)
				* ais.getFormat().getChannels();
		// buffer contains data for 1.0 second

		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					sdl.open(ais.getFormat());
					sdl.start();
					final byte[] buffer = new byte[bufferSize];

					ais.skip((long) (bufferSize * secondsSkipStart));

					ais.read(buffer, 0, buffer.length);

					do {
						sdl.write(buffer, 0, buffer.length);
						ais.read(buffer, 0, buffer.length);
					} while (!isInterrupted()
							&& ais.available() > buffer.length * secondsSkipEnd);
					if (!isInterrupted())
						sdl.drain();
					sdl.stop();
					sdl.close();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		};
		return t;
	}
}