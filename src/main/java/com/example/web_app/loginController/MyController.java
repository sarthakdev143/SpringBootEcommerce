package com.example.web_app.loginController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
public class MyController {

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

    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    @GetMapping({ "/seller", "/sellers", "products", "/products_dashboard", "/product" })
    public String getFarmer() {
        return "Pages/product";
    }

    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    @GetMapping("/product/orders")
    public String getFarmers(Model model) {
        try {
            model.addAttribute("orders", orderService.findAll());
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
        return "Pages/productOrders";
    }

    @PreAuthorize("hasAuthority('ROLE_SELLER')")
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
            // Handle exception
            e.printStackTrace();
        }
        return "Pages/productUpload";
    }

    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    @PostMapping("/product/upload/submit")
    public String submitForm(@ModelAttribute("obj") ProductDTO productDto,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("fieldImages") MultipartFile[] fieldImages, final RedirectAttributes redirectAttributes)
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
            if (fieldImages != null) {
                Set<byte[]> x = new HashSet<byte[]>();
                for (MultipartFile a : fieldImages) {
                    x.add(a.getBytes());
                }
                product.setFieldIamgs(x);
            }
            productRepository.save(product);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                    WebUtils.getMessage("Product added successfully"));
        } catch (Exception e) {
            // Handle exception
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(e.getMessage()));
        }
        return "redirect:/product/upload";
    }



    @GetMapping("/admin")
    public String getAdmin() {
        return "Pages/admin";
    }

    

}
