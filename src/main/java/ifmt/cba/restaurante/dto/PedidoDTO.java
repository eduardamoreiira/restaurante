package ifmt.cba.restaurante.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class PedidoDTO {

    private int codigo;
    private ClienteDTO cliente;
    private LocalDate dataPedido;
    private LocalTime horaPedido;
    private LocalTime horaProducao;
    private LocalTime horaPronto;
    private LocalTime horaEntrega;
    private LocalTime horaFinalizado;
    private EstadoPedidoDTO estado;
    private EntregadorDTO entregador;
    private List<ItemPedidoDTO> listaItens;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
