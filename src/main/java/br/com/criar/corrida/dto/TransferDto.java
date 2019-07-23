package br.com.criar.corrida.dto;


import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransferDto<T> {

    private HttpStatus httpStatus;
    private String description;
    private T t;


}
