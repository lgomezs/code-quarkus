package org.acme.mapper;

import org.acme.dto.CustomerDTO;
import org.acme.repository.CustomerView;
import org.mapstruct.Mapper;
import org.openapi.quarkus.openapi_yml.model.CustomerRequest;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface OrderMapper {

	CustomerDTO asCustomerDTO(CustomerRequest customerRequest);

	List<CustomerRequest> asCustomerRequest(List<CustomerView> customerDTO);
}
