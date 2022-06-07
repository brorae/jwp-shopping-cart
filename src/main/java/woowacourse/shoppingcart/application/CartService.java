package woowacourse.shoppingcart.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.dao.CartItemDao;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.Cart;
import woowacourse.shoppingcart.domain.Username;
import woowacourse.shoppingcart.dto.CartIdRequest;
import woowacourse.shoppingcart.dto.CartRequest;
import woowacourse.shoppingcart.dto.CartResponse;
import woowacourse.shoppingcart.dto.CartResponses;
import woowacourse.shoppingcart.dto.DeleteProductRequest;
import woowacourse.shoppingcart.dto.UpdateCartRequest;
import woowacourse.shoppingcart.dto.UpdateCartRequests;
import woowacourse.shoppingcart.exception.InvalidProductException;

@Service
@Transactional(rollbackFor = Exception.class)
public class CartService {

    private final CartItemDao cartItemDao;
    private final CustomerDao customerDao;

    public CartService(final CartItemDao cartItemDao, final CustomerDao customerDao) {
        this.cartItemDao = cartItemDao;
        this.customerDao = customerDao;
    }

    public Long addCart(String username, CartRequest cartRequest) {
        Long customerId = customerDao.findByUsername(new Username(username)).getId();
        try {
            return cartItemDao.addCartItem(customerId, cartRequest.getProductId(),
                    cartRequest.getQuantity(), cartRequest.getChecked());
        } catch (Exception e) {
            throw new InvalidProductException();
        }
    }

    public CartResponses findCartsByUsername(String username) {
        List<Cart> carts = findCartIdsByUsername(username);
        List<CartResponse> cartResponses = carts.stream()
                .map(CartResponse::from)
                .collect(Collectors.toList());
        return new CartResponses(cartResponses);
    }

    private List<Cart> findCartIdsByUsername(String username) {
        Long customerId = customerDao.findByUsername(new Username(username)).getId();
        return cartItemDao.findByCustomerId(customerId);
    }

    public CartResponses updateCartItems(String username, UpdateCartRequests updateCartRequests) {
        List<UpdateCartRequest> products = updateCartRequests.getProducts();
        List<Cart> carts = products.stream()
                .map(updateCartRequest -> updateCartRequest.toCart())
                .collect(Collectors.toList());
        cartItemDao.updateCartItem(carts);
        List<Cart> foundCarts = findCartIdsByUsername(username);
        List<CartResponse> cartResponses = foundCarts.stream()
                .map(CartResponse::from)
                .collect(Collectors.toList());
        return new CartResponses(cartResponses);
    }

    public void deleteCart(DeleteProductRequest deleteProductRequest) {
        List<Long> cartIds = deleteProductRequest.getProducts().stream()
                .map(CartIdRequest::getId)
                .collect(Collectors.toList());
        cartItemDao.deleteCartItem(cartIds);
    }

    public void deleteAllCart(String username) {
        Long customerId = customerDao.findByUsername(new Username(username)).getId();
        cartItemDao.deleteAllCartItem(customerId);
    }
}
