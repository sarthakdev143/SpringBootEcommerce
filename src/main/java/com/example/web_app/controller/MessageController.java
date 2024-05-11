package com.example.web_app.controller;

import com.example.web_app.domain.User;
import com.example.web_app.model.MessageDTO;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.service.MessageService;
import com.example.web_app.util.CustomCollectors;
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
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(final MessageService messageService,
            final UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("senderValues", userRepository.findAll(Sort.by("userId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getUserId, User::getUserName)));
        model.addAttribute("receiverValues", userRepository.findAll(Sort.by("userId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getUserId, User::getUserName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("messages", messageService.findAll());
        return "message/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("message") final MessageDTO messageDTO) {
        return "message/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("message") @Valid final MessageDTO messageDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "message/add";
        }
        messageService.create(messageDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("message.create.success"));
        return "redirect:/messages";
    }

    @GetMapping("/edit/{messageId}")
    public String edit(@PathVariable(name = "messageId") final Integer messageId,
            final Model model) {
        model.addAttribute("message", messageService.get(messageId));
        return "message/edit";
    }

    @PostMapping("/edit/{messageId}")
    public String edit(@PathVariable(name = "messageId") final Integer messageId,
            @ModelAttribute("message") @Valid final MessageDTO messageDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "message/edit";
        }
        messageService.update(messageId, messageDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("message.update.success"));
        return "redirect:/messages";
    }

    @PostMapping("/delete/{messageId}")
    public String delete(@PathVariable(name = "messageId") final Integer messageId,
            final RedirectAttributes redirectAttributes) {
        messageService.delete(messageId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("message.delete.success"));
        return "redirect:/messages";
    }

}
