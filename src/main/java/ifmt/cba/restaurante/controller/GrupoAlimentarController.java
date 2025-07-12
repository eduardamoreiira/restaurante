package ifmt.cba.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.dto.GrupoAlimentarDTO;
import ifmt.cba.restaurante.negocio.GrupoAlimentarNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

//Implementação Eduarda

@RestController
@RequestMapping("/grupos-alimentares") // Define a URL base para este controlador
public class GrupoAlimentarController {

    @Autowired
    private GrupoAlimentarNegocio grupoAlimentarNegocio;

    // Endpoint para CRIAR um novo Grupo Alimentar (POST)
    @PostMapping
    public ResponseEntity<GrupoAlimentarDTO> inserirGrupoAlimentar(@RequestBody GrupoAlimentarDTO grupoAlimentarDTO) {
        try {
            GrupoAlimentarDTO novoGrupo = grupoAlimentarNegocio.inserir(grupoAlimentarDTO);
            return new ResponseEntity<>(novoGrupo, HttpStatus.CREATED);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para ATUALIZAR um Grupo Alimentar (PUT)
    @PutMapping
    public ResponseEntity<GrupoAlimentarDTO> alterarGrupoAlimentar(@RequestBody GrupoAlimentarDTO grupoAlimentarDTO) {
        try {
            GrupoAlimentarDTO grupoAtualizado = grupoAlimentarNegocio.alterar(grupoAlimentarDTO);
            return new ResponseEntity<>(grupoAtualizado, HttpStatus.OK);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para EXCLUIR um Grupo Alimentar (DELETE)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirGrupoAlimentar(@PathVariable int codigo) {
        try {
            grupoAlimentarNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotValidDataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR TODOS os Grupos Alimentares (GET)
    @GetMapping
    public ResponseEntity<List<GrupoAlimentarDTO>> pesquisarTodosGruposAlimentares() {
        try {
            List<GrupoAlimentarDTO> grupos = grupoAlimentarNegocio.pesquisaTodos();
            if (grupos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(grupos, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Grupo Alimentar por NOME (GET)
    @GetMapping("/nome/{parteNome}")
    public ResponseEntity<GrupoAlimentarDTO> pesquisarGrupoAlimentarPorNome(@PathVariable String parteNome) {
        try {
            GrupoAlimentarDTO grupo = grupoAlimentarNegocio.pesquisaPorNome(parteNome);
            return new ResponseEntity<>(grupo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Grupo Alimentar por CÓDIGO (GET)
    @GetMapping("/{codigo}")
    public ResponseEntity<GrupoAlimentarDTO> pesquisarGrupoAlimentarPorCodigo(@PathVariable int codigo) {
        try {
            GrupoAlimentarDTO grupo = grupoAlimentarNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(grupo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
