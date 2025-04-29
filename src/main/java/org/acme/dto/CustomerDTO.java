package org.acme.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CustomerDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = -5181308844672324472L;

  private String code;

  private String accountNumber;

  private String names;

  private String surname;

  private String phone;

  private String address;

  private List<ProductDTO> products;

}
