# NutriSci Calorie Tracker

## Overview

NutriSci Calorie Tracker is a Java-based desktop application designed to help users monitor their daily caloric intake and nutritional information. It provides features for user profile management, meal logging, nutrition analysis, and food swap recommendations. The application uses a MySQL database to store user data and leverages the JFreeChart library for data visualization.

## Features

- **User Authentication:** Secure login and registration for users.
- **Profile Management:** Create, view, and update user profiles with details like gender, age, height, weight, and activity level.
- **Calorie Calculation:** Automatically calculates Basal Metabolic Rate (BMR) and daily calorie needs based on user profile.
- **Meal Logging:** Log daily meals (breakfast, lunch, dinner, snacks) with specific food items and quantities.
- **Nutrition Analysis:** View detailed nutritional breakdowns (calories, protein, carbs, fats, fiber) for each meal.
- **Data Visualization:** Generates charts (pie, bar, line) to visualize nutritional data.
- **Food Swap Engine:** Recommends healthier food alternatives based on user-defined goals (e.g., reduce calories, increase protein).

## Architecture

The application is organized into the following packages:

- **`Model`**: Contains the main application logic and entry point (`Application_initiator.java`).
- **`Graphical_User_Interface`**: Manages all the Swing-based GUI components, including the main window, dialogs, and various views for profile management, meal logging, and visualization.
- **`User_Profile_Management`**: Handles user creation, authentication (`AuthService`), and profile data management (`ProfileManager`, `UserProfile`).
- **`Meal_Logging_Calculation`**: Contains classes for managing food items (`FoodItem`), meals (`Meal`), and the service for logging them (`MealService`).
- **`Nutrition_Analysis_Visualization`**: Responsible for analyzing nutritional data (`NutritionAnalysis`) and generating charts (`ChartGenerator`).
- **`Food_Swap_Engine`**: Implements the logic for finding and recommending food swaps (`SwapRecommendation`).
- **`DatabaseConnector`**: A singleton class that manages the connection to the MySQL database.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server
- JFreeChart library

### Database Setup

1.  Create a MySQL database named `nutrisci_db`.
2.  The application will automatically create the necessary tables on its first run.
3.  Update the database credentials (URL, username, password) in `src/DatabaseConnector/DatabaseConnector.java` if they differ from the defaults.

### Compilation & Execution

1.  **Compile the code:**
    Open a terminal or command prompt in the project's root directory and run the following command:
    ```bash
    javac -d bin -cp "path/to/jfreechart.jar" src/**/*.java
    ```
    *(Replace `"path/to/jfreechart.jar"` with the actual path to your JFreeChart JAR files.)*

2.  **Run the application:**
    ```bash
    java -cp "bin;path/to/jfreechart.jar" Model.Application_initiator
    ```
    *(Adjust the classpath separator (`:`) for Linux/macOS if needed.)*
