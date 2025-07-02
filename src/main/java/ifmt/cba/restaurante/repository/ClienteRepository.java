package ifmt.cba.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

    Cliente findByNomeIgnoreCaseStartingWith(String nome);

    Cliente findByCPF(String cpf);

}
