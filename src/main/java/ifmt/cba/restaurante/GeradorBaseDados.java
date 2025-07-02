package ifmt.cba.restaurante;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ifmt.cba.restaurante.dto.BairroDTO;
import ifmt.cba.restaurante.dto.CardapioDTO;
import ifmt.cba.restaurante.dto.ClienteDTO;
import ifmt.cba.restaurante.dto.EntregadorDTO;
import ifmt.cba.restaurante.dto.EstadoPedidoDTO;
import ifmt.cba.restaurante.dto.GrupoAlimentarDTO;
import ifmt.cba.restaurante.dto.ItemPedidoDTO;
import ifmt.cba.restaurante.dto.MovimentoEstoqueDTO;
import ifmt.cba.restaurante.dto.PedidoDTO;
import ifmt.cba.restaurante.dto.PreparoProdutoDTO;
import ifmt.cba.restaurante.dto.ProdutoDTO;
import ifmt.cba.restaurante.dto.RegistroEstoqueDTO;
import ifmt.cba.restaurante.dto.TipoPreparoDTO;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.negocio.BairroNegocio;
import ifmt.cba.restaurante.negocio.CardapioNegocio;
import ifmt.cba.restaurante.negocio.ClienteNegocio;
import ifmt.cba.restaurante.negocio.EntregadorNegocio;
import ifmt.cba.restaurante.negocio.GrupoAlimentarNegocio;
import ifmt.cba.restaurante.negocio.PedidoNegocio;
import ifmt.cba.restaurante.negocio.PreparoProdutoNegocio;
import ifmt.cba.restaurante.negocio.ProdutoNegocio;
import ifmt.cba.restaurante.negocio.RegistroEstoqueNegocio;
import ifmt.cba.restaurante.negocio.TipoPreparoNegocio;

@Component
public class GeradorBaseDados implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private BairroNegocio bairroNegocio;

    @Autowired
    private ClienteNegocio clienteNegocio;

    @Autowired
    private GrupoAlimentarNegocio grupoAlimentarNegocio;

    @Autowired
    private EntregadorNegocio entregadorNegocio;

    @Autowired
    private TipoPreparoNegocio tipoPreparoNegocio;

    @Autowired
    private ProdutoNegocio produtoNegocio;

    @Autowired
    private PreparoProdutoNegocio preparoProdutoNegocio;

    @Autowired
    private CardapioNegocio cardapioNegocio;

    @Autowired
    private RegistroEstoqueNegocio registroEstoqueNegocio;

    @Autowired
    private PedidoNegocio pedidoNegocio;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        try {
            System.out.println("Iniciando a geracao da massa de dados");
            this.inserirBairro();
            this.inserirCliente();
            this.inserirGrupoAlimentar();
            this.inserirEntregador();
            this.inserirTipoPreparo();
            this.inserirProduto();
            this.inserirPreparoProduto();
            this.inserirCardapio();
            this.inserirMovimentoEstoque();
            this.inserirPedido();
            System.out.println("Finalizado a geracao da massa de dados");

        } catch (NotValidDataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void inserirPedido() throws NotValidDataException, NotFoundException {

        ClienteDTO clienteDTO = clienteNegocio.pesquisaPorNome("Cliente 01");
        PreparoProdutoDTO preparoProdutoDTO = preparoProdutoNegocio.pesquisaPorNome("Arroz Cozido");
        List<ItemPedidoDTO> lista = new ArrayList<ItemPedidoDTO>();
        ItemPedidoDTO itemPedidoDTO = new ItemPedidoDTO();
        itemPedidoDTO.setPreparoProduto(preparoProdutoDTO);
        itemPedidoDTO.setQuantidadePorcao(2);
        lista.add(itemPedidoDTO);

        preparoProdutoDTO = preparoProdutoNegocio.pesquisaPorNome("Costela Suina");
        itemPedidoDTO = new ItemPedidoDTO();
        itemPedidoDTO.setPreparoProduto(preparoProdutoDTO);
        itemPedidoDTO.setQuantidadePorcao(1);
        lista.add(itemPedidoDTO);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setDataPedido(LocalDate.now());
        pedidoDTO.setHoraPedido(LocalTime.now());
        pedidoDTO.setCliente(clienteDTO);
        pedidoDTO.setEstado(EstadoPedidoDTO.REGISTRADO);
        pedidoDTO.setListaItens(lista);
        pedidoNegocio.inserir(pedidoDTO);

        // --------------------------------------------------------------------
        clienteDTO = clienteNegocio.pesquisaPorNome("Cliente 02");
        preparoProdutoDTO = preparoProdutoNegocio.pesquisaPorNome("Arroz Cozido");
        lista = new ArrayList<ItemPedidoDTO>();
        itemPedidoDTO = new ItemPedidoDTO();
        itemPedidoDTO.setPreparoProduto(preparoProdutoDTO);
        itemPedidoDTO.setQuantidadePorcao(3);
        lista.add(itemPedidoDTO);

        preparoProdutoDTO = preparoProdutoNegocio.pesquisaPorNome("Alcatra Bovina Grelhada");
        itemPedidoDTO = new ItemPedidoDTO();
        itemPedidoDTO.setPreparoProduto(preparoProdutoDTO);
        itemPedidoDTO.setQuantidadePorcao(2);
        lista.add(itemPedidoDTO);

        pedidoDTO = new PedidoDTO();
        pedidoDTO.setDataPedido(LocalDate.now());
        pedidoDTO.setHoraPedido(LocalTime.now());
        pedidoDTO.setCliente(clienteDTO);
        pedidoDTO.setEstado(EstadoPedidoDTO.REGISTRADO);
        pedidoDTO.setListaItens(lista);
        pedidoNegocio.inserir(pedidoDTO);
    }

    private void inserirMovimentoEstoque() throws NotValidDataException, NotFoundException {

        ProdutoDTO produtoDTO = produtoNegocio.pesquisaPorNome("Arroz Branco");

        RegistroEstoqueDTO registroEstoqueDTO = new RegistroEstoqueDTO();
        registroEstoqueDTO.setData(LocalDate.now());
        registroEstoqueDTO.setMovimento(MovimentoEstoqueDTO.COMPRA);
        registroEstoqueDTO.setProduto(produtoDTO);
        registroEstoqueDTO.setQuantidade(100);
        registroEstoqueNegocio.inserir(registroEstoqueDTO);

        produtoDTO = produtoNegocio.pesquisaPorNome("Alcatra bovina");
        registroEstoqueDTO = new RegistroEstoqueDTO();
        registroEstoqueDTO.setData(LocalDate.now());
        registroEstoqueDTO.setMovimento(MovimentoEstoqueDTO.VENCIMENTO);
        registroEstoqueDTO.setProduto(produtoDTO);
        registroEstoqueDTO.setQuantidade(50);
        registroEstoqueNegocio.inserir(registroEstoqueDTO);

        produtoDTO = produtoNegocio.pesquisaPorNome("Batata Doce");
        registroEstoqueDTO = new RegistroEstoqueDTO();
        registroEstoqueDTO.setData(LocalDate.now());
        registroEstoqueDTO.setMovimento(MovimentoEstoqueDTO.PRODUCAO);
        registroEstoqueDTO.setProduto(produtoDTO);
        registroEstoqueDTO.setQuantidade(80);
        registroEstoqueNegocio.inserir(registroEstoqueDTO);
    }

    private void inserirCardapio() throws NotValidDataException, NotFoundException {

        List<PreparoProdutoDTO> listaItens = new ArrayList<PreparoProdutoDTO>();
        PreparoProdutoDTO preparoProdutoDTO = preparoProdutoNegocio.pesquisaPorNome("Arroz cozido");
        listaItens.add(preparoProdutoDTO);

        preparoProdutoDTO = preparoProdutoNegocio.pesquisaPorNome("Costela suina"); //
        listaItens.add(preparoProdutoDTO);

        preparoProdutoDTO = preparoProdutoNegocio.pesquisaPorNome("Alcatra bovina grelhada");
        listaItens.add(preparoProdutoDTO);

        CardapioDTO cardapioDTO = new CardapioDTO();
        cardapioDTO.setNome("Carnes vermelhas com arroz cozido");
        cardapioDTO.setDescricao("O cardapio oferece duas opcoes de carnes vermelhas acompanhado com arroz cozido");
        cardapioDTO.setListaPreparoProduto(listaItens);

        cardapioNegocio.inserir(cardapioDTO);
    }

    private void inserirPreparoProduto() throws NotValidDataException, NotFoundException {
        ProdutoDTO produtoDTO = produtoNegocio.pesquisaPorNome("Arroz Branco");
        TipoPreparoDTO tipoPreparoDTO = tipoPreparoNegocio.pesquisaPorDescricao("Cozimento em agua");
        PreparoProdutoDTO preparoProdutoDTO = new PreparoProdutoDTO();
        preparoProdutoDTO.setNome("Arroz Cozido");
        preparoProdutoDTO.setProduto(produtoDTO);
        preparoProdutoDTO.setTipoPreparo(tipoPreparoDTO);
        preparoProdutoDTO.setTempoPreparo(25);
        preparoProdutoDTO.setValorPreparo(0.5f);
        preparoProdutoNegocio.inserir(preparoProdutoDTO);

        produtoDTO = produtoNegocio.pesquisaPorNome("Costela suina");
        tipoPreparoDTO = tipoPreparoNegocio.pesquisaPorDescricao("Assado no forno");
        preparoProdutoDTO = new PreparoProdutoDTO();
        preparoProdutoDTO.setNome("Costela suina no forno");
        preparoProdutoDTO.setProduto(produtoDTO);
        preparoProdutoDTO.setTipoPreparo(tipoPreparoDTO);
        preparoProdutoDTO.setTempoPreparo(60);
        preparoProdutoDTO.setValorPreparo(4.0f);
        preparoProdutoNegocio.inserir(preparoProdutoDTO);

        produtoDTO = produtoNegocio.pesquisaPorNome("Alcatra bovina");
        tipoPreparoDTO = tipoPreparoNegocio.pesquisaPorDescricao("Grelhado");
        preparoProdutoDTO = new PreparoProdutoDTO();
        preparoProdutoDTO.setNome("Alcatra bovina grelhada");
        preparoProdutoDTO.setProduto(produtoDTO);
        preparoProdutoDTO.setTipoPreparo(tipoPreparoDTO);
        preparoProdutoDTO.setTempoPreparo(20);
        preparoProdutoDTO.setValorPreparo(3.0f);
        preparoProdutoNegocio.inserir(preparoProdutoDTO);
    }

    private void inserirProduto() throws NotValidDataException, NotFoundException {

        GrupoAlimentarDTO grupoDTO = grupoAlimentarNegocio.pesquisaPorNome("Proteinas");

        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Alcatra bovina");
        produtoDTO.setEstoque(1000);
        produtoDTO.setEstoqueMinimo(100);
        produtoDTO.setCustoUnidade(2.0f);
        produtoDTO.setValorEnergetico(50);
        produtoDTO.setGrupoAlimentar(grupoDTO);
        produtoNegocio.inserir(produtoDTO);

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Costela suina");
        produtoDTO.setEstoque(30);
        produtoDTO.setEstoqueMinimo(50);
        produtoDTO.setCustoUnidade(1.5f);
        produtoDTO.setValorEnergetico(60);
        produtoDTO.setGrupoAlimentar(grupoDTO);
        produtoNegocio.inserir(produtoDTO);

        grupoDTO = grupoAlimentarNegocio.pesquisaPorNome("Legumes");

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Batata Inglesa");
        produtoDTO.setEstoque(2000);
        produtoDTO.setEstoqueMinimo(300);
        produtoDTO.setCustoUnidade(1.0f);
        produtoDTO.setValorEnergetico(80);
        produtoDTO.setGrupoAlimentar(grupoDTO);
        produtoNegocio.inserir(produtoDTO);

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Batata Doce");
        produtoDTO.setEstoque(100);
        produtoDTO.setEstoqueMinimo(200);
        produtoDTO.setCustoUnidade(1.3f);
        produtoDTO.setValorEnergetico(70);
        produtoDTO.setGrupoAlimentar(grupoDTO);
        produtoNegocio.inserir(produtoDTO);

        grupoDTO = grupoAlimentarNegocio.pesquisaPorNome("Carboidratos");

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Arroz Branco");
        produtoDTO.setEstoque(1000);
        produtoDTO.setEstoqueMinimo(500);
        produtoDTO.setCustoUnidade(1.7f);
        produtoDTO.setValorEnergetico(100);
        produtoDTO.setGrupoAlimentar(grupoDTO);
        produtoNegocio.inserir(produtoDTO);

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Arroz Integral");
        produtoDTO.setEstoque(1000);
        produtoDTO.setEstoqueMinimo(500);
        produtoDTO.setCustoUnidade(1.9f);
        produtoDTO.setValorEnergetico(90);
        produtoDTO.setGrupoAlimentar(grupoDTO);
        produtoNegocio.inserir(produtoDTO);

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Fubá de Milho");
        produtoDTO.setEstoque(500);
        produtoDTO.setEstoqueMinimo(200);
        produtoDTO.setCustoUnidade(1.4f);
        produtoDTO.setValorEnergetico(75);
        produtoDTO.setGrupoAlimentar(grupoDTO);
        produtoNegocio.inserir(produtoDTO);
    }

    private void inserirTipoPreparo() throws NotValidDataException, NotFoundException {

        TipoPreparoDTO tipoPreparoDTO = new TipoPreparoDTO();
        tipoPreparoDTO.setDescricao("Cozimento em agua");
        tipoPreparoNegocio.inserir(tipoPreparoDTO);

        tipoPreparoDTO = new TipoPreparoDTO();
        tipoPreparoDTO.setDescricao("Assado no forno");
        tipoPreparoNegocio.inserir(tipoPreparoDTO);

        tipoPreparoDTO = new TipoPreparoDTO();
        tipoPreparoDTO.setDescricao("Grelhado");
        tipoPreparoNegocio.inserir(tipoPreparoDTO);
    }

    private void inserirEntregador() throws NotValidDataException, NotFoundException {

        EntregadorDTO entregadorDTO = new EntregadorDTO();
        entregadorDTO.setNome("Entregador 01");
        entregadorDTO.setTelefone("65 99999-7070");
        entregadorDTO.setRG("456789-1");
        entregadorDTO.setCPF("234.432.567-12");
        entregadorNegocio.inserir(entregadorDTO);

        entregadorDTO = new EntregadorDTO();
        entregadorDTO.setNome("Entregador 02");
        entregadorDTO.setTelefone("65 98888-7070");
        entregadorDTO.setRG("987654-5");
        entregadorDTO.setCPF("345.765.890-20");
        entregadorNegocio.inserir(entregadorDTO);
    }

    private void inserirBairro() throws NotValidDataException {

        BairroDTO bairroDTO = new BairroDTO();
        bairroDTO.setNome("Centro");
        bairroDTO.setCustoEntrega(7.00f);
        bairroNegocio.inserir(bairroDTO);

        bairroDTO = new BairroDTO();
        bairroDTO.setNome("Coxipo");
        bairroDTO.setCustoEntrega(8.00f);
        bairroNegocio.inserir(bairroDTO);

        bairroDTO = new BairroDTO();
        bairroDTO.setNome("Jardim Tres Americas");
        bairroDTO.setCustoEntrega(10.00f);
        bairroNegocio.inserir(bairroDTO);
    }

    private void inserirCliente() throws NotValidDataException, NotFoundException {

        BairroDTO bairroDTO = bairroNegocio.pesquisaPorNome("Centro");
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente 01");
        clienteDTO.setCPF("234.345.656-55");
        clienteDTO.setRG("234567-9");
        clienteDTO.setTelefone("65 99999-7070");
        clienteDTO.setLogradouro("Rua das flores");
        clienteDTO.setNumero("123");
        clienteDTO.setPontoReferencia("Proximo a nada");
        clienteDTO.setBairro(bairroDTO);
        clienteNegocio.inserir(clienteDTO);

        bairroDTO = bairroNegocio.pesquisaPorNome("Coxipo");
        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente 02");
        clienteDTO.setCPF("123.432.678-99");
        clienteDTO.setRG("123456-8");
        clienteDTO.setTelefone("65 98888-3030");
        clienteDTO.setLogradouro("Rua das pedras");
        clienteDTO.setNumero("456");
        clienteDTO.setPontoReferencia("Final da rua");
        clienteDTO.setBairro(bairroDTO);
        clienteNegocio.inserir(clienteDTO);
    }

    private void inserirGrupoAlimentar() throws NotValidDataException, NotFoundException {

        GrupoAlimentarDTO grupoDTO = new GrupoAlimentarDTO();
        grupoDTO.setNome("Carboidratos");
        grupoAlimentarNegocio.inserir(grupoDTO);

        grupoDTO = new GrupoAlimentarDTO();
        grupoDTO.setNome("Legumes");
        grupoAlimentarNegocio.inserir(grupoDTO);

        grupoDTO = new GrupoAlimentarDTO();
        grupoDTO.setNome("Proteinas");
        grupoAlimentarNegocio.inserir(grupoDTO);

        grupoDTO = new GrupoAlimentarDTO();
        grupoDTO.setNome("Verduras");
        grupoAlimentarNegocio.inserir(grupoDTO);

    }
}
