package ifmt.cba.restaurante.negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.MovimentoEstoqueDTO;
import ifmt.cba.restaurante.dto.RegistroEstoqueDTO;
import ifmt.cba.restaurante.entity.Produto;
import ifmt.cba.restaurante.entity.RegistroEstoque;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.ProdutoRepository;
import ifmt.cba.restaurante.repository.RegistroEstoqueRepository;

@Service
public class RegistroEstoqueNegocio {

    private ModelMapper modelMapper;

	@Autowired
	private RegistroEstoqueRepository registroRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	public RegistroEstoqueNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public RegistroEstoqueDTO inserir(RegistroEstoqueDTO registroEstoqueDTO) throws NotValidDataException, NotFoundException {

		RegistroEstoque registroEstoque = this.toEntity(registroEstoqueDTO);
		String mensagemErros = registroEstoque.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			Produto produtoTemp = produtoRepository.findById(registroEstoque.getProduto().getCodigo()).get();
			if(registroEstoque.getMovimento() == MovimentoEstoqueDTO.COMPRA){
				produtoTemp.setEstoque(produtoTemp.getEstoque() + registroEstoque.getQuantidade());
			}else{
				produtoTemp.setEstoque(produtoTemp.getEstoque() - registroEstoque.getQuantidade());
			}
			
			produtoRepository.save(produtoTemp);
			registroEstoque = registroRepository.save(registroEstoque);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir registro de estoque - " + ex.getMessage());
		}
		return this.toDTO(registroEstoque);
	}

	public RegistroEstoqueDTO excluir(RegistroEstoqueDTO registroEstoqueDTO) throws NotValidDataException, NotFoundException {

		RegistroEstoque registroEstoque = this.toEntity(registroEstoqueDTO);
		String mensagemErros = registroEstoque.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			Produto produtoTemp = produtoRepository.findById(registroEstoque.getProduto().getCodigo()).get();
			if(registroEstoque.getMovimento() == MovimentoEstoqueDTO.COMPRA){
				produtoTemp.setEstoque(produtoTemp.getEstoque() - registroEstoque.getQuantidade());
			}else{
				produtoTemp.setEstoque(produtoTemp.getEstoque() + registroEstoque.getQuantidade());
			}
			
			produtoRepository.save(produtoTemp);
			registroEstoque = registroRepository.save(registroEstoque);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir registro de estoque - " + ex.getMessage());
		}
		return this.toDTO(registroEstoque);
	}

	public RegistroEstoqueDTO pesquisaCodigo(int codigo) throws NotFoundException {
		try {
			RegistroEstoque registroEstoque = registroRepository.findById(codigo).get();
			if (registroEstoque != null) {
				return this.toDTO(registroEstoque);
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar registro de estoque pelo codigo - " + ex.getMessage());
		}
	}

    public List<RegistroEstoqueDTO> buscarPorMovimento(MovimentoEstoqueDTO movimento) throws NotFoundException {
        try {
			return this.toDTOAll(registroRepository.findByMovimento(movimento));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar registro de estoque por tipo de movimento - " + ex.getMessage());
		}
    }

	public List<RegistroEstoqueDTO> buscarPorMovimentoEData(MovimentoEstoqueDTO movimento, LocalDate data) throws NotFoundException {
		try {
			return this.toDTOAll(registroRepository.findByMovimentoAndData(movimento, data));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar registro de estoque por tipo de movimento e data - " + ex.getMessage());
		}
	}

	public List<RegistroEstoqueDTO> buscarPorProduto(Produto produto) throws NotFoundException {
		try {
			return this.toDTOAll(registroRepository.findByProduto(produto));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar registro de estoque por produto - " + ex.getMessage());
		}
	}

	public List<RegistroEstoqueDTO> toDTOAll(List<RegistroEstoque> listaRegistro) {
		List<RegistroEstoqueDTO> listaDTO = new ArrayList<RegistroEstoqueDTO>();

		for (RegistroEstoque registroEstoque : listaRegistro) {
			listaDTO.add(this.toDTO(registroEstoque));
		}
		return listaDTO;
	}

	public RegistroEstoqueDTO toDTO(RegistroEstoque registroEstoque) {
		return this.modelMapper.map(registroEstoque, RegistroEstoqueDTO.class);
	}

	public RegistroEstoque toEntity(RegistroEstoqueDTO registroEstoqueDTO) {
		return this.modelMapper.map(registroEstoqueDTO, RegistroEstoque.class);
	}
}
