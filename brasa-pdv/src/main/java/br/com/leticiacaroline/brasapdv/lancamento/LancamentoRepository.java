package br.com.leticiacaroline.brasapdv.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.leticiacaroline.brasapdv.comum.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByDataBetweenOrderByDataDescIdDesc(LocalDate inicio, LocalDate fim);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByFornecedorId(Long fornecedorId);

    @Query("select sum(l.valor) from Lancamento l "
            + "where l.tipo = :tipo and l.data between :inicio and :fim")
    BigDecimal somaPorTipo(@Param("tipo") TipoLancamento tipo,
                           @Param("inicio") LocalDate inicio,
                           @Param("fim") LocalDate fim);

    @Query("select l.categoria.nome as nome, sum(l.valor) as total from Lancamento l "
            + "where l.tipo = :tipo and l.data between :inicio and :fim "
            + "group by l.categoria.nome order by sum(l.valor) desc")
    List<TotalPorNome> totalPorCategoria(@Param("tipo") TipoLancamento tipo,
                                         @Param("inicio") LocalDate inicio,
                                         @Param("fim") LocalDate fim);

    @Query("select l.fornecedor.nome as nome, sum(l.valor) as total from Lancamento l "
            + "where l.tipo = :tipo and l.data between :inicio and :fim "
            + "group by l.fornecedor.nome order by sum(l.valor) desc")
    List<TotalPorNome> totalPorFornecedor(@Param("tipo") TipoLancamento tipo,
                                          @Param("inicio") LocalDate inicio,
                                          @Param("fim") LocalDate fim);
}
