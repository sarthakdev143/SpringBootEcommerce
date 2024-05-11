package com.example.web_app.controller;

import com.example.web_app.domain.Role;
import com.example.web_app.domain.User;
import com.example.web_app.loginController.Constant;
import com.example.web_app.model.UserDTO;
import com.example.web_app.repos.RoleRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.service.RoleService;
import com.example.web_app.service.UserService;
import com.example.web_app.util.CustomCollectors;
import com.example.web_app.util.ReferencedWarning;
import com.example.web_app.util.WebUtils;
import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(final UserService userService, final RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("roleIdValues", roleRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Role::getId, Role::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("user") final UserDTO userDTO) {
        return "user/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("user") @Valid final UserDTO userDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/add";
        }
        userService.create(userDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.create.success"));
        return "redirect:/users";
    }

    @Autowired
    RoleService roleService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable(name = "userId") final Integer userId, final Model model) {
        model.addAttribute("user", userService.get(userId));
        model.addAttribute("roles", roleService.findAll());
        return "user/edit";
    }

    @PostMapping("/edit/{userId}")
    public String edit(@PathVariable(name = "userId") final Integer userId,
            @ModelAttribute("user") @Valid final UserDTO userDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }
        try {
            User temp = userRepository.findById(userId).get();
            if (!userDTO.getRoleId().isEmpty()) {
                Set<Role> roleSet = userDTO.getRoleId().stream()
                        .map(roleId -> roleRepository.findById(roleId).get())
                        .collect(Collectors.toSet());
                temp.setRoleId(roleSet);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                        WebUtils.getMessage("Role assigned successfully"));

            } else {
                Role roleUser = roleRepository.findById(Constant.ROLE_BUYER)
                        .orElseThrow(() -> new IllegalStateException("User role not found"));

                Set<Role> roles = new HashSet<>();
                roles.add(roleUser);
                temp.setRoleId(roles);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                        WebUtils.getMessage(
                                "Role assigned successfully and Buyer role is given bkz you can't give null role to the user"));

            }
            userRepository.save(temp);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage("Error : ", e.getMessage()));

        }
        return "redirect:/users";
    }

    @PostMapping("/delete/{userId}")
    public String delete(@PathVariable(name = "userId") final Integer userId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(userId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            userService.delete(userId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("user.delete.success"));
        }
        return "redirect:/users";
    }

}
