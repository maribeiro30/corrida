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
                                                    calcularResultadoCorrida(apuracoes, r, optDto);

                                                }});

        apuracoes.getRetornoResultados().stream().sorted(comparing(RetornoResultadoDto::getTempoTotal, Comparator.naturalOrder())
                                                          .thenComparing(RetornoResultadoDto::getUltimaVoltaCompleta, Comparator.naturalOrder()));
        final int[] seq = {0};
        apuracoes.getRetornoResultados().forEach(r-> {{r.setColocacao(++seq[0]);
                                                       r.setTempoFinal(convert(r.getTempoTotal(), PATTERN_MINUTS)); }});
        return TransferDto.<ResultadosDto>builder().t(apuracoes).httpStatus(HttpStatus.OK).build();
    }

    private void calcularResultadoCorrida(ResultadosDto apuracoes, ResultadoDto r, Optional<RetornoResultadoDto> optDto) {
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
    }

    @Override
    public TransferDto<ResultadosDto> melhorVoltaCadaPiloto(ResultadosDto resultados) {
        Map<Integer,RetornoResultadoDto> map =  new HashMap<>();

       resultados.getResultados().stream().forEach(r -> {{
           calcularMelhorVoltaCorridaCadaPiloto(map, r);

       }});

        return TransferDto.<ResultadosDto>builder()
                .httpStatus(HttpStatus.OK)
                .t(ResultadosDto.builder().retornoResultados(map.values().stream().collect(Collectors.toList())).build())
                .build();
    }

    private void calcularMelhorVoltaCorridaCadaPiloto(Map<Integer, RetornoResultadoDto> map, ResultadoDto r) {
        if(map.containsKey(r.getPiloto().getCodigo())) {
             if(map.get(r.getPiloto().getCodigo()).getMelhorVoltaPiloto().compareTo(r.getTempoVolta()) > 0) {
                 map.get(r.getPiloto().getCodigo()).setMelhorVoltaPiloto(r.getTempoVolta());
                 map.get(r.getPiloto().getCodigo()).setMelhorVoltaPilotoString(convert(r.getTempoVolta(), PATTERN_MINUTS));
             }

        }else{
            map.put(r.getPiloto().getCodigo(), RetornoResultadoDto.builder()
                    .piloto(r.getPiloto())
                    .melhorVoltaPiloto(r.getTempoVolta())
                    .melhorVoltaPilotoString(convert(r.getTempoVolta(), PATTERN_MINUTS))
                    .build());
        }
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
                                                                            .melhorVoltaString(convert(melhorVolta.getTempoVolta(), PATTERN_MINUTS))
                                                                            .build()))
                        .build())
                .build();
    }

    @Override
    public TransferDto<ResultadosDto> velocidadeMediaCadaPilotoDuranteCorrida(ResultadosDto resultados) {
        Map<PilotoDto,List<BigDecimal>> map = new HashMap<PilotoDto, List<BigDecimal>>();

        resultados.getResultados().stream().forEach(r -> {{
            agruparVelocidadesCadaVoltaPorPiloto(map, r);
        }});

        List<RetornoResultadoDto> retornos = new ArrayList<>();
        for( PilotoDto p: map.keySet()) {
            calcularVelocidadeMediaCadaPilotoDuranteCorrida(map, retornos, p);
        }

        return TransferDto.<ResultadosDto>builder()
                .httpStatus(HttpStatus.OK)
                .t(ResultadosDto.builder()
                        .retornoResultados(retornos)
                        .build())
                .build();
    }

    private void agruparVelocidadesCadaVoltaPorPiloto(Map<PilotoDto, List<BigDecimal>> map, ResultadoDto r) {
        if(map.containsKey(r.getPiloto())) {
            map.get(r.getPiloto()).add(new BigDecimal(r.getVelocidadeMedia().doubleValue()));
        }else {
            map.put(r.getPiloto(), new ArrayList<BigDecimal>());
            map.get(r.getPiloto()).add(r.getVelocidadeMedia());
        }
    }

    private void calcularVelocidadeMediaCadaPilotoDuranteCorrida(Map<PilotoDto, List<BigDecimal>> map, List<RetornoResultadoDto> retornos, PilotoDto p) {
        BigDecimal sumary = new BigDecimal(map.get(p).stream().mapToDouble(BigDecimal::doubleValue).sum());
        Integer voltas = map.get(p).size();
        BigDecimal vm = new BigDecimal(sumary.doubleValue() / voltas).setScale(3, RoundingMode.HALF_DOWN);
        retornos.add(RetornoResultadoDto.builder()
                .piloto(p)
                .velocidadeMediaProva(vm.doubleValue())
                .build());
    }

    @Override
    public TransferDto<ResultadosDto> diferencaPilotosPrimeiroColocadoEmCadaVolta(ResultadosDto resultados) {

        Map<Integer,List<ResultadoDto>> numeroVoltas = new HashMap<>();
        preencherAgrupandoPorVoltas(resultados, numeroVoltas);

        Integer[] controle = {0,0}; //[0] volta anterior; [1] colocacao
        List<RetornoResultadoDto> retornos = new ArrayList<>();
        for(Integer numero : numeroVoltas.keySet()) {
            controle[1] = 1;
            numeroVoltas.get(numero).stream().sorted((t1, t2) -> t1.getTempoVolta().compareTo(t2.getTempoVolta()))
                    .forEach(c -> {
                        {
                            calcularTempoCadaPilotoPorVoltaPrimeiroColocado(controle, retornos, numero, c);
                            ++controle[1];
                        }
                    });
        }

        return TransferDto.<ResultadosDto>builder()
                .httpStatus(HttpStatus.OK)
                .t(ResultadosDto.builder()
                        .retornoResultados(retornos)
                        .build())
                .build();
    }

    private void calcularTempoCadaPilotoPorVoltaPrimeiroColocado(Integer[] controle, List<RetornoResultadoDto> retornos, Integer numero, ResultadoDto c) {
        if(!controle[0].equals(numero)){
            retornos.add(RetornoResultadoDto.builder()
                    .piloto(c.getPiloto())
                    .colocacao(controle[1])
                    .tempoVolta(c.getTempoVolta())
                    .volta(c.getNumeroVoltas())
                    .build());
            controle[0] = numero;
        }else{
            LocalTime lt = c.getTempoVolta().minusMinutes(retornos.get(0).getTempoVolta().getMinuteOfHour())
                    .minusSeconds(retornos.get(0).getTempoVolta().getSecondOfMinute())
                    .minusMillis(retornos.get(0).getTempoVolta().getMillisOfSecond());
            retornos.add(RetornoResultadoDto.builder()
                    .piloto(c.getPiloto())
                    .colocacao(controle[1])
                    .tempoVolta(c.getTempoVolta())
                    .volta(c.getNumeroVoltas())
                    .diferencaPrimeiroColocado(lt)
                    .diferencaPrimeiroColocadoCorrida(convert(lt,PATTERN_MINUTS))
                    .build());
        }
    }

    private void preencherAgrupandoPorVoltas(ResultadosDto resultados, Map<Integer, List<ResultadoDto>> numeroVoltas) {
        resultados.getResultados().stream()
                                    .sorted(comparing(ResultadoDto::getNumeroVoltas))
                                        .forEach(r -> {{
                                            if(numeroVoltas.containsKey(r.getNumeroVoltas()))
                                                numeroVoltas.get(r.getNumeroVoltas()).add(ResultadoDto.builder()
                                                                                            .colunaPiloto(r.getColunaPiloto())
                                                                                            .horario(r.getHorario())
                                                                                            .numeroVoltas(r.getNumeroVoltas())
                                                                                            .piloto(r.getPiloto())
                                                                                            .tempoVolta(r.getTempoVolta())
                                                                                            .velocidadeMedia(r.getVelocidadeMedia())
                                                                                            .build());
                                            else {
                                                List<ResultadoDto> list = new ArrayList<>();
                                                list.add(ResultadoDto.builder()
                                                        .colunaPiloto(r.getColunaPiloto())
                                                        .horario(r.getHorario())
                                                        .numeroVoltas(r.getNumeroVoltas())
                                                        .piloto(r.getPiloto())
                                                        .tempoVolta(r.getTempoVolta())
                                                        .velocidadeMedia(r.getVelocidadeMedia())
                                                        .build());
                                                numeroVoltas.put(r.getNumeroVoltas(), list);
                                            }
                                        }});
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
