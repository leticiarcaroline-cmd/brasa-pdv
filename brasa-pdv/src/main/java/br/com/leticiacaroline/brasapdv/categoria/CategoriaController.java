package br.com.leticiacaroline.brasapdv.categoria;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.leticiacaroline.brasapdv.comum.TipoLancamento;
import br.com.leticiacaroline.brasapdv.lancamento.LancamentoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    public record CategoriaRequest(
            @NotBlank(message = "Informe o nome da categoria")
            @Size(max = 60, message = "Nome da categoria muito longo") String nome,
            @NotNull(message = "Informe o tipo da categoria (ENTRADA ou SAIDA)") TipoLancamento tipo) {
    }

    private final CategoriaRepository categorias;
    private final LancamentoRepository lancamentos;

    public CategoriaController(CategoriaRepository categorias, LancamentoRepository lancamentos) {
        this.categorias = categorias;
        this.lancamentos = lancamentos;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categorias.findAllByOrderByNomeAsc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria criar(@Valid @RequestBody CategoriaRequest req) {
        return categorias.save(new Categoria(req.nome().trim(), req.tipo()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Long id) {
        if (!categorias.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }
        if (lancamentos.existsByCategoriaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Essa categoria tem lançamentos. Apague os lançamentos antes");
        }
        categorias.deleteById(id);
    }
}
