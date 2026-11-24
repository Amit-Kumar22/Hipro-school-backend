package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.entities.AccessAttribute;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.services.AccessAttributeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Access Attribute APIs", description = "APIs for managing access attributes")
@RestController
@RequestMapping("/v1/access-attribute")
public class AccessAttributeController {

	@Autowired
	private AccessAttributeService accessAttributeService;

	@Operation(summary = "Get all access attributes")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved access attributes") })
	@GetMapping
	public ResponseEntity<List<AccessAttribute>> getAllAttributes() {
		log.info(">> getAllAttributes()");
		return ResponseEntity.ok(accessAttributeService.getAllAttributes());
	}

	@Operation(summary = "Get access attribute by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Access attribute found"),
			@ApiResponse(responseCode = "404", description = "Access attribute not found") })
	@GetMapping("/{attributeId}")
	public ResponseEntity<AccessAttribute> getAttributeById(@PathVariable Long attributeId) {
		log.info(">> getAttributeById({})", attributeId);
		return ResponseEntity.ok(accessAttributeService.getAttributeById(attributeId));
	}

	@Operation(summary = "Add a new access attribute")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Access attribute created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request data") })
	@PostMapping("/add")
	public ResponseEntity<Void> addAttribute(@Valid @RequestBody AccessAttribute req) {
		log.info(">> addAttribute({})", req);
		accessAttributeService.addAttribute(req);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "Update an existing access attribute")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Access attribute updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request data"),
			@ApiResponse(responseCode = "404", description = "Access attribute not found") })
	@PutMapping("/update/{attributeId}")
	public ResponseEntity<Void> updateAttribute(@PathVariable Long attributeId,
			@Valid @RequestBody AccessAttribute req) {
		log.info(">> updateAttribute({}, {})", attributeId, req);
		accessAttributeService.updateAttribute(attributeId, req);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(summary = "Delete an access attribute by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Access attribute deleted successfully"),
			@ApiResponse(responseCode = "409", description = "Cannot delete access attribute due to constraint"),
			@ApiResponse(responseCode = "404", description = "Access attribute not found") })
	@DeleteMapping("/delete/{attributeId}")
	public ResponseEntity<Void> deleteAttribute(@PathVariable Long attributeId) {
		log.info(">> deleteAttribute({})", attributeId);
		try {
			accessAttributeService.deleteAttribute(attributeId);
		} catch (Exception e) {
			throw new ServiceException("Please delete all dependent permissions before deleting this attribute.");
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
