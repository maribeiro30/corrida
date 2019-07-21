package br.com.criar.corrida.service;

import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;

import java.io.File;

public interface CorridaService {


    TransferDto<ResultadosDto> processarLog(File log);


}
