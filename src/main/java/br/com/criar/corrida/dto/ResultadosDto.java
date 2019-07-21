package br.com.criar.corrida.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ResultadosDto {

    private List<ResultadoDto> resultados;

}
