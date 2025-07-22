package models;
import java.sql.Date;
import java.time.LocalDate;

//ok so the way that the work flow goes is that you you have UI -> creates user object -> calls of user data access operation to create the user in data (and also generate an id)
public class User {
    private int userId;
    private String name;
    private String gender;
    private Date birthDate;
    private double height;
    private double weight;
    private String activityLevel;
    private java.sql.Timestamp createdAt;
    
    // Default constructor
    public User() {}
    
    // Constructor with parameters
    public User(String name, String gender, Date birthDate, 
                double height, double weight, String activityLevel) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
    }    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date date) { this.birthDate = date; }
    
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return name; // This is what shows in the dropdown
    }
}
