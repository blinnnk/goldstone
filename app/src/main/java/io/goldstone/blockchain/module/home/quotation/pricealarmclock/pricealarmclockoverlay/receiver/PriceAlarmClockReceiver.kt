package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Vibrator
import com.blinnnk.extension.isNull
import java.io.IOException

@Suppress("DEPRECATION")
/**
 * @date 16/08/2018 5:02 PM
 * @author wcx
 */
class PriceAlarmClockReceiver : BroadcastReceiver() {
  object PriceAlarmClockProperty {
    var mediaPlayer: MediaPlayer? = null
    var vibrator: Vibrator? = null
  }

  /**
   * @data 07/24/2018 10/05
   * @author wcx
   * @description 立即停止闹钟和震动
   */
  companion object {
    fun stopAlarmClock() {
      PriceAlarmClockProperty.mediaPlayer?.let {
        if (it.isPlaying) {
          it.stop()
          it.release()
          PriceAlarmClockProperty.mediaPlayer = null
        }
      }
      PriceAlarmClockProperty.vibrator?.cancel()
    }
  }

  override fun onReceive(
    context: Context?,
    intent: Intent?
  ) {
    intent?.let {
      val bundle = it.extras
      val flag = bundle.getBoolean("msg")
      if (flag) {
        context?.let {
          startMediaPlayer(it)
          startVibrator(it)
        }
      } else {
        stopMediaPlayer()
        stopVibrator()
        resumeMediaPlayer()
      }
    }
  }

  /**
   * @data 07/24/2018 10/05
   * @author wcx
   * @description 初始化和启动MediaPlay
   */
  private fun startMediaPlayer(context: Context) {
    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    if (PriceAlarmClockProperty.mediaPlayer.isNull()) {
      PriceAlarmClockProperty.mediaPlayer = MediaPlayer()
      PriceAlarmClockProperty.mediaPlayer!!.setOnErrorListener(object : MediaPlayer.OnErrorListener {
        override fun onError(
          p0: MediaPlayer?,
          p1: Int,
          p2: Int
        ): Boolean {
          p0?.release()
          return true
        }
      })
    }
    try {
      PriceAlarmClockProperty.mediaPlayer?.let {
        if (it.isPlaying) {
          it.stop()
          it.release()
          PriceAlarmClockProperty.mediaPlayer = MediaPlayer()
          PriceAlarmClockProperty.mediaPlayer!!.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(
              p0: MediaPlayer?,
              p1: Int,
              p2: Int
            ): Boolean {
              p0?.release()
              return true
            }
          })
        }
        it.reset();
        it.setDataSource(
          context,
          uri
        )
        it.setAudioStreamType(AudioManager.STREAM_RING)
        it.isLooping = true
        it.prepare()
        it.start()
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }

  }

  /**
   * 停止MediaPlay
   */
  private fun stopMediaPlayer() {
    PriceAlarmClockProperty.mediaPlayer?.let {
      if (it.isPlaying) {
        it.stop()
      }
    }
  }

  /**
   * @data 07/24/2018 10/05
   * @author wcx
   * @description 释放mediaPlayer
   */
  private fun resumeMediaPlayer() {
    PriceAlarmClockProperty.mediaPlayer?.release()
    PriceAlarmClockProperty.mediaPlayer = null
  }

  /**
   * @data 07/24/2018 10/05
   * @author wcx
   * @description 开始震动
   */
  @SuppressLint("MissingPermission")
  private fun startVibrator(context: Context) {
    if (PriceAlarmClockProperty.vibrator.isNull()) {
      PriceAlarmClockProperty.vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    val pattern = longArrayOf(
      800,
      1500,
      500,
      1300)

    PriceAlarmClockProperty.vibrator?.vibrate(
      pattern,
      2
    )
  }

  /**
   * @data 07/24/2018 10/05
   * @author wcx
   * @description 停止震动
   */
  @SuppressLint("MissingPermission")
  private fun stopVibrator() {
    PriceAlarmClockProperty.vibrator?.cancel()
  }
}