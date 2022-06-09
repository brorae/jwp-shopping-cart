package woowacourse.shoppingcart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.dao.ProductDao;
import woowacourse.shoppingcart.domain.Product;
import woowacourse.shoppingcart.domain.Products;
import woowacourse.shoppingcart.dto.ProductRequest;
import woowacourse.shoppingcart.dto.ProductResponse;
import woowacourse.shoppingcart.dto.ProductResponses;

@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long addProduct(ProductRequest productRequest) {
        Product product = productDao.save(productRequest.toProduct());
        return product.getId();
    }

    public ProductResponses findProducts(int size, int page) {
        Products products = productDao.findProducts();
        Products productsOfPage = products.getProductsOfPage(size, page);
        return ProductResponses.from(productsOfPage);
    }

    public ProductResponse findProductById(Long productId) {
        Product product = productDao.findProductById(productId);
        return ProductResponse.from(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProductById(Long productId) {
        productDao.delete(productId);
    }
}
