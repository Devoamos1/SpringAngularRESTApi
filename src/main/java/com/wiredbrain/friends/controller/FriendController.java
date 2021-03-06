package com.wiredbrain.friends.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wiredbrain.friends.model.Friend;
import com.wiredbrain.friends.services.FriendService;
import com.wiredbrain.friends.util.ErrorMessage;
import com.wiredbrain.friends.util.FieldErrorMessage;

import org.springframework.web.bind.annotation.CrossOrigin;



//Create a rest controller
@RestController
public class FriendController {
	
	@Autowired
	FriendService friendService;
	
	//Generate REST CRUD operation Methods 
	
	//GET
	public
	@GetMapping("/friend")
	@CrossOrigin(origins = "http://localhost:4200")
	Iterable<Friend> read(){
		return friendService.findAll();
	}
	
	public
	@PostMapping("/friend")
	Friend create (@Valid @RequestBody Friend friend) {
			return friendService.save(friend);
	
	}
    
	public
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	List<FieldErrorMessage>exceptionHandler(MethodArgumentNotValidException e){
	List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
	List<FieldErrorMessage> fieldErrorMessages = fieldErrors.stream().map(fieldError -> new FieldErrorMessage(fieldError.getField(), fieldError.getDefaultMessage())).collect(Collectors.toList());
	
		return fieldErrorMessages;
	}

	
	//Update only existing friends (Local method)
	@PutMapping("/friend")
	ResponseEntity <Friend> update(@RequestBody Friend friend) {
		if(friendService.findById(friend.getId()).isPresent()) {
			return new ResponseEntity(friendService.save(friend), HttpStatus.OK);
		}else {
			return new ResponseEntity(friendService.save(friend), HttpStatus.BAD_REQUEST);
		}
	}
	
	public
	@DeleteMapping("/friend/{id}")
	void delete(@PathVariable Integer id) {
		friendService.deleteById(id);
	}
	
	public
	@GetMapping("/friend/{id}")
		Optional<Friend> findById(@PathVariable Integer id){
			return friendService.findById(id);
		
	}
	
	public
	@GetMapping("/friend/search")
	Iterable<Friend> findByQuery(
	@RequestParam(value = "first", required = false) String firstName, @RequestParam(value = "last", required = false) String lastName)
	{
		if(firstName != null && lastName !=null) {
			return friendService.findByFirstNameAndLastName(firstName, lastName);
		}
		else if(firstName!= null) {
			return friendService.findByFirstName(firstName);
		}
			
		else if(lastName!=null) {
			return friendService.findByLastName(lastName);
		}
		else {
			return friendService.findAll();
		}
			
	}
	
	public
	@GetMapping("/wrong")
	Friend somethingIswrong() {
		
		throw new ValidationException("Something is wrong"); 
		
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ValidationException.class)
	String exceptionHandler(ValidationException e) {
		return e.getMessage();
	}
	
}
