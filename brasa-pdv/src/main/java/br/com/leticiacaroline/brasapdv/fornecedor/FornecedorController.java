package br.com.leticiacaroline.brasapdv.fornecedor;

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

import br.com.leticiacaroline.brasapdv.lancamento.LancamentoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    public record FornecedorRequest(
            @NotBlank(message = "Informe o nome do fornecedor")
            @Size(max = 80, message = "Nome do fornecedor muito longo") String nome,
            @Size(max = 30, message = "Telefone muito longo") String telefone) {
    }

    private final FornecedorRepository fornecedores;
    private final LancamentoRepository lancamentos;

    public FornecedorController(FornecedorRepository fornecedores, LancamentoRepository lancamentos) {
        this.fornecedores = fornecedores;
        this.lancamentos = lancamentos;
    }

    @GetMapping
    public List<Fornecedor> listar() {
        return fornecedores.findAllByOrderByNomeAsc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fornecedor criar(@Valid @RequestBody FornecedorRequest req) {
        String tel = req.telefone() == null || req.telefone().isBlank() ? null : req.telefone().trim();
        return fornecedores.save(new Fornecedor(req.nome().trim(), tel));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Long id) {
        if (!fornecedores.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado");
        }
        if (lancamentos.existsByFornecedorId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esse fornecedor tem compras registradas. Apague os lançamentos antes");
        }
        fornecedores.deleteById(id);
    }
}
