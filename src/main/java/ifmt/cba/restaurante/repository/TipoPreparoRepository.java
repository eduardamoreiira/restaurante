package ifmt.cba.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.TipoPreparo;

public interface TipoPreparoRepository extends JpaRepository<TipoPreparo, Integer>{

    TipoPreparo findByDescricaoIgnoreCaseStartingWith(String nome);

}
