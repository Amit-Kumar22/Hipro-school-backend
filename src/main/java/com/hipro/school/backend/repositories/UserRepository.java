package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE (COALESCE(:email, '') = '' OR u.email = :email) OR (COALESCE(:phone, '') = '' OR u.phone = :phone)")
	Optional<User> findByPhoneOrEmail(String email, String phone);

	Optional<User> findByPhoneAndOtp(String phone, String opt);

	// @Query("select u from User u where u.isAdmin=false and
	// u.franchise.status=:status ORDER BY u.franchise.createdDate DESC")
	// List<User> findAllAgenciesByStatus(@Param("status") Status status);

	@Query("SELECT u FROM User u WHERE u.id IN :userIds")
	List<User> findByIds(List<Integer> userIds);

	@Query("SELECT u FROM User u WHERE u.id = :userId")
	Optional<User> findByUserId(Integer userId);

	Optional<User> findByEmailAndRole(String email, Role role);

	Optional<User> findByIdAndRole(Integer userId, Role role);

	@Query("SELECT u.fullName FROM User u")
	User findByFullName(String name);

	@Query("select u from User u where u.role = :role")
	Page<User> findAllByRole(Role role, Pageable pageable);

	@Query("select u from User u where u.role = :role")
	List<User> findAllByRole(Role role);

	@Query("select u from User u where u.role = :role AND u.email = :email")
	Optional<User> findAllByRoleAndEmail(Role role, String email);

	@Query("select u from User u where u.role = :role AND u.id = :userId")
	Optional<User> findAllByRoleAndId(Role role, Integer userId);

	@Query("SELECT u FROM User u WHERE u.role = :role AND u.email = :email")
	Page<User> findAllByRoleAndEmail(@Param("role") Role role, @Param("email") String email, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.role NOT IN :excludedRoles")
	List<User> findAllExcludingRoles(@Param("excludedRoles") List<Role> excludedRoles);

	@Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
	long countByStatus(@Param("status") com.hipro.school.backend.enums.UserStatus status);
}
