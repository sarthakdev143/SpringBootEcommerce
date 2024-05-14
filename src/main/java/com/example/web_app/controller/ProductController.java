package com.example.web_app.controller;

import com.example.web_app.domain.Category;
import com.example.web_app.domain.User;
import com.example.web_app.model.ProductDTO;
import com.example.web_app.repos.CategoryRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.service.ProductService;
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
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(final ProductService productService,
            final UserRepository userRepository, final CategoryRepository categoryRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("userId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getUserId, User::getUserName)));
        model.addAttribute("categoryValues",
                categoryRepository.findAll(Sort.by("id"))
                        .stream()
                        .collect(CustomCollectors.toSortedMap(Category::getId, Category::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("product") final ProductDTO productDTO) {
        return "product/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("product") @Valid final ProductDTO productDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "product/add";
        }
        productService.create(productDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("product.create.success"));
        return "redirect:/products";
    }

    @GetMapping("/edit/{productId}")
    public String edit(@PathVariable(name = "productId") final Integer productId,
            final Model model) {
        model.addAttribute("product", productService.get(productId));
        return "product/edit";
    }

    @PostMapping("/edit/{productId}")
    public String edit(@PathVariable(name = "productId") final Integer productId,
            @ModelAttribute("product") @Valid final ProductDTO productDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "product/edit";
        }
        productService.update(productId, productDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("product.update.success"));
        return "redirect:/products";
    }

    @PostMapping("/delete/{productId}")
    public String delete(@PathVariable(name = "productId") final Integer productId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = productService.getReferencedWarning(productId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(),
                            referencedWarning.getParams().toArray()));
        } else {
            productService.delete(productId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("product.delete.success"));
        }
        return "redirect:/products";
    }

}
