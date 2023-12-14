package es_grupoL.AppGestaoHorarios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Configuration class for the application.
 */
public class ConfigApp {
	private static final String arquivoConfiguracao = "config.txt";
	private String formatoData;
	private String formatoHora;
	private List<String> camposMapeamento = new ArrayList<>();

	/**
	 * Constructor to initialize the configuration by loading it from the file.
	 */
	public ConfigApp() {
		carregarConfiguracao();
	}

	/**
	 * Gets the configuration file name.
	 *
	 * @return The configuration file name.
	 */
	public String getFicheiroConf() {
		return arquivoConfiguracao;
	}

	/**
	 * Loads the configuration from the file.
	 */
	public void carregarConfiguracao() {
		try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoConfiguracao))) {

			formatoData = leitor.readLine();
			formatoHora = leitor.readLine();
			String camposMapeamentoLine = leitor.readLine();
			if (camposMapeamentoLine != null) {
				camposMapeamento = Arrays.asList(camposMapeamentoLine.split(";"));
			}

		} catch (IOException e) {
			System.err.println("Erro ao carregar o arquivo de configuração. Usando configurações padrão.");
			formatoData = "%Y-%m-%d";
			formatoHora = "%H:%M:%S";
		}
	}

	/**
	 * Saves the configuration to the file.
	 */
	public void salvarConfiguracao() {
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoConfiguracao))) {
			// Writes the date and time format to the file

			if (formatoData == null) {
				formatoData = "%Y-%m-%d";
			}
			if (formatoHora == null) {
				formatoHora = "%H:%M:%S";
			}
			escritor.write(formatoData);
			escritor.write(formatoHora);
			escritor.newLine();
			if (camposMapeamento != null) {
				for (String campo : camposMapeamento) {
					escritor.write(campo);
					escritor.write(";");
				}
			}

		} catch (IOException e) {
			System.err.println("Erro ao salvar o arquivo de configuração.");
		}
	}

	/**
	 * Gets the date format.
	 *
	 * @return The date format.
	 */
	public String getFormatoData() {
		return formatoData;
	}

	/**
	 * Gets the mapping fields.
	 *
	 * @return The list of mapping fields.
	 */
	public List<String> getCamposMapeamento() {
		return camposMapeamento;
	}

	/**
	 * Gets the time format.
	 *
	 * @return The time format.
	 */
	public String getFormatoHora() {
		return formatoHora;
	}

	/**
	 * Sets the date format.
	 *
	 * @param formatoData The new date format.
	 */
	public void setFormatoData(String formatoData) {
		this.formatoData = formatoData;
	}

	/**
	 * Sets the time format.
	 *
	 * @param formatoHora The new time format.
	 */
	public void setFormatoHora(String formatoHora) {
		this.formatoHora = formatoHora;
	}

	/**
	 * Sets the mapping fields.
	 *
	 * @param camposMapeamento The new list of mapping fields.
	 */
	public void setCamposMapeamento(List<String> camposMapeamento) {
		this.camposMapeamento = camposMapeamento;
	}

	/**
	 * The main method for testing the configuration options.
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		ConfigApp configuracaoApp = new ConfigApp();

		System.out.println("Formato de Data: " + configuracaoApp.getFormatoData());
		System.out.println("Formato de Hora: " + configuracaoApp.getFormatoHora());
		System.out.println("Campos de Mapeamento: " + configuracaoApp.getCamposMapeamento());

	}
}
