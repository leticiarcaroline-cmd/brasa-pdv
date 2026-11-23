package br.com.leticiacaroline.brasapdv.relatorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.leticiacaroline.brasapdv.comum.Periodo;
import br.com.leticiacaroline.brasapdv.comum.TipoLancamento;
import br.com.leticiacaroline.brasapdv.lancamento.LancamentoRepository;
import br.com.leticiacaroline.brasapdv.lancamento.TotalPorNome;

@Service
public class RelatorioService {

    public record Resumo(LocalDate inicio, LocalDate fim, BigDecimal totalEntradas, BigDecimal totalSaidas,
                         BigDecimal saldo, BigDecimal margemPercentual) {
    }

    /** Uma "fatia" do gráfico: quanto uma categoria (ou fornecedor) pesa nas saídas. */
    public record Fatia(String nome, BigDecimal total, BigDecimal percentual) {
    }

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final LancamentoRepository lancamentos;

    public RelatorioService(LancamentoRepository lancamentos) {
        this.lancamentos = lancamentos;
    }

    public Resumo resumo(Periodo p) {
        BigDecimal entradas = zeroSeNulo(lancamentos.somaPorTipo(TipoLancamento.ENTRADA, p.inicio(), p.fim()));
        BigDecimal saidas = zeroSeNulo(lancamentos.somaPorTipo(TipoLancamento.SAIDA, p.inicio(), p.fim()));
        BigDecimal saldo = entradas.subtract(saidas);
        BigDecimal margem = entradas.signum() == 0
                ? BigDecimal.ZERO
                : saldo.multiply(CEM).divide(entradas, 1, RoundingMode.HALF_UP);
        return new Resumo(p.inicio(), p.fim(), entradas, saidas, saldo, margem);
    }

    public List<Fatia> saidasPorCategoria(Periodo p) {
        return fatias(lancamentos.totalPorCategoria(TipoLancamento.SAIDA, p.inicio(), p.fim()), p);
    }

    public List<Fatia> saidasPorFornecedor(Periodo p) {
        return fatias(lancamentos.totalPorFornecedor(TipoLancamento.SAIDA, p.inicio(), p.fim()), p);
    }

    private List<Fatia> fatias(List<TotalPorNome> linhas, Periodo p) {
        BigDecimal totalSaidas = zeroSeNulo(lancamentos.somaPorTipo(TipoLancamento.SAIDA, p.inicio(), p.fim()));
        return linhas.stream()
                .map(l -> new Fatia(l.getNome(), l.getTotal(), percentual(l.getTotal(), totalSaidas)))
                .toList();
    }

    private BigDecimal percentual(BigDecimal parte, BigDecimal todo) {
        if (todo.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return parte.multiply(CEM).divide(todo, 1, RoundingMode.HALF_UP);
    }

    private BigDecimal zeroSeNulo(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
