package br.com.criar.corrida.service;

import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;

import java.nio.file.Path;

public interface CorridaService {

    TransferDto<ResultadosDto> processarLog(Path log, String resultadoEsperado);

    TransferDto<ResultadosDto> resultadoCorrida(ResultadosDto resultados);

    TransferDto<ResultadosDto> melhorVoltaCadaPiloto(ResultadosDto resultados);

    TransferDto<ResultadosDto> melhorVoltaCorrida(ResultadosDto resultados);

    TransferDto<ResultadosDto> velocidadeMediaCadaPilotoDuranteCorrida(ResultadosDto resultados);

    TransferDto<ResultadosDto>  diferencaPilotosPrimeiroColocadoEmCadaVolta(ResultadosDto resultados);

    TransferDto<ResultadosDto> diferencaPilotosAposVencedor(ResultadosDto resultados);


}
