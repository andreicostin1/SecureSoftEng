package service.vaxapp;
import java.io.IOException;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import service.vaxapp.model.User;
import service.vaxapp.service.UserService;
 
@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
 
    @Autowired
    private UserService userService;
     
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails =  (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        System.out.println("success");

        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user);
        }
        
        super.setDefaultTargetUrl("/login");
        super.onAuthenticationSuccess(request, response, authentication);
    }
     
}