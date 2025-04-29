package org.acme.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Data
@Getter
@Setter
public class ProductDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 3843475235211926833L;
	private Long id;

	private Long product;

	private String name;

	private String code;

	private String description;
}
