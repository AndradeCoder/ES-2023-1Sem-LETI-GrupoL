package es_grupoL.AppGestaoHorarios;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigApp {
	private static final String arquivo_configuracao = "config.properties";
    private Properties propriedades;
    
    public ConfigApp() {
        propriedades = new Properties();
        carregarConfiguracao();
    }
    
    private void carregarConfiguracao() {
        try (FileInputStream ac = new FileInputStream(arquivo_configuracao)) {
            propriedades.load(ac);
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de configuração. Usando configurações padrão.");
        }
    }
    
    public void salvarConfiguracao() {
        try (FileOutputStream ac = new FileOutputStream(arquivo_configuracao)) {
            propriedades.store(ac, null);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo de configuração.");
        }
    }

    public String getFormatoDataHora() {
        return propriedades.getProperty("formatoDataHora", "%Y-%m-%d %H:%M:%S");
    }

    public void setFormatoDataHora(String formatoDataHora) {
        propriedades.setProperty("formatoDataHora", formatoDataHora);
    }

    public static void main(String[] args) {
        ConfigApp configuracaoApp = new ConfigApp();
        System.out.println("Formato de Data/Hora: " + configuracaoApp.getFormatoDataHora());
    }
}
