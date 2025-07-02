package ifmt.cba.restaurante.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import ifmt.cba.restaurante.dto.BairroDTO;
import ifmt.cba.restaurante.exception.NotFoundException;
import ifmt.cba.restaurante.exception.NotValidDataException;
import ifmt.cba.restaurante.negocio.BairroNegocio;

@RestController()
@RequestMapping("/bairro")
public class BairroController {

    @Autowired
    private BairroNegocio bairroNegocio;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BairroDTO> buscarTodos() throws NotFoundException, NotValidDataException {
        List<BairroDTO> listaBairroTempDTO = bairroNegocio.pesquisaTodos();

        for (BairroDTO bairroDTO : listaBairroTempDTO) {
            addHateoasLinksCRUD(bairroDTO);
        }
        
        return listaBairroTempDTO;
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO buscarPorID(@PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.pesquisaCodigo(codigo);
        addHateoasLinksCRUD(bairroTempDTO);
        return bairroTempDTO;
    }

    @GetMapping(value = "/nome/{nome}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO buscarPorNome(@PathVariable("nome") String nome) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.pesquisaPorNome(nome);
        addHateoasLinksCRUD(bairroTempDTO);
        return bairroTempDTO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO inserirBairro(@RequestBody BairroDTO bairroDTO) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.inserir(bairroDTO);
        
        addHateoasLinksCRUD(bairroTempDTO);
        return bairroTempDTO;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BairroDTO alterarBairro(@RequestBody BairroDTO bairroDTO) throws NotFoundException, NotValidDataException {
        BairroDTO bairroTempDTO = bairroNegocio.alterar(bairroDTO);
        
        addHateoasLinksCRUD(bairroTempDTO);
        return bairroTempDTO;
    }

    @DeleteMapping(value = "/{codigo}")
    public ResponseEntity<?> excluirBairro(@PathVariable("codigo") int codigo) throws NotFoundException, NotValidDataException {
        bairroNegocio.excluir(codigo);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinksCRUD(BairroDTO bairroDTO) throws NotFoundException, NotValidDataException {
        bairroDTO.add(linkTo(methodOn(BairroController.class).buscarPorID(bairroDTO.getCodigo())).withSelfRel().withType("GET"));
        bairroDTO.add(linkTo(methodOn(BairroController.class).buscarPorNome(bairroDTO.getNome())).withRel("buscarPorNome").withType("GET"));
        bairroDTO.add(linkTo(methodOn(BairroController.class).inserirBairro(bairroDTO)).withRel("inserirBairro").withType("POST"));
        bairroDTO.add(linkTo(methodOn(BairroController.class).alterarBairro(bairroDTO)).withRel("alterarBairro").withType("PUT"));
        bairroDTO.add(linkTo(methodOn(BairroController.class).excluirBairro(bairroDTO.getCodigo())).withRel("excluirBairro").withType("DELETE"));
    }

}
