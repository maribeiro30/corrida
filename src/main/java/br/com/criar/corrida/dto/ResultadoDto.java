package br.com.criar.corrida.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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



}
