package com.jonathan.springboot.firstrestapi.user;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDetailsRestRepository extends PagingAndSortingRepository<UserDetails, Long>{
	public List<UserDetails> findByRole(String role);
}
