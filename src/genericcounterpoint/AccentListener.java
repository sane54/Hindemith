/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericcounterpoint;

import org.jfugue.*;
import java.util.ArrayList;
/**
 *
 * @author alyssa
 */
public class AccentListener implements ParserListener{
 
    Double duration_tally = 0.00;
    Double previous_duration = 0.00;
    ArrayList<MelodicNote> melody_note_array = new ArrayList();
    MusicStringParser parser = new MusicStringParser();
    int ticker = 0;
    int pending_note_index = -1;
    int pattern_index =0;
    Double pending_duration = 0.00;
    Double prior_duration = 0.00;
    boolean syncopate = false;
    int prior_index = 0;
    
    public ArrayList<MelodicNote> listen(Pattern p) {
        parser.addParserListener(this);
        System.out.println("about to parse" + p.getMusicString());
        try {
            parser.parse(p);
        } catch (JFugueException e)
        {
            e.printStackTrace();
        }
        System.out.println("accentlistener returning array");
        printArray(melody_note_array);
        return melody_note_array;
    }

    public void voiceEvent(Voice voice){
		
	}

 
    public void tempoEvent(Tempo tempo) {
		
	}

    public void instrumentEvent(Instrument instrument){
	}

    
    public void layerEvent(Layer layer){
		
	}

    
    public void measureEvent(Measure measure){
	}
    
    
    public void timeEvent(Time time){
	}
    
    
    public void keySignatureEvent(KeySignature keySig){
	}
    
    
    public void controllerEvent(Controller controller){
	}
    
    
    public void channelPressureEvent(ChannelPressure channelPressure){
	}
    
    
    public void polyphonicPressureEvent(PolyphonicPressure polyphonicPressure){
	}
    
    
    public void pitchBendEvent(PitchBend pitchBend){
	}

    
    @Override
    public void noteEvent(Note note){
         System.out.println("about to run get accent on " + note.getMusicString() + "with duration " + note.getDecimalDuration()); 
         getAccent(note, note.getDecimalDuration());
         
		
	}

    
    public void sequentialNoteEvent(Note note){
	}

    
    public void parallelNoteEvent(Note note){
	}
	
    public void getAccent(Note note, double duration ) {

        Boolean accented = false;
        Boolean is_rest = false;
        double start_time = duration_tally;
        duration_tally = duration_tally + duration;
        ticker = (int)(duration_tally * 16)%4;
        System.out.println("ticker = " + ticker);

        if( pending_note_index >= 0){             
            //If Note is not "R" (ie a rest)
            if (!note.getMusicString().contains("C0sa0d0")) {
                System.out.println(note.getMusicString() + "does not contain R");
                //The note not being a rest closes out the previous pending note
                //Pending duration is now that note's effective duration
                //regardless of how short the sounding duration of the note is
                //We can now determine if this note should be accented
                if (pending_duration > 0.125){
                    melody_note_array.get(pending_note_index).setAccent(true);
                    System.out.println("greater than 3 16ths rule applied");
                }
                else {
                    if (pending_duration  > 0.0625) {
                        if (syncopate) {
                            melody_note_array.get(pending_note_index).setAccent(true);
                            syncopate = false;
                            System.out.println("syncopate rule applied");
                        }
                        if (pending_duration > prior_duration && prior_duration >= 0) {
                            melody_note_array.get(pending_note_index).setAccent(true);
                            System.out.println("greater than prior note rule applied");
                        }
                    }
                }

                //new pending and prior durations
                prior_index = pending_note_index;
                pending_note_index = pattern_index;
                prior_duration = pending_duration;
                pending_duration = duration;

                if (ticker == 4) {
                    syncopate = true;
                    System.out.println("syncopate flag set");
                }
        }

        //Else Just add the rest's duration to the previous duration 
        else {
            System.out.println("adding rest to pending_duration");
            pending_duration += duration;
            is_rest = true;
        }
    }
    if (note.getMusicString().contains("A")) {
        accented = true;
        System.out.println("Start of Quarter Rule Applied");
    }
    MelodicNote my_melody_note = new MelodicNote();
    my_melody_note.setDuration(duration);
    my_melody_note.setStartTime(start_time);
    my_melody_note.setAccent(accented);
    my_melody_note.setTotalVoiceDuration(duration_tally);
    if (is_rest) my_melody_note.setRest(true);
    else if (pending_note_index == -1) {
        pending_note_index = 0;
        pending_duration = duration;
        System.out.println("creating pending note index");
    }
    melody_note_array.add(my_melody_note);
    pattern_index++;
    }   
    
    public void printArray(ArrayList<MelodicNote> melody_note_array ) {
        String pitch_string = "RR";
        String acc_string = "A";
             
        for(MelodicNote m_note : melody_note_array) {
            if(m_note.rest == false) pitch_string = "P";
            if(m_note.accented== false) acc_string = "N";
            System.out.print( acc_string+ pitch_string + "s   ");
            
        }
    }
}
