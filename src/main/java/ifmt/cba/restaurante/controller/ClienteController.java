package ifmt.cba.restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ifmt.cba.restaurante.dto.ClienteDTO;
import ifmt.cba.restaurante.negocio.ClienteNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

//Implementação Eduarda

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteNegocio clienteNegocio;

    // Endpoint para CRIAR um novo Cliente (POST)
    @PostMapping
    public ResponseEntity<ClienteDTO> inserirCliente(@RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO novoCliente = clienteNegocio.inserir(clienteDTO);
            return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para ATUALIZAR um Cliente (PUT)
    @PutMapping
    public ResponseEntity<ClienteDTO> alterarCliente(@RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO clienteAtualizado = clienteNegocio.alterar(clienteDTO);
            return new ResponseEntity<>(clienteAtualizado, HttpStatus.OK);
        } catch (NotValidDataException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para EXCLUIR um Cliente por código (DELETE)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirCliente(@PathVariable int codigo) {
        try {
            clienteNegocio.excluir(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotValidDataException e) { // Ex: "Nao pode excluir cliente relacionado com pedidos"
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoints para PESQUISAR Clientes
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> pesquisarTodosClientes() {
        try {
            List<ClienteDTO> clientes = clienteNegocio.pesquisaTodos();
            if (clientes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Cliente por parte do nome (GET)
    @GetMapping("/nome/{parteNome}")
    public ResponseEntity<ClienteDTO> pesquisarClientePorNome(@PathVariable String parteNome) {
        try {
            ClienteDTO cliente = clienteNegocio.pesquisaPorNome(parteNome);
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para PESQUISAR Cliente por CÓDIGO (GET)
    @GetMapping("/{codigo}")
    public ResponseEntity<ClienteDTO> pesquisarClientePorCodigo(@PathVariable int codigo) {
        try {
            ClienteDTO cliente = clienteNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}