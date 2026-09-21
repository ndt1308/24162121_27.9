package vn.iotstar.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = "user")
	Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	Page<Product> findByUserEmailIgnoreCaseAndNameContainingIgnoreCase(String email, String name, Pageable pageable);

	long countByUserEmailIgnoreCase(String email);

	/** Đếm số sản phẩm của nhiều user trong 1 câu query: [userId, count] */
	@Query("SELECT p.user.id, COUNT(p) FROM Product p WHERE p.user.id IN :ids GROUP BY p.user.id")
	List<Object[]> countByUserIds(@Param("ids") Collection<Long> ids);
}
