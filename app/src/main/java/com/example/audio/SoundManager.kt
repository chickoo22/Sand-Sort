package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class SoundManager(private val context: Context) {

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    fun playBottleTap() {
        if (isVibrationEnabled) {
            vibrate(15)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            generateTone(frequency = 780.0, durationMs = 45, volume = 0.35f, decay = true)
        }
    }

    fun playPour() {
        if (isVibrationEnabled) {
            vibrate(25)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Gentle sand stream sound: modulated noise + warm resonance
            generateSandPourSound(durationMs = 280, volume = 0.28f)
        }
    }

    fun playCorkPop() {
        if (isVibrationEnabled) {
            vibrate(50)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Cork pop: pitch sweeps quickly upwards with percussive envelope
            generatePitchSweep(startFreq = 220.0, endFreq = 900.0, durationMs = 80, volume = 0.55f)
        }
    }

    fun playLevelWin() {
        if (isVibrationEnabled) {
            vibratePattern(longArrayOf(0, 40, 80, 60, 80, 100))
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Bright arpeggio chime: C5, E5, G5, C6
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (note in notes) {
                generateTone(frequency = note, durationMs = 120, volume = 0.4f, decay = true)
                kotlinx.coroutines.delay(65)
            }
        }
    }

    fun playButtonTap() {
        if (isVibrationEnabled) {
            vibrate(10)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            generateTone(frequency = 540.0, durationMs = 30, volume = 0.25f, decay = true)
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern(timings: LongArray) {
        try {
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createWaveform(timings, -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(timings, -1)
                }
            }
        } catch (_: Exception) {}
    }

    private fun generateTone(frequency: Double, durationMs: Int, volume: Float, decay: Boolean) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val rawSine = sin(2.0 * Math.PI * frequency * time)
                val envelope = if (decay) {
                    (1.0 - (i.toDouble() / numSamples)).coerceIn(0.0, 1.0)
                } else {
                    1.0
                }
                buffer[i] = (rawSine * envelope * Short.MAX_VALUE * volume).toInt().toShort()
            }

            playBuffer(buffer, sampleRate)
        } catch (_: Exception) {}
    }

    private fun generatePitchSweep(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                val time = i.toDouble() / sampleRate
                val rawSine = sin(2.0 * Math.PI * currentFreq * time)
                val envelope = sin(Math.PI * t) // smooth attack and decay
                buffer[i] = (rawSine * envelope * Short.MAX_VALUE * volume).toInt().toShort()
            }

            playBuffer(buffer, sampleRate)
        } catch (_: Exception) {}
    }

    private fun generateSandPourSound(durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)
            var lastNoise = 0.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                // Soft pink/brown filtered noise + slight grain resonance
                val white = (Random.nextDouble() * 2.0 - 1.0)
                val filtered = (lastNoise * 0.7) + (white * 0.3)
                lastNoise = filtered
                val tone = sin(2.0 * Math.PI * 440.0 * (i.toDouble() / sampleRate)) * 0.15
                val envelope = sin(Math.PI * t)
                val sample = (filtered * 0.85 + tone) * envelope * Short.MAX_VALUE * volume
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            playBuffer(buffer, sampleRate)
        } catch (_: Exception) {}
    }

    private fun playBuffer(buffer: ShortArray, sampleRate: Int) {
        var track: AudioTrack? = null
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = buffer.size.coerceAtLeast(minBufferSize)

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            // Let it play out asynchronously
            Thread.sleep((buffer.size * 1000L) / sampleRate + 20)
        } catch (_: Exception) {
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }
}
