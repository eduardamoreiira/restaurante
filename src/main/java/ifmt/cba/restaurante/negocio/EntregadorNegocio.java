package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.EntregadorDTO;
import ifmt.cba.restaurante.entity.Entregador;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.EntregadorRepository;

@Service
public class EntregadorNegocio {

    private ModelMapper modelMapper;

	@Autowired
	private EntregadorRepository entregadorRepository;

	public EntregadorNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public EntregadorDTO inserir(EntregadorDTO entregadorDTO) throws NotValidDataException, NotFoundException {

		Entregador entregador = this.toEntity(entregadorDTO);
		String mensagemErros = entregador.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			if (entregadorRepository.findByCPF(entregador.getCPF()) != null) {
				throw new NotValidDataException("Ja existe esse entregador");
			}
			entregador = entregadorRepository.save(entregador);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir o entregador - " + ex.getMessage());
		}
		return this.toDTO(entregador);
	}

	public EntregadorDTO alterar(EntregadorDTO entregadorDTO) throws NotValidDataException, NotFoundException {

		Entregador entregador = this.toEntity(entregadorDTO);
		String mensagemErros = entregador.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			if (entregadorRepository.findById(entregador.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse entregador");
			}
			entregador = entregadorRepository.save(entregador);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao alterar o entregador - " + ex.getMessage());
		}
		return this.toDTO(entregador);
	}

	public void excluir(int codigo) throws NotValidDataException, NotFoundException {

		try {
			Entregador entregador = entregadorRepository.findById(codigo).get();
			if ( entregador == null) {
				throw new NotFoundException("Nao existe esse entregador");
			}
			entregadorRepository.delete(entregador);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir o entregador - " + ex.getMessage());
		}
	}

	public List<EntregadorDTO> pesquisaTodos() throws NotFoundException {
		try {
			return this.toDTOAll(entregadorRepository.findAll());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar entregador - " + ex.getMessage());
		}
	}

	public EntregadorDTO pesquisaPorNome(String parteNome) throws NotFoundException {
		try {
			return this.toDTO(entregadorRepository.findByNomeIgnoreCaseStartingWith(parteNome));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar entregador pelo nome - " + ex.getMessage());
		}
	}

	public EntregadorDTO pesquisaCodigo(int codigo) throws NotFoundException {
		try {
			return this.toDTO(entregadorRepository.findById(codigo).get());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar entregador pelo codigo - " + ex.getMessage());
		}
	}

	public List<EntregadorDTO> toDTOAll(List<Entregador> listaEntregador) {
		List<EntregadorDTO> listaDTO = new ArrayList<EntregadorDTO>();

		for (Entregador entregador : listaEntregador) {
			listaDTO.add(this.toDTO(entregador));
		}
		return listaDTO;
	}

	public EntregadorDTO toDTO(Entregador entregador) {
		return this.modelMapper.map(entregador, EntregadorDTO.class);
	}

	public Entregador toEntity(EntregadorDTO entregadorDTO) {
		return this.modelMapper.map(entregadorDTO, Entregador.class);
	}
}
