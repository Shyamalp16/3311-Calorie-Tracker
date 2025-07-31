package logic.facade;

import models.FoodSwapRecommendation;
import java.util.List;

public class FoodSwapResult {
    private final boolean success;
    private final String message;
    private final List<FoodSwapRecommendation> recommendations;

    public FoodSwapResult(boolean success, String message, List<FoodSwapRecommendation> recommendations) {
        this.success = success;
        this.message = message;
        this.recommendations = recommendations;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<FoodSwapRecommendation> getRecommendations() {
        return recommendations;
    }
}