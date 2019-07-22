package br.com.criar.corrida.dto;

import lombok.*;
import org.joda.time.LocalTime;

import java.math.BigDecimal;


@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoDto {

    //Campos entrada
    private LocalTime horario;
    private String colunaPiloto;
    private PilotoDto piloto;
    private Integer numeroVoltas;
    private LocalTime tempoVolta;
    private BigDecimal velocidadeMedia;

    private Boolean finalizouCorrida;
    private LocalTime tempoTotal;
    private Integer ultimaVoltaCompleta;
    private Integer posicaoChegada;


}
