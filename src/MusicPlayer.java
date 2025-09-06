
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class MusicPlayer {
    private static String[] titles = {};
    private static AudioInputStream[] music = new AudioInputStream[titles.length];
    private static Clip[] audioClips = new Clip[titles.length];
    private static Clip audioClip;
    private static String currentMusic = "";

    public void playMusic(){
//        try {
//            for (int i = 0; i < titles.length; i++) {
//                music[i] = music(titles[i] + ".wav");
//                audioClips[i] = AudioSystem.getClip();
//                audioClips[i].open(music[i]);
//            }
//        }
//        catch(UnsupportedAudioFileException uafe){
//
//        }
//        catch(IOException ioe){
//
//        }
//        catch(LineUnavailableException lue){
//
//        }
//        audioClip = audioClips[2];
//        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    private static AudioInputStream music(String fileName) throws UnsupportedAudioFileException, IOException {
        URL url = MusicPlayer.class.getResource("/bg/" + fileName);
        return AudioSystem.getAudioInputStream(url);
    }
    public static void changeMusic(String title){
//        if(!title.equals(currentMusic)) {
//            audioClip.stop();
//            audioClip.setFramePosition(0);
//            currentMusic = title;
//            for (int i = 0; i < titles.length; i++) {
//                if (title.equals(titles[i])) {
//                    audioClip = audioClips[i];
//                    audioClip.loop(Clip.LOOP_CONTINUOUSLY);
//                }
//            }
//        }
    }
    public String toString(){
        return currentMusic;
    }
    public boolean equals(Object other){
        return currentMusic.equals(((MusicPlayer)other).currentMusic);
    }
}

