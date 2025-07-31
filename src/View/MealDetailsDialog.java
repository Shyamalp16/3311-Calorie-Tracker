package View;

import models.Meal;
import models.MealItem;
import models.Food;
import logic.facade.INutritionFacade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MealDetailsDialog extends JDialog {
    private Meal meal;
    private List<MealItem> mealItems;
    private INutritionFacade nutritionFacade;

    public MealDetailsDialog(Frame owner, Meal meal, List<MealItem> mealItems, INutritionFacade nutritionFacade) {
        super(owner, "Meal Details", true);
        this.meal = meal;
        this.mealItems = mealItems;
        this.nutritionFacade = nutritionFacade;
        initUI();
    }

    private void initUI() {
        setSize(600, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel(meal.getMealType() + " Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Food", "Quantity", "Unit", "Calories", "Protein", "Carbs", "Fat", "Fiber", "Vitamin A", "Vitamin B", "Vitamin C", "Vitamin D"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        for (MealItem item : mealItems) {
            Food food = nutritionFacade.getFoodById(item.getFoodId());
            String foodName = (food != null) ? food.getFoodDescription() : "Unknown Food";
            tableModel.addRow(new Object[]{
                foodName,
                item.getQuantity(),
                item.getUnit(),
                item.getCalories(),
                item.getProtein(),
                item.getCarbs(),
                item.getFats(),
                item.getFiber(),
                item.getVitaminA(),
                item.getVitaminB(),
                item.getVitaminC(),
                item.getVitaminD()
            });
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> setVisible(false));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}