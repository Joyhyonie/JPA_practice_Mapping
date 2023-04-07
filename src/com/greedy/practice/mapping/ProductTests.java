package com.greedy.practice.mapping;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductTests {

	private static EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	
	@BeforeAll
	public static void initFectory() {
		entityManagerFactory = Persistence.createEntityManagerFactory("jpatest"); /* xml 파일에 작성한 persistence-unit의 이름을 입력 */
	}
	
	@BeforeEach
	public void initManager() {
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	@AfterAll
	public static void closeFactory() {
		entityManagerFactory.close();
	}
	
	@AfterEach
	public void closeManager() {
		entityManager.close();
	}
	
	@Test
	public void 상품_등록_테스트() {
		
		// given
		Product product1 = new Product();
		product1.setProductName("갤럭시북3");
		product1.setProductPrice(2000000);
		product1.setReleaseDate(new java.sql.Date(System.currentTimeMillis()));
		product1.setSupplierName(SupplierType.SAMSUNG);
		
		Product product2 = new Product();
		product2.setProductName("스타일러");
		product2.setProductPrice(3000000);
		product2.setReleaseDate(new java.sql.Date(System.currentTimeMillis()));
		product2.setSupplierName(SupplierType.LG);
		
		Product product3 = new Product();
		product3.setProductName("에어팟프로2");
		product3.setProductPrice(350000);
		product3.setReleaseDate(new java.sql.Date(System.currentTimeMillis()));
		product3.setSupplierName(SupplierType.APPLE);
		
		// when
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(product1);
		entityManager.persist(product2);
		entityManager.persist(product3);
		transaction.commit();
		
		/* 아래의 jpql로 조회하는 것은 DB가 아닌 1차 캐시(영속성 컨텍스트)안에 있는 엔티티를 조회해옴 (이미 위에서 영속성 컨텍스트를 거쳐 insert 되었기 때문)
		 * 현재 TBL_PRODUCT에 insert를 하는 구문과 jpql로 select하는 구문이 한 트랜잭션 안에서 이루어지고 있는 중 (특수 상황)
		 * 따라서 프로퍼티 접근을 활용했던 '상품가격'은 DB에서는 가공된 상태로 insert 되었지만 1차 캐시 안의 '상품가격'은 가공 전 상태이므로 
		 * 실제 가공된 가격(DB안에 있는 값)을 아래의 then에서 조회해오고자 한다면, clear()를 통해 1차 캐시를 초기화한 뒤 조회하면 됨 */
		entityManager.clear();
		
		// then
		String jpql = "SELECT A FROM product A";
		List<Object> productList = entityManager.createQuery(jpql, Object.class).getResultList();
		
		productList.forEach(System.out::println);
		
	}
}
