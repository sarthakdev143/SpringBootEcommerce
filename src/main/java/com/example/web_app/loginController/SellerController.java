package com.example.web_app.loginController;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.web_app.domain.Product;
import com.example.web_app.domain.User;
import com.example.web_app.model.ProductDTO;
import com.example.web_app.repos.CategoryRepository;
import com.example.web_app.repos.ProductRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.service.CategoryService;
import com.example.web_app.service.OrderService;
import com.example.web_app.service.ProductService;

import com.example.web_app.util.WebUtils;

import java.util.List;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryService categoryService;

    @GetMapping
    public String getFarmer() {
        return "Pages/product";
    }

    @GetMapping("/orders")
    public String getFarmers(Model model) {
        try {
            model.addAttribute("orders", orderService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Pages/productOrders";
    }

    @GetMapping("/product/upload")
    public String getFarmers1(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByEmail(username).get();

            List<ProductDTO> products = productRepository.findByUser(user).stream()
                    .map(x -> productService.mapToDTO(x, new ProductDTO())).collect(Collectors.toList());
            model.addAttribute("obj", new ProductDTO());
            model.addAttribute("products", products);
            model.addAttribute("categories", categoryService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Pages/productUpload";
    }

    @PostMapping("/product/upload/submit")
    public String submitForm(@ModelAttribute("obj") ProductDTO productDto,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            final RedirectAttributes redirectAttributes)
            throws IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByEmail(username).get();

            Product product = new Product();
            product.setDescription(productDto.getDescription());
            product.setName(productDto.getName());
            product.setPrice(productDto.getPrice());
            product.setQuantity(productDto.getQuantity());
            product.setUser(user);
            product.setCategory(categoryRepository.findById(productDto.getCategory()).get());

            if (!thumbnail.isEmpty()) {
                product.setThumbnail(thumbnail.getBytes());
            }

            productRepository.save(product);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                    WebUtils.getMessage("Product added successfully"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(e.getMessage()));
        }
        return "redirect:/seller/product/upload";
    }

}
