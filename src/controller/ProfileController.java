package controller;

import models.User;
import models.UserSettings;
import Database.UserDAO;
import Database.UserSettingsDAO;
import models.UnitSystem;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ProfileController {

    private UserDAO userDAO;
    private UserSettingsDAO settingsDAO;

    public ProfileController() {
        this.userDAO = new UserDAO();
        this.settingsDAO = new UserSettingsDAO();
    }

    public User createUser(String name, String username, String password, String gender, String birthDateStr,
                           double height, double weight, String activityLevel, UnitSystem unitSystem) {
        try {
            Date birthDate = parseDate(birthDateStr);
            double heightInCm = UnitSystem.convertHeight(height, unitSystem, UnitSystem.METRIC);
            double weightInKg = UnitSystem.convertWeight(weight, unitSystem, UnitSystem.METRIC);

            User newUser = new User(name, username, password, gender, birthDate, heightInCm, weightInKg, activityLevel);
            User savedUser = userDAO.createUser(newUser);

            if (savedUser != null) {
                UserSettings userSettings = new UserSettings(savedUser.getUserId(), unitSystem);
                settingsDAO.saveSettings(userSettings);
            }

            return savedUser;
        } catch (ParseException e) {
            // In a real application, you would have a more robust error handling mechanism
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateUser(User user, UserSettings settings) {
        User updatedUser = userDAO.updateUser(user);
        UserSettings updatedSettings = settingsDAO.saveSettings(settings);
        return updatedUser != null && updatedSettings != null;
    }

    public User login(String username, String password) {
        return userDAO.login(username, password).orElse(null);
    }

    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsedDate = sdf.parse(dateStr);
        return new Date(parsedDate.getTime());
    }
}
