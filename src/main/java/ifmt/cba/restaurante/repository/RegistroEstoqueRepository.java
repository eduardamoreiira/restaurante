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

}