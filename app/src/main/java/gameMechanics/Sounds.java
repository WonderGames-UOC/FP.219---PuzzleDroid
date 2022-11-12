package gameMechanics;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.example.puzzledroid.R;

//TODO: Sound effects greetings
//Sound Effect from <a href="https://pixabay.com/sound-effects/?utm_source=link-attribution&amp;utm_medium=referral&amp;utm_campaign=music&amp;utm_content=92097">Pixabay</a>
//Sound Effect from <a href="https://pixabay.com/sound-effects/?utm_source=link-attribution&amp;utm_medium=referral&amp;utm_campaign=music&amp;utm_content=36118">Pixabay</a>
//Sound Effect from <a href="https://pixabay.com/?utm_source=link-attribution&amp;utm_medium=referral&amp;utm_campaign=music&amp;utm_content=6185">Pixabay</a>
//Sound Effect from <a href="https://pixabay.com/?utm_source=link-attribution&amp;utm_medium=referral&amp;utm_campaign=music&amp;utm_content=6346">Pixabay</a>

public class Sounds {
    private final String TAG = "Sounds";
    private int cancelSound, selectSoundA, selectSoundB, swapSound, victorySound;
    private AudioAttributes soundAttrb;
    private SoundPool soundPool;
    private Context context;

    public Sounds(Context context){
        Log.d(TAG, "Sounds builder");
        try{
            this.context = context;
            soundAttrb = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            this.soundPool = new SoundPool.Builder().setAudioAttributes(soundAttrb).setMaxStreams(2).build();
            this.cancelSound = this.soundPool.load(context, R.raw.cancel, 1);
            this.selectSoundA = this.soundPool.load(context, R.raw.clickselect, 0);
            this.selectSoundB = this.soundPool.load(context, R.raw.clickselect2, 0);
            this.swapSound =  this.soundPool.load(context, R.raw.swap, 1);
            this.victorySound = this.soundPool.load(context, R.raw.success_fanfare_trumpets, 2);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public int getCancelSound() {
        return cancelSound;
    }

    public void setCancelSound(int cancelSound) {
        this.cancelSound = cancelSound;
    }

    public int getSelectSoundA() {
        return selectSoundA;
    }

    public void setSelectSoundA(int selectSoundA) {
        this.selectSoundA = selectSoundA;
    }

    public int getSelectSoundB() {
        return selectSoundB;
    }

    public void setSelectSoundB(int selectSoundB) {
        this.selectSoundB = selectSoundB;
    }

    public int getSwapSound() {
        return swapSound;
    }

    public void setSwapSound(int swapSound) {
        this.swapSound = swapSound;
    }

    public AudioAttributes getSoundAttrb() {
        return soundAttrb;
    }

    public void setSoundAttrb(AudioAttributes soundAttrb) {
        this.soundAttrb = soundAttrb;
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public void setSoundPool(SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    public int getVictorySound() {
        return victorySound;
    }

    public void setVictorySound(int victorySound) {
        this.victorySound = victorySound;
    }
}
