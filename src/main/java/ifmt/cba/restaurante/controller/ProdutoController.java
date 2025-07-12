package ifmt.cba.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.dto.ProdutoDTO;
import ifmt.cba.restaurante.negocio.ProdutoNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

//Implementação Eduarda

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoNegocio produtoNegocio;

    // Endpoint para CRIAR um novo Produto (POST)
    @PostMapping
    public ResponseEntity<ProdutoDTO> inserirProduto(@RequestBody ProdutoDTO produtoDTO) {
        try {
            ProdutoDTO novoProd = produtoNegocio.inserir(produtoDTO);
            return new ResponseEntity<>(novoProd, HttpStatus.CREATED);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para ATUALIZAR um Produto (PUT)
    @PutMapping
    public ResponseEntity<ProdutoDTO> alterarProduto(@RequestBody ProdutoDTO produtoDTO) {
        try {
            ProdutoDTO produtoAtualizado = produtoNegocio.alterar(produtoDTO);
            return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para EXCLUIR um Produto por código (DELETE)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirProduto(@PathVariable int codigo) {
        try {
            produtoNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotValidDataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para CRIAR um novo Entregador (POST)
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> pesquisarTodosProdutos() {
        try {
            List<ProdutoDTO> produtos = produtoNegocio.pesquisaTodos();
            if (produtos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoints adicionais para pesquisar produtos
    @GetMapping("/nome/{parteNome}")
    public ResponseEntity<ProdutoDTO> pesquisarProdutoPorNome(@PathVariable String parteNome) {
        try {
            ProdutoDTO produto = produtoNegocio.pesquisaPorNome(parteNome);
            return new ResponseEntity<>(produto, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para pesquisar produtos abaixo do estoque mínimo
    @GetMapping("/estoque-minimo")
    public ResponseEntity<List<ProdutoDTO>> pesquisarProdutoAbaixoEstoqueMinimo() {
        try {
            List<ProdutoDTO> produtos = produtoNegocio.pesquisaProdutoAbaixoEstoqueMinimo();
            if (produtos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Produto por CÓDIGO (GET)
    @GetMapping("/{codigo}")
    public ResponseEntity<ProdutoDTO> pesquisarProdutoPorCodigo(@PathVariable int codigo) {
        try {
            ProdutoDTO produto = produtoNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(produto, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}