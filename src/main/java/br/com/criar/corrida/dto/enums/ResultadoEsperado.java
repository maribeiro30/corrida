package br.com.criar.corrida.dto.enums;

import java.util.Arrays;
import java.util.stream.Collectors;



public enum ResultadoEsperado {


    MELHOR_VOLTA_PILOTO(1,"A melhor volta de cada piloto."),
    MELHOR_VOLTA_CORRIDA(2,"A melhor volta da corrida"),
    VELOCIDADE_MEDIA_PILOTO_CORRIDA(3,"Calcular a velocidade média de cada piloto durante toda corrida."),
    DIFERENCA_PILOTO_PRIMEIRO_COLOCADO_CADA_VOLTA(4,"Descobrir quanto tempo cada piloto chegou do primeiro colocado em cada volta."),
    DIFERENCA_PILOTO_APOS_VENCEDOR(5,"Descobrir quanto tempo cada piloto chegou após o vencedor.");


    private Integer codigo;
    private String descricao;

    private ResultadoEsperado(Integer codigo, String descricao){
        this.codigo = codigo;
        this.descricao = descricao;
    }


    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static ResultadoEsperado getResultadoEsperado(Integer codigo) {
        return Arrays.stream(ResultadoEsperado.values()).filter(c -> c.codigo.equals(codigo)).findAny()
                .orElse(null);

    }

    public static String getDominios(){
        return Arrays.stream(ResultadoEsperado.values()).map(Object::toString).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return "Codigo=" + codigo +
                ", descricao='" + descricao;
    }
}
