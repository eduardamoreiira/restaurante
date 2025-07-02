package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.ClienteDTO;
import ifmt.cba.restaurante.entity.Cliente;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.ClienteRepository;
import ifmt.cba.restaurante.repository.PedidoRepository;

@Service
public class ClienteNegocio {

    private ModelMapper modelMapper;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private PedidoRepository pedidoRepository;

	public ClienteNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public ClienteDTO inserir(ClienteDTO clienteDTO) throws NotValidDataException {

		Cliente cliente = this.toEntity(clienteDTO);
		String mensagemErros = cliente.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			if (clienteRepository.findByCPF(cliente.getCPF()) != null) {
				throw new NotValidDataException("Ja existe esse cliente");
			}
			cliente = clienteRepository.save(cliente);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir o cliente - " + ex.getMessage());
		}
		return this.toDTO(cliente);
	}

	public ClienteDTO alterar(ClienteDTO clienteDTO) throws NotValidDataException, NotFoundException {

		Cliente cliente = this.toEntity(clienteDTO);
		String mensagemErros = cliente.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			if (clienteRepository.findById(cliente.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse cliente");
			}
			cliente = clienteRepository.save(cliente);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao alterar o cliente - " + ex.getMessage());
		}
		return this.toDTO(cliente);
	}

	public void excluir(int codigo) throws NotValidDataException, NotFoundException {

		try {
			Cliente cliente = clienteRepository.findById(codigo).get();
			if (cliente == null) {
				throw new NotFoundException("Nao existe esse cliente");
			}

			if (pedidoRepository.findByCliente(cliente).size() > 0){
				throw new NotValidDataException("Nao pode excluir cliente relacionado com pedidos");
			}

			clienteRepository.delete(cliente);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir o cliente - " + ex.getMessage());
		}
	}

	public List<ClienteDTO> pesquisaTodos() throws NotFoundException {
		try {
			return this.toDTOAll(clienteRepository.findAll());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar cliente - " + ex.getMessage());
		}
	}

	public ClienteDTO pesquisaPorNome(String parteNome) throws NotFoundException {
		try {
			return this.toDTO(clienteRepository.findByNomeIgnoreCaseStartingWith(parteNome));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar cliente pelo nome - " + ex.getMessage());
		}
	}

	public ClienteDTO pesquisaCodigo(int codigo) throws NotFoundException {
		try {
			return this.toDTO(clienteRepository.findById(codigo).get());
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar cliente pelo codigo - " + ex.getMessage());
		}
	}

	public List<ClienteDTO> toDTOAll(List<Cliente> listaCliente) {
		List<ClienteDTO> listaDTO = new ArrayList<ClienteDTO>();

		for (Cliente cliente : listaCliente) {
			listaDTO.add(this.toDTO(cliente));
		}
		return listaDTO;
	}

	public ClienteDTO toDTO(Cliente cliente) {
		return this.modelMapper.map(cliente, ClienteDTO.class);
	}

	public Cliente toEntity(ClienteDTO clienteDTO) {
		return this.modelMapper.map(clienteDTO, Cliente.class);
	}
}
