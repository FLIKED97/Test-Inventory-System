package com.smartinventory.system.controller;

import com.smartinventory.system.controller.payload.UpdateProductPayload;
import com.smartinventory.system.entity.Product;
import com.smartinventory.system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(("catalogue/products/{productId:\\d+}"))
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId){
        return  this.productService.findProduct(productId).orElseThrow();
    }

    @GetMapping
    public String getProduct(){
        return "catalogue/products/product";
    }
    @GetMapping("edit")
    public String getProductEditPage(){
        return "catalogue/products/edit_product";
    }

    @PostMapping("edit")
    public String updateProduct(@ModelAttribute("product") Product product,
                                 UpdateProductPayload payload){
        this.productService.updateProduct(product.getId(), payload.title(), payload.details());
    return "redirect:/catalogue/products/%d".formatted(product.getId());
    }
    @PostMapping("delete")
    private String deleteProduct(@ModelAttribute("product") Product product){

        this.productService.deleteProduct(product.getId());

        return "redirect:/catalogue/products/list";
    }
}
