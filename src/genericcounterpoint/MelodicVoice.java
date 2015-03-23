/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericcounterpoint;

import java.util.ArrayList;


/**
 *
 * @author alyssa
 */
public class MelodicVoice {
    private ArrayList<MelodicNote> note_arraylist = new ArrayList();
    enum VoiceRange {bass, tenor, alto, soprano, ultra};
    private VoiceRange range_id;
    private int range_min;
    private int range_max;
    private int pitch_center;


    public void setRange (String input_range_id) {
        
        if ("bass".equals(input_range_id)) range_id = VoiceRange.bass;
if ("tenor".equals(input_range_id)) range_id = VoiceRange.tenor;
if ("alto".equals(input_range_id)) range_id = VoiceRange.alto;
if ("soprano".equals(input_range_id)) range_id = VoiceRange.soprano;
if ("ultra".equals(input_range_id)) range_id = VoiceRange.ultra;
        
        
        if (range_id == VoiceRange.bass) {
            range_min = 12;
            range_max = 36;
        }
        else if (range_id == VoiceRange.tenor) {
            range_min = 36;
            range_max = 60;
        }
        else if (range_id == VoiceRange.alto) {
            range_min = 43;
            range_max = 67;
        }
        else if (range_id == VoiceRange.soprano) {
            range_min = 48;
            range_max = 72;
        }
        else if (range_id == VoiceRange.ultra) {
            range_min = 72;
            range_max = 96;
        }
    }

    public void setPitchCenter (int input_pitch_center) {
        pitch_center = input_pitch_center;
    }
    
    public void addMelodicNote (MelodicNote input_melodic_note){
        note_arraylist.add(input_melodic_note);
    }
    
    public MelodicNote getMelodicNote (int index){
       return  note_arraylist.get(index);
    }
    
    public int getVoiceLength() {
        return note_arraylist.size();
    }
    
    public ArrayList getNoteArray(){
        return note_arraylist;
    }
    
    public void setNoteArray(ArrayList<MelodicNote> input_note_array){
        note_arraylist  = input_note_array;
    }
    
    public int getRangeMin(){
        return range_min;
    }
    
    public int getRangeMax(){
        return range_max;
    }
    public VoiceRange getRangeId(){
        return range_id;
    }
    
    public int getPitchCenter(){
        return pitch_center;
    }
    
}

