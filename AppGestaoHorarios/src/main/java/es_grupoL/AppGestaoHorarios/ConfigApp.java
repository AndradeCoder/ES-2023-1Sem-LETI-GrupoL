package es_grupoL.AppGestaoHorarios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ConfigApp {
    private static final String arquivoConfiguracao = "config.txt";
    private String formatoDataHora;
    private List<String> camposMapeamento = new ArrayList<>();

    public ConfigApp() {
        carregarConfiguracao();
    }
    
    public String getFicheiroConf() {
    	return arquivoConfiguracao;
    }

    public void carregarConfiguracao() {
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoConfiguracao))) {
            // Lê a primeira linha do arquivo como formatoDataHora
            formatoDataHora = leitor.readLine();
            String camposMapeamentoLine = leitor.readLine();
            if(camposMapeamentoLine != null) {
            	camposMapeamento = Arrays.asList(camposMapeamentoLine.split(";"));
            }
            //Botoes.getInstance().getUserFileToTable().setMappedHeader(camposMapeamento);
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de configuração. Usando configurações padrão.");
            formatoDataHora = "%Y-%m-%d %H:%M:%S";
        }
}

    public void salvarConfiguracao() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoConfiguracao))) {
            // Escreve o formatoDataHora no arquivo
            escritor.write(formatoDataHora);
            escritor.newLine();
            if(camposMapeamento != null) {
            	for(String campo: camposMapeamento) {
            		escritor.write(campo);
            		escritor.write(";");
            	}
            }
            System.out.println(camposMapeamento);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo de configuração.");
        }
    }

    public String getFormatoDataHora() {
        return formatoDataHora;
    }
    
    public List<String> getCamposMapeamento() {
    	return camposMapeamento;
    }

    public void setFormatoDataHora(String formatoDataHora) {
        this.formatoDataHora = formatoDataHora;
    }
    
    public void setCamposMapeamento(List<String> camposMapeamento) {
    	this.camposMapeamento = camposMapeamento;
    }

    public static void main(String[] args) {
        ConfigApp configuracaoApp = new ConfigApp();
        System.out.println("Formato de Data/Hora: " + configuracaoApp.getFormatoDataHora());
        System.out.println("Campos de Mapeamento: " + configuracaoApp.getCamposMapeamento());
    }
}