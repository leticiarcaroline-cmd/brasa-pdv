package br.com.leticiacaroline.brasapdv.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.leticiacaroline.brasapdv.categoria.Categoria;
import br.com.leticiacaroline.brasapdv.categoria.CategoriaRepository;
import br.com.leticiacaroline.brasapdv.comum.Periodo;
import br.com.leticiacaroline.brasapdv.comum.TipoLancamento;
import br.com.leticiacaroline.brasapdv.fornecedor.Fornecedor;
import br.com.leticiacaroline.brasapdv.fornecedor.FornecedorRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    public record LancamentoRequest(
            @NotBlank(message = "Informe a descrição")
            @Size(max = 120, message = "Descrição muito longa") String descricao,
            @NotNull(message = "Informe o valor")
            @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero") BigDecimal valor,
            @NotNull(message = "Informe o tipo (ENTRADA ou SAIDA)") TipoLancamento tipo,
            @NotNull(message = "Informe a data") LocalDate data,
            @NotNull(message = "Informe a categoria") Long categoriaId,
            Long fornecedorId) {
    }

    public record LancamentoResponse(Long id, String descricao, BigDecimal valor, TipoLancamento tipo,
                                     LocalDate data, Long categoriaId, String categoria,
                                     Long fornecedorId, String fornecedor) {
        static LancamentoResponse de(Lancamento l) {
            Fornecedor f = l.getFornecedor();
            return new LancamentoResponse(l.getId(), l.getDescricao(), l.getValor(), l.getTipo(),
                    l.getData(), l.getCategoria().getId(), l.getCategoria().getNome(),
                    f == null ? null : f.getId(), f == null ? null : f.getNome());
        }
    }

    private final LancamentoRepository lancamentos;
    private final CategoriaRepository categorias;
    private final FornecedorRepository fornecedores;

    public LancamentoController(LancamentoRepository lancamentos, CategoriaRepository categorias,
                                FornecedorRepository fornecedores) {
        this.lancamentos = lancamentos;
        this.categorias = categorias;
        this.fornecedores = fornecedores;
    }

    @GetMapping
    public List<LancamentoResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        Periodo p = Periodo.de(inicio, fim);
        return lancamentos.findByDataBetweenOrderByDataDescIdDesc(p.inicio(), p.fim()).stream()
                .map(LancamentoResponse::de)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoResponse criar(@Valid @RequestBody LancamentoRequest req) {
        Lancamento l = new Lancamento();
        preencher(l, req);
        return LancamentoResponse.de(lancamentos.save(l));
    }

    @PutMapping("/{id}")
    public LancamentoResponse editar(@PathVariable Long id, @Valid @RequestBody LancamentoRequest req) {
        Lancamento l = lancamentos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lançamento não encontrado"));
        preencher(l, req);
        return LancamentoResponse.de(lancamentos.save(l));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Long id) {
        if (!lancamentos.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lançamento não encontrado");
        }
        lancamentos.deleteById(id);
    }

    /** Copia os dados do pedido para o lançamento, aplicando as regras de negócio. */
    private void preencher(Lancamento l, LancamentoRequest req) {
        Categoria categoria = categorias.findById(req.categoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não encontrada"));
        if (categoria.getTipo() != req.tipo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A categoria \"" + categoria.getNome() + "\" é de "
                            + (categoria.getTipo() == TipoLancamento.ENTRADA ? "entrada" : "saída")
                            + " e não combina com o tipo escolhido");
        }
        Fornecedor fornecedor = null;
        if (req.fornecedorId() != null) {
            if (req.tipo() == TipoLancamento.ENTRADA) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Fornecedor só pode ser informado em saídas (compras)");
            }
            fornecedor = fornecedores.findById(req.fornecedorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Fornecedor não encontrado"));
        }
        l.setDescricao(req.descricao().trim());
        l.setValor(req.valor());
        l.setTipo(req.tipo());
        l.setData(req.data());
        l.setCategoria(categoria);
        l.setFornecedor(fornecedor);
    }
}
