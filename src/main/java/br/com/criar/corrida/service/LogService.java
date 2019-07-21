package br.com.criar.corrida.service;

import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;

import java.io.File;

public interface LogService {

    TransferDto<ResultadosDto> parse(File file);

}
