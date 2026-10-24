
package br.com.antecipe.antecipe;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ValidacaoService {

    private final Set<String> tiposPermitidos = Set.of(
            "DUPLICATA",
            "DUPLICATA DE SERVIÇO",
            "CCB",
            "CHEQUE",
            "NOTA COMERCIAL"
    );

    public List<String> validar(Recebivel recebivel) {
        List<String> motivos = new ArrayList<>();

        if (recebivel.getDocumentoSacado() == null
                || recebivel.getDocumentoSacado().isBlank()) {
            motivos.add("Documento do sacado ausente.");
        }

        if (recebivel.getValorNominal() == null
                || recebivel.getValorNominal()
                .compareTo(BigDecimal.ZERO) <= 0) {
            motivos.add("Valor nominal inválido.");
        }

        String tipo = recebivel.getTipoRecebivel() == null
                ? ""
                : recebivel.getTipoRecebivel()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!tiposPermitidos.contains(tipo)) {
            motivos.add("Tipo de recebível não permitido.");
        }

        if (recebivel.getDataVencimento() == null) {
            motivos.add("Data de vencimento ausente.");
        } else {
            long dias = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    recebivel.getDataVencimento()
            );

            if (dias < 1 || dias > 180) {
                motivos.add(
                        "Prazo do recebível fora do limite permitido."
                );
            }
        }

        if (!"VERIFICADA".equalsIgnoreCase(
                recebivel.getSituacaoCadastral() == null
                        ? ""
                        : recebivel.getSituacaoCadastral().trim())) {
            motivos.add(
                    "Situação cadastral do sacado não verificada."
            );
        }

        return motivos;
    }
}
