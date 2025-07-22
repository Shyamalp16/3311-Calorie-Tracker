# NutriSci: SwEATch to Better!

NutriSci is a desktop application designed to help users track their meals, analyze their nutritional intake, and receive smart food recommendations to achieve their health goals. The application is built using Java Swing and leverages the Canadian Nutrient File for accurate nutritional data.

## Features

*   **User Profiles:** Create and manage personal profiles with details like gender, age, height, weight, and activity level.
*   **Meal Tracking:** Log daily meals to keep a record of food intake.
*   **Nutritional Analysis:** Get a detailed breakdown of the nutritional content of your meals.
*   **Food Recommendations:** Receive intelligent food swap suggestions to better meet your nutritional goals.
*   **Dashboard:** A personalized dashboard to visualize your progress and nutritional data.

## Project Structure

The project is organized into the following packages:

*   `main`: Contains the main application entry point.
*   `models`: Defines the data models for the application (e.g., `User`, `Food`, `Meal`).
*   `gui`: Contains all the user interface components, built with Java Swing.
*   `Database`: Handles all interactions with the database, including data access objects (DAOs).
*   `logic`: Implements the core business logic of the application, such as nutrient calculation and food swap recommendations.
*   `charts`: Intended for displaying nutritional data in charts and graphs.

## How to Run

1.  **Database Setup:**
    *   Create a database (e.g., in MySQL or PostgreSQL).
    *   Update the `src/config.properties` file with your database connection details (URL, username, password).
    *   Create the necessary tables in your database. The schema can be inferred from the `UserDAO` class.

2.  **Compile and Run:**
    *   Compile the Java source files.
    *   Run the `main.Main` class to start the application.

## Dependencies

*   Java Development Kit (JDK)
*   Java Swing (part of the JDK)
*   JDBC Driver for your chosen database
*   JFreeChart (for charts and graphs)
