package br.com.antecipe.antecipe;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Recebivel {

    private String numeroNota;
    private String documentoSacado;
    private String tipoRecebivel;
    private String situacaoCadastral;
    private BigDecimal valorNominal;
    private LocalDate dataVencimento;

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getDocumentoSacado() {
        return documentoSacado;
    }

    public void setDocumentoSacado(String documentoSacado) {
        this.documentoSacado = documentoSacado;
    }

    public String getTipoRecebivel() {
        return tipoRecebivel;
    }

    public void setTipoRecebivel(String tipoRecebivel) {
        this.tipoRecebivel = tipoRecebivel;
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(String situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public BigDecimal getValorNominal() {
        return valorNominal;
    }

    public void setValorNominal(BigDecimal valorNominal) {
        this.valorNominal = valorNominal;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
}