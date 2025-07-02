package ifmt.cba.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.PreparoProduto;
import ifmt.cba.restaurante.entity.Produto;
import ifmt.cba.restaurante.entity.TipoPreparo;

public interface PreparoProdutoRepository extends JpaRepository<PreparoProduto, Integer>{

    PreparoProduto findByNomeIgnoreCaseStartingWith(String nome);
    
    List<PreparoProduto> findByProduto(Produto produto);

    List<PreparoProduto> findByTipoPreparo(TipoPreparo tipoPreparo);

    PreparoProduto findByProdutoAndTipoPreparo(Produto produto, TipoPreparo tipoPreparo);

}
