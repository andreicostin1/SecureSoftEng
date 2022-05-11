package service.vaxapp.validator;

import service.vaxapp.service.UserService;
import service.vaxapp.model.User;
import service.vaxapp.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        // ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");

        if ((user.getEmail().length() < 6 || user.getEmail().length() > 64) ||
                (!isUserValid(user.getEmail())) ||
                (userService.findByEmail(user.getEmail()) != null) ||
                (userService.findByPPS(user.getPPS()) != null) ||
                (userService.isUserUnderage(user.getDateOfBirth())) ||
                (!user.getPasswordConfirm().equals(user.getPassword())) ||
                (!isStrong(user.getPassword())))
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
    }

    private boolean isUserValid(String email) {
        final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    private static boolean isStrong(String password) {
        final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,32})";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();

        // (?=.*[a-z]) The string must contain at least 1 lowercase alphabetical
        // character
        // (?=.*[A-Z]) The string must contain at least 1 uppercase alphabetical
        // character
        // (?=.*[0-9]) The string must contain at least 1 numeric character
        // (?=.*[!@#$%^&*]) The string must contain at least one special character, but
        // we are escaping reserved RegEx characters to avoid conflict
        // (?=.{8,}) The string must be eight characters or longer
        // ((?=.*[a-z])(?=.*\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})
    }
}
