package ifmt.cba.restaurante.exception;

import java.util.Date;

public record ExceptionResponse( 
    Date data, 
    String mensagem, 
    String detalhes) { }
