package es_grupoL.AppGestaoHorarios;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class FileToTable {

	private boolean autoMapped = false;
	private File file;
	private List<String> header = new ArrayList<String>();

	public FileToTable(File file) {
		this.file = file;
	}

	/*
	 * Devolve o ficheiro no formato de um HashMap, que contem uma chave para o
	 * numero de linha e uma lista com as colunas da linha como valor
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
		// System.out.println(file);
		return fileLineInfo;
	}

	public static void createHTML(Map<Integer, ArrayList<String>> data) {
		StringBuilder jsCode = new StringBuilder();
		jsCode.append("<script type=\"text/javascript\">\n\n");
		jsCode.append("var tabledata = [");

		// Iterar pelos dados do mapa e preencher o array JavaScript
		for (Map.Entry<Integer, ArrayList<String>> entry : data.entrySet()) {
			int key = entry.getKey();
			List<String> rowData = entry.getValue();
			List<ColunasHorario> rowDataColumnName = ColunasHorario.constantsList();
			int column = 0;

			jsCode.append("{");
			for (ColunasHorario ch : rowDataColumnName) {
				if (column == rowData.size() - 1)
					jsCode.append(ch + ":\"" + rowData.get(column) + "\"},\n"); // Ultima coluna
				else {
					jsCode.append(ch + ":\"" + rowData.get(column) + "\","); // Resto das colunas
					column++;
				}
			}
		}

		// Remover a vírgula final se houver dados
		if (!data.isEmpty()) {
			jsCode.deleteCharAt(jsCode.length() - 1);
		}

		jsCode.append("];\n");
		jsCode.append("var table = new Tabulator(\"#example-table\", {\n");
		jsCode.append("data:tabledata,\n");
		jsCode.append("layout:\"fitDatafill\",\n");
		jsCode.append("pagination:\"local\",\n");
		jsCode.append("paginationSize:10,\n");
		jsCode.append("paginationSizeSelector:[5, 10, 20, 40],\n");
		jsCode.append("movableColumns:true,\n");
		jsCode.append("paginationCounter:\"rows\",\n");
		jsCode.append("initialSort: [{column:\"building\",dir:\"asc\"},],\n");
		jsCode.append("columns: [\n");

		List<ColunasHorario> colunasHorario = ColunasHorario.constantsList();
		for (ColunasHorario ch : colunasHorario)
			jsCode.append(
					"{ title: \"" + ch.getColumnName() + "\", field: \"" + ch + "\", headerFilter: \"input\" },\n");

		jsCode.append("]\n");
		jsCode.append("});\n");
		jsCode.append("</script>");

		// Escrever o código JavaScript gerado para um ficheiro HTML
		try (FileWriter writer = new FileWriter("ScheduleApp.html")) {
			writer.write("<!DOCTYPE html>\n<html lang=\"en\">\n");
			writer.write("<head>\r\n" + "<meta charset=\"utf-8\" />\r\n"
					+ "		<link href=\"https://unpkg.com/tabulator-tables@4.8.4/dist/css/tabulator.min.css\" rel=\"stylesheet\">\r\n"
					+ "		<script type=\"text/javascript\" src=\"https://unpkg.com/tabulator-tables@4.8.4/dist/js/tabulator.min.js\"></script>\r\n"
					+ "	</head>");
			writer.write("\n<body>\n\r" + "<H1>Horário</H1>	\r\n" + "		<div id=\"example-table\"></div>\n\n");
			writer.write(jsCode.toString());
			writer.write("</body>\n</html>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean hasHeader() {
		List<String> colunasHorario = ColunasHorario.valuesList();
		try (CSVParser parser = CSVParser.parse(new FileReader(file), CSVFormat.EXCEL.withDelimiter(';'))) {
			for (CSVRecord record : parser)
				if (record.toList().containsAll(colunasHorario)) {
					this.setAutoMapped(true);
					this.setHeader(record.toList());
					return true;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isAutoMapped() {
		return autoMapped;
	}

	public void setAutoMapped(boolean autoMapped) {
		this.autoMapped = autoMapped;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public static void main(String[] args) {
		File file = new File("C:\\Users\\leoth\\Downloads\\HorarioDeExemplo.csv");
		FileToTable ftt = new FileToTable(file);
		FileToTable a = new FileToTable(file);
		FileToTable b = new FileToTable(file);
		ftt.readCSV(); // Teste temporário e este caminho só funciona no meu pc como é óbvio
		Map<Integer, ArrayList<String>> map = a.readCSV(); // Teste temporário e este caminho só funciona no meu pc como é óbvio
		
		createHTML(map);
		for (String s : map.get(1))
			System.out.println(s);
		System.out.println(ftt.getHeader());
		System.out.println("AutoMapped: " + ftt.isAutoMapped());
		System.out.println("AutoMapped: " + b.isAutoMapped());
		System.out.println("AutoMapped: " + a.isAutoMapped());
	}
}
