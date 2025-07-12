package ifmt.cba.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.dto.EntregadorDTO;
import ifmt.cba.restaurante.negocio.EntregadorNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

//Implementação Eduarda

@RestController
@RequestMapping("/entregadores")
public class EntregadorController {

    @Autowired
    private EntregadorNegocio entregadorNegocio;

    // Endpoint para CRIAR um novo Entregador (POST)
    @PostMapping
    public ResponseEntity<EntregadorDTO> inserirEntregador(@RequestBody EntregadorDTO entregadorDTO) {
        try {
            EntregadorDTO novoEntregador = entregadorNegocio.inserir(entregadorDTO);
            return new ResponseEntity<>(novoEntregador, HttpStatus.CREATED);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para ATUALIZAR um Entregador (PUT)
    @PutMapping
    public ResponseEntity<EntregadorDTO> alterarEntregador(@RequestBody EntregadorDTO entregadorDTO) {
        try {
            EntregadorDTO entregadorAtualizado = entregadorNegocio.alterar(entregadorDTO);
            return new ResponseEntity<>(entregadorAtualizado, HttpStatus.OK); // Retorna 200 OK
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null); // Retorna 400 Bad Request
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found se o entregador não existe
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Erro genérico 500
        }
    }

    // Endpoint para EXCLUIR um Entregador por código (DELETE)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirEntregador(@PathVariable int codigo) {
        try {
            entregadorNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotValidDataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR TODOS os Entregadores (GET)
    @GetMapping
    public ResponseEntity<List<EntregadorDTO>> pesquisarTodosEntregadores() {
        try {
            List<EntregadorDTO> entregadores = entregadorNegocio.pesquisaTodos();
            if (entregadores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(entregadores, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Entregador por NOME (GET)
    @GetMapping("/nome/{parteNome}")
    public ResponseEntity<EntregadorDTO> pesquisarEntregadorPorNome(@PathVariable String parteNome) {
        try {
            EntregadorDTO entregador = entregadorNegocio.pesquisaPorNome(parteNome);
            return new ResponseEntity<>(entregador, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Entregador por CÓDIGO (GET)
    @GetMapping("/{codigo}")
    public ResponseEntity<EntregadorDTO> pesquisarEntregadorPorCodigo(@PathVariable int codigo) {
        try {
            EntregadorDTO entregador = entregadorNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(entregador, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}