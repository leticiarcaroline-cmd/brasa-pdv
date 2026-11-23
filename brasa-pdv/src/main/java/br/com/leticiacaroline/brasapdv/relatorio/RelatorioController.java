package br.com.leticiacaroline.brasapdv.relatorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.leticiacaroline.brasapdv.comum.Periodo;
import br.com.leticiacaroline.brasapdv.relatorio.RelatorioService.Fatia;
import br.com.leticiacaroline.brasapdv.relatorio.RelatorioService.Resumo;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorios;

    public RelatorioController(RelatorioService relatorios) {
        this.relatorios = relatorios;
    }

    @GetMapping("/resumo")
    public Resumo resumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorios.resumo(Periodo.de(inicio, fim));
    }

    @GetMapping("/saidas-por-categoria")
    public List<Fatia> saidasPorCategoria(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorios.saidasPorCategoria(Periodo.de(inicio, fim));
    }

    @GetMapping("/saidas-por-fornecedor")
    public List<Fatia> saidasPorFornecedor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorios.saidasPorFornecedor(Periodo.de(inicio, fim));
    }
}
