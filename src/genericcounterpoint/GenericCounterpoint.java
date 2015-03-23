/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericcounterpoint;

import RhythmModules.VarTimeSigFunkPatternGenerator;
import ModeModules.Dorian;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Logger; //the Java Logger
import java.util.Random;
import java.util.logging.Level;
import org.jfugue.*;
/**
 *
 * @author alyssa
 */
public class GenericCounterpoint {

    /**
     * @param args the command line arguments
    
    
    */
    
    
static ArrayList<MelodicVoice> unbuilt_voices = new ArrayList();
static ArrayList<MelodicVoice> built_voices = new ArrayList();
static Integer [] consonances = {0, 3, 4, 7, 8, 9};
static Integer [] perfect_consonances = {0, 7};
static ArrayList<MotionCount> motion_counts = new ArrayList();
static ArrayList<PitchCount>pitch_counts = new ArrayList();
static int same_consonant_threshold = 6;
static Random roll = new Random();
static int tempo_bpm = 80;
static int sample_size = 5;
private static final Logger logger = Logger.getLogger("org.jfugue");


    public static void main(String[] args) {
        int piece_length = 16;
        String [] voice_array = {"tenor", "soprano"};
        int number_of_voices = voice_array.length;
        ModeModule my_mode_module = new Dorian();
        Random my_roll = new Random();
        VarTimeSigFunkPatternGenerator james = new VarTimeSigFunkPatternGenerator();
        
        


        Pattern [] rhythm_patterns = james.generate(piece_length, number_of_voices);
        for (int i = 0; i < number_of_voices; i++) {
            MelodicVoice this_voice = new MelodicVoice();
            this_voice.setRange(voice_array[i]);
            AccentListener my_accent_listener = new AccentListener();
            this_voice.setNoteArray(my_accent_listener.listen(rhythm_patterns[i]));
            System.out.println("adding voice " + i + " to unbuiltvoices");
            unbuilt_voices.add(this_voice);
        }
        int ub_size =unbuilt_voices.size();
        for (int i = 0; i < ub_size; i++){
            int voice_index = my_roll.nextInt(unbuilt_voices.size()); 
            System.out.println(" about to build voice pitches for "+ i);
            MelodicVoice nextVoice = buildVoicePitches(unbuilt_voices.get(voice_index), number_of_voices, my_mode_module);
            ArrayList<MelodicNote> verify_array = nextVoice.getNoteArray();
            System.out.println("Return Me: ");
            for(MelodicNote verify: verify_array) { 
             if (verify.getRest()) System.out.println("rest " + verify.getDuration() + "  " );
             else System.out.println(verify.getPitch() + " " + verify.getDuration() + "   ");
            }
            built_voices.add(nextVoice);
            unbuilt_voices.remove(voice_index);
        }

        Pattern music_output = new Pattern();
        music_output.addElement(new Tempo (tempo_bpm));
        
        for (byte i = 0; i < built_voices.size(); i++){
        // create a jfugue musicstring from the built voice
            MelodicVoice final_voice = built_voices.get(i);
            ArrayList<MelodicNote> final_note_array = final_voice.getNoteArray();
            Voice jf_voice = new Voice(i);
            music_output.addElement(jf_voice);
            for (MelodicNote final_note : final_note_array) {
                int jf_int = 0;
                Note jf_note = new Note();
                jf_note.setDecimalDuration(final_note.getDuration());
                if (final_note.getPitch() != null && final_note.getRest() == false ) {
                     jf_int = final_note.getPitch();
                     byte jf_note_byte = (byte)jf_int;
                    jf_note.setValue(jf_note_byte);
                }
                else {
                    System.out.println("setting jf note to rest");
                    jf_note.setRest(true);
                    jf_note.setAttackVelocity((byte)0);
                    jf_note.setDecayVelocity((byte)0);
                }
                music_output.addElement(jf_note);
            } // add the musicstring to a pattern
        }
        
        //save and play the pattern
        System.out.println(music_output.getMusicString());
        Player my_player = new Player();
        my_player.play(music_output);
    }
    
    public static MelodicVoice buildVoicePitches (MelodicVoice alter_me, int number_of_voices, ModeModule my_mode_module){
    MelodicVoice return_me = new MelodicVoice();
    Integer pitch_center  = 0;
    ArrayList<LinkedList> built_voice_queues = new ArrayList();
    int previous_cp_pitch = -13;
    boolean skip_harmonic_check = false;
    boolean skip_melodic_check = false;
    int previous_melodic_interval = 0;
    int trough = 0;
    int trough_count = 0;
    int peak = 0;
    int peak_count = 0;
    MelodicNote [] previous_notes = new MelodicNote[built_voices.size()];
    MelodicNote [] save_notes = new MelodicNote[built_voices.size()];
    int same_consonant_count = 0;
    int voice_pitch_count = 0;
    System.out.println("voiceRange min " + alter_me.getRangeMin() + "   voicerange max " + alter_me.getRangeMax());
    pitch_center = my_mode_module.getPitchCenter(alter_me.getRangeMin(), alter_me.getRangeMax());
    System.out.println("pitchcenter = " + pitch_center);
    
    if (!built_voices.isEmpty())
    for (MelodicVoice built_voice : built_voices) {
         LinkedList <MelodicNote> cf_stack = new LinkedList<>();
         ArrayList <MelodicNote> temp = built_voice.getNoteArray();
         for (MelodicNote b_voice_note : temp){
         cf_stack.add(b_voice_note);
         }
         built_voice_queues.add(cf_stack);
         System.out.println("created stack of melodic notes for each previously built voice ");
     }
     else {
         skip_harmonic_check = true;
         System.out.println("built voices Empty - start first melody");
     }
    
    
    for (int i = 0; i < alter_me.getVoiceLength(); i++){ //for each melodic note in the CP voice
        System.out.println("assigning pitch to note " + i +" of " + alter_me.getVoiceLength());
        MelodicNote CP_note = alter_me.getMelodicNote(i);
        if (CP_note.rest) {
            System.out.println(" is rest ");
            return_me.addMelodicNote(CP_note);
            continue;
        }
         
        //get pitch_candidate pitches from mode_module
        ArrayList<Integer> pitch_candidate_values = new ArrayList();
        ArrayList<PitchCandidate> pitch_candidates = new ArrayList();
        if(voice_pitch_count == 0) { // If there is no previous pitch ie this is the first note 
            pitch_candidate_values = my_mode_module.getFirstNotePitchCandidates(alter_me.getRangeMin(), alter_me.getRangeMax()) ;
            skip_melodic_check = true;
            System.out.println("using first note pitch candidates");
        }
        else {
             System.out.println("getting pitch candidates from my modemodule");
             pitch_candidate_values = my_mode_module.getPitchCandidates(previous_cp_pitch);
             if (pitch_candidate_values.isEmpty()) System.out.println("EMPTY ARRAY!!!!");
         }
         System.out.println("voice_pitch_count" + voice_pitch_count);
         System.out.print("pitch candidates: ");
         
         //build pitch_candidate object array with info from rhythm patterns and mode module
         for (Integer pitch_candidate_value : pitch_candidate_values) {
             System.out.print(pitch_candidate_value + " ");
             PitchCandidate myPC = new PitchCandidate();
             myPC.setPitch(pitch_candidate_value);
             pitch_candidates.add(myPC);
         }
         System.out.println();
         
        //Evaluate each pitch candidate
        for (PitchCandidate myPC : pitch_candidates){
            int cand_pitch = myPC.getPitch();
            int melody_motion_to_cand = 0;
            boolean cand_prev_cf_diss = true;
            
            //randomly decrement non-tonics
            if (cand_pitch%12 != my_mode_module.getTonic() && roll.nextInt(2) == 1){
                myPC.decrementRank(Decrements.is_not_tonic);
                System.out.println(cand_pitch + " is not tonic");
            }
            
            //decrement illegal notes
            if(cand_pitch <0 || cand_pitch > 127) {
                myPC.decrementRank(Decrements.illegal_note);
                System.out.println(cand_pitch + " is illegal note");                
            }

            //decrement motion outside of voice range                
            if (cand_pitch < alter_me.getRangeMin() || cand_pitch > alter_me.getRangeMax()) {
                myPC.decrementRank(Decrements.outside_range);
                 System.out.println(cand_pitch + " outside range " + alter_me.getRangeMin() + "-" + alter_me.getRangeMax());                
            }

            //decrement too far from pitch center
            if (abs(cand_pitch - pitch_center) > 16) {
                 myPC.decrementRank(Decrements.remote_from_pitchcenter);
                 System.out.println(cand_pitch + " too far from pitch center" + pitch_center);               
            } 

            if (voice_pitch_count > 0) melody_motion_to_cand = cand_pitch - previous_cp_pitch;
            if (voice_pitch_count > 1){
                //The candidate has already followed the preceding note too often. (method created)
                //need to create a method for this
                
                //look for previous_cp_pitch in PitchCount
                //if it's there get_count 
                // if the count is greater than samplesize
                //check if previous_cp_pitch and pitch_candidate in MOtion Counts
                //if so get count - then divide motion count by pitch count
                    // get the percentage from mode module
                //if actual count is greater than mode module percentage decrement
                for (PitchCount my_pitch_count: pitch_counts) {
                    if(my_pitch_count.getPitch() == previous_cp_pitch%12)
                        if(my_pitch_count.getCount() > sample_size)
                            for (MotionCount my_motion_count: motion_counts){
                               //logger.log(Level.INFO, "Entering Motion Counts");
                                //System.out.println("pitch_count for " + previous_cp_pitch %12 + " = " + my_pitch_count.getCount());
                                //System.out.println("motion count " + my_motion_count.getCount());
                                if (my_motion_count.getPreviousPitch()== previous_cp_pitch %12 && my_motion_count.getSucceedingPitch() == cand_pitch %12) {
                                    double actual = (double)my_motion_count.getCount()/(double)my_pitch_count.getCount();
                                    System.out.println("actual = " + actual);
                                    double thresh = 0.20;
                                    if (my_mode_module.getMelodicMotionProbability(cand_pitch, previous_cp_pitch)!= null){
                                      thresh = my_mode_module.getMelodicMotionProbability(cand_pitch, previous_cp_pitch);
                                      System.out.println("motion probability of " + previous_cp_pitch +" to " + cand_pitch + " = " + thresh  );
                                    }
                                    else System.out.println("motion probability of " + previous_cp_pitch +" to " + cand_pitch + " is NULL");
                                        
                                    if (actual >= thresh) {
                                        myPC.decrementRank(Decrements.melodic_motion_quota_exceed);
                                        System.out.println(cand_pitch + " is approached too often from " + previous_cp_pitch);
                                    }
                                }
                            }
                }


                //Peak/Trough check
                // a melodic phrase should have no more than two peaks and two troughs
                // a peak is defined as a change in melodic direction 
                // so when a candidate pitch wants to go in the opposite direction of 
                // the previous melodic interval we want to increment the peak or trough count accordingly
                // and determine whether we have more than two peaks or more than two troughs
                // note that the melody can always go higher or lower than the previous peak or trough

                if (previous_melodic_interval < 0 && melody_motion_to_cand > 0 ) {// will there be a change in direction from - to +  ie trough?
                        if (previous_cp_pitch == trough) trough_count++; //will this trough = previous trough? then increment
                }        
                if (previous_melodic_interval > 0 && melody_motion_to_cand <0){ // will there be a trough?
                        if (previous_cp_pitch == peak) peak_count++; //will this trough = previous trough? then increment
                }
                if (peak_count > 2 || trough_count > 2) {
                        peak_count--; //remember to decrement these counts since we won't actually use this pitch
                        trough_count--;
                        myPC.decrementRank(Decrements.peak_trough_quota_exceed);
                        System.out.println(cand_pitch + " duplicates previous peak or trough");
                }
                //Motion after Leaps checks
                //First check if the melody does not go in opposite direction of leap
                // then check if there are two successive leaps in the same direction
                if (previous_melodic_interval > 4 && melody_motion_to_cand > 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    System.out.println(melody_motion_to_cand + " to "+ cand_pitch + " is bad motion after leap");
                    if (melody_motion_to_cand > 4) {
                        myPC.decrementRank(Decrements.successive_leaps);
                        System.out.println(cand_pitch + " is successive leap");
                    }
                        
                }    
                if (previous_melodic_interval < -4 && melody_motion_to_cand < 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    System.out.println(melody_motion_to_cand + " to "+cand_pitch + " is bad motion after leap");
                    if (melody_motion_to_cand < -4) {
                        myPC.decrementRank(Decrements.successive_leaps);  
                        System.out.println(cand_pitch + " is successive leap");
                    }

                }



            }
            // end melody checks
         
            //begin Harmonic checks
            if (!skip_harmonic_check) {    
                int built_voice_index = 0;
                MelodicNote CF_note = null;
                int previous_interval = 0;
                //Will need to evaluate each CP pitch candidate against the counterpoint notes 
                //in each previously built voice
                while (built_voice_index < built_voices.size()) {
                    do 
                    {
                        //if last CF note held over into this CP, 
                        if (save_notes[built_voice_index] != null) {
                            CF_note = save_notes[built_voice_index];
                            System.out.println("using saved note");
                            save_notes[built_voice_index] = null;
                        }
                        else {//get new CF note from the stack
                            if (built_voice_queues.get(built_voice_index).isEmpty()) break;
                            CF_note = (MelodicNote) built_voice_queues.get(built_voice_index).pop();
                            if (CF_note == null) {
                                System.out.println("CF note from stack is null");
                                break;
                            }
                            else System.out.println("popped CF note from stack with pitch =" + CF_note.getPitch() +" and previous duration =" + CF_note.getPreviousDuration());
                            
                        }
                        
                        if (CF_note.getRest()) {
                            System.out.println("Note is rest - continue");
                            continue;
                        }
                        
                        //There may be lots of CF notes before this CP note so 
                        //this code below will skip past them. 
                        if (CP_note.getStartTime() > CF_note.getPreviousDuration()){
                            System.out.println("CP_note start time of "+ CP_note.getStartTime() +" is greater than the duration of CF note = " + CF_note.getPreviousDuration());
                            System.out.println("so skipping past this CF note");
                            continue;
                        }
                        //There may be a lot of CP notes before the first CF note
                        //This breaks out of the do loop and goes to look for CFs in the next voice
                        if (CF_note.getStartTime() > CP_note.getPreviousDuration()){
                            System.out.println("CF_note start time of "+ CF_note.getStartTime() +" is greater than the duration of CP note = " + CP_note.getPreviousDuration());
                            System.out.println("so we don't need to evaluate CP against this CF voice");
                            break;
                        }
                        
                        //compute interval whether consonant
                        boolean this_interval_consonant = true;
                        int this_interval = abs(myPC.getPitch() - CF_note.getPitch())%12;
                        System.out.println("this interval  = " + this_interval);
                        for (Integer consonance : consonances) {
                            if (this_interval == consonance) this_interval_consonant = true;
                        }
                        if(this_interval_consonant) System.out.println("this interval consonant");
                        //If this isn't the first note in the voice    
                        if (voice_pitch_count > 0) {   
                            
                            //compute previous_interval
                            boolean previous_interval_consonant = false;
                            if (previous_notes[built_voice_index] != null)
                                previous_interval = abs(previous_cp_pitch - previous_notes[built_voice_index].getPitch()%12);
                            else System.out.println("previous_notes for CF voice " + built_voice_index+ " is null");
                            for (Integer consonance : consonances) {
                                if (previous_interval == consonance) previous_interval_consonant = true;
                            }
                            System.out.println("previous interval  = " + previous_interval);
                            
                            if (CP_note.getStartTime() > CF_note.getStartTime()){
                                System.out.println("CP starts after CF");   
                                    //If CP_Repeats CP2_Candidate.Rank -= Repeated_Intervals_Decrement
                                if (myPC.getPitch()-previous_cp_pitch == 0)
                                    myPC.decrementRank(Decrements.seq_of_same_cons);
                                if (previous_interval_consonant){
                                    if (!this_interval_consonant && melody_motion_to_cand >2) 
                                    myPC.decrementRank(Decrements.bad_diss_approach_from_cons);
                                }
                                    else {//ie Previous_Interval is dissonant
                                        if (!this_interval_consonant){
                                            if (melody_motion_to_cand >2)
                                            myPC.decrementRank(Decrements.bad_cons_approach_from_diss);
                                        }
                                        else {//New_Interval is dissonant
                                             if (melody_motion_to_cand >4)
                                                myPC.decrementRank(Decrements.bad_diss_approach_from_diss);                                      }
                                }
                            }
                            else if (CP_note.getStartTime() < CF_note.getStartTime()) {
                                System.out.println("CP starts before CF"); 
                                    if (previous_interval_consonant){
                                        if (this_interval_consonant)
                                             myPC.decrementRank(Decrements.seq_same_type_cons);    
                                    else {//ie Previous_Interval is dissonant
                                        if (this_interval_consonant)
                                            if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch() > 2)
                                              myPC.decrementRank(Decrements.bad_cons_approach_from_diss);
                                        else //New_Interval is dissonant
                                             if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch() > 4)
                                              myPC.decrementRank(Decrements.bad_diss_approach_from_diss);
                                    }
                            }
                            else  {
                                System.out.println("CP and CF start at the same time");         
                               if (previous_interval_consonant){
                                    if (this_interval_consonant){
                                        for (Integer consonance : consonances) {
                                            if ((CP_note.getPitch() - previous_notes[built_voice_index].getPitch())%12 == consonance) {
                                            cand_prev_cf_diss = false;    
                                            }
                                        }
                                        
                                        if (cand_prev_cf_diss == true)
                                               myPC.decrementRank(Decrements.diss_cp_previous_cf);
                                        
                                        if (previous_interval == this_interval) {
                                            same_consonant_count++;
                                            if (same_consonant_count > same_consonant_threshold)
                                                myPC.decrementRank(Decrements.seq_of_same_cons);
                                        }
                                        //Too many of same type of interval
                                        for (Integer perfect_consonance : perfect_consonances) {
                                            if (this_interval%12 == perfect_consonance) {
                                                if (this_interval%12 == previous_interval %12 ){
                                                    myPC.decrementRank(Decrements.seq_same_type_cons);
                                                    if (melody_motion_to_cand > 0)
                                                        if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()>0 )
                                                            myPC.decrementRank(Decrements.parallel_perf_consonance);
                                                    if (melody_motion_to_cand < 0)
                                                        if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()<0 )
                                                            myPC.decrementRank(Decrements.parallel_perf_consonance);    
                                                }
                                            } else {
                                                if (melody_motion_to_cand > 0)
                                                    if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()>0 )
                                                        myPC.decrementRank(Decrements.direct_motion_perf_cons);
                                                if (melody_motion_to_cand < 0)
                                                    if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()<0 )
                                                        myPC.decrementRank(Decrements.direct_motion_perf_cons);
                                            }
                                        }
                                        //If dissonance between CP1 and CF2 is this resolved?
                                    }
                                    else //New_Interval is dissonant
                                        myPC.decrementRank(Decrements.motion_into_diss_both_voices_change);
                               }
                               else //ie Previous_Interval is dissonant
                                    if (this_interval_consonant){

                                        for (Integer consonance : consonances) {
                                            if ((CP_note.getPitch() - previous_notes[built_voice_index].getPitch())%12 == consonance) {
                                            cand_prev_cf_diss = false;    
                                            }
                                        }
                                        
                                        if (cand_prev_cf_diss == true)
                                               myPC.decrementRank(Decrements.diss_cp_previous_cf);
                                                if (melody_motion_to_cand > 0)
                                                    if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()>0 )
                                                        myPC.decrementRank(Decrements.direct_motion_perf_cons);
                                                if (melody_motion_to_cand < 0)
                                                    if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()<0 )
                                                        myPC.decrementRank(Decrements.direct_motion_perf_cons);
                                    }
                                    else { //New_Interval is dissonant
                                        myPC.decrementRank(Decrements.motion_into_diss_both_voices_change);
                                        myPC.decrementRank(Decrements.seq_of_diss);
                                        if(this_interval%12 == previous_interval %12){
                                            myPC.decrementRank(Decrements.seq_same_type_diss);
                                                if (melody_motion_to_cand > 0)
                                                    if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()>0 )
                                                        myPC.decrementRank(Decrements.direct_motion_perf_cons);
                                                if (melody_motion_to_cand < 0)
                                                    if (previous_notes[built_voice_index].getPitch() - CF_note.getPitch()<0 )
                                                        myPC.decrementRank(Decrements.direct_motion_perf_cons);
                                        }
                                    }
                              }

                            }

                        }
                        if (CP_note.getPreviousDuration() > CF_note.getPreviousDuration()){
                            System.out.println("CP held into next CF"); 
                            previous_cp_pitch = CP_note.getPitch();
                            previous_notes[built_voice_index] = CF_note;
                        }
                    } while (CP_note.getPreviousDuration() > CF_note.getPreviousDuration());

                    if (CF_note == null) {
                        System.out.println("CF note is null");
                        break;
                                                }
                    if (CP_note.getPreviousDuration() < CF_note.getPreviousDuration()){
                        save_notes[built_voice_index] = CF_note;
                    }
                    previous_notes[built_voice_index] = CF_note;
                    built_voice_index++;
                }//evaluate the CP  pitch candidate against the simultaneous CF note(s) in NEXT voice
            }//end if no skip harmonic check
        }//go back and evaluate next CP pitch Candidate
        //pick highest ranking pitch candidate -> return_me[i]
        ArrayList<PitchCandidate> pitch_winners = new ArrayList();
        for (PitchCandidate myPC : pitch_candidates){
            System.out.println( "pitch candidate pitch: "+ myPC.getPitch() + " and rank: " + myPC.getRank() );
            if (pitch_winners.isEmpty()) pitch_winners.add(myPC);
            else if (myPC.getRank() > pitch_winners.get(0).getRank()) {
                     for (int d = 0; d < pitch_winners.size(); d++){
                         pitch_winners.remove(d);
                     }
                     pitch_winners.add(myPC);
                }
            else if (Objects.equals(myPC.getRank(), pitch_winners.get(0).getRank())) pitch_winners.add(myPC);
            
        }
        int cp_winner = pitch_winners.get(0).getPitch();
         
        if (pitch_winners.size() >1) cp_winner = pitch_winners.get(roll.nextInt(pitch_winners.size())).getPitch();
        //re-assign variables and move on to next CP note
        System.out.println("CP winner" + cp_winner);
        CP_note.setPitch(cp_winner);
        return_me.addMelodicNote(CP_note);
        if (previous_cp_pitch != -13) {
            if (previous_melodic_interval < 0 && cp_winner - previous_cp_pitch  > 0 ) {// will there be a change in direction from - to +  ie trough?
                if (cp_winner == trough) trough_count++;
                else {
                    trough = cp_winner;
                    System.out.println("setting new trough = " + previous_cp_pitch);
                }
            }
        
            if (previous_melodic_interval > 0 && cp_winner - previous_cp_pitch  < 0 ) {// will there be a change in direction from - to +  ie trough?
                if (cp_winner == peak) peak_count++;
                else{
                    peak = cp_winner;
                    System.out.println("setting new peak = " + previous_cp_pitch);
                }
            } 
            previous_melodic_interval = cp_winner - previous_cp_pitch;
            System.out.println("previous melodic interval = " + previous_melodic_interval);
            boolean add_pitch = true;
            for(int pc = 0; pc < pitch_counts.size(); pc++) {
                if (pitch_counts.get(pc).getPitch() == previous_cp_pitch%12) {
                    pitch_counts.get(pc).incrementCount();
                    //logger.log(Level.INFO, "Score Partwise Check");
                    add_pitch = false;
                }        
            }
            
            if (add_pitch == true){
            PitchCount my_pitch_count = new PitchCount(previous_cp_pitch %12);
            pitch_counts.add(my_pitch_count);               
            }
            
            boolean add_motn = true;
            
            for(int mc = 0; mc< motion_counts.size(); mc++){
                if (motion_counts.get(mc).getPreviousPitch() == previous_cp_pitch %12 && motion_counts.get(mc).getSucceedingPitch() == cp_winner %12) {
                     motion_counts.get(mc).incrementCount();
                     add_motn = false;
                }   
            }
            if(add_motn ==true) {
                MotionCount my_motionCount = new MotionCount(previous_cp_pitch %12, cp_winner %12);
                motion_counts.add(my_motionCount);
            }
        }
        

        previous_cp_pitch = cp_winner;
        voice_pitch_count++;
    }//loop through next CP note 


    return return_me;

    } //end method
    
    public static class PitchCandidate {
        Integer rank = 0;
        Integer pitch = 0;
        
        public Integer getRank (){
        return rank;
        }
        
        public void decrementRank(int decrement){
            rank -= decrement;
        }
        
        public void setPitch (Integer input_pitch){
            pitch = input_pitch;
        }
        
        public Integer getPitch (){
            return pitch;
        }
    }
    
    public static class MotionCount {
        
       private final int previous_pitch;
       private final int succeeding_pitch;
       private int count;

        
       public int getCount() {
       return count;
       }
       
       public int getPreviousPitch() {
       return previous_pitch;
       }
       
       public int getSucceedingPitch() {
       return succeeding_pitch;
       }
       
       public void incrementCount(){
           count++;
       }
               
       private MotionCount (int input_prev_pitch, int input_succ_pitch) {
           this.previous_pitch = input_prev_pitch;
           this.succeeding_pitch = input_succ_pitch;
           this.count = 1;
       }
    }
    
    public static class PitchCount {
        private final int the_pitch;
        private int count;
       
        private PitchCount(int input_the_pitch){
            this.the_pitch = input_the_pitch;
        }
        
        public void incrementCount(){
           count++;
        }
        
        public int getPitch() {
        return the_pitch;
        }
        
        public int getCount() {
        return count;
        }
    }
}


