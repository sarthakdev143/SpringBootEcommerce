package com.example.web_app.controller;

import com.example.web_app.domain.Grain;
import com.example.web_app.domain.User;
import com.example.web_app.model.OrderDTO;
import com.example.web_app.repos.GrainRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final GrainRepository grainRepository;

    public OrderController(final OrderService orderService, final UserRepository userRepository,
            final GrainRepository grainRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.grainRepository = grainRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("buyerValues", userRepository.findAll(Sort.by("userId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getUserId, User::getUserName)));
        model.addAttribute("grainValues", grainRepository.findAll(Sort.by("grainId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Grain::getGrainId, Grain::getGrainName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "order/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("order") final OrderDTO orderDTO) {
        return "order/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("order") @Valid final OrderDTO orderDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "order/add";
        }
        orderService.create(orderDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("order.create.success"));
        return "redirect:/orders";
    }

    @GetMapping("/edit/{orderId}")
    public String edit(@PathVariable(name = "orderId") final Integer orderId, final Model model) {
        model.addAttribute("order", orderService.get(orderId));
        return "order/edit";
    }

    @PostMapping("/edit/{orderId}")
    public String edit(@PathVariable(name = "orderId") final Integer orderId,
            @ModelAttribute("order") @Valid final OrderDTO orderDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "order/edit";
        }
        orderService.update(orderId, orderDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("order.update.success"));
        return "redirect:/orders";
    }

    @PostMapping("/delete/{orderId}")
    public String delete(@PathVariable(name = "orderId") final Integer orderId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = orderService.getReferencedWarning(orderId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            orderService.delete(orderId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("order.delete.success"));
        }
        return "redirect:/orders";
    }

}
