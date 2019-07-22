package br.com.criar.corrida.service;

import br.com.criar.corrida.dto.ResultadoDto;
import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;
import br.com.criar.corrida.dto.enums.LayoutLog;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class LogServiceImpl implements LogService {


    @Override
    public TransferDto<ResultadosDto> parse(Path path) {
        TransferDto<ResultadosDto> transferHeader = null;
        try (BufferedReader buffer = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            transferHeader = processarTxt(buffer);
            Files.deleteIfExists(path);
            return transferHeader;
        } catch (IOException e) {
            return TransferDto.<ResultadosDto>builder().httpStatus(HttpStatus.BAD_REQUEST).description(e.getMessage()).build();
        }
    }

    private TransferDto<ResultadosDto> processarTxt(BufferedReader buffer) throws IOException {
        ResultadosDto resultados = ResultadosDto.builder().resultados(new ArrayList<ResultadoDto>()).build();

        int readLineCount = 0;
        String line = null;
        while((line = buffer.readLine()) != null) {
            ++readLineCount;
            if(readLineCount == 1)
                continue;
            else resultados.getResultados().add(processarBody(line,readLineCount));
        }

        return TransferDto.<ResultadosDto>builder().httpStatus(HttpStatus.OK).t(resultados).build();
    }

    private ResultadoDto processarBody(String line, Integer countLine) {
        final ResultadoDto body = new ResultadoDto();

        Arrays.stream(LayoutLog.values()).forEach(l -> l.processarCampo(body, line));
        return body;
    }


}
