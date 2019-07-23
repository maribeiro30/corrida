package br.com.criar.corrida.service;

import br.com.criar.corrida.dto.PilotoDto;
import br.com.criar.corrida.dto.ResultadoDto;
import br.com.criar.corrida.dto.ResultadosDto;
import br.com.criar.corrida.dto.TransferDto;
import br.com.criar.corrida.utils.JodaUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest

public class LogServiceImplTest {

    private final static String PATH_FILE = "./src/test/massadados/corrida.log";
    private Path path = null;

    @Autowired
    protected LogServiceImpl service;

    @Before
    public void setUp() throws Exception {
        path = Paths.get(PATH_FILE);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseOpenFileTest() throws IOException {

        Path pathTemp = Paths.get("teste_corrida.log");
        Files.copy(path,pathTemp, StandardCopyOption.REPLACE_EXISTING);

        TransferDto<ResultadosDto> transferResultados = service.parse(pathTemp);

        assertEquals("Falha ao abrir ou processar arquivo de  log.",HttpStatus.OK, transferResultados.getHttpStatus());
        assertNotNull("Arquivo processado, mas não extraiu linhas.",transferResultados.getT());
        assertEquals("Extração das linhas do arquivo de log esta com falhas",transferResultados.getT().getResultados().size(),23);

    }


    @Test
    public void parseOpenFileFailureTest(){
        Path pathTemp = Paths.get("failure.log");

        TransferDto<ResultadosDto> transferResultados = service.parse(pathTemp);

        assertNotEquals("Não retornou falha ao tentar abrir arquivo inexistente",HttpStatus.OK, transferResultados.getHttpStatus());

    }

    @Test
    public void verificandoConteudoExtraidoTest() throws IOException {

        //linha 16
        ResultadoDto tst = ResultadoDto.builder()
                            .horario(JodaUtils.convert( "23:52:01.796",JodaUtils.PATTERN_HOURS))
                            .colunaPiloto("011 – S.VETTEL")
                            .piloto(PilotoDto.builder()
                                            .codigo(11)
                                            .nome("S.VETTEL")
                                            .build())
                            .numeroVoltas(1)
                            .tempoVolta(JodaUtils.convert("3:31.315",JodaUtils.PATTERN_MINUTS))
                            .velocidadeMedia(new BigDecimal("13.169"))
                .build();

        Path pathTemp = Paths.get("teste_corrida.log");
        Files.copy(path, pathTemp, StandardCopyOption.REPLACE_EXISTING);

        TransferDto<ResultadosDto> transferResultados = service.parse(pathTemp);

        ResultadoDto dto = transferResultados.getT().getResultados().get(15);

        assertTrue("Valor do atributo \"ColunaPiloto\" recuperadp não bate com valor do teste.", tst.getColunaPiloto().trim().equals(dto.getColunaPiloto().trim()));
        assertTrue("Valor do atributo \"Horario\" recuperadp não bate com valor do teste.", tst.getHorario().equals(dto.getHorario()));
        assertTrue("Valor do atributo \"NumeroVoltas\" recuperadp não bate com valor do teste.", tst.getNumeroVoltas().equals(dto.getNumeroVoltas()));
        assertTrue("Valor do atributo \"Piloto\" recuperadp não bate com valor do teste.", tst.getPiloto().equals(dto.getPiloto()));
        assertTrue("Valor do atributo \"TempoVolta\" recuperadp não bate com valor do teste.", tst.getTempoVolta().equals(dto.getTempoVolta()));
        assertTrue("Valor do \"VelocidadeMedia\" atributo recuperadp não bate com valor do teste.", tst.getVelocidadeMedia().equals(dto.getVelocidadeMedia()));


    }


}