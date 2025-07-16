package ifmt.cba.restaurante.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.dto.MovimentoEstoqueDTO;
import ifmt.cba.restaurante.entity.Produto;
import ifmt.cba.restaurante.entity.RegistroEstoque;

public interface RegistroEstoqueRepository extends JpaRepository<RegistroEstoque, Integer>{

    List<RegistroEstoque> findByMovimento(MovimentoEstoqueDTO movimento);

    List<RegistroEstoque> findByMovimentoAndData(MovimentoEstoqueDTO movimento, LocalDate data);

    List<RegistroEstoque> findByProduto(Produto produto);

    /**
     * Busca registros de estoque por período e tipo de movimento
     * @param dataInicial Data inicial do período
     * @param dataFinal Data final do período
     * @param movimento Tipo do movimento de estoque
     * @return Lista de registros de estoque que atendem aos critérios
     */
    
    List<RegistroEstoque> findByDataBetweenAndMovimentoIn(LocalDate dataInicial, LocalDate dataFinal, List<MovimentoEstoqueDTO> movimentos);

}