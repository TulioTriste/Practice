package me.sebaarkadia.practice.duel;

public enum DuelState
{
    STARTING("STARTING", 0), 
    FIGHTING("FIGHTING", 1), 
    ENDING("ENDING", 2);
    
    private DuelState(final String s, final int n) {
    }
}
