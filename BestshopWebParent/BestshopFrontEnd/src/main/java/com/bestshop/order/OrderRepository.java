package com.bestshop.order;

import com.bestshop.common.entity.Customer;
import com.bestshop.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o LEFT JOIN o.orderDetails od WHERE o.customer.id = ?2 " +
            "AND (o.id LIKE CONCAT('%', ?1, '%') OR od.product.name LIKE CONCAT('%', ?1, '%'))")
    public Page<Order> findAll(String keyword, Integer customerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
    public Page<Order> findAll(Integer customerId, Pageable pageable);

    public Order findByIdAndCustomer(Integer id, Customer customer);

}