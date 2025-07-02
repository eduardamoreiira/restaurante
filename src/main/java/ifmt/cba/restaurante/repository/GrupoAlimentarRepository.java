package ifmt.cba.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.GrupoAlimentar;

public interface GrupoAlimentarRepository extends JpaRepository<GrupoAlimentar, Integer>{

    GrupoAlimentar findByNomeIgnoreCaseStartingWith(String nome);

}