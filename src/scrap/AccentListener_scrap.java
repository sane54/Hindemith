/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrap;

import genericcounterpoint.MelodicNote;
import org.jfugue.*;
import java.util.ArrayList;
/**
 *
 * @author alyssa
 */
public class AccentListener_scrap implements ParserListener{
 
    Double duration_tally = 0.00;
    Double previous_duration = 0.00;
    ArrayList<MelodicNote> melody_note_array = new ArrayList();
    MusicStringParser parser = new MusicStringParser();
    
    public ArrayList<MelodicNote> listen(Pattern p) {
        parser.addParserListener(this);
        try {
            parser.parse(p);
        } catch (JFugueException e)
        {
        }
        System.out.println("accentlistener returning array");
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
		//If Note is not "R" (ie a rest)
		if (!note.getMusicString().contains("R")) {
			//Determine if note is accented
			if (duration > previous_duration) {
			accented = true;
			System.out.println("Duration Greater Rule Applied");
			}
			else if (note.getMusicString().contains("A")) {
			accented = true;
			System.out.println("Start of Quarter Rule Applied");
			}
			//else if (duration == Previousduration) {
			//	if (duration == 0.0625 && (((duration + duration_tally) % BeatNoteduration == 0) || ((3* duration) + duration_tally) % BeatNoteduration == 0 )) {
			//	Accented = true;	
			//	System.out.println("16th Note Rule Engaged");
			//	}
			//} //Not sure if the above is needed
			
			//Append to string or array of whether note is accented or unaccented
			//if (Accented == true)	PatternAccentExtractor.AddAccent('A');
			//else PatternAccentExtractor.AddAccent('U');
			//if (Accented == true)	Funkifier_2.AddAccent('A');
			//else Funkifier_2.AddAccent('U');
			previous_duration = duration;		
		}

		//Else Just add the rest's duration to the previous duration 
		else {
                    previous_duration = previous_duration + duration;
                    is_rest = true;
                }
		
		MelodicNote my_melody_note = new MelodicNote();
		my_melody_note.setDuration(duration);
		my_melody_note.setStartTime(start_time);
		my_melody_note.setAccent(accented);
		my_melody_note.setTotalVoiceDuration(duration_tally);
                if (is_rest) my_melody_note.setRest(true);
		
		melody_note_array.add(my_melody_note);
		
		//Reset BeatsPerBar
		//if (duration_tally/BeatNoteduration == BeatsPerBar) duration_tally = 0.00;
		//System.out.println("Resetting duration Tally " + duration_tally);
	}   
}
