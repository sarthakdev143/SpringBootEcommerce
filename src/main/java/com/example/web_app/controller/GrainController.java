package com.example.web_app.controller;

import com.example.web_app.domain.User;
import com.example.web_app.model.GrainDTO;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.service.GrainService;
import com.example.web_app.util.CustomCollectors;
import com.example.web_app.util.ReferencedWarning;
import com.example.web_app.util.WebUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/grains")
public class GrainController {

    private final GrainService grainService;
    private final UserRepository userRepository;

    public GrainController(final GrainService grainService, final UserRepository userRepository) {
        this.grainService = grainService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("sellerValues", userRepository.findAll(Sort.by("userId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getUserId, User::getUserName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("grains", grainService.findAll());
        return "grain/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("grain") final GrainDTO grainDTO) {
        return "grain/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("grain") @Valid final GrainDTO grainDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "grain/add";
        }
        grainService.create(grainDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("grain.create.success"));
        return "redirect:/grains";
    }

    @GetMapping("/edit/{grainId}")
    public String edit(@PathVariable(name = "grainId") final Integer grainId, final Model model) {
        model.addAttribute("grain", grainService.get(grainId));
        return "grain/edit";
    }

    @PostMapping("/edit/{grainId}")
    public String edit(@PathVariable(name = "grainId") final Integer grainId,
            @ModelAttribute("grain") @Valid final GrainDTO grainDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "grain/edit";
        }
        grainService.update(grainId, grainDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("grain.update.success"));
        return "redirect:/grains";
    }

    @PostMapping("/delete/{grainId}")
    public String delete(@PathVariable(name = "grainId") final Integer grainId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = grainService.getReferencedWarning(grainId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            grainService.delete(grainId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("grain.delete.success"));
        }
        return "redirect:/grains";
    }

}
