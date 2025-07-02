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
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @Column(name = "nome")
    private String nome;

    @Column(name = "custo_unidade")
    private float custoUnidade;

    @Column(name = "valor_energetico")
    private int valorEnergetico;

    @Column(name = "estoque")
    private int estoque;

    @Column(name = "estoque_minimo")
    private int estoqueMinimo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_grupo")
    private GrupoAlimentar grupoAlimentar;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar() {
        String retorno = "";

        if (this.nome == null || this.nome.length() < 3) {
            retorno += "Nome invalido";
        }

        if (this.custoUnidade <= 0) {
            retorno += "Custo por unidade invalido";
        }

        if (this.valorEnergetico < 0) {
            retorno += "Valor energetico invalido";
        }

        if (estoque < 0) {
            retorno += "Estoque invalido";
        }

        if (this.grupoAlimentar == null || !this.grupoAlimentar.validar().isEmpty()) {
            retorno += "Grupo alimentar invalido";
        }

        return retorno;
    }
}
