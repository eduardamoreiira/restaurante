package ifmt.cba.restaurante.controller;

import ifmt.cba.restaurante.dto.PreparoProdutoDTO;
import ifmt.cba.restaurante.dto.ProdutoDTO;
import ifmt.cba.restaurante.dto.TipoPreparoDTO;
import ifmt.cba.restaurante.negocio.PreparoProdutoNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/preparos-produtos")
public class PreparoProdutoController {

    @Autowired
    private PreparoProdutoNegocio preparoProdutoNegocio;

    @PostMapping
    public ResponseEntity<PreparoProdutoDTO> inserirPreparoProduto(@RequestBody PreparoProdutoDTO preparoProdutoDTO) {
        try {
            PreparoProdutoDTO novoPreparoProduto = preparoProdutoNegocio.inserir(preparoProdutoDTO);
            return new ResponseEntity<>(novoPreparoProduto, HttpStatus.CREATED); // Retorna 201 Created
        } catch (NotValidDataException e) {

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error
        }
    }


    @PutMapping("/{codigo}")
    public ResponseEntity<PreparoProdutoDTO> alterarPreparoProduto(
            @PathVariable int codigo,
            @RequestBody PreparoProdutoDTO preparoProdutoDTO) {
        try {

            preparoProdutoDTO.setCodigo(codigo);
            PreparoProdutoDTO preparoAlterado = preparoProdutoNegocio.alterar(preparoProdutoDTO);
            return new ResponseEntity<>(preparoAlterado, HttpStatus.OK); // Retorna 200 OK
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (NotValidDataException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirPreparoProduto(@PathVariable int codigo) {
        try {
            preparoProdutoNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content para exclusão bem-sucedida
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (NotValidDataException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{codigo}")
    public ResponseEntity<PreparoProdutoDTO> pesquisaPorCodigo(@PathVariable int codigo) {
        try {
            PreparoProdutoDTO preparo = preparoProdutoNegocio.pesquisaPorCodigo(codigo);
            return new ResponseEntity<>(preparo, HttpStatus.OK); // Retorna 200 OK
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<List<PreparoProdutoDTO>> pesquisaTodos() {
        try {
            List<PreparoProdutoDTO> lista = preparoProdutoNegocio.pesquisaTodos();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/pesquisa-nome")
    public ResponseEntity<PreparoProdutoDTO> pesquisaPorNome(@RequestParam String nome) {
        try {
            PreparoProdutoDTO preparo = preparoProdutoNegocio.pesquisaPorNome(nome);
            return new ResponseEntity<>(preparo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/pesquisa-por-produto")
    public ResponseEntity<List<PreparoProdutoDTO>> pesquisaPorProduto(@RequestBody ProdutoDTO produtoDTO) {
        try {
            List<PreparoProdutoDTO> lista = preparoProdutoNegocio.pesquisaPorProduto(produtoDTO);
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/pesquisa-por-tipo-preparo")
    public ResponseEntity<List<PreparoProdutoDTO>> pesquisaPorTipoPreparo(@RequestBody TipoPreparoDTO tipoPreparoDTO) {
        try {
            List<PreparoProdutoDTO> lista = preparoProdutoNegocio.pesquisaPorTipoPreparo(tipoPreparoDTO);
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/pesquisa-por-produto-e-tipo-preparo")
    public ResponseEntity<PreparoProdutoDTO> pesquisaPorProdutoETipoPreparo(
            @RequestBody ProdutoDTO produtoDTO,
            @RequestBody TipoPreparoDTO tipoPreparoDTO) {
        try {

            PreparoProdutoDTO preparo = preparoProdutoNegocio.pesquisaPorProdutoETipoPreparo(produtoDTO, tipoPreparoDTO);
            return new ResponseEntity<>(preparo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
