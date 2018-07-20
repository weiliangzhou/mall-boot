package com.zwl.service;

import com.zwl.model.po.Product;
import com.zwl.model.vo.BuyResult;

import java.util.List;

/**
 * @author 二师兄超级帅
 * @Title: ProductService
 * @ProjectName parent
 * @Description: TODO
 * @date 2018/7/515:04
 */
public interface ProductService {

    List<Product> getProductList(String merchantId);

    void updateProduct(Product product);

    BuyResult buy(Product product);

    List<Product> getAdminProductList();
}
