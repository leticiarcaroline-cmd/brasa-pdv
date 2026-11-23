package br.com.leticiacaroline.brasapdv.fornecedor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    List<Fornecedor> findAllByOrderByNomeAsc();
}
