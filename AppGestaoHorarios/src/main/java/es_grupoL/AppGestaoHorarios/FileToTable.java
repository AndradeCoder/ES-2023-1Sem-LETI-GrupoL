package es_grupoL.AppGestaoHorarios;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * The {@code FileToTable} class is responsible for reading data from a CSV file and
 * generating an HTML representation of the data for visualization
 * 
 * @version 1.4
 */
public class FileToTable {

	private boolean columnsMapped = false;
	private File file;
	private static List<String> mappedHeader = new ArrayList<>();
	private static String MAIN_FOLDER = "ScheduleApp/";	// Pasta onde estão localizados todos os ficheiros, incluindo os templates
	private static final VelocityEngine velocityEngine = new VelocityEngine();

	//atributo não utilizado
	//private ConfigApp configuracao_aplicacao;

	/**
	 * Constructs a {@code FileToTable} object for processing a given CSV file.
	 *
	 * @param file The CSV file to be processed.
	 * @param configuracao_aplicacao The saved configurations made in the previous application run
	 */
	public FileToTable(File file) {
		this.file = file;
		//this.configuracao_aplicacao = configuracao_aplicacao;
	}

	/**
	 * Reads the CSV file and converts it into a {@code Map} containing rows of data.
	 *
	 * @return A {@code Map} where the key is the row number and the value is a list of
	 *         column values. The lowest key number is 1, indicating the first line of the file.
	 */
	public Map<Integer, ArrayList<String>> readCSV() {
		Map<Integer, ArrayList<String>> fileLineInfo = new HashMap<>();

		CSVFormat format = CSVFormat.EXCEL.withDelimiter(';');
		if (hasHeader())
			format = format.withFirstRecordAsHeader();

		try (CSVParser parser = CSVParser.parse(new FileReader(file), format)) {
			for (CSVRecord record : parser) {
				List<String> columns = record.toList();
				fileLineInfo.put((int) record.getRecordNumber(), (ArrayList<String>) columns);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return fileLineInfo;
	}

	//métodos formatarData e formatarHora inacabados, não foi possível alterar os valores de data e horario na tabela no website
	private String formatarData(String valor) {
		String formatoData = Botoes.getInstance().getConfiguracao_aplicacao().getFormatoData();

		return String.format(formatoData, valor);
	}

	private String formatarHora(String valor) {
		String formatoHora = Botoes.getInstance().getConfiguracao_aplicacao().getFormatoHora();

		return String.format(formatoHora, valor);
	}

	/**
     * Reads the CSV file and converts it into a {@code Map} containing rows of data.
     *
     * @return A {@code Map} where the key is the row number and the value is a list of
     *         column values. The lowest key number is 1, indicating the first line of the file.
     */
	public void createHTML(Map<Integer, ArrayList<String>> data) {
		try {
			// Conteúdos de ScheduleApp.html
			velocityEngine.init();
			VelocityContext contextForScheduleApp = createContextForScheduleApp();
			generateTemplate(MAIN_FOLDER + "Templates/ScheduleApp-template.html", contextForScheduleApp, MAIN_FOLDER + "ScheduleApp.html");

			// Conteúdos de data.js, inclui os ficheiros do horário e salas
			createContextForDataJavaScript(data, MAIN_FOLDER + "data.js");

			// Conteúdos de main.js
			createContextForMainJavaScript(MAIN_FOLDER+"Templates/main-template.js", MAIN_FOLDER+"main.js");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
     * Creates a VelocityContext for ScheduleApp.
     *
     * @return The VelocityContext containing various options for ScheduleApp.
     */
	private static VelocityContext createContextForScheduleApp() {
		VelocityContext context = new VelocityContext();
		context.put("metricasPredefinidasOptions", ScheduleMetrics.metricOptions());
		context.put("selectCampoOptions", ScheduleMetrics.columnOptions());
		context.put("selectOperadorOptions", ScheduleMetrics.operatorOptions());
		context.put("datalistOptions", ScheduleMetrics.datalistOptions());
		return context;
	}

	/**
     * Creates JavaScript data from the given CSV data and writes it to the specified output path.
     *
     * @param data The CSV data to be converted to JavaScript.
     * @param outputPath The path where the JavaScript data will be written.
     */
	private static void createContextForDataJavaScript(Map<Integer, ArrayList<String>> data, String outputPath) {
		StringBuilder csvFiles = new StringBuilder();

		csvFiles.append("\tvar tabledata = [");    // Converter conteúdos do ficheiro do horário num array JavaScript
		for (Map.Entry<Integer, ArrayList<String>> entry : data.entrySet()) {
			List<String> rowData = entry.getValue();
			int column = 0;

			csvFiles.append("\t{");
			for (String columnName : mappedHeader) {
				ColunasHorario fieldName = ColunasHorario.getConstant(columnName);
				String fieldValue = rowData.get(column);

				if (ColunasHorario.isColumnOfNumbers(fieldName)) {
					if (column == rowData.size() - 1)
						csvFiles.append(fieldName + ": " + fieldValue + "},\n"); // Ultima coluna
					else {
						csvFiles.append(fieldName + ": " + fieldValue + ","); // Resto das colunas
						column++;
					}
				} else {
					if (column == rowData.size() - 1)
						csvFiles.append(fieldName + ": \"" + fieldValue + "\"},\n"); // Ultima coluna
					else {
						csvFiles.append(fieldName + ": \"" + fieldValue + "\","); // Resto das colunas
						column++;
					}
				}
			}
		}

		// Remover caracteres que estão a mais
		if (!data.isEmpty())
			csvFiles.delete(csvFiles.length() - 2, csvFiles.length());
		csvFiles.append("];\n\n");

		csvFiles.append("\tconst classroomsData = [");    // Converter conteúdos do ficheiro das salas num array JavaScript
		Map<Integer, ArrayList<String>> classroomsFileMap = ColunasSalas.getClassroomsFileToTable().readCSV();
		for (Map.Entry<Integer, ArrayList<String>> entry : classroomsFileMap.entrySet()) {
			List<String> rowData = entry.getValue();
			int column = 0;

			csvFiles.append("\t{");
			for (String columnName : ColunasSalas.valuesList()) {
				ColunasSalas fieldName = ColunasSalas.getConstant(columnName);
				String fieldValue = rowData.get(column);

				if (ColunasSalas.isColumnOfNumbers(fieldName) && entry.getKey() != 1) {
					if (column == rowData.size() - 1)
						csvFiles.append(fieldName + ": " + fieldValue + "},\n"); // Ultima coluna
					else {
						csvFiles.append(fieldName + ": " + fieldValue + ","); // Resto das colunas
						column++;
					}
				} else {
					if (column == rowData.size() - 1)
						csvFiles.append(fieldName + ": \"" + fieldValue + "\"},\n"); // Ultima coluna
					else {
						csvFiles.append(fieldName + ": \"" + fieldValue + "\","); // Resto das colunas
						column++;
					}
				}
			}
		}
		csvFiles.append("];\n\n");

		try (PrintWriter printWriter = new PrintWriter(outputPath)) {
			printWriter.print(csvFiles.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
     * Creates JavaScript data for main.js from the specified template and writes it to the output path.
     *
     * @param template The path to the template file.
     * @param outputPath The path where the JavaScript data will be written.
     */
	private static void createContextForMainJavaScript(String template, String outputPath) {
		StringBuilder tabulatorColumns = new StringBuilder();
		try (Scanner scanner = new Scanner(new File(template))){
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				System.out.println("Read line: " + line);
				if(!line.contains("//Insert here"))
					tabulatorColumns.append(line+"\n");
				else {
					tabulatorColumns.append("\n");
					for (String column : mappedHeader)
						tabulatorColumns.append("\t\t{ title: \"" + column + "\", field: \"" + ColunasHorario.getConstant(column) + "\", headerFilter: \"input\" },\n");
				}
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (PrintWriter printWriter = new PrintWriter(outputPath)) {
			printWriter.print(tabulatorColumns.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
     * Generates HTML using a Velocity template and writes it to the specified output path.
     *
     * @param templatePath The path to the Velocity template.
     * @param context The VelocityContext containing data for the template.
     * @param outputPath The path where the generated HTML will be written.
     */
	private static void generateTemplate(String templatePath, VelocityContext context, String outputPath) {
		try {
			Template template = velocityEngine.getTemplate(templatePath);
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			String finalHtml = writer.toString();

			try (PrintWriter printWriter = new PrintWriter(outputPath)) {
				printWriter.print(finalHtml);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the CSV file from user has a header containing expected column names.
	 *
	 * @return {@code true} if a matching header is found, {@code false} otherwise.
	 */
	private boolean hasHeader() {
		List<String> colunasHorario = ColunasHorario.valuesList();
		try (CSVParser parser = CSVParser.parse(new FileReader(file), CSVFormat.EXCEL.withDelimiter(';'))) {
			for (CSVRecord record : parser)
				if (record.toList().containsAll(colunasHorario)) {
					setColumnsMapped(true);
					setMappedHeader(record.toList());
					return true;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	//	public void formatDateAndTimes(Map<Integer, ArrayList<String>> data) {
	//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	//		
	//		for(Map.Entry<Integer, ArrayList<String>> entry : data.entrySet()) {
	//			for (int columnIndex : ColunasHorario.listOfDateAndTimes()) {
	//				Date date;
	//				try {
	//					if(columnIndex == 8) {
	//						System.out.println("Data: "+);
	//						date = dateFormat.parse(entry.getValue().get(columnIndex));
	//						String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
	//		                entry.getValue().set(columnIndex, formattedDate);
	//					}
	//				} catch (ParseException e) {
	//					e.printStackTrace();
	//				}
	//			}
	//		}
	//	}

	/**
	 * Checks if the CSV columns are automatically mapped.
	 *
	 * @return {@code true} if columns are automatically mapped, {@code false} otherwise.
	 */
	public boolean isColumnsMapped() {
		return columnsMapped;
	}

	/**
	 * Sets whether the CSV columns are automatically mapped.
	 *
	 * @param autoMapped {@code true} to indicate automatic mapping, {@code false} otherwise.
	 */
	public void setColumnsMapped(boolean autoMapped) {
		this.columnsMapped = autoMapped;
	}

	/**
	 * Gets the header data extracted from the CSV file.
	 *
	 * @return A list of header column names. List will be empty if the file has no
	 *         header.
	 */
	public List<String> getMappedHeader() {
		return mappedHeader;
	}

	/**
	 * Sets the header data extracted from the CSV file.
	 *
	 * @param header A list of header column names.
	 */
	public void setMappedHeader(List<String> header) {
		this.mappedHeader = header;
	}

	/**
	 * The main method for testing {@code FileToTable} class functionality.
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		File file = new File("C:\\Users\\leoth\\Downloads\\HorarioDeExemplo.csv");
		FileToTable ftt = new FileToTable(file);
		FileToTable a = new FileToTable(file);
		FileToTable b = new FileToTable(file);
		ftt.readCSV(); // Teste temporário e este caminho só funciona no meu pc como é óbvio
		Map<Integer, ArrayList<String>> map = a.readCSV(); // Teste temporário e este caminho só funciona no meu pc como é óbvio

		ftt.createHTML(map);
		for (String s : map.get(2))
			System.out.println(s);
		System.out.println(ftt.getMappedHeader());
		System.out.println("AutoMapped: " + ftt.isColumnsMapped());
		System.out.println("AutoMapped: " + b.isColumnsMapped());
		System.out.println("AutoMapped: " + a.isColumnsMapped());
	}
}
