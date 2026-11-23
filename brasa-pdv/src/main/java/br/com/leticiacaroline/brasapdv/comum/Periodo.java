package br.com.leticiacaroline.brasapdv.comum;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Período de consulta. Se não vier nada, usa o mês atual. */
public record Periodo(LocalDate inicio, LocalDate fim) {

    public static Periodo de(LocalDate inicio, LocalDate fim) {
        LocalDate i = inicio != null ? inicio : LocalDate.now().withDayOfMonth(1);
        LocalDate f = fim != null ? fim : i.withDayOfMonth(i.lengthOfMonth());
        if (f.isBefore(i)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data final não pode ser antes da data inicial");
        }
        return new Periodo(i, f);
    }
}
