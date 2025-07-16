package ifmt.cba.restaurante.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.repository.BairroRepository;
import ifmt.cba.restaurante.entity.Bairro;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/configuracoes")
@CrossOrigin(origins = "*")
public class ConfiguracoesController {
    
    @Autowired
    private BairroRepository bairroRepository;

    // Armazena o percentual de custo fixo em memória (pode ser substituído por persistência real futuramente)
    private static double percentualCustoFixo = 0.0;

    /**
     * Endpoint para consultar o percentual de custo fixo de produção
     * @return ResponseEntity com o percentual configurado
     */
    @GetMapping("/consultarcusto-fixo")
    public ResponseEntity<Map<String, Object>> consultarCustoFixo() {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("percentualCustoFixo", percentualCustoFixo);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Endpoint para definir o percentual de custo fixo de produção
     * @param requestBody Map contendo o percentual a ser configurado
     * @return ResponseEntity com mensagem de confirmação
     */
    @PutMapping("/custo-fixo")
    public ResponseEntity<Map<String, Object>> definirCustoFixo(@RequestBody Map<String, Object> requestBody) {
        try {
            double percentual = ((Number) requestBody.get("percentual")).doubleValue();

            if (percentual < 0) {
                throw new NotValidDataException("O percentual não pode ser negativo");
            }

            percentualCustoFixo = percentual;
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Percentual de custo fixo atualizado com sucesso");
            resposta.put("percentualCustoFixo", percentualCustoFixo);
            return ResponseEntity.ok(resposta);

        } catch (NotValidDataException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Dados inválidos");
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.status(400).body(erro);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno");
            erro.put("mensagem", "Erro ao configurar percentual: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para consultar todos os custos de entrega configurados
     * @return ResponseEntity com a lista de custos por bairro
     */
    @GetMapping("/custos-entrega")
    public ResponseEntity<Map<String, Object>> consultarCustosEntrega() {
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
            erro.put("mensagem", "Erro ao consultar custos de entrega: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * Endpoint para atualizar os custos de entrega por bairro
     * @param configuracoes Lista de configurações com bairroId e custoEntrega
     * @return ResponseEntity com os custos atualizados
     */
    @PutMapping("/custos-entrega")
    public ResponseEntity<Map<String, Object>> atualizarCustosEntrega(@RequestBody List<Map<String, Object>> configuracoes) {
        try {
            List<Map<String, Object>> custosAtualizados = new ArrayList<>();
            List<String> erros = new ArrayList<>();

            for (Map<String, Object> config : configuracoes) {
                try {
                    Integer bairroId = ((Number) config.get("bairroId")).intValue();
                    double custoEntrega = ((Number) config.get("custoEntrega")).doubleValue();

                    if (custoEntrega < 0) {
                        erros.add("Bairro " + bairroId + ": O custo de entrega não pode ser negativo");
                        continue;
                    }

                    Bairro bairro = bairroRepository.findById(bairroId)
                        .orElseThrow(() -> new NotFoundException("Bairro não encontrado: " + bairroId));

                    bairro.setCustoEntrega((float) custoEntrega);
                    bairroRepository.save(bairro);

                    Map<String, Object> custoAtualizado = new HashMap<>();
                    custoAtualizado.put("bairroId", bairroId);
                    custoAtualizado.put("bairro", bairro.getNome());
                    custoAtualizado.put("custoEntrega", custoEntrega);
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
            erro.put("mensagem", "Erro ao atualizar custos de entrega: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }
}
