package com.example.web_app.controller;

import com.example.web_app.domain.Order;
import com.example.web_app.model.ReviewDTO;
import com.example.web_app.repos.OrderRepository;
import com.example.web_app.service.ReviewService;
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
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final OrderRepository orderRepository;

    public ReviewController(final ReviewService reviewService,
            final OrderRepository orderRepository) {
        this.reviewService = reviewService;
        this.orderRepository = orderRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("orderValues", orderRepository.findAll(Sort.by("orderId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Order::getOrderId, Order::getOrderId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("reviews", reviewService.findAll());
        return "review/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("review") final ReviewDTO reviewDTO) {
        return "review/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("review") @Valid final ReviewDTO reviewDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "review/add";
        }
        reviewService.create(reviewDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("review.create.success"));
        return "redirect:/reviews";
    }

    @GetMapping("/edit/{reviewId}")
    public String edit(@PathVariable(name = "reviewId") final Integer reviewId, final Model model) {
        model.addAttribute("review", reviewService.get(reviewId));
        return "review/edit";
    }

    @PostMapping("/edit/{reviewId}")
    public String edit(@PathVariable(name = "reviewId") final Integer reviewId,
            @ModelAttribute("review") @Valid final ReviewDTO reviewDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "review/edit";
        }
        reviewService.update(reviewId, reviewDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("review.update.success"));
        return "redirect:/reviews";
    }

    @PostMapping("/delete/{reviewId}")
    public String delete(@PathVariable(name = "reviewId") final Integer reviewId,
            final RedirectAttributes redirectAttributes) {
        reviewService.delete(reviewId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("review.delete.success"));
        return "redirect:/reviews";
    }

}
