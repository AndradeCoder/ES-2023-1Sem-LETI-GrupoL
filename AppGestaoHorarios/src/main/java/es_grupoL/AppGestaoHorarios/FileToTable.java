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

/**
 * The {@code FileToTable} class is responsible for reading data from a CSV file and
 * generating an HTML representation of the data for visualization
 * 
 * @version 1.0
 */
public class FileToTable {

	private boolean columnsMapped = false;
	private File file;
	private List<String> mappedHeader = new ArrayList<>();

	/**
	 * Constructs a {@code FileToTable} object for processing a given CSV file.
	 *
	 * @param file The CSV file to be processed.
	 */
	public FileToTable(File file) {
		this.file = file;
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

	/**
	 * Creates an HTML representation of the data for visualization using
	 * JavaScript.
	 *
	 * @param data A {@code Map} containing data rows to be converted into an HTML table.
	 */
	public void createHTML(Map<Integer, ArrayList<String>> data) {
		System.out.println("VER ISTO "+mappedHeader);
		StringBuilder jsCode = new StringBuilder();
		jsCode.append("<script type=\"text/javascript\">\n\n");
		jsCode.append("var tabledata = [");

		// Iterar pelos dados do mapa e preencher o array JavaScript
		for (Map.Entry<Integer, ArrayList<String>> entry : data.entrySet()) {
			List<String> rowData = entry.getValue();
			int column = 0;

			jsCode.append("{");  
			for (String fieldName : this.mappedHeader) {
				if (column == rowData.size() - 1)
					jsCode.append(ColunasHorario.getConstant(fieldName) + ": \"" + rowData.get(column) + "\"},\n"); // Ultima coluna
				else {
					jsCode.append(ColunasHorario.getConstant(fieldName) + ": \"" + rowData.get(column) + "\","); // Resto das colunas
					column++;
				}
			}
		}

		// Remover a vírgula final se houver dados
		if (!data.isEmpty()) {
			jsCode.deleteCharAt(jsCode.length() - 1);
		}

		jsCode.append("];\n\n");
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

		for (String column : this.mappedHeader)
			jsCode.append(
					"{ title: \"" + column + "\", field: \"" + ColunasHorario.getConstant(column) + "\", headerFilter: \"input\" },\n");

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
			writer.write("\n<body>\n\r" + "<H1>Horário</H1>	\r\n" + "<div>\r\n"
					+ "    <select id=\"select-campo\">\r\n"
					+ "    	<option value=\"InscritosNoTurno\">Inscritos no turno</option>\r\n"
					+ "    	<option value=\"HoraInicioDaAula\">Hora inicio da aula</option>\r\n"
					+ "    	<option value=\"HoraFimDaAula\">Hora fim da aula</option>\r\n"
					+ "    	<option value=\"SalaAtribuídaÀAula\">Sala atribuida</option>\r\n"
					+ "    	<option value=\"UnidadeCurricular\">Unidade curricular</option>\r\n"
					+ "    	<option value=\"Curso\">Curso</option>\r\n"
					+ "    	<option value=\"Turno\">Turno</option>\r\n"
					+ "    	<option value=\"Turma\">Turma</option>\r\n"
					+ "    	<option value=\"DiaDaSemana\">Dia da semana</option>\r\n"
					+ "    	<option value=\"DataDaAula\">Data da aula</option>\r\n"
					+ "    	<option value=\"InscritosNoTurno\">Inscritos no turno</option>\r\n"
					+ "    	<option value=\"CaracteristicasDaSalaPedidaParaAAula\">Características da sala pedida para a aula</option>\r\n"
					+ "		<option value=\"Edificio\">Edifício</option>\r\n"
			    	+ "		<option value=\"CapacidadeNormal\">Capacidade normal</option>\r\n"
			    	+ "		<option value=\"CapacidadeExame\">Capacidade exame</option>\r\n"
			    	+ "		<option value=\"NumCaracteristicas\">Nº características</option>\r\n"
			    	+ "		<option value=\"AnfiteatroAulas\">Anfiteatro aulas</option>\r\n"
			    	+ "		<option value=\"ApoioTecnicoEventos\">Apoio técnico eventos</option>\r\n"
			    	+ "		<option value=\"Arq1\">Arq 1</option>\r\n"
			    	+ "		<option value=\"Arq2\">Arq 2</option>\r\n"
			    	+ "		<option value=\"Arq3\">Arq 3</option>\r\n"
			    	+ "		<option value=\"Arq4\">Arq 4</option>\r\n"
			    	+ "		<option value=\"Arq5\">Arq 5</option>\r\n"
			    	+ "		<option value=\"Arq6\">Arq 6</option>\r\n"
			    	+ "		<option value=\"Arq9\">Arq 9</option>\r\n"
			    	+ "		<option value=\"BYOD\">Bring Your Own Device</option>\r\n"
			    	+ "		<option value=\"FocusGroup\">Focus Group</option>\r\n"
			    	+ "		<option value=\"HorarioSalaVisivelPortalPublico\">Horário sala visível portal público</option>\r\n"
			    	+ "		<option value=\"LaboratorioArquiteturaComputadoresI\">Laboratório de Arquitectura de Computadores I</option>\r\n"
			    	+ "		<option value=\"LaboratorioArquiteturaComputadoresII\">Laboratório de Arquitectura de Computadores II</option>\r\n"
			    	+ "		<option value=\"LaboratorioBasesEngenharia\">Laboratório de Bases de Engenharia</option>\r\n"
			    	+ "		<option value=\"LaboratorioEletronica\">Laboratório de Electrónica</option>\r\n"
			    	+ "		<option value=\"LaboratorioInformatica\">Laboratório de Informática</option>\r\n"
			    	+ "		<option value=\"LaboratorioJornalismo\">Laboratório de Jornalismo</option>\r\n"
			    	+ "		<option value=\"LaboratorioRedesComputadoresI\">Laboratório de Redes de Computadores I</option>\r\n"
			    	+ "		<option value=\"LaboratorioRedesComputadoresII\">Laboratório de Redes de Computadores II</option>\r\n"
			    	+ "		<option value=\"LaboratorioTelecomunicacoes\">Laboratório de Telecomunicações</option>\r\n"
			    	+ "		<option value=\"SalaAulasMestrado\">Sala Aulas Mestrado</option>\r\n"
			    	+ "		<option value=\"SalaAulasMestradoPlus\">Sala Aulas Mestrado Plus</option>\r\n"
			    	+ "		<option value=\"SalaNEE\">Sala NEE</option>\r\n"
			    	+ "		<option value=\"SalaProvas\"> Sala Provas</option>\r\n"
			    	+ "		<option value=\"SalaReuniao\">Sala Reunião</option>\r\n"
			    	+ "		<option value=\"SalaArquitetura\">Sala de Arquitetura</option>\r\n"
			    	+ "		<option value=\"SalaAulasNormal\">Sala de Aulas Normal</option>\r\n"
			    	+ "		<option value=\"Videoconferencia\">videoconferência</option>\r\n"
			    	+ "		<option value=\"Atrio\">Átrio</option>\r\n"
					+ "  	</select>\r\n"
					+ "\r\n"
					+ "  	<select id=\"select-operador\">\r\n"
					+ "		<option value=\"&\">&</option>\r\n"
					+ "		<option value=\"+\">+</option>\r\n"
					+ "		<option value=\"-\">-</option>\r\n"
					+ "    	<option value=\"=\">=</option>\r\n"
					+ "    	<option value=\"<\"><</option>\r\n"
					+ "    	<option value=\"<=\"><=</option>\r\n"
					+ "    	<option value=\">\">></option>\r\n"
					+ "    	<option value=\">=\">>=</option>\r\n"
					+ "    	<option value=\"!=\">!=</option>\r\n"
					+ "    	<option value=\"like\">like</option>\r\n"
					+ "  	</select>\r\n"
					+ "\r\n"
					+ "  	<input id=\"input-valor\" type=\"text\" placeholder=\"valor\">\r\n"
					+ "  		\r\n"
					+ "  	<button id=\"button-confirmar\">Confirmar</button>\r\n"
					+ "  		\r\n"
					+ "  	<button id=\"button-resultado\">Obter resultado</button>\r\n"
					+ "\r\n"
					+ "  	<button id=\"button-clear\">Clear Filter</button>\r\n"
					+ "	</div>\r\n"
					+ "\r\n"
					+ "	<div id=\"resultado-obtido\">\r\n"
					+ "  		<h2>Resultado</h2>\r\n"
					+ "  		<div id=\"filter-result\">\r\n"
					+ "    		<!-- Filter results will be displayed here -->\r\n"
					+ "  		</div>\r\n"
					+ "	</div>" + "		<div id=\"example-table\"></div>\n\n");
			writer.write(jsCode.toString());
			writer.write("</body>\n</html>");
		} catch (IOException e) {
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
		for (String s : map.get(1))
			System.out.println(s);
		System.out.println(ftt.getMappedHeader());
		System.out.println("AutoMapped: " + ftt.isColumnsMapped());
		System.out.println("AutoMapped: " + b.isColumnsMapped());
		System.out.println("AutoMapped: " + a.isColumnsMapped());
	}
}
