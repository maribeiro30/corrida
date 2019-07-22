package br.com.criar.corrida.service;

import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;

import java.nio.file.Path;

public interface LogService {


    TransferDto<ResultadosDto> parse(Path path);

}
