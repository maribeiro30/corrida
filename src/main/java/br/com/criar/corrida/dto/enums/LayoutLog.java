package br.com.criar.corrida.dto.enums;

import br.com.criar.corrida.dto.ResultadoDto;

public enum LayoutLog {


    HORA(0,18) {
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {

        }
    },
    PILOTO(17,36){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {

        }
    },
    NUMERO_VOLTA(35,11){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {

        }
    },
    TEMPO_VOLTA(62,24){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {

        }
    },
    VELOCIDADE_MEDIA_VOLTA(76,25){
        @Override
        public void processarCampo(ResultadoDto resultado, String linha) {

        }
    };

    private Integer start;
    private Integer size;

    private LayoutLog(Integer start, Integer size){
        this.start = start;
        this.size  = size;
    }

    abstract void processarCampo(ResultadoDto resultado, String linha);

}
