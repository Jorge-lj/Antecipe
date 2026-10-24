
package br.com.antecipe.antecipe;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfiguracaoService {

    private final List<JsonNode> validadoresAtivos = new ArrayList<>();

    public ConfiguracaoService() {
        carregarValidadores();
    }

    private void carregarValidadores() {
        try {
            JsonMapper mapper = JsonMapper.builder().build();

            ClassPathResource recurso =
                    new ClassPathResource("validadores.json");

            try (InputStream input = recurso.getInputStream()) {
                JsonNode raiz = mapper.readTree(input);

                JsonNode validadores;

                if (raiz.isArray()) {
                    validadores = raiz;
                } else {
                    validadores = raiz.path("validadores");
                }

                if (validadores.isArray()) {
                    for (JsonNode validador : validadores) {
                        if (validador.path("ativo").asBoolean(false)) {
                            validadoresAtivos.add(validador);
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao carregar o arquivo validadores.json", e);
        }
    }

    public List<JsonNode> getValidadoresAtivos() {
        return List.copyOf(validadoresAtivos);
    }
}
