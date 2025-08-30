package com.example.demo.repository;

import com.example.demo.models.StudentPayment;
import com.example.demo.models.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface StudentPaymentRepo extends JpaRepository<StudentPayment, Long> {

  @Query("""
      select coalesce(sum(sp.amountPaid), 0)
      from StudentPayment sp
      where sp.student.id = :studentId
        and sp.group.id   = :groupId
        and sp.paymentType = com.example.demo.models.enums.PaymentType.MONTHLY
        and sp.monthYear   = :monthYear
      """)
  BigDecimal sumMonthlyPaid(
      @Param("studentId") Long studentId,
      @Param("groupId")   Long groupId,
      @Param("monthYear") String monthYear
  );
}
