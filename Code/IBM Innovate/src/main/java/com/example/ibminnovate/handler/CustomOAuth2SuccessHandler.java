package com.example.ibminnovate.handler;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.utility.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Extract user details
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Fetch or create the user in the database
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setProvider("google");
                    newUser.setProviderId(oauth2User.getAttribute("sub")); // Google ID
                    newUser.setRole("USER");
                    newUser.setEnabled(true);
                    newUser.setUserType("google"); // Set userType to "google"
                    return userRepository.save(newUser);
                });



        // Generate a JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Prepare the response data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userId", user.getID());
        responseData.put("email", user.getEmail());
        responseData.put("username", user.getUsername());
        responseData.put("userType", user.getUserType());

        // Redirect to /dashboard with the token and user details as query parameters
        String redirectUrl = "/dashboard?token=" + token +
                "&userId=" + user.getID() +
                "&email=" + user.getEmail() +
                "&username=" + user.getUsername() +
                "&userType=" + user.getUserType();

        // Perform the redirect
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}