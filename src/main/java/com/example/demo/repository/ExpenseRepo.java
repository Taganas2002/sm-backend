package com.example.demo.repository;

import com.example.demo.models.Expense;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepo extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

  @Query("""
     select coalesce(sum(e.totalAmount), 0)
     from Expense e
     where (:from is null or e.expenseDate >= :from)
       and (:to   is null or e.expenseDate <  :to)
  """)
  BigDecimal sumTotal(@Param("from") LocalDate from, @Param("to") LocalDate to);

  @Query("""
     select e.category, coalesce(sum(e.totalAmount), 0)
     from Expense e
     where (:from is null or e.expenseDate >= :from)
       and (:to   is null or e.expenseDate <  :to)
     group by e.category
     order by e.category asc
  """)
  List<Object[]> sumByCategory(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
