package com.dongnao.livedemo.core;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.dongnao.livedemo.filter.softaudiofilter.BaseSoftAudioFilter;
import com.dongnao.livedemo.model.RESAudioBuff;
import com.dongnao.livedemo.model.RTMPConfig;
import com.dongnao.livedemo.model.RTMPCoreParameters;
import com.dongnao.livedemo.rtmp.RESFlvDataCollecter;
import com.dongnao.livedemo.tools.LogTools;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class RESSoftAudioCore {
    RTMPCoreParameters resCoreParameters;
    private final Object syncOp = new Object();
    private MediaCodec dstAudioEncoder;
    private MediaFormat dstAudioFormat;
    //filter
    private Lock lockAudioFilter = null;
    private BaseSoftAudioFilter audioFilter;
    //AudioBuffs
    //buffers to handle buff from queueAudio
    private RESAudioBuff[] orignAudioBuffs;
    private int lastAudioQueueBuffIndex;
    //buffer to handle buff from orignAudioBuffs
    private RESAudioBuff orignAudioBuff;
    private RESAudioBuff filteredAudioBuff;
    private AudioFilterHandler audioFilterHandler;
    private HandlerThread audioFilterHandlerThread;
    private AudioSenderThread audioSenderThread;

    public RESSoftAudioCore(RTMPCoreParameters parameters) {
        resCoreParameters = parameters;
        lockAudioFilter = new ReentrantLock(false);
    }

    public void queueAudio(byte[] rawAudioFrame) {
        int targetIndex = (lastAudioQueueBuffIndex + 1) % orignAudioBuffs.length;
        if (orignAudioBuffs[targetIndex].isReadyToFill) {
            LogTools.d("queueAudio,accept ,targetIndex" + targetIndex);
            System.arraycopy(rawAudioFrame, 0, orignAudioBuffs[targetIndex].buff, 0, resCoreParameters.audioRecoderBufferSize);
            orignAudioBuffs[targetIndex].isReadyToFill = false;
            lastAudioQueueBuffIndex = targetIndex;
            audioFilterHandler.sendMessage(audioFilterHandler.obtainMessage(AudioFilterHandler.WHAT_INCOMING_BUFF, targetIndex, 0));
        } else {
            LogTools.d("queueAudio,abandon,targetIndex" + targetIndex);
        }
    }

    public boolean prepare(RTMPConfig resConfig) {
        synchronized (syncOp) {
            resCoreParameters.mediacodecAACProfile = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
            resCoreParameters.mediacodecAACSampleRate = 44100;
            resCoreParameters.mediacodecAACChannelCount = 1;
            resCoreParameters.mediacodecAACBitRate = 32 * 1024;
            resCoreParameters.mediacodecAACMaxInputSize = 8820;

            dstAudioFormat = new MediaFormat();
            dstAudioEncoder = MediaCodecHelper.createAudioMediaCodec(resCoreParameters, dstAudioFormat);
            if (dstAudioEncoder == null) {
                LogTools.e("create Audio MediaCodec failed");
                return false;
            }
            //audio
            //44100/10=4410,4410*2 = 8820
            int audioQueueNum = resCoreParameters.audioBufferQueueNum;
            int orignAudioBuffSize = resCoreParameters.mediacodecAACSampleRate / 5;
            orignAudioBuffs = new RESAudioBuff[audioQueueNum];
            for (int i = 0; i < audioQueueNum; i++) {
                orignAudioBuffs[i] = new RESAudioBuff(AudioFormat.ENCODING_PCM_16BIT, orignAudioBuffSize);
            }
            orignAudioBuff = new RESAudioBuff(AudioFormat.ENCODING_PCM_16BIT, orignAudioBuffSize);
            filteredAudioBuff = new RESAudioBuff(AudioFormat.ENCODING_PCM_16BIT, orignAudioBuffSize);
            return true;
        }
    }

    public void start(RESFlvDataCollecter flvDataCollecter) {
        synchronized (syncOp) {
            try {
                for (RESAudioBuff buff : orignAudioBuffs) {
                    buff.isReadyToFill = true;
                }
                if (dstAudioEncoder == null) {
                    dstAudioEncoder = MediaCodec.createEncoderByType(dstAudioFormat.getString(MediaFormat.KEY_MIME));
                }
                dstAudioEncoder.configure(dstAudioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                dstAudioEncoder.start();
                lastAudioQueueBuffIndex = 0;
                audioFilterHandlerThread = new HandlerThread("audioFilterHandlerThread");
                audioSenderThread = new AudioSenderThread("AudioSenderThread", dstAudioEncoder, flvDataCollecter);
                audioFilterHandlerThread.start();
                audioSenderThread.start();
                audioFilterHandler = new AudioFilterHandler(audioFilterHandlerThread.getLooper());
            } catch (Exception e) {
                LogTools.trace("RESSoftAudioCore", e);
            }
        }
    }

    public void stop() {
        synchronized (syncOp) {
            audioFilterHandler.removeCallbacksAndMessages(null);
            audioFilterHandlerThread.quit();
            try {
                audioFilterHandlerThread.join();
                audioSenderThread.quit();
                audioSenderThread.join();
            } catch (InterruptedException e) {
                LogTools.trace("RESSoftAudioCore", e);
            }
            dstAudioEncoder.stop();
            dstAudioEncoder.release();
            dstAudioEncoder = null;
        }
    }

    public BaseSoftAudioFilter acquireAudioFilter() {
        lockAudioFilter.lock();
        return audioFilter;
    }

    public void releaseAudioFilter() {
        lockAudioFilter.unlock();
    }

    public void setAudioFilter(BaseSoftAudioFilter baseSoftAudioFilter) {
        lockAudioFilter.lock();
        if (audioFilter != null) {
            audioFilter.onDestroy();
        }
        audioFilter = baseSoftAudioFilter;
        if (audioFilter != null) {
            audioFilter.onInit(resCoreParameters.mediacodecAACSampleRate / 5);
        }
        lockAudioFilter.unlock();
    }

    public void destroy() {
        synchronized (syncOp) {
            lockAudioFilter.lock();
            if (audioFilter != null) {
                audioFilter.onDestroy();
            }
            lockAudioFilter.unlock();
        }
    }

    private class AudioFilterHandler extends Handler {
        public static final int FILTER_LOCK_TOLERATION = 3;//3ms
        public static final int WHAT_INCOMING_BUFF = 1;
        private int sequenceNum;

        AudioFilterHandler(Looper looper) {
            super(looper);
            sequenceNum = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what != WHAT_INCOMING_BUFF) {
                return;
            }
            sequenceNum++;
            int targetIndex = msg.arg1;
            long nowTimeMs = SystemClock.uptimeMillis();
            System.arraycopy(orignAudioBuffs[targetIndex].buff, 0,
                    orignAudioBuff.buff, 0, orignAudioBuff.buff.length);
            orignAudioBuffs[targetIndex].isReadyToFill = true;
            boolean isFilterLocked = lockAudioFilter();
            boolean filtered = false;
            if (isFilterLocked) {
                filtered = audioFilter.onFrame(orignAudioBuff.buff, filteredAudioBuff.buff, nowTimeMs, sequenceNum);
                unlockAudioFilter();
            } else {
                System.arraycopy(orignAudioBuffs[targetIndex].buff, 0,
                        orignAudioBuff.buff, 0, orignAudioBuff.buff.length);
                orignAudioBuffs[targetIndex].isReadyToFill = true;
            }
            //orignAudioBuff is ready
            int eibIndex = dstAudioEncoder.dequeueInputBuffer(-1);
            if (eibIndex >= 0) {
                ByteBuffer dstAudioEncoderIBuffer = dstAudioEncoder.getInputBuffers()[eibIndex];
                dstAudioEncoderIBuffer.position(0);
                dstAudioEncoderIBuffer.put(filtered?filteredAudioBuff.buff:orignAudioBuff.buff, 0, orignAudioBuff.buff.length);
                dstAudioEncoder.queueInputBuffer(eibIndex, 0, orignAudioBuff.buff.length, nowTimeMs * 1000, 0);
            } else {
                LogTools.d("dstAudioEncoder.dequeueInputBuffer(-1)<0");
            }
            LogTools.d("AudioFilterHandler,ProcessTime:" + (System.currentTimeMillis() - nowTimeMs));
        }

        /**
         * @return ture if filter locked & filter!=null
         */

        private boolean lockAudioFilter() {
            try {
                boolean locked = lockAudioFilter.tryLock(FILTER_LOCK_TOLERATION, TimeUnit.MILLISECONDS);
                if (locked) {
                    if (audioFilter != null) {
                        return true;
                    } else {
                        lockAudioFilter.unlock();
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (InterruptedException e) {
            }
            return false;
        }

        private void unlockAudioFilter() {
            lockAudioFilter.unlock();
        }
    }
}
