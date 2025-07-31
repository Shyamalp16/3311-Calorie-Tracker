package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for validating date inputs across the application
 */
public class DateValidator {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Validates a date string for general use (allows past, present, and reasonable future dates)
     * @param dateStr The date string to validate
     * @return ValidationResult containing success status and error message if any
     */
    public static ValidationResult validateDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return new ValidationResult(false, "Date cannot be empty.");
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false); // Strict parsing
        
        try {
            Date parsedDate = sdf.parse(dateStr.trim());
            Date currentDate = new Date();
            
            // Check if date is too far in the future (more than 10 years)
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.YEAR, 10);
            Date maxFutureDate = cal.getTime();
            
            if (parsedDate.after(maxFutureDate)) {
                return new ValidationResult(false, "Date cannot be more than 10 years in the future. Please enter a valid date in YYYY-MM-DD format.");
            }
            
            // Check if date is too far in the past (more than 150 years)
            cal.setTime(currentDate);
            cal.add(Calendar.YEAR, -150);
            Date minPastDate = cal.getTime();
            
            if (parsedDate.before(minPastDate)) {
                return new ValidationResult(false, "Date seems too old. Please enter a valid date in YYYY-MM-DD format.");
            }
            
            return new ValidationResult(true, null, parsedDate);
            
        } catch (ParseException e) {
            return new ValidationResult(false, "Invalid date format. Please enter the date in YYYY-MM-DD format (e.g., 2023-05-15).");
        }
    }
    
    /**
     * Validates a birth date (more restrictive - no future dates allowed)
     * @param dateStr The birth date string to validate
     * @return ValidationResult containing success status and error message if any
     */
    public static ValidationResult validateBirthDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return new ValidationResult(false, "Birth date cannot be empty.");
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);
        
        try {
            Date parsedDate = sdf.parse(dateStr.trim());
            Date currentDate = new Date();
            
            // Birth dates cannot be in the future
            if (parsedDate.after(currentDate)) {
                return new ValidationResult(false, "Birth date cannot be in the future. Please enter a valid birth date in YYYY-MM-DD format.");
            }
            
            // Check if the person would be older than 150 years
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.YEAR, -150);
            Date minDate = cal.getTime();
            
            if (parsedDate.before(minDate)) {
                return new ValidationResult(false, "Birth date seems too old. Please enter a valid birth date in YYYY-MM-DD format.");
            }
            
            return new ValidationResult(true, null, parsedDate);
            
        } catch (ParseException e) {
            return new ValidationResult(false, "Invalid birth date format. Please enter the date in YYYY-MM-DD format (e.g., 1990-05-15).");
        }
    }
    
    /**
     * Result of date validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final Date parsedDate;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this(valid, errorMessage, null);
        }
        
        public ValidationResult(boolean valid, String errorMessage, Date parsedDate) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.parsedDate = parsedDate;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public Date getParsedDate() {
            return parsedDate;
        }
    }
}