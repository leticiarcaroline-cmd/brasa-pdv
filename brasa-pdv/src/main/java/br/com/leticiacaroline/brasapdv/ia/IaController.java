package br.com.leticiacaroline.brasapdv.ia;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.leticiacaroline.brasapdv.comum.Periodo;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final IaService ia;

    public IaController(IaService ia) {
        this.ia = ia;
    }

    @PostMapping("/dicas")
    public IaService.Dicas dicas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ia.gerar(Periodo.de(inicio, fim));
    }
}
