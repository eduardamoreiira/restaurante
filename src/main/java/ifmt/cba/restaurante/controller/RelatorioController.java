package ifmt.cba.restaurante.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.repository.PedidoRepository;
import ifmt.cba.restaurante.repository.ProdutoRepository;
import ifmt.cba.restaurante.repository.RegistroEstoqueRepository;
import ifmt.cba.restaurante.repository.BairroRepository;
import ifmt.cba.restaurante.entity.Pedido;
import ifmt.cba.restaurante.entity.Produto;
import ifmt.cba.restaurante.entity.RegistroEstoque;
import ifmt.cba.restaurante.entity.Bairro;
import ifmt.cba.restaurante.dto.MovimentoEstoqueDTO;
import ifmt.cba.restaurante.dto.EstadoPedidoDTO;
import ifmt.cba.restaurante.exception.NotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller para geração de relatórios do sistema
 * Este controller permite consultar pedidos por período e gerar relatórios mensais.
 * @author Cezarino
 */

@RestController
@RequestMapping("/relatorio")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RegistroEstoqueRepository registroEstoqueRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private BairroRepository bairroRepository;

    // Armazena o percentual de custo fixo em memória (pode ser substituído por persistência real futuramente)
    private static double percentualCustoFixo = 0.0;

    /**
     * Endpoint para consultar pedidos por período
     * @param dataInicial Data inicial do período
     * @param dataFinal Data final do período
     * @return ResponseEntity com os dados do relatório
     */
    @GetMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> consultarPedidos(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal) {
        
        try {
            // Busca os pedidos no período especificado
            List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(dataInicial, dataFinal);
            
            if (pedidos.isEmpty()) {
                throw new NotFoundException("Nenhum pedido encontrado no período especificado");
            }

            // Monta o resultado com informações simplificadas dos pedidos
            List<Map<String, Object>> pedidosSimplificados = pedidos.stream()
                .map(pedido -> {
                    Map<String, Object> pedidoMap = new HashMap<>();
                    pedidoMap.put("codigo", pedido.getCodigo());
                    pedidoMap.put("data", pedido.getDataPedido());
                    pedidoMap.put("cliente", pedido.getCliente().getNome());
                    pedidoMap.put("itens", pedido.getListaItens().stream()
                        .map(item -> Map.of(
                            "produto", item.getPreparoProduto().getNome(),
                            "quantidade", item.getQuantidadePorcao()
                        ))
                        .toList());
                    return pedidoMap;
                })
                .toList();

            // Monta o resultado final
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("periodo", Map.of(
                "dataInicial", dataInicial,
                "dataFinal", dataFinal
            ));
            resultado.put("totalPedidos", pedidos.size());
            resultado.put("pedidos", pedidosSimplificados);
            
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao gerar relatório: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar totalizações mensais
     * @param mes Mês para consulta (1-12)
     * @param ano Ano para consulta
     * @return ResponseEntity com os dados do relatório mensal
     */
    @GetMapping("/mensal")
    public ResponseEntity<Map<String, Object>> consultarRelatorioMensal(
            @RequestParam int mes,
            @RequestParam int ano) {

        try {
            if (mes < 1 || mes > 12) {
                throw new IllegalArgumentException("Mês inválido");
            }

            LocalDate dataInicial = LocalDate.of(ano, mes, 1);
            LocalDate dataFinal = dataInicial.plusMonths(1).minusDays(1);
            
            List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(dataInicial, dataFinal);

            if (pedidos.isEmpty()) {
                throw new NotFoundException("Nenhum pedido encontrado no período especificado");
            }

            // Agrupa pedidos por dia
            Map<LocalDate, List<Map<String, Object>>> pedidosPorDia = pedidos.stream()
                .collect(Collectors.groupingBy(
                    Pedido::getDataPedido,
                    Collectors.mapping(
                        pedido -> Map.of(
                            "codigo", pedido.getCodigo(),
                            "cliente", pedido.getCliente().getNome(),
                            "itens", pedido.getListaItens().stream()
                                .map(item -> Map.of(
                                    "produto", item.getPreparoProduto().getNome(),
                                    "quantidade", item.getQuantidadePorcao()
                                ))
                                .toList()
                        ),
                        Collectors.toList()
                    )
                ));

            // Monta o resultado
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("mes", mes);
            resultado.put("ano", ano);
            resultado.put("totalPedidos", pedidos.size());
            resultado.put("pedidosPorDia", pedidosPorDia);
            
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Parâmetro inválido");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(400).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao gerar relatório: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar produtos descartados por período
     * @param dataInicial Data inicial do período
     * @param dataFinal Data final do período
     * @return ResponseEntity com os dados do relatório de descartes
     */
    @GetMapping("/descartes")
    public ResponseEntity<Map<String, Object>> consultarDescartes(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal) {
        
        try {
            // Busca os registros de estoque com movimento do tipo VENCIMENTO e DANIFICADO no período
            List<RegistroEstoque> registros = registroEstoqueRepository.findByDataBetweenAndMovimentoIn(
            dataInicial, 
            dataFinal, 
            List.of(MovimentoEstoqueDTO.VENCIMENTO, MovimentoEstoqueDTO.DANIFICADO)
            );
            
            if (registros.isEmpty()) {
            throw new NotFoundException("Nenhum descarte encontrado no período especificado");
            }

            // Agrupa os descartes por produto
            Map<String, List<Map<String, Object>>> descartesPorProduto = registros.stream()
                .collect(Collectors.groupingBy(
                    registro -> registro.getProduto().getNome(),
                    Collectors.mapping(
                        registro -> Map.of(
                            "data", registro.getData(),
                            "quantidade", registro.getQuantidade()
                        ),
                        Collectors.toList()
                    )
                ));

            // Calcula total por produto
            Map<String, Integer> totaisPorProduto = registros.stream()
                .collect(Collectors.groupingBy(
                    registro -> registro.getProduto().getNome(),
                    Collectors.summingInt(RegistroEstoque::getQuantidade)
                ));

            // Monta o resultado
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("periodo", Map.of(
                "dataInicial", dataInicial,
                "dataFinal", dataFinal
            ));
            resultado.put("totalProdutosDescartados", registros.stream()
                .mapToInt(RegistroEstoque::getQuantidade)
                .sum());
            resultado.put("totaisPorProduto", totaisPorProduto);
            resultado.put("descartesPorProduto", descartesPorProduto);
            
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao gerar relatório: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar os 10 produtos mais pedidos por período
     * @param dataInicial Data inicial do período
     * @param dataFinal Data final do período
     * @return ResponseEntity com os dados do relatório dos produtos mais pedidos
     */
    @GetMapping("/produtos-mais-pedidos")
    public ResponseEntity<Map<String, Object>> consultarProdutosMaisPedidos(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal) {
        
        try {
            List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(dataInicial, dataFinal);
            
            if (pedidos.isEmpty()) {
                throw new NotFoundException("Nenhum pedido encontrado no período especificado");
            }

            // Agrupa e soma as quantidades por produto
            Map<String, Integer> quantidadePorProduto = pedidos.stream()
                .flatMap(pedido -> pedido.getListaItens().stream())
                .collect(Collectors.groupingBy(
                    item -> item.getPreparoProduto().getProduto().getNome(),
                    Collectors.summingInt(item -> item.getQuantidadePorcao())
                ));

            // Obtém os 10 produtos mais pedidos
            List<Map<String, Object>> produtosMaisPedidos = quantidadePorProduto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> produtoMap = new HashMap<>();
                    produtoMap.put("produto", entry.getKey());
                    produtoMap.put("quantidadePedida", entry.getValue());
                    return produtoMap;
                })
                .collect(Collectors.toList());

            // Monta o resultado
            Map<String, Object> resultado = new HashMap<>();
            Map<String, Object> periodoMap = new HashMap<>();
            periodoMap.put("dataInicial", dataInicial);
            periodoMap.put("dataFinal", dataFinal);
            resultado.put("periodo", periodoMap);
            resultado.put("totalPedidos", pedidos.size());
            resultado.put("produtosMaisPedidos", produtosMaisPedidos);
            
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao gerar relatório: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar os 10 produtos menos pedidos por período
     * @param dataInicial Data inicial do período
     * @param dataFinal Data final do período
     * @return ResponseEntity com os dados do relatório dos produtos menos pedidos
     */
    @GetMapping("/produtos-menos-pedidos")
    public ResponseEntity<Map<String, Object>> consultarProdutosMenosPedidos(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal) {
        
        try {
            // Busca todos os produtos cadastrados
            List<Produto> todosProdutos = produtoRepository.findAll();
            
            if (todosProdutos.isEmpty()) {
                throw new NotFoundException("Nenhum produto cadastrado no sistema");
            }

            List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(dataInicial, dataFinal);
            
            // Inicializa o mapa com todos os produtos com quantidade zero
            Map<String, Integer> quantidadePorProduto = new HashMap<>();
            todosProdutos.forEach(produto -> quantidadePorProduto.put(produto.getNome(), 0));

            // Se houver pedidos, atualiza as quantidades dos produtos pedidos
            if (!pedidos.isEmpty()) {
                pedidos.stream()
                    .flatMap(pedido -> pedido.getListaItens().stream())
                    .forEach(item -> {
                        String nomeProduto = item.getPreparoProduto().getProduto().getNome();
                        quantidadePorProduto.merge(nomeProduto, item.getQuantidadePorcao(), Integer::sum);
                    });
            }

            // Obtém os 10 produtos menos pedidos
            List<Map<String, Object>> produtosMenosPedidos = quantidadePorProduto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> produtoMap = new HashMap<>();
                    produtoMap.put("produto", entry.getKey());
                    produtoMap.put("quantidadePedida", entry.getValue());
                    return produtoMap;
                })
                .collect(Collectors.toList());

            // Monta o resultado
            Map<String, Object> resultado = new HashMap<>();
            Map<String, Object> periodoMap = new HashMap<>();
            periodoMap.put("dataInicial", dataInicial);
            periodoMap.put("dataFinal", dataFinal);
            resultado.put("periodo", periodoMap);
            resultado.put("totalPedidos", pedidos.size());
            resultado.put("produtosMenosPedidos", produtosMenosPedidos);
            
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao gerar relatório: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar o tempo médio de produção dos pedidos por período
     * @param dataInicial Data inicial do período
     * @param dataFinal Data final do período
     * @return ResponseEntity com os dados do tempo médio de produção
     */
    @GetMapping("/tempo-medio-producao")
    public ResponseEntity<Map<String, Object>> consultarTempoMedioProducao(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal) {
        
        try {
            List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(dataInicial, dataFinal);
            
            if (pedidos.isEmpty()) {
                throw new NotFoundException("Nenhum pedido encontrado no período especificado");
            }

            // Filtra apenas pedidos que passaram pelo fluxo completo de produção (REGISTRADO -> PRONTO)
            List<Pedido> pedidosProcessados = pedidos.stream()
                .filter(pedido -> 
                    pedido.getHoraPedido() != null && 
                    pedido.getHoraPronto() != null && 
                    (pedido.getEstado() == EstadoPedidoDTO.PRONTO || 
                     pedido.getEstado() == EstadoPedidoDTO.ENTREGA || 
                     pedido.getEstado() == EstadoPedidoDTO.CONCLUIDO))
                .collect(Collectors.toList());

            if (pedidosProcessados.isEmpty()) {
                throw new NotFoundException("Nenhum pedido com produção finalizada encontrado no período especificado");
            }

            // Calcula estatísticas de tempo por estado
            Map<EstadoPedidoDTO, Long> quantidadePorEstado = pedidos.stream()
                .collect(Collectors.groupingBy(
                    Pedido::getEstado,
                    Collectors.counting()
                ));
            
            // Calcula tempo médio geral (do registro até ficar pronto)
            double tempoMedioMinutos = pedidosProcessados.stream()
                .mapToLong(pedido -> {
                    long minutos = java.time.Duration.between(
                        pedido.getHoraPedido(),
                        pedido.getHoraPronto()
                    ).toMinutes();
                    return minutos;
                })
                .average()
                .orElse(0.0);

            // Calcula tempo mínimo e máximo
            long tempoMinimo = pedidosProcessados.stream()
                .mapToLong(pedido -> java.time.Duration.between(
                    pedido.getHoraPedido(),
                    pedido.getHoraPronto()
                ).toMinutes())
                .min()
                .orElse(0);

            long tempoMaximo = pedidosProcessados.stream()
                .mapToLong(pedido -> java.time.Duration.between(
                    pedido.getHoraPedido(),
                    pedido.getHoraPronto()
                ).toMinutes())
                .max()
                .orElse(0);

            // Calcula tempo médio por tipo de produto
            Map<String, Map<String, Object>> tempoMedioPorProduto = pedidosProcessados.stream()
                .flatMap(pedido -> pedido.getListaItens().stream()
                    .map(item -> Map.entry(
                        item.getPreparoProduto().getNome(),
                        java.time.Duration.between(pedido.getHoraPedido(), pedido.getHoraPronto()).toMinutes()
                    )))
                .collect(Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            double media = list.stream()
                                .mapToLong(Map.Entry::getValue)
                                .average()
                                .orElse(0.0);
                            return Map.of(
                                "tempoMedioMinutos", media,
                                "quantidadePedidos", list.size()
                            );
                        }
                    )
                ));

            // Monta o resultado
            Map<String, Object> resultado = new HashMap<>();
            Map<String, Object> periodoMap = new HashMap<>();
            periodoMap.put("dataInicial", dataInicial);
            periodoMap.put("dataFinal", dataFinal);
            resultado.put("periodo", periodoMap);
            resultado.put("totalPedidosProcessados", pedidosProcessados.size());
            resultado.put("pedidosPorEstado", quantidadePorEstado);
            resultado.put("tempoMedioMinutos", tempoMedioMinutos);
            resultado.put("tempoMinimoMinutos", tempoMinimo);
            resultado.put("tempoMaximoMinutos", tempoMaximo);
            resultado.put("detalhePorProduto", tempoMedioPorProduto);
            
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao gerar relatório: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar o tempo médio de entrega entre o término da produção e a entrega concluída
     * @param dataInicial Data inicial do período
     * @param dataFinal Data final do período
     * @return ResponseEntity com os dados do tempo médio de entrega
     */
    @GetMapping("/tempo-medio-entrega")
    public ResponseEntity<Map<String, Object>> consultarTempoMedioEntrega(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicial,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal) {
        
        try {
            List<Pedido> pedidos = pedidoRepository.findByDataPedidoBetween(dataInicial, dataFinal);
            
            if (pedidos.isEmpty()) {
                throw new NotFoundException("Nenhum pedido encontrado no período especificado");
            }

            // Filtra apenas pedidos que foram entregues (tem horário de conclusão)
            List<Pedido> pedidosEntregues = pedidos.stream()
                .filter(pedido -> 
                    pedido.getHoraPronto() != null && 
                    pedido.getHoraFinalizado() != null && 
                    pedido.getEstado() == EstadoPedidoDTO.CONCLUIDO)
                .collect(Collectors.toList());

            if (pedidosEntregues.isEmpty()) {
                throw new NotFoundException("Nenhum pedido entregue encontrado no período especificado");
            }

            // Calcula estatísticas de tempo por estado
            Map<EstadoPedidoDTO, Long> quantidadePorEstado = pedidos.stream()
                .collect(Collectors.groupingBy(
                    Pedido::getEstado,
                    Collectors.counting()
                ));
            
            // Calcula tempo médio geral (do término da produção até a entrega)
            double tempoMedioMinutos = pedidosEntregues.stream()
                .mapToLong(pedido -> {
                    long minutos = java.time.Duration.between(
                        pedido.getHoraPronto(),
                        pedido.getHoraFinalizado()
                    ).toMinutes();
                    return minutos;
                })
                .average()
                .orElse(0.0);

            // Calcula tempo mínimo e máximo
            long tempoMinimo = pedidosEntregues.stream()
                .mapToLong(pedido -> java.time.Duration.between(
                    pedido.getHoraPronto(),
                    pedido.getHoraFinalizado()
                ).toMinutes())
                .min()
                .orElse(0);

            long tempoMaximo = pedidosEntregues.stream()
                .mapToLong(pedido -> java.time.Duration.between(
                    pedido.getHoraPronto(),
                    pedido.getHoraFinalizado()
                ).toMinutes())
                .max()
                .orElse(0);

            // Calcula tempo médio por bairro
            Map<String, Map<String, Object>> tempoMedioPorBairro = pedidosEntregues.stream()
                .map(pedido -> Map.entry(
                    pedido.getCliente().getBairro().getNome(),
                    java.time.Duration.between(pedido.getHoraPronto(), pedido.getHoraFinalizado()).toMinutes()
                ))
                .collect(Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            double media = list.stream()
                                .mapToLong(Map.Entry::getValue)
                                .average()
                                .orElse(0.0);
                            return Map.of(
                                "tempoMedioMinutos", media,
                                "quantidadeEntregas", list.size()
                            );
                        }
                    )
                ));

            // Calcula tempo médio por entregador
            Map<String, Map<String, Object>> tempoMedioPorEntregador = pedidosEntregues.stream()
                .filter(pedido -> pedido.getEntregador() != null)
                .map(pedido -> Map.entry(
                    pedido.getEntregador().getNome(),
                    java.time.Duration.between(pedido.getHoraPronto(), pedido.getHoraFinalizado()).toMinutes()
                ))
                .collect(Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            double media = list.stream()
                                .mapToLong(Map.Entry::getValue)
                                .average()
                                .orElse(0.0);
                            return Map.of(
                                "tempoMedioMinutos", media,
                                "quantidadeEntregas", list.size()
                            );
                        }
                    )
                ));

            // Monta o resultado
            Map<String, Object> resultado = new HashMap<>();
            Map<String, Object> periodoMap = new HashMap<>();
            periodoMap.put("dataInicial", dataInicial);
            periodoMap.put("dataFinal", dataFinal);
            resultado.put("periodo", periodoMap);
            resultado.put("totalPedidosEntregues", pedidosEntregues.size());
            resultado.put("pedidosPorEstado", quantidadePorEstado);
            resultado.put("tempoMedioMinutos", tempoMedioMinutos);
            resultado.put("tempoMinimoMinutos", tempoMinimo);
            resultado.put("tempoMaximoMinutos", tempoMaximo);
            resultado.put("detalhePorBairro", tempoMedioPorBairro);
            resultado.put("detalhePorEntregador", tempoMedioPorEntregador);
            
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao gerar relatório: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para configurar o percentual de custo fixo de produção
     * @param percentual Valor percentual (ex: 10.5 para 10,5%)
     * @return Mensagem de confirmação
     */
    @PostMapping("/custo-fixo")
    public ResponseEntity<Map<String, Object>> configurarCustoFixo(@RequestParam double percentual) {
        if (percentual < 0) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Percentual inválido");
            erro.put("mensagem", "O percentual não pode ser negativo.");
            return ResponseEntity.status(400).body(erro);
        }
        percentualCustoFixo = percentual;
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("mensagem", "Percentual de custo fixo configurado com sucesso.");
        resposta.put("percentualCustoFixo", percentualCustoFixo);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Endpoint para consultar o percentual de custo fixo de produção configurado
     * @return Percentual atual
     */
    @GetMapping("/custo-fixo")
    public ResponseEntity<Map<String, Object>> consultarCustoFixo() {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("percentualCustoFixo", percentualCustoFixo);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Endpoint para configurar o custo de entrega para um bairro
     * @param bairroId ID do bairro
     * @param valor Valor da entrega
     * @return Mensagem de confirmação
     */
    @PostMapping("/custo-entrega")
    public ResponseEntity<Map<String, Object>> configurarCustoEntrega(
            @RequestParam Integer bairroId,
            @RequestParam double valor) {
        try {
            Bairro bairro = bairroRepository.findById(bairroId)
                .orElseThrow(() -> new NotFoundException("Bairro não encontrado"));

            if (valor < 0) {
                Map<String, Object> erro = new HashMap<>();
                erro.put("erro", "Valor inválido");
                erro.put("mensagem", "O valor da entrega não pode ser negativo.");
                return ResponseEntity.status(400).body(erro);
            }

            bairro.setCustoEntrega((float) valor);
            bairroRepository.save(bairro);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Custo de entrega configurado com sucesso.");
            resposta.put("bairro", bairro.getNome());
            resposta.put("custoEntrega", valor);
            return ResponseEntity.ok(resposta);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao configurar custo de entrega: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar o custo de entrega de um bairro específico
     * @param bairroId ID do bairro
     * @return Valor da entrega para o bairro
     */
    @GetMapping("/custo-entrega")
    public ResponseEntity<Map<String, Object>> consultarCustoEntrega(@RequestParam Integer bairroId) {
        try {
            Bairro bairro = bairroRepository.findById(bairroId)
                .orElseThrow(() -> new NotFoundException("Bairro não encontrado"));

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("bairro", bairro.getNome());
            resposta.put("valorEntrega", bairro.getCustoEntrega());
            return ResponseEntity.ok(resposta);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao consultar custo de entrega: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para listar todos os custos de entrega configurados
     * @return Lista com os custos de entrega por bairro
     */
    @GetMapping("/custos-entrega")
    public ResponseEntity<Map<String, Object>> listarCustosEntrega() {
        try {
            List<Bairro> bairros = bairroRepository.findAll();

            if (bairros.isEmpty()) {
                throw new NotFoundException("Nenhum bairro cadastrado");
            }

            List<Map<String, Object>> custosEntrega = bairros.stream()
                .map(bairro -> {
                    Map<String, Object> custoMap = new HashMap<>();
                    custoMap.put("bairroId", bairro.getCodigo());
                    custoMap.put("bairro", bairro.getNome());
                    custoMap.put("custoEntrega", bairro.getCustoEntrega());
                    return custoMap;
                })
                .collect(Collectors.toList());

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("custosEntrega", custosEntrega);
            return ResponseEntity.ok(resposta);

        } catch (NotFoundException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Não encontrado");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao listar custos de entrega: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para configurar o custo de entrega para vários bairros em lote
     * @param configuracoes Lista de configurações com bairroId e valor
     * @return Mensagem de confirmação com os custos atualizados
     */
    @PostMapping("/custos-entrega/lote")
    public ResponseEntity<Map<String, Object>> configurarCustosEntregaLote(
            @RequestBody List<Map<String, Object>> configuracoes) {
        try {
            List<Map<String, Object>> custosAtualizados = new ArrayList<>();
            List<String> erros = new ArrayList<>();

            for (Map<String, Object> config : configuracoes) {
                try {
                    Integer bairroId = ((Number) config.get("bairroId")).intValue();
                    double valor = ((Number) config.get("valor")).doubleValue();

                    if (valor < 0) {
                        erros.add("Bairro " + bairroId + ": O valor da entrega não pode ser negativo");
                        continue;
                    }

                    Bairro bairro = bairroRepository.findById(bairroId)
                        .orElseThrow(() -> new NotFoundException("Bairro não encontrado: " + bairroId));

                    bairro.setCustoEntrega((float) valor);
                    bairroRepository.save(bairro);

                    Map<String, Object> custoAtualizado = new HashMap<>();
                    custoAtualizado.put("bairroId", bairroId);
                    custoAtualizado.put("bairro", bairro.getNome());
                    custoAtualizado.put("custoEntrega", valor);
                    custosAtualizados.add(custoAtualizado);

                } catch (NotFoundException e) {
                    erros.add(e.getMessage());
                } catch (Exception e) {
                    erros.add("Erro ao processar bairro: " + e.getMessage());
                }
            }

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Processamento de custos de entrega concluído");
            resposta.put("custosAtualizados", custosAtualizados);
            
            if (!erros.isEmpty()) {
                resposta.put("erros", erros);
                return ResponseEntity.status(207).body(resposta); // 207 Multi-Status
            }

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao configurar custos de entrega: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

}