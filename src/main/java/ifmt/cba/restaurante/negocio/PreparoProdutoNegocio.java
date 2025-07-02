package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.PreparoProdutoDTO;
import ifmt.cba.restaurante.dto.ProdutoDTO;
import ifmt.cba.restaurante.dto.TipoPreparoDTO;
import ifmt.cba.restaurante.entity.PreparoProduto;
import ifmt.cba.restaurante.entity.Produto;
import ifmt.cba.restaurante.entity.TipoPreparo;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.PreparoProdutoRepository;

@Service
public class PreparoProdutoNegocio {

    private ModelMapper modelMapper;

	@Autowired
	private PreparoProdutoRepository preparoProdutoRepository;

	public PreparoProdutoNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public PreparoProdutoDTO inserir(PreparoProdutoDTO preparoprodutoDTO) throws NotValidDataException {

		PreparoProduto preparoProduto = this.toEntity(preparoprodutoDTO);
		String mensagemErros = preparoProduto.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			if (preparoProdutoRepository.findByProdutoAndTipoPreparo(preparoProduto.getProduto(), preparoProduto.getTipoPreparo()) != null) {
				throw new NotValidDataException("Ja existe esse preparo de produto");
			}
			preparoProduto = preparoProdutoRepository.save(preparoProduto);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir o preparo de produto - " + ex.getMessage());
		}
		return this.toDTO(preparoProduto);
	}

	public PreparoProdutoDTO alterar(PreparoProdutoDTO preparoProdutoDTO) throws NotValidDataException, NotFoundException {

		PreparoProduto preparoProduto = this.toEntity(preparoProdutoDTO);
		String mensagemErros = preparoProduto.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			if (preparoProdutoRepository.findById(preparoProduto.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse preparo de produto");
			}
			preparoProduto = preparoProdutoRepository.save(preparoProduto);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao alterar o preparo de produto - " + ex.getMessage());
		}
		return this.toDTO(preparoProduto);
	}

	public void excluir(int codigo) throws NotValidDataException, NotFoundException {

		try {
			PreparoProduto preparoProduto = preparoProdutoRepository.findById(codigo).get();
			if (preparoProduto == null) {
				throw new NotFoundException("Nao existe esse preparo de produto");
			}
			preparoProdutoRepository.delete(preparoProduto);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir o produto - " + ex.getMessage());
		}
	}

	public PreparoProdutoDTO pesquisaPorCodigo(int codigo) throws NotFoundException {
		try {
			return this.toDTO(preparoProdutoRepository.findById(codigo).get());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar produto pelo codigo - " + ex.getMessage());
		}
	}

	public List<PreparoProdutoDTO> pesquisaTodos() throws NotFoundException {
		try {
			return this.toDTOAll(preparoProdutoRepository.findAll());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar preparo de produto - " + ex.getMessage());
		}
	}

	public PreparoProdutoDTO pesquisaPorNome(String nome) throws NotFoundException {
		try {
			return this.toDTO(preparoProdutoRepository.findByNomeIgnoreCaseStartingWith(nome));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar preparo de produto por nome - " + ex.getMessage());
		}
	}

    public List<PreparoProdutoDTO> pesquisaPorProduto(ProdutoDTO produtoDTO) throws NotFoundException {
		try {
			return this.toDTOAll(preparoProdutoRepository.findByProduto(this.produtoToEntity(produtoDTO)));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar preparo de produto por produto - " + ex.getMessage());
		}
	}

    public List<PreparoProdutoDTO> pesquisaPorTipoPreparo(TipoPreparoDTO tipoPreparoDTO) throws NotFoundException {
		try {
			return this.toDTOAll(preparoProdutoRepository.findByTipoPreparo(this.tipoPreparoToEntity(tipoPreparoDTO)));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar preparo de produto por tipo preparo - " + ex.getMessage());
		}
	}

    public PreparoProdutoDTO pesquisaPorProdutoETipoPreparo(ProdutoDTO produtoDTO, TipoPreparoDTO tipoPreparoDTO) throws NotFoundException {
		try {
			return this.toDTO(preparoProdutoRepository.findByProdutoAndTipoPreparo(this.produtoToEntity(produtoDTO), this.tipoPreparoToEntity(tipoPreparoDTO)));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar preparo de produto por produto e tipo preparo - " + ex.getMessage());
		}
	}

	public List<PreparoProdutoDTO> toDTOAll(List<PreparoProduto> listaPreparoProduto) {
		List<PreparoProdutoDTO> listDTO = new ArrayList<PreparoProdutoDTO>();

		for (PreparoProduto preparoProduto : listaPreparoProduto) {
			listDTO.add(this.toDTO(preparoProduto));
		}
		return listDTO;
	}

	public PreparoProdutoDTO toDTO(PreparoProduto preparoProduto) {
		return this.modelMapper.map(preparoProduto, PreparoProdutoDTO.class);
	}

	public PreparoProduto toEntity(PreparoProdutoDTO preparoProdutoDTO) {
		return this.modelMapper.map(preparoProdutoDTO, PreparoProduto.class);
	}

	public Produto produtoToEntity(ProdutoDTO produtoDTO) {
		return this.modelMapper.map(produtoDTO, Produto.class);
	}

	public TipoPreparo tipoPreparoToEntity(TipoPreparoDTO tipoPreparoDTODTO) {
		return this.modelMapper.map(tipoPreparoDTODTO, TipoPreparo.class);
	}
}
