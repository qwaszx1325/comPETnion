package com.pet.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.order.EcpayTransactions;

public interface EcpayTransactionsRepository extends JpaRepository<EcpayTransactions, Integer>{

	
}
