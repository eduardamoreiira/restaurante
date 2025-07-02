package ifmt.cba.restaurante.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.dto.EstadoPedidoDTO;
import ifmt.cba.restaurante.entity.Cliente;
import ifmt.cba.restaurante.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer>{

    List<Pedido> findByEstado(EstadoPedidoDTO estado);

    List<Pedido> findByEstadoAndDataPedido(EstadoPedidoDTO estado, LocalDate data);

    List<Pedido> findByCliente(Cliente cliente);

    List<Pedido> findByDataPedidoBetween(LocalDate dataInicial, LocalDate dataFinal);

}