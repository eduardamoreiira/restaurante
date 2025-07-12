package ifmt.cba.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.dto.ColaboradorDTO;
import ifmt.cba.restaurante.negocio.ColaboradorNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

//Implementação Eduarda

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

    @Autowired
    private ColaboradorNegocio colaboradorNegocio;

    // Endpoint para CRIAR um novo Colaborador (POST)
    @PostMapping
    public ResponseEntity<ColaboradorDTO> inserirColaborador(@RequestBody ColaboradorDTO colaboradorDTO) {
        try {
            ColaboradorDTO novoColaborador = colaboradorNegocio.inserir(colaboradorDTO);
            return new ResponseEntity<>(novoColaborador, HttpStatus.CREATED);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para ATUALIZAR um Colaborador (PUT)
    @PutMapping
    public ResponseEntity<ColaboradorDTO> alterarColaborador(@RequestBody ColaboradorDTO colaboradorDTO) {
        try {
            ColaboradorDTO colaboradorAtualizado = colaboradorNegocio.alterar(colaboradorDTO);
            return new ResponseEntity<>(colaboradorAtualizado, HttpStatus.OK);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para EXCLUIR um Colaborador por código (DELETE)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirColaborador(@PathVariable int codigo) {
        try {
            colaboradorNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotValidDataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR todos os Colaboradores (GET)
    @GetMapping
    public ResponseEntity<List<ColaboradorDTO>> pesquisarTodosColaboradores() {
        try {
            List<ColaboradorDTO> colaboradores = colaboradorNegocio.pesquisaTodos();
            if (colaboradores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(colaboradores, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoints adicionais para pesquisar colaboradores
    @GetMapping("/nome/{parteNome}")
    public ResponseEntity<ColaboradorDTO> pesquisarColaboradorPorParteNome(@PathVariable String parteNome) {
        try {
            ColaboradorDTO colaborador = colaboradorNegocio.pesquisaParteNome(parteNome);
            return new ResponseEntity<>(colaborador, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Colaborador por CÓDIGO (GET)
    @GetMapping("/{codigo}")
    public ResponseEntity<ColaboradorDTO> pesquisarColaboradorPorCodigo(@PathVariable int codigo) {
        try {
            ColaboradorDTO colaborador = colaboradorNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(colaborador, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}