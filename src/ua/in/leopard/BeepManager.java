package ua.in.leopard;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Manages beeps and vibrations for {@link CaptureActivity}.
 */
public final class BeepManager {

  private static final String TAG = BeepManager.class.getSimpleName();

  private static final float BEEP_VOLUME = 0.10f;

  private final Context myContext;
  private MediaPlayer mediaPlayer;
  private boolean playBeep;

  BeepManager(Context myContext) {
    this.myContext = myContext;
    this.mediaPlayer = null;
    updatePrefs();
  }

  void updatePrefs() {
    playBeep = shouldBeep(myContext);
    if (playBeep && mediaPlayer == null) {
      // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
      // so we now play on the music stream.
      mediaPlayer = buildMediaPlayer(myContext);
    }
  }

  void playBeep() {
    if (playBeep && mediaPlayer != null) {
      mediaPlayer.start();
    }
  }

  private static boolean shouldBeep(Context activity) {
	  boolean shouldPlayBeep = true;
      AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
      if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
        shouldPlayBeep = false;
      }
      return shouldPlayBeep;
  }

  private static MediaPlayer buildMediaPlayer(Context activity) {
    MediaPlayer mediaPlayer = new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    // When the beep has finished playing, rewind to queue up another one.
    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      public void onCompletion(MediaPlayer player) {
        player.seekTo(0);
      }
    });

    AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep);
    try {
      mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
      file.close();
      mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
      mediaPlayer.prepare();
    } catch (IOException ioe) {
      Log.w(TAG, ioe);
      mediaPlayer = null;
    }
    return mediaPlayer;
  }

}

