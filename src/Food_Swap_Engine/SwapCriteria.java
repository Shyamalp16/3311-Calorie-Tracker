package Food_Swap_Engine;


public class SwapCriteria {
    public enum GoalType {
        INCREASE_FIBER,
        REDUCE_CALORIES,
        INCREASE_PROTEIN,
        REDUCE_CARBS
    }
    
    private GoalType primaryGoal;
    private GoalType secondaryGoal;
    private double intensity;
   
    public GoalType getPrimaryGoal() { return primaryGoal; }
    public void setPrimaryGoal(GoalType primaryGoal) { this.primaryGoal = primaryGoal; }
    public GoalType getSecondaryGoal() { return secondaryGoal; }
    public void setSecondaryGoal(GoalType secondaryGoal) { this.secondaryGoal = secondaryGoal; }
    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }
}

