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
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "rg", nullable = false)
    private String RG;

    @Column(name = "cpf", nullable = false)
    private String CPF;

    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @Column(name = "numero", nullable = false)
    private String numero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bairro")
    private Bairro bairro;

    @Column(name = "referencia", nullable = false)
    private String pontoReferencia;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar() {
        String retorno = "";

        if (this.nome == null || this.nome.length() < 3) {
            retorno += "Nome invalido";
        }

        if (this.RG == null || this.RG.length() == 0) {
            retorno += "RG invalido";
        }

        // falta validar CPF
        if (this.CPF == null || this.CPF.length() < 11) {
            retorno += "CPF invalido";
        }

        if (this.telefone == null || this.telefone.length() < 8) {
            retorno += "Telefone invalido";
        }

        if (this.logradouro == null || this.logradouro.length() == 0) {
            retorno += "Logradouro invalido";
        }

        if (this.numero == null || this.numero.length() == 0) {
            retorno += "Numero invalido";
        }

        if (this.bairro == null) {
            retorno += "Bairro invalido";
        } else {
            retorno += this.bairro.validar();
        }

        if (this.pontoReferencia == null) {
            retorno += "Ponto de referencia invalido";
        }

        return retorno;
    }
}
