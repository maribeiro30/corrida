package br.com.criar.corrida.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultadosDto {

    private List<ResultadoDto> resultados;
    private List<RetornoResultadoDto> retornoResultados;

}
