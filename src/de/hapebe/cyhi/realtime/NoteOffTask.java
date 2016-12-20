package de.hapebe.cyhi.realtime;
import java.util.*;
import javax.sound.midi.*;

public class NoteOffTask extends TimerTask
{

int[] notes = new int[8];
MidiChannel c;

public NoteOffTask(MidiChannel c)
{
	super();
	this.c = c;
	for (int i=0;i<notes.length;i++) notes[i] = -1;
}

public boolean addNote(int note)
{
	boolean ok = false;
	for (int i=0; i<notes.length; i++)
	{
		if (notes[i] == -1)
		{
			notes[i] = note;
			ok = true;
			break;
		}
	}
	//for (int i=0; i<notes.length; i++) System.out.print(" "+notes[i]);
	//System.out.println("");
	return ok;
}

synchronized public void run()
{
	for (int i=0; i<notes.length; i++)
	{
		if (notes[i] != -1)
		{
			c.noteOff(notes[i]);
			notes[i] = -1;
		}
	}
}


}
