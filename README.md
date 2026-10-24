# Projeto Antecipe

**Grupo: HJK**

**Integrantes:**
- Jorge da Silva Leal Junior
- Kauã Victor Faustino da Costa
- Heitor Medeiros Costa Ozório

---

## Sobre o projeto

O **Antecipe** é uma API REST desenvolvida em Java com Spring Boot para importar arquivos CSV de recebíveis, validar informações cadastrais e financeiras, calcular a concentração por sacado e estimar o valor de antecipação dos títulos aprovados.

## Funcionalidades

- Importação de recebíveis por arquivo CSV.
- Validação dos dados obrigatórios.
- Verificação do tipo de recebível permitido.
- Validação do valor nominal e da data de vencimento.
- Verificação da situação cadastral do sacado.
- Cálculo da concentração financeira por raiz do CNPJ.
- Aprovação ou rejeição de recebíveis com indicação dos motivos.
- Cálculo do valor estimado de antecipação.
- Configuração de regras por meio do arquivo `validadores.json`.

## Tecnologias utilizadas

- Java
- Spring Boot
- Maven
- API REST
- CSV
- JSON

## Pré-requisitos

Antes de executar o projeto, instale:

- **JDK:** compatível com a versão do Java configurada no projeto.
- **Maven:** caso o projeto não utilize o Maven Wrapper.
- **PowerShell:** para executar os comandos no Windows.

