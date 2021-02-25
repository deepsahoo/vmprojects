package com.demo.crud.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.crud.model.Employee;
import com.demo.crud.model.Status;
import com.demo.crud.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository repository;


	@Async
	public Future<Status> uploadEmployee(MultipartFile file) throws IllegalStateException, IOException {
		List<Employee> empList = new ArrayList<Employee>();
		File empFile = new File("emp_file");
		file.transferTo(empFile);
		try (Stream<String> lines = Files.lines(Path.of("emp_file")).parallel()) {
			List<String> lineStrs = lines.collect(Collectors.toList());
			for (String line : lineStrs) {
				String empStr[] = line.split(" ");
				Employee emp = new Employee();
				emp.setName(empStr[0]);
				emp.setAge(empStr[1]);
				empList.add(emp);
			}
			empList = repository.saveAll(empList);
			Status status = null;
			if (empList.isEmpty()) {
				status = new Status(0, "Error occured while reading/saving the file", "");
			} else {
				status = new Status(0, "File sucessfully uploaded", "");
			}
			return new AsyncResult<>(status);
		}

	}

	public List<Employee> findAll() {
		return repository.findAll();
	}

	public Employee createOrSaveEmployee(Employee newEmployee) {
		return repository.save(newEmployee);
	}

	public Employee findById(Long id) {
		return repository.findById(id).get();
	}

	public Employee updateEmployee(Employee newEmployee, Long id) {
		return repository.findById(id).map(employee -> {
			employee.setName(newEmployee.getName());
			employee.setAge(newEmployee.getAge());
			return repository.save(employee);
		}).orElseGet(() -> {
			newEmployee.setId(id);
			return repository.save(newEmployee);
		});
	}

	public void deleteById(Long id) {
		repository.deleteById(id);
	}
}
