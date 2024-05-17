package com.example.web_app.loginController;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.web_app.domain.Role;
import com.example.web_app.domain.User;
import com.example.web_app.model.UserDTO;
import com.example.web_app.repos.RoleRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.util.WebUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository rolesRepository;

    @GetMapping("/login")
    public String login() {
        return "Pages/SignIn";
    }

    @GetMapping("/unAuthorized")
    public String unAuthorized() {
        return "Pages/unAuthorized";
    }

    @GetMapping("/signup")
    public String register(Model model) {
        UserDTO userDTO = new UserDTO();
        userDTO.setRoleIdCount(Constant.ROLE_SELLER);
        model.addAttribute("obj", userDTO);
        return "Pages/SingUp";
    }

    @PostMapping("/signup")
    public String registration(@Valid @ModelAttribute("obj") UserDTO userDTO,
            final RedirectAttributes redirectAttributes, BindingResult result, Model model) {
        if (userAlreadyRegisteredByEmail(userDTO.getEmail(), result)
                || userAlreadyRegisteredByUsername(userDTO.getUserName(), result)) {
            return "Pages/SingUp";
        }

        Optional<Role> role = rolesRepository
                .findById((userDTO.getRoleIdCount() != null) ? userDTO.getRoleIdCount() : Constant.ROLE_SELLER);

        Set<Role> roles = new HashSet<>();
        if (role.isPresent()) {
            roles.add(role.get());
        } else {
            Role admin = new Role();
            admin.setId(Constant.ROLE_ADMIN);
            admin.setName("ROLE_ADMIN");

            Role ROLE_BUYER = new Role();
            ROLE_BUYER.setId(Constant.ROLE_BUYER);
            ROLE_BUYER.setName("ROLE_BUYER");

            Role ROLE_SELLER = new Role();
            ROLE_SELLER.setId(Constant.ROLE_SELLER);
            ROLE_SELLER.setName("ROLE_SELLER");
            rolesRepository.saveAll(Arrays.asList(admin, ROLE_BUYER, ROLE_SELLER));
            roles.add(admin);
            roles.add(ROLE_SELLER);
            roles.add(ROLE_BUYER);

        }
        User user = new User();
        try {
            user.setUserName(userDTO.getUserName());
            user.setEmail(userDTO.getEmail());
            user.setCreatedAt(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setRoleId(roles);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                    WebUtils.getMessage("Registration successfully!"));
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage("Registration failed. Please try again."));
        }
        return "redirect:/login";

    }

    private boolean userAlreadyRegisteredByEmail(String email, BindingResult result) {
        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            result.rejectValue("email", null, "Email already taken!");
            return true;
        }
        return false;
    }

    private boolean userAlreadyRegisteredByUsername(String userName, BindingResult result) {
        Optional<User> existingUserByUsername = userRepository.findByUserName(userName);
        if (existingUserByUsername.isPresent()) {
            result.rejectValue("userName", null, "User Name already taken!");
            return true;
        }
        return false;
    }
}
