package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.TipoPreparoDTO;
import ifmt.cba.restaurante.entity.TipoPreparo;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.TipoPreparoRepository;

@Service
public class TipoPreparoNegocio {

    private ModelMapper modelMapper;

	@Autowired
	private TipoPreparoRepository tipoPreparoRepository;
	
	public TipoPreparoNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public TipoPreparoDTO inserir(TipoPreparoDTO tipoPreparoDTO) throws NotValidDataException {

		TipoPreparo tipoPreparo = this.toEntity(tipoPreparoDTO);
		String mensagemErros = tipoPreparo.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			// não pode existir outro com a mesma descricao
			if (tipoPreparoRepository.findByDescricaoIgnoreCaseStartingWith(tipoPreparo.getDescricao()) != null) {
				throw new NotValidDataException("Ja existe esse tipo de preparo");
			}
			tipoPreparo = tipoPreparoRepository.save(tipoPreparo);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir o tipo de preparo - " + ex.getMessage());
		}
		return this.toDTO(tipoPreparo);
	}

	public TipoPreparoDTO alterar(TipoPreparoDTO tipoPreparoDTO) throws NotValidDataException, NotFoundException {

		TipoPreparo tipoPreparo = this.toEntity(tipoPreparoDTO);
		String mensagemErros = tipoPreparo.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			// deve existir para ser alterado
			if (tipoPreparoRepository.findById(tipoPreparo.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse tipo de preparo");
			}
			tipoPreparo = tipoPreparoRepository.save(tipoPreparo);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao alterar o tipo de preparo - " + ex.getMessage());
		}
		return this.toDTO(tipoPreparo);
	}

	public void excluir(int codigo) throws NotValidDataException, NotFoundException {
		try {
			TipoPreparo tipoPreparo = tipoPreparoRepository.findById(codigo).get();
			if (tipoPreparo == null){
				throw new NotFoundException("Tipo de Preparo nao existe");
			}
			tipoPreparoRepository.delete(tipoPreparo);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir o tipo de preparo - " + ex.getMessage());
		}
	}

	public List<TipoPreparoDTO> pesquisaTodos() throws NotFoundException {
		try {
			return this.toDTOAll(tipoPreparoRepository.findAll());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar tipos de preparo - " + ex.getMessage());
		}
	}

	public TipoPreparoDTO pesquisaPorDescricao(String parteDesc) throws NotFoundException {
		try {
			return this.toDTO(tipoPreparoRepository.findByDescricaoIgnoreCaseStartingWith(parteDesc));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar tipo de preparo pela descricao - " + ex.getMessage());
		}
	}

	public TipoPreparoDTO pesquisaCodigo(int codigo) throws NotFoundException {
		try {
			return this.toDTO(tipoPreparoRepository.findById(codigo).get());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar tipo preparo pelo codigo - " + ex.getMessage());
		}
	}

	public List<TipoPreparoDTO> toDTOAll(List<TipoPreparo> listaTipoPreparo) {
		List<TipoPreparoDTO> listDTO = new ArrayList<TipoPreparoDTO>();

		for (TipoPreparo tipoPreparo : listaTipoPreparo) {
			listDTO.add(this.toDTO(tipoPreparo));
		}
		return listDTO;
	}

	public TipoPreparoDTO toDTO(TipoPreparo tipoPreparo) {
		return this.modelMapper.map(tipoPreparo, TipoPreparoDTO.class);
	}

	public TipoPreparo toEntity(TipoPreparoDTO tipoPreparoDTO) {
		return this.modelMapper.map(tipoPreparoDTO, TipoPreparo.class);
	}

}
