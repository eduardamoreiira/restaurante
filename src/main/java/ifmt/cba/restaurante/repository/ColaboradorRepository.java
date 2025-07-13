package ifmt.cba.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifmt.cba.restaurante.entity.Colaborador;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, Integer> {

    Colaborador findByNomeIgnoreCaseStartingWith(String nome);

    Colaborador findByCPF(String cpf);

}
