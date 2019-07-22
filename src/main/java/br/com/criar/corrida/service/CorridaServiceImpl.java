package br.com.criar.corrida.service;


import br.com.criar.corrida.dto.PilotoDto;
import br.com.criar.corrida.dto.ResultadoDto;
import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;
import br.com.criar.corrida.dto.enums.ResultadoEsperado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CorridaServiceImpl implements CorridaService{

    @Autowired
    private LogService logService;

    @Override
    public TransferDto<ResultadosDto> processarLog(Path log, String resultadoEsperado) {
        TransferDto<ResultadosDto>  transferResultados = logService.parse(log);

        if(!HttpStatus.OK.equals(transferResultados.getHttpStatus()))
            return transferResultados;


        if(Objects.isNull(resultadoEsperado))
            return resultadoCorrida(transferResultados.getT());

        switch(ResultadoEsperado.getResultadoEsperado(Integer.valueOf(resultadoEsperado))){
            case MELHOR_VOLTA_PILOTO: return melhorVoltaCadaPiloto(transferResultados.getT());
            case DIFERENCA_PILOTO_APOS_VENCEDOR: return diferencaPilotosAposVencedor(transferResultados.getT());
            case MELHOR_VOLTA_CORRIDA: return melhorVoltaCadaPiloto(transferResultados.getT());
            case VELOCIDADE_MEDIA_PILOTO_CORRIDA: return velocidadeMediaCadaPilotoDuranteCorrida(transferResultados.getT());
            case DIFERENCA_PILOTO_PRIMEIRO_COLOCADO_CADA_VOLTA: return diferencaPilotosPrimeiroColocadoEmCadaVolta(transferResultados.getT());
                default: return TransferDto.<ResultadosDto>builder().httpStatus(HttpStatus.BAD_REQUEST).description("Resultado para calculo nao encontrado.").build();

        }
    }

    @Override
    public TransferDto<ResultadosDto> resultadoCorrida(ResultadosDto resultados) {

        ResultadosDto apuracoes = ResultadosDto.builder().resultados(new ArrayList<ResultadoDto>()).build();

        resultados.getResultados().stream().sorted(Comparator.comparing(ResultadoDto::getNumeroVoltas))
                                                .forEach( r -> {{
                                                    Optional<ResultadoDto> optDto = apuracoes.getResultados().stream().filter(p-> p.getPiloto().getCodigo().equals(r.getPiloto().getCodigo()))
                                                                                            .findFirst();
                                                   if(optDto.isPresent()){
                                                               optDto.get().getTempoTotal().plusMinutes(r.getTempoVolta().getMinuteOfHour());
                                                               optDto.get().getTempoTotal().plusHours(r.getTempoVolta().getMillisOfDay());
                                                               optDto.get().getTempoTotal().plusSeconds(r.getTempoVolta().getSecondOfMinute());
                                                               optDto.get().setUltimaVoltaCompleta(r.getNumeroVoltas());
                                                               if(optDto.get().getUltimaVoltaCompleta() == 4)
                                                                   optDto.get().setFinalizouCorrida(true);

                                                   } else{
                                                        apuracoes.getResultados().add(ResultadoDto.builder()
                                                                .piloto(r.getPiloto())
                                                                .numeroVoltas(r.getNumeroVoltas())
                                                                .tempoTotal(r.getTempoVolta())
                                                                .build());
                                                   }

                                                }});

        resultados.getResultados().stream().sorted(Comparator.comparing(ResultadoDto::getNumeroVoltas).reversed()
                                                          .thenComparing(ResultadoDto::getTempoTotal));
        final int[] seq = {0};
        resultados.getResultados().forEach(r->r.setPosicaoChegada(++seq[0]));
        return TransferDto.<ResultadosDto>builder().t(resultados).httpStatus(HttpStatus.OK).build();
    }

    @Override
    public TransferDto<ResultadosDto> melhorVoltaCadaPiloto(ResultadosDto resultados) {
        return null;
    }

    @Override
    public TransferDto<ResultadosDto> velocidadeMediaCadaPilotoDuranteCorrida(ResultadosDto resultados) {
        return null;
    }

    @Override
    public TransferDto<ResultadosDto> diferencaPilotosPrimeiroColocadoEmCadaVolta(ResultadosDto resultados) {
        return null;
    }

    @Override
    public TransferDto<ResultadosDto> diferencaPilotosAposVencedor(ResultadosDto resultados) {
        return null;
    }

}
