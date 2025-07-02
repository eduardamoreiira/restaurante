package ifmt.cba.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ifmt.cba.restaurante.entity.Colaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Integer>{

    Colaborador findByNomeIgnoreCaseStartingWith(String nome);

    Colaborador findByCPF(String cpf);

}
