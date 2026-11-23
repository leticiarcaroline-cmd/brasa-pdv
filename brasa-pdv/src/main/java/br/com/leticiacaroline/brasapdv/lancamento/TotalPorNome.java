package br.com.leticiacaroline.brasapdv.lancamento;

import java.math.BigDecimal;

/** Resultado das consultas que somam valores agrupados por nome. */
public interface TotalPorNome {

    String getNome();

    BigDecimal getTotal();
}
