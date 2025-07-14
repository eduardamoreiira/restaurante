package ifmt.cba.restaurante.controller;

import ifmt.cba.restaurante.dto.CardapioDTO;
import ifmt.cba.restaurante.negocio.CardapioNegocio;
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
@RequestMapping("/api/cardapios") 
public class CardapioController {

    @Autowired
    private CardapioNegocio cardapioNegocio;

    @PostMapping
    public ResponseEntity<CardapioDTO> inserirCardapio(@RequestBody CardapioDTO cardapioDTO) {
        try {
            CardapioDTO novoCardapio = cardapioNegocio.inserir(cardapioDTO);
            return new ResponseEntity<>(novoCardapio, HttpStatus.CREATED); // Retorna 201 Created
        } catch (NotValidDataException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request (validação, duplicidade)
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Erro inesperado
        }
    }


    @PutMapping("/{codigo}")
    public ResponseEntity<CardapioDTO> alterarCardapio(
            @PathVariable int codigo,
            @RequestBody CardapioDTO cardapioDTO) {
        try {
            cardapioDTO.setCodigo(codigo); 
            CardapioDTO cardapioAlterado = cardapioNegocio.alterar(cardapioDTO);
            return new ResponseEntity<>(cardapioAlterado, HttpStatus.OK); // Retorna 200 OK
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (NotValidDataException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirCardapio(@PathVariable int codigo) {
        try {
            cardapioNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotValidDataException e) { // Ex: se não puder ser excluído por alguma regra de negócio
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{codigo}")
    public ResponseEntity<CardapioDTO> pesquisaCardapioPorCodigo(@PathVariable int codigo) {
        try {
            CardapioDTO cardapio = cardapioNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(cardapio, HttpStatus.OK);
        } catch (NotValidDataException e) { 
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Mapeia para 404
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<CardapioDTO>> pesquisaTodosCardapios() {
        try {
            List<CardapioDTO> lista = cardapioNegocio.pesquisaTodos();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (NotValidDataException e) { 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 se a lista estiver vazia
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pesquisa-nome")
    public ResponseEntity<CardapioDTO> pesquisaCardapioPorNome(@RequestParam String nome) {
        try {
            CardapioDTO cardapio = cardapioNegocio.pesquisaPorNome(nome);
            return new ResponseEntity<>(cardapio, HttpStatus.OK);
        } catch (NotValidDataException e) { 
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
