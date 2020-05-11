package de.cerus.noteblockrecorder.song;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;

public class Sound {

    private Instrument instrument;
    private Note note;
    private int delay;

    public Sound(Instrument instrument, Note note, int delay) {
        this.instrument = instrument;
        this.note = note;
        this.delay = delay;
    }

    public void play(Player player) {
        player.playNote(player.getLocation(), instrument, note);
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public Note getNote() {
        return note;
    }

    public int getDelay() {
        return delay;
    }

}
