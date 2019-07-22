package br.com.criar.corrida.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class PilotoDto {

    private Integer codigo;
    private String nome;

}
