package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.GrupoAlimentarDTO;
import ifmt.cba.restaurante.entity.GrupoAlimentar;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.GrupoAlimentarRepository;
import ifmt.cba.restaurante.repository.ProdutoRepository;

@Service
public class GrupoAlimentarNegocio {

	private ModelMapper modelMapper;

	@Autowired
	private GrupoAlimentarRepository grupoAlimentarRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	public GrupoAlimentarNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public GrupoAlimentarDTO inserir(GrupoAlimentarDTO grupoAlimentarDTO) throws NotValidDataException {

		GrupoAlimentar grupoAlimentar = this.toEntity(grupoAlimentarDTO);
		String mensagemErros = grupoAlimentar.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			// nao pode existir outro com o mesmo nome
			if (grupoAlimentarRepository.findByNomeIgnoreCaseStartingWith(grupoAlimentar.getNome()) != null) {
				throw new NotValidDataException("Ja existe esse grupo alimentar");
			}
			grupoAlimentar = grupoAlimentarRepository.save(grupoAlimentar);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir o grupo alimentar - " + ex.getMessage());
		}
		return this.toDTO(grupoAlimentar);
	}

	public GrupoAlimentarDTO alterar(GrupoAlimentarDTO grupoAlimentarDTO) throws NotValidDataException, NotFoundException {

		GrupoAlimentar grupoAlimentar = this.toEntity(grupoAlimentarDTO);
		String mensagemErros = grupoAlimentar.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			// deve existir para ser alterado
			if (grupoAlimentarRepository.findById(grupoAlimentar.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse grupo alimentar");
			}
			grupoAlimentar = grupoAlimentarRepository.save(grupoAlimentar);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao alterar o grupo alimentar - " + ex.getMessage());
		}
		return this.toDTO(grupoAlimentar);
	}

	public void excluir(int codigo) throws NotValidDataException, NotFoundException {

		try {
			GrupoAlimentar grupoAlimentar = grupoAlimentarRepository.findById(codigo).get();
			if( grupoAlimentar == null){
				throw new NotFoundException("Esse GrupoAlimentar nao existe");
			}

			// nao pode excluir se estiver sendo referenciado por um ou mais produtos
			if (produtoRepository.findByGrupoAlimentar(grupoAlimentar).size() > 0) {
				throw new NotValidDataException("Grupo Alimentar esta relacionado a produtos");
			}
			grupoAlimentarRepository.delete(grupoAlimentar);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir o grupo alimentar - " + ex.getMessage());
		}
	}

	public List<GrupoAlimentarDTO> pesquisaTodos() throws NotFoundException {
		try {
			return this.toDTOAll(grupoAlimentarRepository.findAll());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar grupo alimentar pelo nome - " + ex.getMessage());
		}
	}

	public GrupoAlimentarDTO pesquisaPorNome(String parteNome) throws NotFoundException {
		try {
			return this.toDTO(grupoAlimentarRepository.findByNomeIgnoreCaseStartingWith(parteNome));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar grupo alimentar pelo nome - " + ex.getMessage());
		}
	}

	public GrupoAlimentarDTO pesquisaCodigo(int codigo) throws NotFoundException {
		try {
			return this.toDTO(grupoAlimentarRepository.findById(codigo).get());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar grupo alimentar pelo codigo - " + ex.getMessage());
		}
	}

	public List<GrupoAlimentarDTO> toDTOAll(List<GrupoAlimentar> listaGrupoAlimentar) {
		List<GrupoAlimentarDTO> listDTO = new ArrayList<GrupoAlimentarDTO>();

		for (GrupoAlimentar grupoAlimentar : listaGrupoAlimentar) {
			listDTO.add(this.toDTO(grupoAlimentar));
		}
		return listDTO;
	}

	public GrupoAlimentarDTO toDTO(GrupoAlimentar grupoAlimentar) {
		return this.modelMapper.map(grupoAlimentar, GrupoAlimentarDTO.class);
	}

	public GrupoAlimentar toEntity(GrupoAlimentarDTO grupoAlimentarDTO) {
		return this.modelMapper.map(grupoAlimentarDTO, GrupoAlimentar.class);
	}

}
