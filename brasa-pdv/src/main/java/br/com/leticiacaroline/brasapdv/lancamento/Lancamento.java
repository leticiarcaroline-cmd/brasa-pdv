package br.com.leticiacaroline.brasapdv.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.leticiacaroline.brasapdv.categoria.Categoria;
import br.com.leticiacaroline.brasapdv.comum.TipoLancamento;
import br.com.leticiacaroline.brasapdv.fornecedor.Fornecedor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lancamentos")
@Getter
@Setter
@NoArgsConstructor
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String descricao;

    /** Dinheiro sempre em BigDecimal (double dá erro de arredondamento). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoLancamento tipo;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate data;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    /** Só faz sentido nas saídas (compras). Pode ficar vazio. */
    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
}
