package br.com.antecipe.antecipe;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recebiveis")
public class RelatorioController {

    private final RecebivelCsvConverter csvConverter;
    private final RelatorioService relatorioService;

    public RelatorioController(
            RecebivelCsvConverter csvConverter,
            RelatorioService relatorioService) {
        this.csvConverter = csvConverter;
        this.relatorioService = relatorioService;
    }

    @PostMapping(
            value = "/analisar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analisar(
            @RequestPart("arquivo") MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Envie um arquivo CSV."));
        }

        try {
            List<Recebivel> recebiveis =
                    csvConverter.converter(arquivo);

            return ResponseEntity.ok(
                    relatorioService.analisar(recebiveis));

        } catch (Exception e) {
            return ResponseEntity.status(
                    HttpStatus.BAD_REQUEST).body(
                    Map.of("erro",
                            "Não foi possível processar o CSV.",
                            "detalhe",
                            e.getMessage() == null
                                    ? "Erro desconhecido."
                                    : e.getMessage()));
        }
    }
}