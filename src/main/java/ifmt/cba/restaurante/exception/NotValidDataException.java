package ifmt.cba.restaurante.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotValidDataException extends Exception{

    public NotValidDataException(){
        super("Erro de validacao dos dados");
    }
    public NotValidDataException(String mensagem){
        super(mensagem);
    }
}
