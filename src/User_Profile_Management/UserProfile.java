package User_Profile_Management;


import java.sql.*;
import java.time.LocalDate;


public class UserProfile {
 private int userId;
 private String name;
 private String gender;
 private LocalDate birthDate;
 private double height; // in cm
 private double weight; // in kg
 private String activityLevel;
 private String password;
 
 // Getters and setters
 public int getUserId() { return userId; }
 public void setUserId(int userId) { this.userId = userId; }
 public String getName() { return name; }
 public void setName(String name) { this.name = name; }
 public String getGender() { return gender; }
 public void setGender(String gender) { this.gender = gender; }
 public LocalDate getBirthDate() { return birthDate; }
 public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
 public double getHeight() { return height; }
 public void setHeight(double height) { this.height = height; }
 public double getWeight() { return weight; }
 public void setWeight(double weight) { this.weight = weight; }
 public String getActivityLevel() { return activityLevel; }
 public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
 
 
 
 public String getPassword() {
	return password;
}
 public void setPassword(String password) {
	this.password = password;
 }
 // Calculate BMR using Mifflin-St Jeor Equation
 public double calculateBMR() {
     if (gender == null || birthDate == null || height <= 0 || weight <= 0) {
         return 0;
     }
     
     int age = LocalDate.now().getYear() - birthDate.getYear();
     if (gender.equalsIgnoreCase("male")) {
         return 10 * weight + 6.25 * height - 5 * age + 5;
     } else {
         return 10 * weight + 6.25 * height - 5 * age - 161;
     }
 }
 
 // Calculate daily calorie needs based on activity level
 public double calculateDailyCalories() {
     double bmr = calculateBMR();
     double activityMultiplier = 1.2; // Sedentary default
     
     if (activityLevel != null) {
         switch (activityLevel.toLowerCase()) {
             case "lightly active":
                 activityMultiplier = 1.375;
                 break;
             case "moderately active":
                 activityMultiplier = 1.55;
                 break;
             case "very active":
                 activityMultiplier = 1.725;
                 break;
             case "extra active":
                 activityMultiplier = 1.9;
                 break;
         }
     }
     
     return bmr * activityMultiplier;
 }
 
}

