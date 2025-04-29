package org.acme.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.dto.CustomerDTO;
import org.acme.repository.CustomerRepository;
import org.acme.repository.CustomerView;

import java.util.List;

@Slf4j
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

	@Inject
	CustomerRepository customerRepository;

	@GET
	public List<CustomerView> getAllCustomer() {
		log.info("Getting all customers");
		return this.customerRepository.listAllCustomer();
	}

	@POST
	public Response createCustomer(CustomerDTO customer) {
		log.info("Creating customer {}  ", customer);
		this.customerRepository.create(customer);
		return Response.ok().build();
	}

}
