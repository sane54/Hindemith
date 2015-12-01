/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModeModules;

import genericcounterpoint.ModeModule;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author alyssa
 */
public class Atonal implements ModeModule {
    Random roll = new Random();
    private final int tonic = 0;
    


    @Override
    public ArrayList<Integer> getFirstNotePitchCandidates(int input_range_min, int input_range_max ) {
    System.out.println("starting get first Note Pitch Candidates");
    ArrayList<Integer> pitch_classes = new ArrayList();
    for (int i = 0; i <12; i++) {
        int j = i;
        while (j < input_range_min) j+=12; 
        pitch_classes.add(j);  
    }
        
    return pitch_classes; 
    }
  
    @Override
    public Integer getPitchCenter (int input_range_min, int input_range_max ){
      Integer pitch_candidate = roll.nextInt(12);
      while (pitch_candidate < input_range_min + 8 || pitch_candidate > input_range_max) {
         pitch_candidate +=  12;
      }
      return pitch_candidate;
    }
    
    @Override
    public ArrayList<Integer> getPitchCandidates(int input_previous_pitch){
        
        ArrayList<Integer> pitch_step_candidates = new ArrayList();
        ArrayList<Integer> pitch_candidates = new ArrayList();
        Integer previous_pitch_class = input_previous_pitch % 12;
        for (int i = -12; i< 13; i++) pitch_step_candidates.add(i);
        for (Integer pitch_step_candidate : pitch_step_candidates) {
            pitch_candidates.add(pitch_step_candidate + input_previous_pitch);
        }
        
        return pitch_candidates;
    }
    @Override
    public Double getMelodicMotionProbability (Integer input_current_pitch_cand, Integer input_previous_pitch) {
        Double motion_probability = 0.08;
        return motion_probability;
    }
    @Override
    public Integer getTonic() {
        return tonic;
    }
}
