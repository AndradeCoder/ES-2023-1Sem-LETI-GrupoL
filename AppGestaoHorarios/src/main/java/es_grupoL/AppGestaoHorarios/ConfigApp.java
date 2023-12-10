package es_grupoL.AppGestaoHorarios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigApp {
    private static final String arquivoConfiguracao = "config.txt";
    private String formatoDataHora;

    public ConfigApp() {
        carregarConfiguracao();
    }

    private void carregarConfiguracao() {
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoConfiguracao))) {
            // Lê a primeira linha do arquivo como formatoDataHora
            formatoDataHora = leitor.readLine();
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de configuração. Usando configurações padrão.");
            formatoDataHora = "%Y-%m-%d %H:%M:%S";
        }
    }

    public void salvarConfiguracao() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoConfiguracao))) {
            // Escreve o formatoDataHora no arquivo
            escritor.write(formatoDataHora);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo de configuração.");
        }
    }

    public String getFormatoDataHora() {
        return formatoDataHora;
    }

    public void setFormatoDataHora(String formatoDataHora) {
        this.formatoDataHora = formatoDataHora;
    }

    public static void main(String[] args) {
        ConfigApp configuracaoApp = new ConfigApp();
        System.out.println("Formato de Data/Hora: " + configuracaoApp.getFormatoDataHora());
    }
}