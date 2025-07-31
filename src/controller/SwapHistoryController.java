package controller;

import models.SwapHistory;
import models.Food;
import logic.SwapApplicationService;
import Database.FoodDAO;

import java.util.Date;
import java.util.List;

public class SwapHistoryController {

    private SwapApplicationService swapApplicationService;
    private FoodDAO foodDAO;

    public SwapHistoryController() {
        this.swapApplicationService = new SwapApplicationService();
        this.foodDAO = new FoodDAO();
    }

    public List<SwapHistory> getSwapHistory(int userId) {
        return swapApplicationService.getSwapHistory(userId);
    }

    public Food getFoodById(int foodId) {
        return foodDAO.getFoodById(foodId).orElse(null);
    }

    public boolean revertSwap(int historyId, int userId) {
        return swapApplicationService.revertSwap(historyId, userId);
    }

    public List<SwapHistory> getSwapHistoryForPeriod(int userId, Date startDate, Date endDate) {
        return swapApplicationService.getSwapHistoryForPeriod(userId, startDate, endDate);
    }
}
