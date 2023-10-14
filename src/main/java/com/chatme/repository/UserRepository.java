package com.chatme.repository;

import com.chatme.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long>
{
	List<User> findAllByEmail(String email);

	User findByUsername(String username);

	List<User> findTop25ByUsernameContainingOrEmailContainingOrderByIdAsc(String email, String username);
}
