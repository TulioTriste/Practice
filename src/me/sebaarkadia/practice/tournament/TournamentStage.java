package me.sebaarkadia.practice.tournament;

public enum TournamentStage
{
    FIRST_ROUND("FIRST_ROUND", 0), 
    SECOND_ROUND("SECOND_ROUND", 1), 
    THIRD_ROUND("THIRD_ROUND", 2), 
    QUARTER_S("QUARTER_S", 3), 
    SEMI_FINAL("SEMI_FINAL", 4), 
    FINAL("FINAL", 5);
    
    private TournamentStage(final String s, final int n) {
    }
}
