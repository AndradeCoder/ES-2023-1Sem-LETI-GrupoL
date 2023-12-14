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
    private String formatoData;
    private String formatoHora;
    private List<String> camposMapeamento = new ArrayList<>();


    public ConfigApp() {
        carregarConfiguracao();
    }
    
    public String getFicheiroConf() {
    	return arquivoConfiguracao;
    }

    public void carregarConfiguracao() {
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoConfiguracao))) {
            
            formatoData = leitor.readLine();
            formatoHora = leitor.readLine();
            String camposMapeamentoLine = leitor.readLine();
            if(camposMapeamentoLine != null) {
            	camposMapeamento = Arrays.asList(camposMapeamentoLine.split(";"));
            }
            //Botoes.getInstance().getUserFileToTable().setMappedHeader(camposMapeamento);

            

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

    // Obtém o formato de data
    public String getFormatoData() {
        return formatoData;
    }
    

    public List<String> getCamposMapeamento() {
    	return camposMapeamento;
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
    
    public void setCamposMapeamento(List<String> camposMapeamento) {
    	this.camposMapeamento = camposMapeamento;
    }

    public static void main(String[] args) {
        ConfigApp configuracaoApp = new ConfigApp();

        System.out.println("Formato de Data: " + configuracaoApp.getFormatoData());
        System.out.println("Formato de Hora: " + configuracaoApp.getFormatoHora());
        System.out.println("Campos de Mapeamento: " + configuracaoApp.getCamposMapeamento());
        
    }
}