/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModeModules;

import genericcounterpoint.ModeModule;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author alyssa
 */
public class Dorian implements ModeModule {
    Random roll = new Random();
    private final int tonic = 2;
    
    private static final Map<Integer, Double> step_probability_0;
	static {
		step_probability_0 = new HashMap<>();
		step_probability_0.put(0, 0.04);
		step_probability_0.put(2, 0.38);
		step_probability_0.put(-1, 0.11);
		step_probability_0.put(-5, 0.05);
		step_probability_0.put(5, 0.23);
		step_probability_0.put(4, 0.11);
		step_probability_0.put(-3, 0.04);
		step_probability_0.put(12, 0.02);
                step_probability_0.put(-12, 0.02);
	};

	//0 -1 2 -3 4 5 -5 12
	
private static final Map<Integer, Double> step_probability_2;
static {
	step_probability_2 = new HashMap<>();
	step_probability_2.put(0, 0.25);
	step_probability_2.put(2, 0.17);
	step_probability_2.put(-2, 0.13);
	step_probability_2.put(-5, 0.03);
	step_probability_2.put(5, 0.03);
	step_probability_2.put(3, 0.07);
	step_probability_2.put(-3, 0.02);
	step_probability_2.put(12, 0.125);
        step_probability_2.put(-12, 0.125);
	step_probability_2.put(7, 0.05);
};
// 0 2 -2 3 -3 5 -5 7 12
private static final Map<Integer, Double> step_probability_4;
static {
	step_probability_4 = new HashMap<>();
	step_probability_4.put(0, 0.05);
	step_probability_4.put(-2, 0.35);
	step_probability_4.put(3, 0.17);
	step_probability_4.put(-4, 0.05);
	step_probability_4.put(12, 0.015);
        step_probability_4.put(-12, 0.015);
	step_probability_4.put(1, 0.35);
};
// 0 1 -2 3 -4 12
private static final Map<Integer, Double> step_probability_5;
static {
	step_probability_5 = new HashMap<>();
	step_probability_5.put(0, 0.11);
	step_probability_5.put(2, 0.27);
	step_probability_5.put(-1, 0.27);
	step_probability_5.put(-5, 0.02);
	step_probability_5.put(4, 0.11);
	step_probability_5.put(-3, 0.02);
	step_probability_5.put(12, 0.05);
        step_probability_5.put(-12, 0.05);
};
// 0 -1 2 -3 4 -5 12

private static final Map<Integer, Double> step_probability_7;
static {
	step_probability_7 = new HashMap<>();
	step_probability_7.put(0, 0.06);
	step_probability_7.put(2, 0.39);
	step_probability_7.put(-2, 0.33);
	step_probability_7.put(-5, 0.02);
	step_probability_7.put(5, 0.05);
	step_probability_7.put(3, 0.01);
	step_probability_7.put(-3, 0.09);
	step_probability_7.put(12, 0.005);
        step_probability_7.put(-12, 0.005);
	step_probability_7.put(7, 0.04);
};
// 0 2 -2 3 -3 5 -5 7 12
private static final Map<Integer, Double> step_probability_9;
static {
	step_probability_9 = new HashMap<>();
	step_probability_9.put(0, 0.17);
	step_probability_9.put(2, 0.07);
	step_probability_9.put(-2, 0.40);
	step_probability_9.put(-5, 0.01);
	step_probability_9.put(5, 0.02);
	step_probability_9.put(1, 0.10);
	step_probability_9.put(-4, 0.09);
	step_probability_9.put(12, 0.06);
        step_probability_9.put(-12, 0.06);
	step_probability_9.put(-7, 0.01);
};
// 0 1 2 -2 -3 5 -5 -7 12
private static final Map<Integer, Double> step_probability_10;
static {
	step_probability_10 = new HashMap<>();
	step_probability_10.put(-1, 0.86);
	step_probability_10.put(-3, 0.24);
};
// -1 -3

private static final Map<Integer, Double> step_probability_11;
static {
	step_probability_11 = new HashMap<>();
	step_probability_11.put(-2, 0.33);
	step_probability_11.put(3, 0.12);
	step_probability_11.put(-4, 0.22);
	step_probability_11.put(1, 0.33);
};
// 1 -2 3 -4
    
    
    
    
    
    
    
    
    @Override
    public ArrayList<Integer> getFirstNotePitchCandidates(int input_range_min, int input_range_max ) {
    System.out.println("starting get first Note Pitch Candidates");
    Integer[] pitch_classes = {2, 5, 9};
    ArrayList<Integer> pitch_candidates = new ArrayList();
        System.out.println("starting transposition");
        for (Integer pitch_candidate : pitch_classes) {
            while (pitch_candidate < input_range_min + 8 || pitch_candidate > input_range_max) {
                pitch_candidate +=  12;
            }
            pitch_candidates.add(pitch_candidate);
        }
        System.out.println("finish transposition");
        return pitch_candidates; 
    }
  
    @Override
    public Integer getPitchCenter (int input_range_min, int input_range_max ){
      Integer[] pitch_classes = {0, 2, 4, 5, 7, 9, 10 , 11};
      Integer pitch_candidate = pitch_classes[roll.nextInt(pitch_classes.length)];
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
        System.out.println("note approached from " + previous_pitch_class);
        switch (previous_pitch_class) {
            case ( 0 ): 
                //0 -1 2 -3 4 5 -5 12
                pitch_step_candidates.add(0);
                pitch_step_candidates.add(-1);
                pitch_step_candidates.add(2);
                pitch_step_candidates.add(-3);
                pitch_step_candidates.add(4);
                pitch_step_candidates.add(5);
                pitch_step_candidates.add(-5);
                pitch_step_candidates.add(12);
                pitch_step_candidates.add(-12);
                break;
            case ( 2 ): 
                // 0 2 -2 3 -3 5 -5 7 12
                pitch_step_candidates.add(0);
                pitch_step_candidates.add(-2);
                pitch_step_candidates.add(2);
                pitch_step_candidates.add(-3);
                pitch_step_candidates.add(3);
                pitch_step_candidates.add(5);
                pitch_step_candidates.add(-5);
                pitch_step_candidates.add(12);
                pitch_step_candidates.add(-12);
                break;
            case ( 4 ): 
                // 0 1 -2 3 -4 12
                pitch_step_candidates.add(0);
                pitch_step_candidates.add(-2);
                pitch_step_candidates.add(1);
                pitch_step_candidates.add(3);
                pitch_step_candidates.add(-4);
                pitch_step_candidates.add(12);
                pitch_step_candidates.add(-12);
                break; 
            case ( 5 ): 
                // 0 -1 2 -3 4 -5 12
                pitch_step_candidates.add(0);
                pitch_step_candidates.add(2);
                pitch_step_candidates.add(-1);
                pitch_step_candidates.add(-3);
                pitch_step_candidates.add(4);
                pitch_step_candidates.add(-5);
                pitch_step_candidates.add(12);
                pitch_step_candidates.add(-12);
                break;
            case ( 7 ): 
                // 0 2 -2 3 -3 5 -5 7 12
                pitch_step_candidates.add(0);
                pitch_step_candidates.add(2);
                pitch_step_candidates.add(-2);
                pitch_step_candidates.add(3);
                pitch_step_candidates.add(-3);
                pitch_step_candidates.add(5);
                pitch_step_candidates.add(-5);
                pitch_step_candidates.add(7);
                pitch_step_candidates.add(12);
                pitch_step_candidates.add(-12);
                break;
            case ( 9 ): 
                // 0 1 2 -2 -4 5 -5 -7 12
                pitch_step_candidates.add(0);
                pitch_step_candidates.add(2);
                pitch_step_candidates.add(-2);
                pitch_step_candidates.add(1);
                pitch_step_candidates.add(-4);
                pitch_step_candidates.add(5);
                pitch_step_candidates.add(-5);
                pitch_step_candidates.add(-7);
                pitch_step_candidates.add(12);
                pitch_step_candidates.add(-12);
                break;
            case (10) : 
                // -1 -3
                pitch_step_candidates.add(-1);
                pitch_step_candidates.add(-3);
                break;
            case (11) : 
                // 1 -2 3 -4
                pitch_step_candidates.add(-1);
                pitch_step_candidates.add(-2);
                pitch_step_candidates.add(3);
                pitch_step_candidates.add(-4);
                break;
        }
        
        for (Integer pitch_step_candidate : pitch_step_candidates) {
            pitch_candidates.add(pitch_step_candidate + input_previous_pitch);
        }
        
        return pitch_candidates;
    }
    
    public Double getMelodicMotionProbability (Integer input_current_pitch_cand, Integer input_previous_pitch) {
        Integer difference = input_current_pitch_cand - input_previous_pitch;
        Double motion_probability = 0.00;
        Integer p_class = input_previous_pitch % 12;
        System.out.println("getting probability for " + difference + "steps from " + p_class);
        switch ( p_class ){
            case (0):
                motion_probability = step_probability_0.get(difference);
                System.out.println(step_probability_0.get(difference));
                break;
            case (2):
                motion_probability = step_probability_2.get(difference);
                System.out.println(step_probability_2.get(difference));
                break;
            case (4):
                motion_probability = step_probability_4.get(difference);
                System.out.println(step_probability_4.get(difference));
                break;
            case (5):
                motion_probability = step_probability_5.get(difference);
                System.out.println(step_probability_5.get(difference));
                break;
            case (7):
                motion_probability = step_probability_7.get(difference);
                System.out.println(step_probability_7.get(difference));
                break;
            case (9):
                motion_probability = step_probability_9.get(difference);
                System.out.println(step_probability_9.get(difference));
                break;
            case (10):
                motion_probability = step_probability_10.get(difference);
                System.out.println(step_probability_10.get(difference));
                break;
            case (11):
                motion_probability = step_probability_11.get(difference);
                System.out.println(step_probability_11.get(difference));
                break;
        }
        return motion_probability;
    }
    
    public Integer getTonic() {
        return tonic;
    }
}
