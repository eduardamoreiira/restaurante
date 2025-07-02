package ifmt.cba.restaurante.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "entregador")
public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codigo;

    @Column(name = "nome")
    private String nome;

    @Column(name = "rg")
    private String RG;

    @Column(name = "cpf")
    private String CPF;

    @Column(name = "telefone")
    private String telefone;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String validar(){
        String retorno = "";

        if(this.nome == null || this.nome.length() < 3){
            retorno += "Nome invalido";
        }

        if(this.RG == null || this.RG.length() == 0){
            retorno += "RG invalido";
        }

        //falta validar CPF
        if(this.CPF == null || this.CPF.length() < 11){
            retorno += "CPF invalido";
        }

        if(this.telefone == null || this.telefone.length() < 8){
            retorno += "Telefone invalido";
        }

        return retorno;
    }
}
