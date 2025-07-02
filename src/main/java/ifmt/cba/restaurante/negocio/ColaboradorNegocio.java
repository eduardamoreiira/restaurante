package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.ColaboradorDTO;
import ifmt.cba.restaurante.entity.Colaborador;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.ColaboradorRepository;

@Service
public class ColaboradorNegocio {

    private ModelMapper modelMapper;
	private ColaboradorRepository colaboradorRepository;

	public ColaboradorNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public ColaboradorDTO inserir(ColaboradorDTO colaboradorDTO) throws NotValidDataException {

		Colaborador colaborador = this.toEntity(colaboradorDTO);
		String mensagemErros = colaborador.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			if (colaboradorRepository.findByCPF(colaborador.getCPF()) != null) {
				throw new NotValidDataException("Ja existe esse colaborador");
			}
			colaborador = colaboradorRepository.save(colaborador);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir o colaborador - " + ex.getMessage());
		}
		return this.toDTO(colaborador);
	}

	public ColaboradorDTO alterar(ColaboradorDTO colaboradorDTO) throws NotValidDataException, NotFoundException {

		Colaborador colaborador = this.toEntity(colaboradorDTO);
		String mensagemErros = colaborador.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			if (colaboradorRepository.findById(colaborador.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse colaborador");
			}
			colaborador = colaboradorRepository.save(colaborador);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao alterar o colaborador - " + ex.getMessage());
		}
		return this.toDTO(colaborador);
	}

	public void excluir(int codigo) throws NotFoundException, NotValidDataException {

		try {
			Colaborador colaborador = colaboradorRepository.findById(codigo).get();
			if ( colaborador == null) {
				throw new NotFoundException("Nao existe esse colaborador");
			}
			colaboradorRepository.delete(colaborador);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir o colaborador - " + ex.getMessage());
		}
	}

	public List<ColaboradorDTO> pesquisaTodos() throws NotFoundException {
		try {
			return this.toDTOAll(colaboradorRepository.findAll());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar colaboradores - " + ex.getMessage());
		}
	}

	public ColaboradorDTO pesquisaParteNome(String parteNome) throws NotFoundException {
		try {
			return this.toDTO(colaboradorRepository.findByNomeIgnoreCaseStartingWith(parteNome));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar colaborador pelo nome - " + ex.getMessage());
		}
	}

	public ColaboradorDTO pesquisaCodigo(int codigo) throws NotFoundException {
		try {
			return this.toDTO(colaboradorRepository.findById(null).get());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar colaborador pelo codigo - " + ex.getMessage());
		}
	}

	public List<ColaboradorDTO> toDTOAll(List<Colaborador> listaColaborador) {
		List<ColaboradorDTO> listaDTO = new ArrayList<ColaboradorDTO>();

		for (Colaborador colaborador : listaColaborador) {
			listaDTO.add(this.toDTO(colaborador));
		}
		return listaDTO;
	}

	public ColaboradorDTO toDTO(Colaborador colaborador) {
		return this.modelMapper.map(colaborador, ColaboradorDTO.class);
	}

	public Colaborador toEntity(ColaboradorDTO colaboradorDTO) {
		return this.modelMapper.map(colaboradorDTO, Colaborador.class);
	}
}
