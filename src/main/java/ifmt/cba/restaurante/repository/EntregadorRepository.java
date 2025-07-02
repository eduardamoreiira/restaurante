package ifmt.cba.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.Entregador;

public interface EntregadorRepository extends JpaRepository<Entregador, Integer>{

    Entregador findByNomeIgnoreCaseStartingWith(String nome);

    Entregador findByCPF(String cpf);

}
