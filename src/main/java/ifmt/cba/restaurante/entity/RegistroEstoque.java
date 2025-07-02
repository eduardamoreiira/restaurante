package ifmt.cba.restaurante.entity;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ifmt.cba.restaurante.dto.MovimentoEstoqueDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Entity
@Table(name = "registro_estoque")
public class RegistroEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @Enumerated(EnumType.STRING)
    private MovimentoEstoqueDTO movimento;

    @Temporal(TemporalType.DATE)
    private LocalDate data;

    @Column(name = "quantidade")
    private int quantidade;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar() {
        String retorno = "";

        if (this.produto == null) {
            retorno += "Produto invalido";
        }

        if (this.quantidade <= 0) {
            retorno += "Custo por unidade invalido";
        }

        if (this.movimento == null) {
            retorno += "Motivo invalido";
        }

        if (this.data == null) {
            retorno += "Data invalida";
        }

        return retorno;
    }
}
