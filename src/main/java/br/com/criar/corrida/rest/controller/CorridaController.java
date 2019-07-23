package br.com.criar.corrida.rest.controller;

import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;
import br.com.criar.corrida.service.CorridaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static br.com.criar.corrida.utils.JodaUtils.PATTERM_BR_15_DDMMYYYY_HH24MMSSSSS;
import static br.com.criar.corrida.utils.JodaUtils.formatarnow;

@Controller
@RequestMapping
@Api(value = "Processamento de LOG.", description = "Processar o arquivo de logs da corrida.")
public class CorridaController {

    private final static String RESULTADO_ESPERADO = " Para processamento diferente do resultado final da corrida, " +
                                                        "utilizar a queryparam abaixo: \n" +
            "1-A melhor volta de cada piloto. \n" +
            "2-A melhor volta da corrida \n" +
            "3-Calcular a velocidade média de cada piloto durante toda corrida. \n" +
            "4-Descobrir quanto tempo cada piloto chegou do primeiro colocado em cada volta. \n" +
            "5-Descobrir quanto tempo cada piloto chegou após o vencedor.";


    @Autowired
    private CorridaService corridaService;


    @PostMapping("/log")
    @ApiOperation(value = "Enviar arquivo de log, para processamento. Dominios ",
            consumes = MediaType.TEXT_EVENT_STREAM_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<ResponseEntity<?>> createLog(@ApiParam(value = RESULTADO_ESPERADO, required = false)
                                                 @RequestParam(value="resultado_esperado", required=false) String resultadoEsperado,
                                              @ApiParam(value = "Arquivo de log para upload", required = true)
                                              @RequestParam("file") MultipartFile file) {

        if(!file.isEmpty()){
            String nameFile = formatarnow(PATTERM_BR_15_DDMMYYYY_HH24MMSSSSS).replaceAll(" ","_").concat("-"+file.getOriginalFilename());

            try {

                Path path = Files.write(new File(nameFile).toPath(),file.getBytes());
                final TransferDto<ResultadosDto> transfRetorno = new TransferDto<ResultadosDto>();

                Flux.just(corridaService.processarLog(path,resultadoEsperado)).subscribe(s ->{{
                    transfRetorno.setDescription(s.getDescription());
                    transfRetorno.setHttpStatus(s.getHttpStatus());
                    transfRetorno.setT(s.getT());
                }} );

                return switchResponseByRetorno(transfRetorno);

            } catch (IOException e) {
                return Mono.just(new ResponseEntity<String>(nameFile + ", msg : " + e.getMessage(),HttpStatus.BAD_GATEWAY));
            }

        }else {
            return Mono.just(new ResponseEntity<String>("Arquivo vazio.",HttpStatus.BAD_GATEWAY));
        }
    }

    private Mono<ResponseEntity<?>> switchResponseByRetorno(TransferDto<ResultadosDto> transfRetorno) {
        switch (transfRetorno.getHttpStatus()) {
            case OK:
                return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/json"))
                        .body(transfRetorno.getT()));
            default:
                return Mono.just(new ResponseEntity<String>("msg : " + transfRetorno.getDescription(), transfRetorno.getHttpStatus()));
        }
    }

}
