package ifmt.cba.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.Bairro;

public interface BairroRepository extends JpaRepository<Bairro, Integer>{

    Bairro findByNomeIgnoreCaseStartingWith(String nome);

    List<Bairro> findAll();

}
