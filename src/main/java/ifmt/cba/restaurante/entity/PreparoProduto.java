package ifmt.cba.restaurante.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "preparo_produto")
public class PreparoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @Column(name =  "nome", length = 40, nullable = false)
    private String nome;

    @Column(name = "tempo_preparo")
    private int tempoPreparo;

    @Column(name = "valor_preparo")
    private float valorPreparo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto")
    private Produto produto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_preparo")
    private TipoPreparo tipoPreparo;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar() {
        String retorno = "";

        if (this.produto == null) {
            retorno += "Deve existir um produto relacionado";
        }

        if (this.tipoPreparo == null) {
            retorno += "Deve existir um tipo de preparo relacionado";
        }

        if (this.tempoPreparo <= 0) {
            retorno += "Tempo de preparo invalido";
        }

        if (this.valorPreparo <= 0) {
            retorno += "Valor de preparo invalido";
        }

        return retorno;
    }
}
