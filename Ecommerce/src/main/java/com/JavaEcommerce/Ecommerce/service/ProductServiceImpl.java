package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.exception.ApiException;
import com.JavaEcommerce.Ecommerce.exception.ResourceNotFoundException;
import com.JavaEcommerce.Ecommerce.model.Category;
import com.JavaEcommerce.Ecommerce.model.Product;
import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import com.JavaEcommerce.Ecommerce.payload.ProductResponse;
import com.JavaEcommerce.Ecommerce.repo.CategoryRepository;
import com.JavaEcommerce.Ecommerce.repo.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    FileService fileService;

    @Value("{project-image}")
    private String path;
    @Override
    public ProductDto addprduct(Long categoryId, ProductDto productDto) {

        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        boolean isProductNotPresent=true;
        List<Product> products=category.getProduct();
        for(Product value : products){
            if(value.getProductName().equals(productDto.getProductName()));
            isProductNotPresent=false;
            break;
        }
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDto, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepo.save(product);
            return modelMapper.map(savedProduct, ProductDto.class);
        }else {
            throw new ApiException("Product all Ready exists!!");
        }
    }

    @Override
    public ProductResponse getallProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        //sort and pagination of products
        Sort sortByAndOrder= (sortDir.equalsIgnoreCase("asc"))? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Product> pageProducts= productRepo.findAll(pageable);
        List<Product> products=pageProducts.getContent();
      // List<Product>products= productRepo.findAll();
       List<ProductDto> productDtoList=products.stream()
               .map(product -> modelMapper.map(product,ProductDto.class))
               .toList();
       ProductResponse productResponse=new ProductResponse();
         productResponse.setContent(productDtoList);
         productResponse.setPageNumber(pageProducts.getNumber());
         productResponse.setPageSize(pageProducts.getSize());
         productResponse.setTotalElements(pageProducts.getTotalElements());
         productResponse.setTotalPages((long) pageProducts.getTotalPages());
         productResponse.setLastPage(pageProducts.isLast());
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
    public ProductDto updateproductImage(Long productid, MultipartFile file) throws IOException {
        Product productsFromDb=productRepo.findById(productid)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productid));
        String fileName=fileService.uploadImage(path,file);
        productsFromDb.setImage(fileName);
        Product updatedproduct= productRepo.save(productsFromDb);
        return modelMapper.map(updatedproduct,ProductDto.class);
    }
}
