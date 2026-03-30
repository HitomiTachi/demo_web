package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    private static final int PAGE_SIZE = 5;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // Lấy đường dẫn thư mục images
    private String getUploadDir() {
        String userDir = System.getProperty("user.dir");
        String separator = File.separator;
        
        // Danh sách các đường dẫn có thể
        String[] possiblePaths = {
            // Nếu đang ở thư mục demo/demo
            userDir + separator + "src" + separator + "main" + separator + "resources" + separator + "static" + separator + "images" + separator,
            // Nếu đang ở thư mục gốc demo_validation
            userDir + separator + "demo" + separator + "demo" + separator + "src" + separator + "main" + separator + "resources" + separator + "static" + separator + "images" + separator,
            // Thử với đường dẫn từ classpath (khi chạy từ IDE)
            userDir + separator + "target" + separator + "classes" + separator + "static" + separator + "images" + separator
        };
        
        // Kiểm tra đường dẫn nào tồn tại
        for (String path : possiblePaths) {
            File dir = new File(path);
            if (dir.exists() || dir.getParentFile().exists()) {
                return path;
            }
        }
        
        // Nếu không tìm thấy, tạo thư mục ở đường dẫn đầu tiên
        String defaultPath = possiblePaths[0];
        File dir = new File(defaultPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return defaultPath;
    }

    @GetMapping
    public String listProducts(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) Integer categoryId,
                               @RequestParam(defaultValue = "asc") String sort) {
        Sort.Direction dir = "desc".equalsIgnoreCase(sort) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Integer filterCat = (categoryId != null && categoryId > 0) ? categoryId : null;
        int filterCategoryId = filterCat != null ? filterCat : 0;

        Page<Product> productPage = productService.getProductsByPage(page, PAGE_SIZE, filterCat, dir);
        model.addAttribute("products", productPage);
        model.addAttribute("searchMode", false);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("filterCategoryId", filterCategoryId);
        model.addAttribute("sort", "desc".equalsIgnoreCase(sort) ? "desc" : "asc");
        return "product/list";
    }

    @GetMapping("/search")
    public String searchProducts(Model model, @RequestParam(value = "key", required = false) String key) {
        List<Product> listProduct = productService.GetSearchProducts(key != null ? key : "");
        model.addAttribute("products", listProduct);
        model.addAttribute("searchMode", true);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("filterCategoryId", 0);
        model.addAttribute("sort", "asc");
        return "product/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/add";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                             BindingResult result,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             @RequestParam("categoryId") Integer categoryId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add";
        }

        // Xử lý upload file
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            // Kiểm tra độ dài tên hình ảnh
            if (fileName.length() > 200) {
                result.rejectValue("image", null, "Tên hình ảnh không quá 200 ký tự");
                model.addAttribute("categories", categoryService.getAllCategories());
                return "products/add";
            }
            try {
                String uploadDir = getUploadDir();
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File saveFile = new File(uploadDir + fileName);
                file.transferTo(saveFile);
                product.setImage(fileName);
            } catch (IOException e) {
                result.rejectValue("image", null, "Lỗi upload hình ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                return "products/add";
            }
        }

        // Set category từ categoryId
        product.setCategory(categoryService.getCategoryById(categoryId));
        
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") int id,
                               @Valid @ModelAttribute("product") Product product,
                               BindingResult result,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               @RequestParam("categoryId") Integer categoryId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/edit";
        }

        // Xử lý upload file
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            // Kiểm tra độ dài tên hình ảnh
            if (fileName.length() > 200) {
                result.rejectValue("image", null, "Tên hình ảnh không quá 200 ký tự");
                model.addAttribute("categories", categoryService.getAllCategories());
                return "products/edit";
            }
            try {
                String uploadDir = getUploadDir();
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File saveFile = new File(uploadDir + fileName);
                file.transferTo(saveFile);
                product.setImage(fileName);
            } catch (IOException e) {
                result.rejectValue("image", null, "Lỗi upload hình ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                return "products/edit";
            }
        }

        // Set category từ categoryId
        product.setCategory(categoryService.getCategoryById(categoryId));
        
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        return "redirect:/products";
    }
}
