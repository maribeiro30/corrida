package br.com.criar.corrida.dto;


import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferDto<T> {

    private HttpStatus httpStatus;
    private String description;
    private T t = null;


}
