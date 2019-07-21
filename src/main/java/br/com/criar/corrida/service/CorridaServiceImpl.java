package br.com.criar.corrida.service;


import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CorridaServiceImpl implements CorridaService{
    @Override
    public TransferDto<ResultadosDto> processarLog(File log) {
        return null;

    }

}
