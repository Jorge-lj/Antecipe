package br.com.antecipe.antecipe;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PrecificacaoService {

    // Exemplo provisório: 0,05% ao dia.
    private static final BigDecimal TAXA_DIARIA =
            new BigDecimal("0.0005");

    public BigDecimal calcularValorAntecipado(
            Recebivel recebivel) {

        if (recebivel.getValorNominal() == null
                || recebivel.getDataVencimento() == null) {
            return null;
        }

        long dias = ChronoUnit.DAYS.between(
                LocalDate.now(),
                recebivel.getDataVencimento());

        if (dias < 0) {
            return null;
        }

        BigDecimal taxaTotal = TAXA_DIARIA.multiply(
                BigDecimal.valueOf(dias));

        BigDecimal fator = BigDecimal.ONE.subtract(taxaTotal);

        if (fator.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2,
                    RoundingMode.HALF_UP);
        }

        return recebivel.getValorNominal()
                .multiply(fator)
                .setScale(2, RoundingMode.HALF_UP);
    }
}