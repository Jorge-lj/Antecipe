
package br.com.antecipe.antecipe;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class RecebivelCsvConverter {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Recebivel> converter(MultipartFile arquivo)
            throws Exception {

        List<Recebivel> recebiveis = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        arquivo.getInputStream(),
                        StandardCharsets.UTF_8))) {

            String linhaCabecalho = reader.readLine();

            if (linhaCabecalho == null) {
                throw new IllegalArgumentException("CSV vazio.");
            }

            String[] cabecalhos = linhaCabecalho
                    .replace("\uFEFF", "")
                    .split(";", -1);

            Map<String, Integer> indices = new HashMap<>();

            for (int i = 0; i < cabecalhos.length; i++) {
                indices.put(normalizar(cabecalhos[i]), i);
            }

            String linha;
            int numeroLinha = 1;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;

                if (linha.isBlank()) {
                    continue;
                }

                String[] valores = linha.split(";", -1);

                Recebivel recebivel = new Recebivel();

                recebivel.setNumeroNota(obter(
                        indices, valores,
                        "NU_DOCUMENTO",
                        "numeroNota",
                        "numero_nota",
                        "numero nota"
                ));

                recebivel.setDocumentoSacado(obter(
                        indices, valores,
                        "DOC_SACADO",
                        "documentoSacado",
                        "documento_sacado",
                        "documento sacado"
                ));

                String tipo = obter(
                        indices, valores,
                        "TIPO_RECEBIVEL",
                        "tipoRecebivel",
                        "tipo_recebivel",
                        "tipo recebivel"
                );

                recebivel.setTipoRecebivel(
                        tipo.trim().toUpperCase(Locale.ROOT)
                );

                recebivel.setSituacaoCadastral(obter(
                        indices, valores,
                        "SITUACAO_CADASTRAL",
                        "situacaoCadastral",
                        "situacao_cadastral",
                        "situacao cadastral"
                ));

                String valor = obter(
                        indices, valores,
                        "VALOR_NOMINAL",
                        "valorNominal",
                        "valor_nominal",
                        "valor nominal"
                );

                try {
                    recebivel.setValorNominal(
                            converterValor(valor)
                    );
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                            "Valor nominal inválido na linha "
                                    + numeroLinha + ": " + valor,
                            e
                    );
                }

                String data = obter(
                        indices, valores,
                        "DATA_VENCIMENTO_ORIGINAL",
                        "DATA_VENCIMENTO",
                        "dataVencimento",
                        "data_vencimento",
                        "data vencimento"
                );

                if (!data.isBlank()) {
                    try {
                        recebivel.setDataVencimento(
                                LocalDate.parse(data, FORMATO_DATA)
                        );
                    } catch (RuntimeException e) {
                        throw new IllegalArgumentException(
                                "Data de vencimento inválida na linha "
                                        + numeroLinha + ": " + data,
                                e
                        );
                    }
                }

                recebiveis.add(recebivel);
            }
        }

        return recebiveis;
    }

    private String obter(
            Map<String, Integer> indices,
            String[] valores,
            String... nomes) {

        for (String nome : nomes) {
            Integer indice = indices.get(normalizar(nome));

            if (indice != null && indice < valores.length) {
                return valores[indice].trim();
            }
        }

        return "";
    }

    private BigDecimal converterValor(String valor) {
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }

        String limpo = valor.trim();

        // Formato brasileiro: 2.278,08
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        }

        return new BigDecimal(limpo);
    }

    private String normalizar(String texto) {
        return texto
                .replace("\uFEFF", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
