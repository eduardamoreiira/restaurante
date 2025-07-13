package ifmt.cba.restaurante.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.dto.MovimentoEstoqueDTO;
import ifmt.cba.restaurante.dto.RegistroEstoqueDTO;
import ifmt.cba.restaurante.entity.Produto;
import ifmt.cba.restaurante.dto.ProdutoDTO;
import ifmt.cba.restaurante.negocio.RegistroEstoqueNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.ProdutoRepository;

//Implementação Eduarda

@RestController
@RequestMapping("/estoque/registros")
public class RegistroEstoqueController {

    @Autowired
    private RegistroEstoqueNegocio registroEstoqueNegocio;

    // Endpoint para inserir um registro de estoque (POST)
    @PostMapping
    public ResponseEntity<RegistroEstoqueDTO> inserirRegistroEstoque(
            @RequestBody RegistroEstoqueDTO registroEstoqueDTO) {
        try {
            RegistroEstoqueDTO novoRegistro = registroEstoqueNegocio.inserir(registroEstoqueDTO);
            return new ResponseEntity<>(novoRegistro, HttpStatus.CREATED);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) { // Captura NotFoundException do produto
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para reverter um registro de estoque (DELETE)
    @DeleteMapping
    public ResponseEntity<RegistroEstoqueDTO> reverterRegistroEstoque(
            @RequestBody RegistroEstoqueDTO registroEstoqueDTO) {
        try {
            RegistroEstoqueDTO registroRevertido = registroEstoqueNegocio.excluir(registroEstoqueDTO);
            return new ResponseEntity<>(registroRevertido, HttpStatus.OK); // Retorna 200 OK com o registro revertido
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoints para buscar registros de estoque
    @GetMapping("/{codigo}")
    public ResponseEntity<RegistroEstoqueDTO> pesquisarRegistroEstoquePorCodigo(@PathVariable int codigo) {
        try {
            RegistroEstoqueDTO registro = registroEstoqueNegocio.pesquisaCodigo(codigo);
            if (registro == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(registro, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para buscar todos os registros de estoque (GET)
    @GetMapping("/movimento/{movimento}")
    public ResponseEntity<List<RegistroEstoqueDTO>> buscarRegistrosPorMovimento(
            @PathVariable MovimentoEstoqueDTO movimento) {
        try {
            List<RegistroEstoqueDTO> registros = registroEstoqueNegocio.buscarPorMovimento(movimento);
            if (registros.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(registros, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para buscar registros por movimento e data (GET)
    @GetMapping("/movimento-data")
    public ResponseEntity<List<RegistroEstoqueDTO>> buscarRegistrosPorMovimentoEData(
            @RequestParam MovimentoEstoqueDTO movimento,
            @RequestParam LocalDate data) {
        try {
            List<RegistroEstoqueDTO> registros = registroEstoqueNegocio.buscarPorMovimentoEData(movimento, data);
            if (registros.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(registros, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para buscar registros por produto (POST)
    @GetMapping("/produto/{codigoProduto}")
    public ResponseEntity<List<RegistroEstoqueDTO>> buscarRegistrosPorProduto(
            @PathVariable int codigoProduto) {
        try {
            // Cria uma instância de Produto com o código fornecido
            Produto produto = new Produto();

            // Passa a entidade Produto para o método do negócio
            List<RegistroEstoqueDTO> registros = registroEstoqueNegocio.buscarPorProduto(produto);
            if (registros.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(registros, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}