package br.com.criar.corrida.service;


import br.com.criar.corrida.dto.*;
import br.com.criar.corrida.dto.enums.ResultadoEsperado;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.criar.corrida.utils.JodaUtils.PATTERN_MINUTS;
import static br.com.criar.corrida.utils.JodaUtils.convert;
import static java.util.Comparator.comparing;


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
            case MELHOR_VOLTA_CORRIDA: return melhorVoltaCorrida(transferResultados.getT());
            case VELOCIDADE_MEDIA_PILOTO_CORRIDA: return velocidadeMediaCadaPilotoDuranteCorrida(transferResultados.getT());
            case DIFERENCA_PILOTO_PRIMEIRO_COLOCADO_CADA_VOLTA: return diferencaPilotosPrimeiroColocadoEmCadaVolta(transferResultados.getT());
                default: return TransferDto.<ResultadosDto>builder().httpStatus(HttpStatus.BAD_REQUEST).description("Resultado para calculo nao encontrado.").build();

        }
    }

    @Override
    public TransferDto<ResultadosDto> resultadoCorrida(ResultadosDto resultados) {

        ResultadosDto apuracoes = ResultadosDto.builder().retornoResultados(new ArrayList<RetornoResultadoDto>()).build();

        resultados.getResultados().stream().sorted(comparing(ResultadoDto::getNumeroVoltas))
                                                .forEach( r -> {{
                                                    Optional<RetornoResultadoDto> optDto = apuracoes.getRetornoResultados().stream().filter(p-> p.getPiloto().getCodigo().equals(r.getPiloto().getCodigo()))
                                                                                            .findFirst();
                                                   if(optDto.isPresent()){
                                                               LocalTime lt = optDto.get().getTempoTotal().plusMinutes(r.getTempoVolta().getMinuteOfHour()).plusSeconds(r.getTempoVolta().getSecondOfMinute());
                                                               optDto.get().setTempoTotal(lt);
                                                               optDto.get().setUltimaVoltaCompleta(r.getNumeroVoltas());
                                                               if(optDto.get().getUltimaVoltaCompleta() == 4)
                                                                   optDto.get().setTerminouCorrida(true);
                                                               else optDto.get().setTerminouCorrida(false);

                                                   } else{
                                                        apuracoes.getRetornoResultados().add(RetornoResultadoDto.builder()
                                                                .piloto(r.getPiloto())
                                                                .ultimaVoltaCompleta(r.getNumeroVoltas())
                                                                .tempoTotal(r.getTempoVolta())
                                                                .build());
                                                   }

                                                }});

        apuracoes.getRetornoResultados().stream().sorted(comparing(RetornoResultadoDto::getTempoTotal, Comparator.naturalOrder())
                                                          .thenComparing(RetornoResultadoDto::getUltimaVoltaCompleta, Comparator.naturalOrder()));
        final int[] seq = {0};
        apuracoes.getRetornoResultados().forEach(r-> {{r.setColocacao(++seq[0]);
                                                       r.setTempoFinal(convert(r.getTempoTotal(), PATTERN_MINUTS)); }});
        return TransferDto.<ResultadosDto>builder().t(apuracoes).httpStatus(HttpStatus.OK).build();
    }

    @Override
    public TransferDto<ResultadosDto> melhorVoltaCadaPiloto(ResultadosDto resultados) {
        Map<Integer,RetornoResultadoDto> map =  new HashMap<>();

       resultados.getResultados().stream().forEach(r -> {{
           if(map.containsKey(r.getPiloto().getCodigo())) {
                if(map.get(r.getPiloto().getCodigo()).getMelhorVoltaPiloto().compareTo(r.getTempoVolta()) > 0) {
                    map.get(r.getPiloto().getCodigo()).setMelhorVoltaPiloto(r.getTempoVolta());
                    map.get(r.getPiloto().getCodigo()).setMelhorVoltaPilotoProva(convert(r.getTempoVolta(), PATTERN_MINUTS));
                }

           }else{
               map.put(r.getPiloto().getCodigo(), RetornoResultadoDto.builder()
                       .piloto(r.getPiloto())
                       .melhorVoltaPiloto(r.getTempoVolta())
                       .melhorVoltaPilotoProva(convert(r.getTempoVolta(), PATTERN_MINUTS))
                       .build());
           }

       }});


        return TransferDto.<ResultadosDto>builder()
                .httpStatus(HttpStatus.OK)
                .t(ResultadosDto.builder().retornoResultados(map.values().stream().collect(Collectors.toList())).build())
                .build();
    }


    @Override
    public TransferDto<ResultadosDto> melhorVoltaCorrida(ResultadosDto resultados) {

        ResultadoDto melhorVolta = resultados.getResultados()
                                                .stream()
                                                    .min((o1, o2) -> o1.getTempoVolta().compareTo(o2.getTempoVolta())).get();


        return TransferDto.<ResultadosDto>builder()
                .httpStatus(HttpStatus.OK)
                .t(ResultadosDto.builder()
                        .retornoResultados(Arrays.asList(RetornoResultadoDto.builder()
                                                                            .piloto(melhorVolta.getPiloto())
                                                                            .melhorVoltaProva(convert(melhorVolta.getTempoVolta(), PATTERN_MINUTS))
                                                                            .build()))
                        .build())
                .build();
    }

    @Override
    public TransferDto<ResultadosDto> velocidadeMediaCadaPilotoDuranteCorrida(ResultadosDto resultados) {
        Map<PilotoDto,List<BigDecimal>> map = new HashMap<PilotoDto, List<BigDecimal>>();

        resultados.getResultados().stream().forEach(r -> {{
            if(map.containsKey(r.getPiloto())) {
                map.get(r.getPiloto()).add(new BigDecimal(r.getVelocidadeMedia().doubleValue()));
            }else {
                map.put(r.getPiloto(), new ArrayList<BigDecimal>());
                map.get(r.getPiloto()).add(r.getVelocidadeMedia());
            }
        }});

        List<RetornoResultadoDto> retornos = new ArrayList<>();
        for( PilotoDto p: map.keySet()) {
            BigDecimal sumary = new BigDecimal(map.get(p).stream().mapToDouble(BigDecimal::doubleValue).sum());
            Integer voltas = map.get(p).size();
            BigDecimal vm = new BigDecimal(sumary.doubleValue() / voltas).setScale(3, RoundingMode.HALF_DOWN);
            retornos.add(RetornoResultadoDto.builder()
                    .piloto(p)
                    .velocidadeMediaProva(vm.doubleValue())
                    .build());
        }


        return TransferDto.<ResultadosDto>builder()
                .httpStatus(HttpStatus.OK)
                .t(ResultadosDto.builder()
                        .retornoResultados(retornos)
                        .build())
                .build();
    }

    @Override
    public TransferDto<ResultadosDto> diferencaPilotosPrimeiroColocadoEmCadaVolta(ResultadosDto resultados) {
/*
        List<RetornoResultadoDto> apuracoes = new ArrayList<>();

        resultados.getResultados().stream().sorted(comparing(ResultadoDto::getNumeroVoltas))
                .forEach( r -> {{
                    Optional<RetornoResultadoDto> optDto = apuracoes.getRetornoResultados().stream().filter(p-> p.getPiloto().getCodigo().equals(r.getPiloto().getCodigo()))
                            .findFirst();
                    if(optDto.isPresent()){
                        LocalTime lt = optDto.get().getTempoTotal().plusMinutes(r.getTempoVolta().getMinuteOfHour()).plusSeconds(r.getTempoVolta().getSecondOfMinute());
                        optDto.get().setTempoTotal(lt);
                        optDto.get().setUltimaVoltaCompleta(r.getNumeroVoltas());
                        if(optDto.get().getUltimaVoltaCompleta() == 4)
                            optDto.get().setTerminouCorrida(true);
                        else optDto.get().setTerminouCorrida(false);

                    } else{
                        apuracoes.getRetornoResultados().add(RetornoResultadoDto.builder()
                                .piloto(r.getPiloto())
                                .ultimaVoltaCompleta(r.getNumeroVoltas())
                                .tempoTotal(r.getTempoVolta())
                                .build());
                    }

                }});

        apuracoes.getRetornoResultados().stream().sorted(comparing(RetornoResultadoDto::getTempoTotal, Comparator.naturalOrder())
                .thenComparing(RetornoResultadoDto::getUltimaVoltaCompleta, Comparator.naturalOrder()));
        final int[] seq = {0};
        apuracoes.getRetornoResultados().forEach(r-> {{r.setColocacao(++seq[0]);
            r.setTempoFinal(convert(r.getTempoTotal(), PATTERN_MINUTS)); }});

*/
        return null;
    }

    @Override
    public TransferDto<ResultadosDto> diferencaPilotosAposVencedor(ResultadosDto resultados) {
        TransferDto<ResultadosDto> transferResultado = resultadoCorrida(resultados);
        if(!HttpStatus.OK.equals(transferResultado.getHttpStatus()))
            return transferResultado;

        LocalTime primeiro = transferResultado.getT().getRetornoResultados().get(0).getTempoTotal();

        transferResultado.getT().getRetornoResultados().forEach( r -> {{
            if(Integer.valueOf("1").equals(r.getColocacao()))
                return;

            r.setDiferencaPrimeiroColocado(r.getTempoTotal().minusMinutes(primeiro.getMinuteOfHour())
                                                                .minusSeconds(primeiro.getSecondOfMinute())
                                                                    .minusMillis(primeiro.getMillisOfSecond()));

            r.setDiferencaPrimeiroColocadoCorrida(convert(r.getDiferencaPrimeiroColocado(),PATTERN_MINUTS));
        }});

        return transferResultado;

    }

}
