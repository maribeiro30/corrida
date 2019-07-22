package br.com.criar.corrida.dto.enums;

import br.com.criar.corrida.dto.PilotoDto;
import br.com.criar.corrida.dto.ResultadoDto;

import java.math.BigDecimal;

import static  br.com.criar.corrida.utils.JodaUtils.convert;
import static  br.com.criar.corrida.utils.JodaUtils.PATTERN_HOURS;
import static  br.com.criar.corrida.utils.JodaUtils.PATTERN_MINUTS;


public enum LayoutLog {


    HORA(0,18) {
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {
            resultado.setHorario(convert(matche(linha), PATTERN_HOURS));
        }
    },
    PILOTO(17,36){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {
            resultado.setColunaPiloto(matche(linha));
            resultado.setPiloto(PilotoDto.builder().codigo(Integer.valueOf(resultado.getColunaPiloto().split("–")[0].trim()))
                                        .nome(resultado.getColunaPiloto().split("–")[1].trim())
                                        .build());
        }
    },
    NUMERO_VOLTA(55,62){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {
            resultado.setNumeroVoltas(Integer.valueOf( matche(linha).trim()));
        }
    },
    TEMPO_VOLTA(61,86){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {
            resultado.setTempoVolta(convert(matche(linha), PATTERN_MINUTS));
        }
    },
    VELOCIDADE_MEDIA_VOLTA(85,102){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {
            BigDecimal vlrMedia = new BigDecimal(linha.substring(85).trim().replace(",","."));
            vlrMedia.setScale(3);
            resultado.setVelocidadeMedia(vlrMedia);
        }
    };

    private Integer start;
    private Integer size;

    private LayoutLog(Integer start, Integer size){
        this.start = start;
        this.size  = size;
    }

    public abstract void processarCampo(ResultadoDto resultado, String linha);

    public String matche(String linha){
        return linha.substring(start,size);
    }

}
