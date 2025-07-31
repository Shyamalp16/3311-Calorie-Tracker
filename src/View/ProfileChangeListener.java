package View;

import models.User;
import models.UserSettings;


public interface ProfileChangeListener {
    
    void onProfileUpdated(User updatedUser);
    void onSettingsUpdated(UserSettings updatedSettings);
    void onProfileAndSettingsUpdated(User updatedUser, UserSettings updatedSettings);
    void onUnitSystemChanged(models.UnitSystem oldUnitSystem, models.UnitSystem newUnitSystem);
} 