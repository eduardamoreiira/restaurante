package ifmt.cba.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.dto.TipoPreparoDTO;
import ifmt.cba.restaurante.negocio.TipoPreparoNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

//Implementação Eduarda

@RestController
@RequestMapping("/tipos-preparo")
public class TipoPreparoController {

    @Autowired
    private TipoPreparoNegocio tipoPreparoNegocio;

    // Endpoint para CRIAR um novo Tipo de Preparo (POST)
    @PostMapping
    public ResponseEntity<TipoPreparoDTO> inserirTipoPreparo(@RequestBody TipoPreparoDTO tipoPreparoDTO) {
        try {
            TipoPreparoDTO novoTipoPreparo = tipoPreparoNegocio.inserir(tipoPreparoDTO);
            return new ResponseEntity<>(novoTipoPreparo, HttpStatus.CREATED);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para ATUALIZAR um Tipo de Preparo (PUT)
    @PutMapping
    public ResponseEntity<TipoPreparoDTO> alterarTipoPreparo(@RequestBody TipoPreparoDTO tipoPreparoDTO) {
        try {
            TipoPreparoDTO tipoPreparoAtualizado = tipoPreparoNegocio.alterar(tipoPreparoDTO);
            return new ResponseEntity<>(tipoPreparoAtualizado, HttpStatus.OK);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para EXCLUIR um Tipo de Preparo por código (DELETE)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirTipoPreparo(@PathVariable int codigo) {
        try {
            tipoPreparoNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotValidDataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR todos os Tipos de Preparo (GET)
    @GetMapping
    public ResponseEntity<List<TipoPreparoDTO>> pesquisarTodosTiposPreparo() {
        try {
            List<TipoPreparoDTO> tiposPreparo = tipoPreparoNegocio.pesquisaTodos();
            if (tiposPreparo.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tiposPreparo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoints adicionais para pesquisar tipos de preparo
    @GetMapping("/descricao/{parteDesc}")
    public ResponseEntity<TipoPreparoDTO> pesquisarTipoPreparoPorDescricao(@PathVariable String parteDesc) {
        try {
            TipoPreparoDTO tipoPreparo = tipoPreparoNegocio.pesquisaPorDescricao(parteDesc);
            return new ResponseEntity<>(tipoPreparo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Tipo de Preparo por CÓDIGO (GET)
    @GetMapping("/{codigo}")
    public ResponseEntity<TipoPreparoDTO> pesquisarTipoPreparoPorCodigo(@PathVariable int codigo) {
        try {
            TipoPreparoDTO tipoPreparo = tipoPreparoNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(tipoPreparo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}