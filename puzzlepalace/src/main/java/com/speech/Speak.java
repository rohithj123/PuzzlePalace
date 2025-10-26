package com.speech;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Speak {
    private static final String VOICE_NAME = "kevin16";
    private static final Object VOICE_LOCK = new Object();
    private static Voice activeVoice;

    public static void speak(String text){
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        VoiceManager voiceManger = VoiceManager.getInstance();
        Voice voice = voiceManger.getVoice(VOICE_NAME);

        if(voice == null){
            System.err.println("Voice not found: " + VOICE_NAME);
            return;
        }

        synchronized (VOICE_LOCK) {
            activeVoice = voice;
        }

        try {
            voice.allocate();
            voice.speak(text);
        } finally {
            voice.deallocate();
            synchronized (VOICE_LOCK) {
                if (activeVoice == voice) {
                    activeVoice = null;
                }
            }
        }
    }

    public static void stop() {
        Voice voiceToStop;
        synchronized (VOICE_LOCK) {
            voiceToStop = activeVoice;
        }

        if (voiceToStop != null) {
            if (voiceToStop.getAudioPlayer() != null) {
                voiceToStop.getAudioPlayer().cancel();
            }
        }
    }
}

