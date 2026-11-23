package br.com.leticiacaroline.brasapdv.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.leticiacaroline.brasapdv.categoria.Categoria;
import br.com.leticiacaroline.brasapdv.categoria.CategoriaRepository;
import br.com.leticiacaroline.brasapdv.comum.TipoLancamento;
import br.com.leticiacaroline.brasapdv.fornecedor.Fornecedor;
import br.com.leticiacaroline.brasapdv.fornecedor.FornecedorRepository;
import br.com.leticiacaroline.brasapdv.lancamento.Lancamento;
import br.com.leticiacaroline.brasapdv.lancamento.LancamentoRepository;

/** Na primeira execução, cria categorias e (opcionalmente) dados de exemplo para a tela não ficar vazia. */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final CategoriaRepository categorias;
    private final FornecedorRepository fornecedores;
    private final LancamentoRepository lancamentos;
    private final boolean criarExemplos;

    public DadosIniciais(CategoriaRepository categorias, FornecedorRepository fornecedores,
                         LancamentoRepository lancamentos,
                         @Value("${app.dados-exemplo:true}") boolean criarExemplos) {
        this.categorias = categorias;
        this.fornecedores = fornecedores;
        this.lancamentos = lancamentos;
        this.criarExemplos = criarExemplos;
    }

    @Override
    public void run(String... args) {
        if (categorias.count() > 0) {
            return;
        }
        Categoria carnes = categorias.save(new Categoria("Carnes", TipoLancamento.SAIDA));
        Categoria bebidas = categorias.save(new Categoria("Bebidas", TipoLancamento.SAIDA));
        Categoria hortifruti = categorias.save(new Categoria("Hortifruti", TipoLancamento.SAIDA));
        Categoria carvao = categorias.save(new Categoria("Carvão e gás", TipoLancamento.SAIDA));
        Categoria salarios = categorias.save(new Categoria("Salários", TipoLancamento.SAIDA));
        Categoria aluguel = categorias.save(new Categoria("Aluguel e contas", TipoLancamento.SAIDA));
        Categoria salao = categorias.save(new Categoria("Vendas no salão", TipoLancamento.ENTRADA));
        Categoria delivery = categorias.save(new Categoria("Vendas delivery", TipoLancamento.ENTRADA));

        if (!criarExemplos) {
            return;
        }
        Fornecedor frigorifico = fornecedores.save(new Fornecedor("Frigorífico Boi Bom", "(44) 3000-1111"));
        Fornecedor distribuidora = fornecedores.save(new Fornecedor("Distribuidora Gelada", "(44) 3000-2222"));
        Fornecedor horta = fornecedores.save(new Fornecedor("Horta do Seu Zé", null));

        LocalDate mes = LocalDate.now().withDayOfMonth(1);
        salvar("Picanha 20 kg", "1450.00", TipoLancamento.SAIDA, mes, carnes, frigorifico);
        salvar("Costela 15 kg", "780.00", TipoLancamento.SAIDA, mes.plusDays(2), carnes, frigorifico);
        salvar("Fraldinha 12 kg", "540.00", TipoLancamento.SAIDA, mes.plusDays(6), carnes, frigorifico);
        salvar("Cerveja (10 caixas)", "920.00", TipoLancamento.SAIDA, mes.plusDays(1), bebidas, distribuidora);
        salvar("Refrigerante e suco", "340.00", TipoLancamento.SAIDA, mes.plusDays(5), bebidas, distribuidora);
        salvar("Saladas e legumes da semana", "290.00", TipoLancamento.SAIDA, mes.plusDays(3), hortifruti, horta);
        salvar("Carvão (20 sacos)", "260.00", TipoLancamento.SAIDA, mes.plusDays(4), carvao, null);
        salvar("Folha de pagamento", "4200.00", TipoLancamento.SAIDA, mes.plusDays(4), salarios, null);
        salvar("Aluguel", "3500.00", TipoLancamento.SAIDA, mes.plusDays(2), aluguel, null);
        salvar("Vendas do fim de semana", "5200.00", TipoLancamento.ENTRADA, mes.plusDays(1), salao, null);
        salvar("Vendas do fim de semana", "6100.00", TipoLancamento.ENTRADA, mes.plusDays(8), salao, null);
        salvar("Pedidos de delivery da semana", "2300.00", TipoLancamento.ENTRADA, mes.plusDays(6), delivery, null);
    }

    private void salvar(String descricao, String valor, TipoLancamento tipo, LocalDate data,
                        Categoria categoria, Fornecedor fornecedor) {
        Lancamento l = new Lancamento();
        l.setDescricao(descricao);
        l.setValor(new BigDecimal(valor));
        l.setTipo(tipo);
        l.setData(data);
        l.setCategoria(categoria);
        l.setFornecedor(fornecedor);
        lancamentos.save(l);
    }
}
