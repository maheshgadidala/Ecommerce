package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.exception.ResourceNotFoundException;
import com.JavaEcommerce.Ecommerce.model.Category;
import com.JavaEcommerce.Ecommerce.model.Product;
import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import com.JavaEcommerce.Ecommerce.payload.ProductResponse;
import com.JavaEcommerce.Ecommerce.repo.CategoryRepository;
import com.JavaEcommerce.Ecommerce.repo.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;
    @Override
    public ProductDto addprduct(Long categoryId, ProductDto productDto) {

        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
        Product product=modelMapper.map(productDto,Product.class);
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct=productRepo.save(product);

        return modelMapper.map(savedProduct,ProductDto.class);
    }

    @Override
    public ProductResponse getallProducts() {
       List<Product>products= productRepo.findAll();
       List<ProductDto> productDtoList=products.stream()
               .map(product -> modelMapper.map(product,ProductDto.class))
               .toList();
       ProductResponse productResponse=new ProductResponse();
       productResponse.setContent(productDtoList);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        List<Product> products=productRepo.findByCategoryOrderByPriceAsc(category);
        List<ProductDto> productDtoList=products.stream()
                .map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDtoList);
        return productResponse;

    }

    @Override
    public ProductResponse searchProductKeyword(String keyword) {
        List<Product> products=productRepo.findByProductNameLikeIgnoreCase("%"+keyword+"%");
        List<ProductDto> productDtoList=products.stream()
                .map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDtoList);
        return productResponse;
    }

    @Override
    public ProductDto updadateProduct(ProductDto productDto, Long productId) {
        Product productsFromDb=productRepo.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        Product product=modelMapper.map(productDto,Product.class);
        productsFromDb.setProductName(product.getProductName());
        productsFromDb.setProductDesc(product.getProductDesc());
        productsFromDb.setQuantity(product.getQuantity());
        productsFromDb.setPrice(product.getPrice());
        productsFromDb.setDiscount(product.getDiscount());
        productsFromDb.setSpecialPrice(product.getSpecialPrice());

        Product savedPrducts=productRepo.save(productsFromDb);
        return modelMapper.map(savedPrducts,ProductDto.class);
    }

    public ProductDto deleteProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProdcutId" ,productId));
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        productRepo.delete(product);
        return productDto;
    }

    @Override
    public ProductDto updateproductImage(Long productid, MultipartFile image) {
        Product productsFromDb=productRepo.findById(productid)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productid));
        String path=("Images/");
        String fileName=uploadImage(path,image);
        productsFromDb.setImage(fileName);
        Product updatedproduct= productRepo.save(productsFromDb);
        return modelMapper.map(updatedproduct,ProductDto.class);
    }

    private String uploadImage(String path, MultipartFile image) {
        return null;
    }
}
