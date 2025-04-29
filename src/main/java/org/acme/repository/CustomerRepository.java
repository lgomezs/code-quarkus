package org.acme.repository;

import java.util.ArrayList;
import java.util.List;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.acme.dto.CustomerDTO;
import org.acme.entity.Customer;
import org.acme.entity.Product;

@Slf4j
@ApplicationScoped
public class CustomerRepository {

  @Inject
  EntityManager em;

  @Inject
  CriteriaBuilderFactory cbf;

  @Inject
  EntityViewManager evm;

  public List<CustomerView> listAllCustomer() {
    log.info("Getting all customers");
    final CriteriaBuilder<Customer> cb = this.cbf.create(this.em, Customer.class);
    return this.evm.applySetting(EntityViewSetting.create(CustomerView.class), cb).getResultList();
  }

  @Transactional
  public void create(CustomerDTO customerRequest) {
    log.info("create customer");
    final Customer customer1 = new Customer();
    customer1.setCode(customerRequest.getCode());
    customer1.setPhone(customerRequest.getPhone());
    customer1.setNames(customerRequest.getNames());
    customer1.setPhone(customerRequest.getPhone());
    customer1.setAccountNumber(customerRequest.getAccountNumber());

    if (customerRequest.getProducts() != null) {
      final List<Product> productList = new ArrayList<>();
      customerRequest.getProducts().forEach(productDTO -> {
        final Product product = new Product();
        product.setProduct(productDTO.getProduct());
        product.setCustomer(customer1);
        productList.add(product);
      });

      customer1.setProducts(productList);
    }
    this.em.persist(customer1);
  }
}
