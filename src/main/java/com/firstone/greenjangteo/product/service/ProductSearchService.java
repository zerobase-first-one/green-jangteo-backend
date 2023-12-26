package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ProductNameDto;
import com.firstone.greenjangteo.product.domain.document.ProductDocument;
import com.firstone.greenjangteo.product.domain.dto.ImageDto;
import com.firstone.greenjangteo.product.domain.dto.search.ProductSaveAllRequest;
import com.firstone.greenjangteo.product.domain.dto.search.ProductSearchResponse;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import com.firstone.greenjangteo.product.repository.search.ProductSearchRepository;
import com.firstone.greenjangteo.product.repository.search.ProductSearchQueryRepository;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductSearchQueryRepository productSearchQueryRepository;

    private final StoreService storeService;
    private final ProductImageService productImageService;

    @Transactional
    public void saveAllProducts(ProductSaveAllRequest productSaveAllRequest) {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < productSaveAllRequest.getProductSaveRequestList().size(); i++) {
            Store store = storeService.getStore(productSaveAllRequest.getProductSaveRequestList().get(i).getUserId());
            productList.add(Product.of(productSaveAllRequest.getProductSaveRequestList().get(i), store));
            Product product = productRepository.save(productList.get(i));

            List<String> imageList = productSaveAllRequest.getProductSaveRequestList().get(i).getImages();
            for (int j = 0; j < imageList.size(); j++) {
                productImageService.saveProductImage(product, imageList.get(j), j);
            }
        }
    }

    @Transactional
    public void saveAllProductDocument() {
        productSearchRepository.deleteAll();
        List<ProductDocument> productDocumentList = productRepository.findAll().stream().map(ProductDocument::from).collect(Collectors.toList());
        productSearchRepository.saveAll(productDocumentList);
    }


    public List<ProductSearchResponse> findByProductName(String productName, Pageable pageable) {
        List<ProductSearchResponse> productSearchResponses = productSearchRepository.findByName(productName, pageable)
                .stream()
                .map(ProductSearchResponse::from)
                .collect(Collectors.toList());
        for (int i = 0; i < productSearchResponses.size(); i++) {
            List<ImageDto> productImage = productImageService.getProductImages(productSearchResponses.get(i).getId()).stream().map(ImageDto::toImageDto).collect(Collectors.toList());
            productSearchResponses.get(i).setImages(productImage);
        }
        return productSearchResponses;
    }

    public List<ProductNameDto> findByStartWithProductName(String productName) {
        return productSearchQueryRepository.findByStartWithProductName(productName)
                .stream()
                .map(ProductNameDto::of)
                .collect(Collectors.toList());
    }

    public List<ProductSearchResponse> findByCategory(String category, Pageable pageable) {
        List<ProductSearchResponse> productSearchResponses = productSearchQueryRepository.findByCategory(category, pageable)
                .stream()
                .map(ProductSearchResponse::from)
                .collect(Collectors.toList());
        for (ProductSearchResponse productSearchResponse : productSearchResponses) {
            List<ImageDto> productImage = productImageService.getProductImages(productSearchResponse.getId()).stream().map(ImageDto::toImageDto).collect(Collectors.toList());
            productSearchResponse.setImages(productImage);
        }
        return productSearchResponses;
    }
}
