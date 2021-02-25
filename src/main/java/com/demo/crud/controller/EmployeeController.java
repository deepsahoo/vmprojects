package com.demo.crud.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.demo.crud.model.Employee;
import com.demo.crud.model.Status;
import com.demo.crud.service.EmployeeService;

@RestController
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	private HashMap<Integer, Future<Status>> futureObjects = new HashMap<>();

	@PostMapping("/employees/upload")
	DeferredResult<ResponseEntity<?>> uploadEmployee(@RequestParam("file") MultipartFile file) throws IOException {
		DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
		Future<Status> so = employeeService.uploadEmployee(file);
		int identifier = new Random().nextInt();
		futureObjects.put(identifier, so); 
		Status po = new Status(identifier, "file is getting processed, please consult on the referenced URL",
				"/employees/status?id=" + identifier);
		ResponseEntity<Status> responseEntity = new ResponseEntity<>(po, HttpStatus.ACCEPTED);
		deferredResult.setResult(responseEntity);
		return deferredResult;
	}

	@GetMapping("/employees/status")
	DeferredResult<ResponseEntity<?>> get(@RequestParam("id") int id) throws InterruptedException, ExecutionException {
		DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
		Future<Status> futureSO = futureObjects.get(id); 
		if (futureSO.isDone()) { 
			Status so = futureSO.get();
			ResponseEntity<Status> responseEntity = new ResponseEntity<>(so, HttpStatus.CREATED);
			deferredResult.setResult(responseEntity);
		} else {
			Status po = new Status(id, "File is  still under process, please consult on the referenced URL",
					"/employee_crud/status?id=" + id);
			ResponseEntity<Status> responseEntity = new ResponseEntity<>(po, HttpStatus.ACCEPTED);

			deferredResult.setResult(responseEntity);
		}
		return deferredResult;
	}

	@GetMapping(value = "/employees")
	public List<Employee> getAllEmployees() {
		return employeeService.findAll();
	}

	@PostMapping("/employees")
	Employee createOrSaveEmployee(@RequestBody Employee newEmployee) {
		return employeeService.createOrSaveEmployee(newEmployee);
	}

	@GetMapping("/employees/{id}")
	Employee getEmployeeById(@PathVariable Long id) {
		return employeeService.findById(id);
	}

	@PutMapping("/employees/{id}")
	Employee updateEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
		return employeeService.updateEmployee(newEmployee, id);
	}

	@DeleteMapping("/employees/{id}")
	void deleteEmployee(@PathVariable Long id) {
		employeeService.deleteById(id);
	}

}
