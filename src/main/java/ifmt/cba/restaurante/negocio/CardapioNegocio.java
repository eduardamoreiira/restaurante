package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.CardapioDTO;
import ifmt.cba.restaurante.entity.Cardapio;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.CardapioRepository;

@Service
public class CardapioNegocio {

	private ModelMapper modelMapper;
	@Autowired
	private CardapioRepository cadapioRepository;

	public CardapioNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public CardapioDTO inserir(CardapioDTO cardapioDTO) throws NotValidDataException {

		Cardapio cardapio = this.toEntity(cardapioDTO);
		String mensagemErros = cardapio.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			if (cadapioRepository.findByNomeIgnoreCaseStartingWith(cardapio.getNome()) != null) {
				throw new NotValidDataException("Ja existe esse cardapio");
			}

			cardapio = cadapioRepository.save(cardapio);
        } catch (Exception ex) {
            throw new NotValidDataException("Erro ao incluir o cardapio - " + ex.getMessage());
        }
        return this.toDTO(cardapio);
	}

	public CardapioDTO alterar(CardapioDTO cardapioDTO) throws NotValidDataException, NotFoundException {

		Cardapio cardapio = this.toEntity(cardapioDTO);
		String mensagemErros = cardapio.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			if (cadapioRepository.findById(cardapio.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse cardapio");
			}
			cardapio = cadapioRepository.save(cardapio);
        } catch (Exception ex) {
            throw new NotValidDataException("Erro ao alterar o cardapio - " + ex.getMessage());
        }
        return this.toDTO(cardapio);
	}

	public void excluir(int codigo) throws NotValidDataException, NotFoundException {

		try {
			Cardapio cardapio = cadapioRepository.findById(codigo).get();
			if (cardapio == null) {
				throw new NotFoundException("Nao existe esse cardapio");
			}
			cadapioRepository.delete(cardapio);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir o cardapio - " + ex.getMessage());
		}
	}

	public List<CardapioDTO> pesquisaTodos() throws NotValidDataException {
		try {
			return this.toDTOAll(cadapioRepository.findAll());
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao pesquisar cardapios - " + ex.getMessage());
		}
	}

	public CardapioDTO pesquisaPorNome(String nome) throws NotValidDataException {
		try {
			return this.toDTO(cadapioRepository.findByNomeIgnoreCaseStartingWith(nome));
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao pesquisar cardapio pelo nome - " + ex.getMessage());
		}
	}

	public CardapioDTO pesquisaCodigo(int codigo) throws NotValidDataException {
		try {
			return this.toDTO(cadapioRepository.findById(codigo).get());
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao pesquisar cardapio pelo codigo - " + ex.getMessage());
		}
	}

	public List<CardapioDTO> toDTOAll(List<Cardapio> listaCardapio) {
		List<CardapioDTO> listDTO = new ArrayList<CardapioDTO>();

		for (Cardapio cardapio : listaCardapio) {
			listDTO.add(this.toDTO(cardapio));
		}
		return listDTO;
	}

	public CardapioDTO toDTO(Cardapio cardapio) {
		return this.modelMapper.map(cardapio, CardapioDTO.class);
	}

	public Cardapio toEntity(CardapioDTO cardapioDTO) {
		return this.modelMapper.map(cardapioDTO, Cardapio.class);
	}
}
