package br.com.criar.corrida.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoDto {

    private LocalTime horario;
    private String colunaPiloto;
    private PilotoDto piloto;
    private Integer numeroVoltas;
    private LocalTime tempoVolta;
    private BigDecimal velocidadeMedia;


}
