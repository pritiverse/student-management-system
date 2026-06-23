package com.example.backend.validation;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    // Indian phone number:
    // - 10 digits, starting with 6-9
    // - optional +91 prefix
    private static final Pattern INDIAN_PHONE_REGEX = Pattern.compile("^(?:\\+?91)?[6-9]\\d{9}$");

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_REGEX.matcher(email.trim()).matches();
    }

    public static boolean isValidIndianPhone(String phone) {
        if (phone == null) return false;
        return INDIAN_PHONE_REGEX.matcher(phone.trim()).matches();
    }

    public static boolean isValidEnrolledYear(Integer enrolledYear) {
        if (enrolledYear == null) return false;
        return enrolledYear >= 1900 && enrolledYear <= 2100;
    }

    public static boolean isValidDob(LocalDate dob) {
        if (dob == null) return false;
        return !dob.isAfter(LocalDate.now());
    }
}

