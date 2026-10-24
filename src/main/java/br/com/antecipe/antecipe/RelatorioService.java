
package br.com.antecipe.antecipe;

import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    private final ValidacaoService validacaoService;
    private final PrecificacaoService precificacaoService;
    private final ConfiguracaoService configuracaoService;

    public RelatorioService(
            ValidacaoService validacaoService,
            PrecificacaoService precificacaoService,
            ConfiguracaoService configuracaoService) {

        this.validacaoService = validacaoService;
        this.precificacaoService = precificacaoService;
        this.configuracaoService = configuracaoService;
    }

    private BigDecimal obterLimiteConcentracao() {
        for (JsonNode validador :
                configuracaoService.getValidadoresAtivos()) {

            if ("CONCENTRACAO_MAXIMA_SACADO".equals(
                    validador.path("codigo").asText())) {

                return BigDecimal.valueOf(
                        validador.path("parametros")
                                .path("percentual_maximo")
                                .asDouble(10.0));
            }
        }

        return new BigDecimal("100.0");
    }

    public Map<String, Object> analisar(
            List<Recebivel> recebiveis) {

        BigDecimal total = recebiveis.stream()
                .map(Recebivel::getValorNominal)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> totalPorSacado = new HashMap<>();

        for (Recebivel recebivel : recebiveis) {
            if (recebivel.getDocumentoSacado() == null
                    || recebivel.getValorNominal() == null) {
                continue;
            }

            String documento = recebivel.getDocumentoSacado()
                    .replaceAll("\\D", "");

            String chave = documento.length() >= 8
                    ? documento.substring(0, 8)
                    : documento;

            totalPorSacado.merge(
                    chave,
                    recebivel.getValorNominal(),
                    BigDecimal::add);
        }

        BigDecimal limite = obterLimiteConcentracao();
        List<Map<String, Object>> itens = new ArrayList<>();

        int aprovados = 0;
        int rejeitados = 0;

        for (Recebivel recebivel : recebiveis) {
            BigDecimal valor = recebivel.getValorNominal() == null
                    ? BigDecimal.ZERO
                    : recebivel.getValorNominal();

            String documento = recebivel.getDocumentoSacado() == null
                    ? ""
                    : recebivel.getDocumentoSacado()
                    .replaceAll("\\D", "");

            String chave = documento.length() >= 8
                    ? documento.substring(0, 8)
                    : documento;

            BigDecimal totalSacado = totalPorSacado.getOrDefault(
                    chave, BigDecimal.ZERO);

            BigDecimal percentual = total.compareTo(BigDecimal.ZERO) > 0
                    ? totalSacado.multiply(new BigDecimal("100"))
                    .divide(total, 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            List<String> motivos = new ArrayList<>(
                    validacaoService.validar(recebivel));

            if (percentual.compareTo(limite) > 0) {
                motivos.add(
                        "Concentração do sacado acima do limite permitido.");
            }

            boolean aprovado = motivos.isEmpty();

            if (aprovado) {
                aprovados++;
            } else {
                rejeitados++;
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("numeroNota", recebivel.getNumeroNota());
            item.put("documentoSacado",
                    recebivel.getDocumentoSacado());
            item.put("situacaoCadastral",
                    recebivel.getSituacaoCadastral());
            item.put("valorNominal", valor);
            item.put("concentracaoSacadoPercentual", percentual);
            item.put("aprovado", aprovado);
            item.put("motivos", motivos);
            item.put("valorAntecipado", aprovado
                    ? precificacaoService
                    .calcularValorAntecipado(recebivel)
                    : null);

            itens.add(item);
        }

        Map<String, Object> relatorio = new LinkedHashMap<>();
        relatorio.put("totalRecebiveis", recebiveis.size());
        relatorio.put("totalCarteira", total);
        relatorio.put("aprovados", aprovados);
        relatorio.put("rejeitados", rejeitados);
        relatorio.put("limiteConcentracaoPercentual", limite);
        relatorio.put("recebiveis", itens);

        return relatorio;
    }
}
