package ifmt.cba.restaurante.controller;

import ifmt.cba.restaurante.dto.PedidoDTO;
import ifmt.cba.restaurante.dto.EntregadorDTO;
import ifmt.cba.restaurante.dto.EstadoPedidoDTO;
import ifmt.cba.restaurante.negocio.PedidoNegocio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos") 
public class PedidoController {

    @Autowired 
    private PedidoNegocio pedidoNegocio;

    @PutMapping("/{codigo}/producao")
    public ResponseEntity<PedidoDTO> iniciarProducaoPedido(@PathVariable int codigo) { 
        try {

            PedidoDTO pedidoParaAtualizar = pedidoNegocio.pesquisaCodigo(codigo);
            
            pedidoNegocio.mudarPedidoParaProducao(pedidoParaAtualizar);
  
            PedidoDTO pedidoAtualizado = pedidoNegocio.pesquisaCodigo(codigo);
            
            return new ResponseEntity<>(pedidoAtualizado, HttpStatus.OK); // Retorna 200 OK
        } catch (NotFoundException e) { 
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Retorna 404
        } catch (NotValidDataException e) { 
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Retorna 400
        } catch (Exception e) { 
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{codigo}/pronto")
    public ResponseEntity<PedidoDTO> finalizarProducaoPedido(@PathVariable int codigo) {
        try {
            PedidoDTO pedidoParaAtualizar = pedidoNegocio.pesquisaCodigo(codigo);
            pedidoNegocio.mudarPedidoParaPronto(pedidoParaAtualizar);
            PedidoDTO pedidoAtualizado = pedidoNegocio.pesquisaCodigo(codigo);
            return new ResponseEntity<>(pedidoAtualizado, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NotValidDataException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{codigoPedido}/entrega/{codigoEntregador}")
    public ResponseEntity<PedidoDTO> despacharPedidoParaEntrega(
            @PathVariable int codigoPedido,
            @PathVariable int codigoEntregador) { 
        try {
            PedidoDTO pedidoParaAtualizar = pedidoNegocio.pesquisaCodigo(codigoPedido);

            EntregadorDTO entregadorDTO = new EntregadorDTO();
            entregadorDTO.setCodigo(codigoEntregador); 

            pedidoParaAtualizar.setEntregador(entregadorDTO); 

            pedidoNegocio.mudarPedidoParaEntrega(pedidoParaAtualizar);
            

            PedidoDTO pedidoAtualizado = pedidoNegocio.pesquisaCodigo(codigoPedido);
            return new ResponseEntity<>(pedidoAtualizado, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NotValidDataException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
    @PutMapping("/{codigoPedido}/finalizar-entrega")
    public ResponseEntity<PedidoDTO> finalizarEntregaPedido(@PathVariable int codigoPedido) {
        try {
            PedidoDTO pedidoParaAtualizar = pedidoNegocio.pesquisaCodigo(codigoPedido);
            pedidoNegocio.mudarPedidoParaConcluido(pedidoParaAtualizar); 
            PedidoDTO pedidoAtualizado = pedidoNegocio.pesquisaCodigo(codigoPedido);
            return new ResponseEntity<>(pedidoAtualizado, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NotValidDataException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/registrados")
    public ResponseEntity<List<PedidoDTO>> consultarPedidosRegistrados() {
        try {
            
            List<PedidoDTO> pedidosRegistrados = pedidoNegocio.pesquisaPorEstado(EstadoPedidoDTO.REGISTRADO);
            return new ResponseEntity<>(pedidosRegistrados, HttpStatus.OK);
        } catch (NotFoundException e) { 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/prontos")
    public ResponseEntity<List<PedidoDTO>> consultarPedidosProntos() {
        try {
            List<PedidoDTO> pedidosProntos = pedidoNegocio.pesquisaPorEstado(EstadoPedidoDTO.PRONTO);
            return new ResponseEntity<>(pedidosProntos, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}