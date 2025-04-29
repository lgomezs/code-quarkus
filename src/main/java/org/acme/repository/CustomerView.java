package org.acme.repository;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import org.acme.entity.Customer;

@EntityView(Customer.class)
public interface CustomerView {

  @IdMapping
  Long getId();

  String getCode();

  String getAccountNumber();

  String getNames();

  String getSurname();

  String getPhone();

  String getAddress();

}
