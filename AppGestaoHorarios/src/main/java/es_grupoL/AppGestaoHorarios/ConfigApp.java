package es_grupoL.AppGestaoHorarios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigApp {
    private static final String arquivoConfiguracao = "config.txt";
    private String formatoData;
    private String formatoHora;

    public ConfigApp() {
        carregarConfiguracao();
    }

    private void carregarConfiguracao() {
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoConfiguracao))) {
            // Lê a primeira linha do arquivo como formatoDataHora
            formatoData = leitor.readLine();
            formatoHora = leitor.readLine();
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de configuração. Usando configurações padrão.");
            formatoData = "%Y-%m-%d";
            formatoHora = "%H:%M:%S";
        }
    }

    public void salvarConfiguracao() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoConfiguracao))) {
            // Escreve o formatoDataHora no arquivo
        	if(formatoData == null) {
        		formatoData = "%Y-%m-%d";
        	}
        	if(formatoHora == null) {
        		formatoHora = "%H:%M:%S";
        	}
            escritor.write(formatoData);
            escritor.write(formatoHora);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo de configuração.");
        }
    }

    // Obtém o formato de data
    public String getFormatoData() {
        return formatoData;
    }
    
    // Obtém o formato de horário
    public String getFormatoHora() {
        return formatoHora;
    }
    
    // Define o formato de data
    public void setFormatoData(String formatoData) {
        this.formatoData = formatoData;
    }
    
    // Define o formato de horário
    public void setFormatoHora(String formatoHora) {
        this.formatoHora = formatoHora;
    }

    public static void main(String[] args) {
        ConfigApp configuracaoApp = new ConfigApp();
        System.out.println("Formato de Data: " + configuracaoApp.getFormatoData());
        System.out.println("Formato de Hora: " + configuracaoApp.getFormatoHora());
    }
}