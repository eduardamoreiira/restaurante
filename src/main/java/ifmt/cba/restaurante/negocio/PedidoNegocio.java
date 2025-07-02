package ifmt.cba.restaurante.negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.ClienteDTO;
import ifmt.cba.restaurante.dto.EstadoPedidoDTO;
import ifmt.cba.restaurante.dto.ItemPedidoDTO;
import ifmt.cba.restaurante.dto.PedidoDTO;
import ifmt.cba.restaurante.entity.Cliente;
import ifmt.cba.restaurante.entity.ItemPedido;
import ifmt.cba.restaurante.entity.Pedido;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.ClienteRepository;
import ifmt.cba.restaurante.repository.ItemPedidoRepository;
import ifmt.cba.restaurante.repository.PedidoRepository;

@Service
public class PedidoNegocio {

	private ModelMapper modelMapper;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	public PedidoNegocio() {
		this.modelMapper = new ModelMapper();
	}

	public PedidoDTO inserir(PedidoDTO pedidoDTO) throws NotValidDataException {

		Pedido pedido = this.toEntity(pedidoDTO);
		String mensagemErros = pedido.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}

		try {
			pedido.setEstado(EstadoPedidoDTO.REGISTRADO);
			pedido = pedidoRepository.save(pedido);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao incluir pedido - " + ex.getMessage());
		}
		return this.toDTO(pedido);
	}

	public PedidoDTO alterar(PedidoDTO pedidoDTO) throws NotValidDataException, NotFoundException {

		Pedido pedido = this.toEntity(pedidoDTO);
		String mensagemErros = pedido.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			if (pedidoRepository.findById(pedido.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse pedido");
			}
			pedido = pedidoRepository.save(pedido);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao alterar pedido - " + ex.getMessage());
		}
		return this.toDTO(pedido);
	}

	public void excluir(PedidoDTO pedidoDTO) throws NotValidDataException, NotFoundException {

		Pedido pedido = this.toEntity(pedidoDTO);
		try {
			if (pedidoRepository.findById(pedido.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse pedido");
			}
			pedidoRepository.delete(pedido);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir pedido - " + ex.getMessage());
		}
	}

	public void excluirItemPedido(ItemPedidoDTO itemPedidoDTO) throws NotValidDataException, NotFoundException {

		try {
			ItemPedido itemPedido = itemPedidoRepository.findById(itemPedidoDTO.getCodigo()).get();
			if (itemPedido == null) {
				throw new NotFoundException("Nao existe esse item de pedido");
			}
			itemPedidoRepository.delete(itemPedido);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir item de pedido - " + ex.getMessage());
		}
	}

	public ItemPedidoDTO alterarItemPedido(ItemPedidoDTO itemPedidoDTO) throws NotValidDataException, NotFoundException {

		ItemPedido itemPedido = this.toItemPedidoEntity(itemPedidoDTO);
		String mensagemErros = itemPedido.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NotValidDataException(mensagemErros);
		}
		try {
			if (itemPedidoRepository.findById(itemPedido.getCodigo()) == null) {
				throw new NotFoundException("Nao existe esse item de pedido");
			}
			itemPedido = itemPedidoRepository.save(itemPedido);
		} catch (Exception ex) {
			throw new NotValidDataException("Erro ao excluir item de pedido - " + ex.getMessage());
		}
		return this.toItemPeditoDTO(itemPedido);
	}

	public PedidoDTO pesquisaCodigo(int codigo) throws NotFoundException {
		try {
			Pedido pedido = pedidoRepository.findById(codigo).get();
			if (pedido != null) {
				return this.toDTO(pedido);
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar pedido pelo codigo - " + ex.getMessage());
		}
	}


	public List<PedidoDTO> pesquisaPorData(LocalDate dataInicial, LocalDate dataFinal) throws NotFoundException {
		try {
			return this.toDTOAll(pedidoRepository.findByDataPedidoBetween(dataInicial, dataFinal));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar pedido por data - " + ex.getMessage());
		}
	}

	public List<PedidoDTO> pesquisaPorEstado(EstadoPedidoDTO estado) throws NotFoundException {
		try {
			return this.toDTOAll(pedidoRepository.findByEstado(estado));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar pedido pelo estado - " + ex.getMessage());
		}
	}


	public List<PedidoDTO> pesquisaPorEstadoEData(EstadoPedidoDTO estado, LocalDate data) throws NotFoundException {
		try {
			return this.toDTOAll(pedidoRepository.findByEstadoAndDataPedido(estado, data));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar pedido pelo estado e data - " + ex.getMessage());
		}
	}

	public List<PedidoDTO> pesquisaPorCliente(ClienteDTO clienteDTO) throws NotFoundException {

		try {
			Cliente cliente = this.clienteRepository.findById(clienteDTO.getCodigo()).get();
			if (cliente == null) {
				throw new NotFoundException("Cliente nao existe");
			}
			return this.toDTOAll(pedidoRepository.findByCliente(cliente));
		} catch (Exception ex) {
			throw new NotFoundException("Erro ao pesquisar pedido pelo cliente - " + ex.getMessage());
		}
	}

	public void mudarPedidoParaProducao(PedidoDTO pedidoDTO) throws NotValidDataException {

		if (pedidoDTO.getEstado().equals(EstadoPedidoDTO.REGISTRADO)) {
			pedidoDTO.setEstado(EstadoPedidoDTO.PRODUCAO);
			try {
				this.alterar(pedidoDTO);
			} catch (NotValidDataException e) {
				throw new NotValidDataException("Pedido nao pode mudar para Producao");
			} catch (NotFoundException e) {
				throw new NotValidDataException("Pedido nao encontrado");
			}
		} else {
			throw new NotValidDataException("Pedido esta no estado: " + pedidoDTO.getEstado() + ", nao pode mudar para Producao");
		}
	}

	public void mudarPedidoParaPronto(PedidoDTO pedidoDTO) throws NotValidDataException, NotFoundException {

		if (pedidoDTO.getEstado().equals(EstadoPedidoDTO.PRODUCAO)) {
			pedidoDTO.setEstado(EstadoPedidoDTO.PRONTO);
			try {
				this.alterar(pedidoDTO);
			} catch (NotValidDataException e) {
				throw new NotValidDataException("Pedido nao pode mudar para Pronto");
			} catch (NotFoundException e) {
				throw new NotValidDataException("Pedido nao encontrado");
			}
		} else {
			throw new NotValidDataException("Pedido esta no estado: " + pedidoDTO.getEstado() + ", nao pode mudar para Pronto");
		}
	}

	public void mudarPedidoParaEntrega(PedidoDTO pedidoDTO) throws NotValidDataException, NotFoundException {

		if (pedidoDTO.getEstado().equals(EstadoPedidoDTO.PRONTO)) {
			pedidoDTO.setEstado(EstadoPedidoDTO.ENTREGA);
			try {
				this.alterar(pedidoDTO);
			} catch (NotValidDataException e) {
				throw new NotValidDataException("Pedido nao pode mudar para Entrega");
			} catch (NotFoundException e) {
				throw new NotValidDataException("Pedido nao encontrado");
			}
		} else {
			throw new NotValidDataException("Pedido esta no estado: " + pedidoDTO.getEstado() + ", nao pode mudar para Entrega");
		}
	}

	public void mudarPedidoParaConcluido(PedidoDTO pedidoDTO) throws NotValidDataException, NotFoundException {

		if (pedidoDTO.getEstado().equals(EstadoPedidoDTO.ENTREGA)) {
			pedidoDTO.setEstado(EstadoPedidoDTO.CONCLUIDO);
			try {
				this.alterar(pedidoDTO);
			} catch (NotValidDataException e) {
				throw new NotValidDataException("Pedido nao pode mudar para Concluido");
			} catch (NotFoundException e) {
				throw new NotValidDataException("Pedido nao encontrado");
			}
		} else {
			throw new NotValidDataException("Pedido esta no estado: " + pedidoDTO.getEstado() + ", nao pode mudar para Concluido");
		}
		
	}

	public List<PedidoDTO> toDTOAll(List<Pedido> listaPedido) {
		List<PedidoDTO> listaDTO = new ArrayList<PedidoDTO>();

		for (Pedido pedido : listaPedido) {
			listaDTO.add(this.toDTO(pedido));
		}
		return listaDTO;
	}

	public PedidoDTO toDTO(Pedido pedido) {
		return this.modelMapper.map(pedido, PedidoDTO.class);
	}

	public Pedido toEntity(PedidoDTO pedidoDTO) {
		return this.modelMapper.map(pedidoDTO, Pedido.class);
	}

	public ItemPedidoDTO toItemPeditoDTO(ItemPedido itemPedido) {
		return this.modelMapper.map(itemPedido, ItemPedidoDTO.class);
	}

	public ItemPedido toItemPedidoEntity(ItemPedidoDTO itemPedidoDTO) {
		return this.modelMapper.map(itemPedidoDTO, ItemPedido.class);
	}
}
