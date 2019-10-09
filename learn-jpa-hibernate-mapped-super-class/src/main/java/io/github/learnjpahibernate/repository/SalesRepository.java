package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.learnjpahibernate.model.Sales;
import io.github.learnjpahibernate.model.UpdatableSales;

@Repository
public interface SalesRepository extends JpaRepository<UpdatableSales, Sales.SalesId> {

}