package com.example.nioto.emojigame.activities;

import android.app.Application;
import android.content.Context;

import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


public class SolveEnigmaActivityTest {

   private SolveEnigmaActivity classTest = new SolveEnigmaActivity();

   private final String ENIGMA_SOLUTION_1 = "Il n\'y a pas de fumée sans feu";

   @Test
   public void putSolutionInCharArray_Test(){
       ArrayList<Character> solutionInCharArray = classTest.putSolutionInCharArray(ENIGMA_SOLUTION_1);
      assertEquals("I", solutionInCharArray.get(0).toString());
      assertEquals("l", solutionInCharArray.get(1).toString());
      assertEquals(" ", solutionInCharArray.get(2).toString());
      assertEquals("n", solutionInCharArray.get(3).toString());
      assertEquals("\'", solutionInCharArray.get(4).toString());
      assertEquals("y", solutionInCharArray.get(5).toString());
      assertEquals("a", solutionInCharArray.get(6).toString());
      assertEquals("p", solutionInCharArray.get(7).toString());
      assertEquals("u", solutionInCharArray.get(solutionInCharArray.size()-1).toString());
   }

   @Test
   public void putSolutionInTypeArray_Test(){
      ArrayList<Character> solutionInTypeArray = classTest.putSolutionInTypeArray(ENIGMA_SOLUTION_1);
      ArrayList<Character> arrayExpected = new ArrayList<>(Arrays.asList('A', 'A','C', 'A','D', 'A','C', 'A','C', 'A', 'A', 'A','C', 'A', 'A','C', 'A', 'A', 'A', 'A', 'A','C', 'A', 'A', 'A', 'A','C', 'A', 'A', 'A'));
      assertEquals(solutionInTypeArray, arrayExpected);
   }

   @Test
   public void determineOffsetMax_Test(){
      int solution = classTest.determineOffsetMax();
      assertEquals(18, solution);
   }

}