package com.sprint.deokhugamteam7.domain.comment.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.concurrent.CountDownLatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
public class DataInitializer {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	TransactionTemplate transactionTemplate;

	CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

	static final int BULK_INSERT_SIZE = 200;
	static final int EXECUTE_COUNT = 600;

//	@Test
//	void initialize() throws InterruptedException {
//		ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//		for (int cnt_i = 0; cnt_i < EXECUTE_COUNT; cnt_i++) {
//			executorService.submit(() -> {
//				insert();
//				latch.countDown();
//				System.out.println("latch.getCountDown(): " + latch.getCount());
//			});
//		}
//
//		latch.await();
//		executorService.shutdown();
//	}
//
//	void insert() {
//		transactionTemplate.executeWithoutResult(status -> {
//			for (int cnt_i = 0; cnt_i < BULK_INSERT_SIZE; cnt_i++) {
//				Comment comment = Comment.create(
//
//				);
//				entityManager.persist(comment);
//			}
//		});
//	}
}
