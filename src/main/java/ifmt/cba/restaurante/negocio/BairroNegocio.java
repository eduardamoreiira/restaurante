package ifmt.cba.restaurante.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ifmt.cba.restaurante.dto.BairroDTO;
import ifmt.cba.restaurante.entity.Bairro;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.repository.BairroRepository;

@Service
public class BairroNegocio {

    private ModelMapper modelMapper;

    @Autowired
    private BairroRepository bairroRepository;

    public BairroNegocio() {
        this.modelMapper = new ModelMapper();
    }

    public BairroDTO inserir(BairroDTO bairroDTO) throws NotValidDataException {

        Bairro bairro = this.toEntity(bairroDTO);
        String mensagemErros = bairro.validar();

        if (!mensagemErros.isEmpty()) {
            throw new NotValidDataException(mensagemErros);
        }

        try {
             
            if (bairroRepository.findByNomeIgnoreCaseStartingWith(bairro.getNome()) != null) {
                throw new NotValidDataException("Ja existe esse bairro");
            }

            bairro = bairroRepository.save(bairro);
        } catch (Exception ex) {
            throw new NotValidDataException("Erro ao incluir o bairro - " + ex.getMessage());
        }
        return this.toDTO(bairro);
    }

    public BairroDTO alterar(BairroDTO bairroDTO) throws NotValidDataException, NotFoundException {

        Bairro bairro = this.toEntity(bairroDTO);
        String mensagemErros = bairro.validar();
        if (!mensagemErros.isEmpty()) {
            throw new NotValidDataException(mensagemErros);
        }
        try {
            if (bairroRepository.findById(bairro.getCodigo()).get() == null) {
                throw new NotFoundException("Nao existe esse bairro");
            }
            bairroRepository.save(bairro);
            bairro = bairroRepository.save(bairro);
        } catch (Exception ex) {
            throw new NotValidDataException("Erro ao alterar o bairro - " + ex.getMessage());
        }
        return this.toDTO(bairro);
    }

    public void excluir(int codigo) throws NotValidDataException {

        try {
            Bairro bairro = bairroRepository.findById(codigo).get();
            if (bairro == null) {
                throw new NotValidDataException("Nao existe esse bairro");
            }

            bairroRepository.delete(bairro);
        } catch (Exception ex) {
            throw new NotValidDataException("Erro ao excluir o bairro - " + ex.getMessage());
        }
    }

    public List<BairroDTO> pesquisaTodos() throws NotFoundException {
        try {
            return this.toDTOAll(bairroRepository.findAll());
        } catch (Exception ex) {
            throw new NotFoundException("Erro ao pesquisar bairro pelo nome - " + ex.getMessage());
        }
    }

    public BairroDTO pesquisaPorNome(String parteNome) throws NotFoundException {
        try {
            return this.toDTO(bairroRepository.findByNomeIgnoreCaseStartingWith(parteNome));
        } catch (Exception ex) {
            throw new NotFoundException("Erro ao pesquisar bairro pelo nome - " + ex.getMessage());
        }
    }

    public BairroDTO pesquisaCodigo(int codigo) throws NotFoundException {
        try {
            return this.toDTO(bairroRepository.findById(codigo).get());
        } catch (Exception ex) {
            throw new NotFoundException("Erro ao pesquisar bairro pelo codigo - " + ex.getMessage());
        }
    }

    public List<BairroDTO> toDTOAll(List<Bairro> listaBarro) {
        List<BairroDTO> listaDTO = new ArrayList<BairroDTO>();

        for (Bairro bairro : listaBarro) {
            listaDTO.add(this.toDTO(bairro));
        }
        return listaDTO;
    }

    public BairroDTO toDTO(Bairro bairro) {
        return this.modelMapper.map(bairro, BairroDTO.class);
    }

    public Bairro toEntity(BairroDTO bairroDTO) {
        return this.modelMapper.map(bairroDTO, Bairro.class);
    }
}
