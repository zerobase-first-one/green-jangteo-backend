package com.firstone.greenjangteo.product.repository.search;

import com.firstone.greenjangteo.product.domain.document.ProductDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument,Long> {

    List<ProductDocument> findByName(String productName, Pageable pageable);
}