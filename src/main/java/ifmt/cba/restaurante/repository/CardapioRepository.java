package ifmt.cba.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.Cardapio;

public interface CardapioRepository extends JpaRepository<Cardapio, Integer>{

    Cardapio findByNomeIgnoreCaseStartingWith(String nome);

}

