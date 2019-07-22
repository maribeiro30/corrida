package br.com.criar.corrida.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
public class ResultadosDto {

    private List<ResultadoDto> resultados;

}
