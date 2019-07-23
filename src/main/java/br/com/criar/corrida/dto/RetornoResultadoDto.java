package br.com.criar.corrida.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.joda.time.LocalTime;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value= {"tempoTotal","melhorVoltaPiloto","diferencaPrimeiroColocado","tempoVolta"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetornoResultadoDto {

    private PilotoDto piloto;
    @JsonProperty("completou-a-prova")
    private Boolean terminouCorrida;
    private LocalTime tempoTotal;
    @JsonProperty("tempo-prova")
    private String tempoFinal;
    private Integer ultimaVoltaCompleta;
    @JsonProperty("colocação")
    private Integer colocacao;
    private LocalTime diferencaPrimeiroColocado;
    @JsonProperty("diferenca-primeiro-colocado")
    private String diferencaPrimeiroColocadoCorrida;


    private LocalTime melhorVoltaPiloto;
    @JsonProperty("melhor-volta-piloto")
    private String melhorVoltaPilotoString;
    @JsonProperty("melhor-volta-prova")
    private String melhorVoltaString;

    @JsonProperty("valocidade-media-prova")
    private Double velocidadeMediaProva;

    private LocalTime tempoVolta;
    @JsonProperty("tempo-por-volta")
    private String tempoVoltaString;
    @JsonProperty("volta")
    private Integer volta;

}
