package org.acme.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.dto.CustomerDTO;
import org.acme.mapper.OrderMapper;
import org.acme.repository.CustomerRepository;
import org.acme.repository.CustomerView;
import org.openapi.quarkus.openapi_yml.api.CustomerApi;
import org.openapi.quarkus.openapi_yml.model.CustomerRequest;

import java.util.List;

@Slf4j
public class CustomerResource implements CustomerApi {

	@Inject
	CustomerRepository customerRepository;

	@Inject
	OrderMapper orderMapper;

	@Override
	public Response createCustomer(CustomerRequest customer) {
		log.info("Creating customer {}  ", customer);
		final CustomerDTO customerDTO = this.orderMapper.asCustomerDTO(customer);
		this.customerRepository.create(customerDTO);
		return Response.ok().build();
	}

	@Override
	public List<CustomerRequest> findAllCustomers() {
		log.info("Getting all customers");
		final List<CustomerView> customerViewList = this.customerRepository.listAllCustomer();
		return this.orderMapper.asCustomerRequest(customerViewList);
	}
}
