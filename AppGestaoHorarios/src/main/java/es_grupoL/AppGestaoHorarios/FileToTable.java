package es_grupoL.AppGestaoHorarios;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * @version 1.2
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
		//formatDateAndTimes(data);
		StringBuilder jsCode = new StringBuilder();
		jsCode.append("<script type=\"text/javascript\">\n\n");
		jsCode.append("\tvar tabledata = [");

		// Iterar pelos dados do mapa e preencher o array JavaScript
		for (Map.Entry<Integer, ArrayList<String>> entry : data.entrySet()) {
			List<String> rowData = entry.getValue();
			int column = 0;

			jsCode.append("{");  
			for (String fieldName : this.mappedHeader) {
				if(ColunasHorario.columnDataType(ColunasHorario.getConstant(fieldName))) {
					if (column == rowData.size() - 1)
						jsCode.append(ColunasHorario.getConstant(fieldName) + ": " + rowData.get(column) + "},\n"); // Ultima coluna
					else {
						jsCode.append(ColunasHorario.getConstant(fieldName) + ": " + rowData.get(column) + ","); // Resto das colunas
						column++;
					}
				} else {
					if (column == rowData.size() - 1)
						jsCode.append(ColunasHorario.getConstant(fieldName) + ": \"" + rowData.get(column) + "\"},\n"); // Ultima coluna
					else {
						jsCode.append(ColunasHorario.getConstant(fieldName) + ": \"" + rowData.get(column) + "\","); // Resto das colunas
						column++;
					}
				}
			}
		}

		// Remover a vírgula final se houver dados
		if (!data.isEmpty()) {
			jsCode.deleteCharAt(jsCode.length() - 1);
		}

		jsCode.append("];\n\n");
		jsCode.append("\tconst classroomsData = [");

		Map<Integer, ArrayList<String>> classroomsFileMap = ColunasSalas.getClassroomsFileToTable().readCSV();
		for (Map.Entry<Integer, ArrayList<String>> entry : classroomsFileMap.entrySet()) {
			List<String> rowData = entry.getValue();
			int column = 0;

			jsCode.append("{");  
			for (String fieldName : ColunasSalas.valuesList()) {
				if(ColunasSalas.columnDataType(ColunasSalas.getConstant(fieldName)) && entry.getKey() != 1) {
					if (column == rowData.size() - 1)
						jsCode.append(ColunasSalas.getConstant(fieldName) + ": " + rowData.get(column) + "},\n"); // Ultima coluna
					else {
						jsCode.append(ColunasSalas.getConstant(fieldName) + ": " + rowData.get(column) + ","); // Resto das colunas
						column++;
					}
				} else {
					if (column == rowData.size() - 1)
						jsCode.append(ColunasSalas.getConstant(fieldName) + ": \"" + rowData.get(column) + "\"},\n"); // Ultima coluna
					else {
						jsCode.append(ColunasSalas.getConstant(fieldName) + ": \"" + rowData.get(column) + "\","); // Resto das colunas
						column++;
					}
				}
			}
		}
		jsCode.append("];\n\n");
		jsCode.append("let formula = \"\";\r\n"
				+ "		let filterFunction;\r\n"
				+ "		let campo = document.getElementById(\"select-campo\");\r\n"
				+ "		let operador = document.getElementById(\"select-operador\");\r\n"
				+ "		let valor = document.getElementById(\"input-valor\");\r\n"
				+ "		let typeResult = document.getElementById(\"button-resultado\");\r\n"
				+ "		let filterResultElement = document.getElementById(\"filter-result\");\r\n"
				+ "		let capacidadeMin = document.getElementById(\"input-capacidadeMin\");\r\n"
				+ "		let capacidadeMax = document.getElementById(\"input-capacidadeMax\");\r\n"
				+ "		let carateristica = document.getElementById(\"input-caraterística\");\r\n"
				+ "		const horarioHeader = document.getElementById('horarioHeader');\r\n"
				+ "		const resultadosHeader = document.getElementById('resultadosHeader');\r\n"
				+ "		let metricasPredefinidas = document.getElementById(\"metricasPredefinidas\");\r\n"
				+ "		let userDefinedMetrics = [];\r\n"
				+ "\r\n"
				+ "		function adicionarExpressoes() {\r\n"
				+ "			const campoValue = campo.options[campo.selectedIndex].value;\r\n"
				+ "			const operadorValue = operador.options[operador.selectedIndex].value;\r\n"
				+ "			let valorValue = valor.value;\r\n"
				+ "			const selectedOption = campo.options[campo.selectedIndex];\r\n"
				+ "\r\n"
				+ "			const allowedPattern = /^[a-zA-ZÀ-ÿ0-9\\s().+*\\/!'-]+$/;\r\n"
				+ "			if (!allowedPattern.test(valorValue) && valorValue) {\r\n"
				+ "				alert(`Invalid input string: ${valorValue}`);\r\n"
				+ "				throw new Error('Invalid input string.');\r\n"
				+ "			}\r\n"
				+ "			console.log(valorValue);\r\n"
				+ "\r\n"
				+ "			formula += campoValue ? `item.${campoValue} ` : '';\r\n"
				+ "			formula += `${operadorValue} `;\r\n"
				+ "			if ((campoValue && operadorValue && !isNaN(valorValue)) || (!campoValue && !operadorValue && !valorValue))\r\n"
				+ "				formula += valorValue ? `${Number(valorValue)} ` : `'' `;\r\n"
				+ "			else if (!campoValue && !operadorValue && !isNaN(valorValue))\r\n"
				+ "				formula += `${Number(valorValue)} `;\r\n"
				+ "			else\r\n"
				+ "				formula += valorValue ? `'${valorValue}' ` : '';\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		let classroomInfo;\r\n"
				+ "		let joinedData = tabledata.map(function (item) {\r\n"
				+ "			classroomInfo = classroomsData.find(function (classroom) {\r\n"
				+ "				return classroom.NomeSala === item.SalaAtribuidaAAula;\r\n"
				+ "			});\r\n"
				+ "\r\n"
				+ "			if (classroomInfo) {\r\n"
				+ "				for (let column in classroomInfo)\r\n"
				+ "					// update do valor ou adicionar a propriedade ao objeto item\r\n"
				+ "					item[column] = classroomInfo[column];\r\n"
				+ "			}\r\n"
				+ "			return item;\r\n"
				+ "		});\r\n"
				+ "\r\n"
				+ "		function ColumnName(data, targetValue) {\r\n"
				+ "			var firstRow = data[0];\r\n"
				+ "\r\n"
				+ "			for (var columnName in firstRow) {\r\n"
				+ "				if (firstRow[columnName] === targetValue) {\r\n"
				+ "					return columnName;\r\n"
				+ "				}\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			return null;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function atualizarResultado() {\r\n"
				+ "			filterResultElement.textContent = \"Fórmula: \" + formula;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function parseCustomFilterString(formula) {\r\n"
				+ "			filterFunction = new Function('item', `return ${formula};`);\r\n"
				+ "			console.log(filterFunction);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function showHorarioHeader() {\r\n"
				+ "			horarioHeader.classList.remove('hidden');\r\n"
				+ "			resultadosHeader.classList.add('hidden');\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function showResultadosHeader() {\r\n"
				+ "			horarioHeader.classList.add('hidden');\r\n"
				+ "			resultadosHeader.classList.remove('hidden');\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function addToFormula() {\r\n"
				+ "			adicionarExpressoes();\r\n"
				+ "			atualizarResultado();\r\n"
				+ "			campo.value = \"\";\r\n"
				+ "			operador.value = \"\";\r\n"
				+ "			valor.value = \"\";\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		let filteredData;\r\n"
				+ "		function showResults() {\r\n"
				+ "			let metricChosen = metricasPredefinidas.options[metricasPredefinidas.selectedIndex].value;\r\n"
				+ "			typeResult.disabled = true;\r\n"
				+ "			if (metricChosen != \"\") {\r\n"
				+ "				resolverMetricas();\r\n"
				+ "			} else if (metricChosen == \"\" || metricasPredefinidas.value == \"\") {\r\n"
				+ "				parseCustomFilterString(formula);\r\n"
				+ "				filteredData = joinedData.filter(filterFunction);\r\n"
				+ "				console.log(\"Joined:\", joinedData);\r\n"
				+ "				console.log(\"Filtered:\", filteredData);\r\n"
				+ "\r\n"
				+ "				document.getElementById(\"resultados\").innerHTML = \"Número de aulas: <span id='resultados_numero'>\" + filteredData.length + \"</span><button id='saveMetricButton'>Guardar métrica</button>\";\r\n"
				+ "\r\n"
				+ "				document.getElementById(\"saveMetricButton\").addEventListener(\"click\", function () {\r\n"
				+ "					saveMetric(formula);\r\n"
				+ "				});\r\n"
				+ "				showResultadosHeader();\r\n"
				+ "				table.setData(filteredData);\r\n"
				+ "			}\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function clearMetric() {\r\n"
				+ "			metricasPredefinidas.value = \"\";\r\n"
				+ "			campo.value = \"\";\r\n"
				+ "			operador.value = \"\";\r\n"
				+ "			valor.value = \"\";\r\n"
				+ "			formula = \"\";\r\n"
				+ "			capacidadeMin.value = \"\";\r\n"
				+ "			capacidadeMax.value = \"\";\r\n"
				+ "			carateristica.value = \"\";\r\n"
				+ "			typeResult.disabled = false;\r\n"
				+ "			atualizarResultado();\r\n"
				+ "			showHorarioHeader();\r\n"
				+ "\r\n"
				+ "			table.getColumns().forEach(function (column) {\r\n"
				+ "				if (!defaultColumns.includes(column))\r\n"
				+ "					table.deleteColumn(column.getField());\r\n"
				+ "			});\r\n"
				+ "			table.setData(tabledata);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function resolverMetricas() {\r\n"
				+ "			let metricChosen = metricasPredefinidas.options[metricasPredefinidas.selectedIndex].value;\r\n"
				+ "			switch (metricChosen) {\r\n"
				+ "				case \"filtro1\": customFilter1(); break;\r\n"
				+ "				case \"filtro2\": customFilter1(); customFilter2(); customFilter2(); break;\r\n"
				+ "				case \"filtro3\": customFilter3(); break;\r\n"
				+ "				case \"filtro4\": customFilter4(); break;\r\n"
				+ "				case \"filtro5\": customFilter5(); break;\r\n"
				+ "				default: metricasPredefinidas.value = \"\";\r\n"
				+ "					let metric = userDefinedMetrics.find(option => option.value == metricChosen);\r\n"
				+ "					if (metric)\r\n"
				+ "						formula = metric.formula;\r\n"
				+ "					showResults();\r\n"
				+ "			}\r\n"
				+ "			atualizarResultado();\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function customFilter1() {\r\n"
				+ "			formula += `item.InscritosNoTurno > item.CapacidadeNormal`;\r\n"
				+ "			parseCustomFilterString(formula);\r\n"
				+ "			filteredData = joinedData.filter(filterFunction);\r\n"
				+ "			console.log(\"Filtered:\", filteredData);\r\n"
				+ "\r\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número de aulas em sobrelotação: <span id='resultados_numero'>\" + filteredData.length + \"</span>\";\r\n"
				+ "			showResultadosHeader();\r\n"
				+ "			table.setData(filteredData);\r\n"
				+ "\r\n"
				+ "			return filteredData;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function customFilter2() {\r\n"
				+ "			if (table.getColumn(\"AlunosEmExcesso\"))\r\n"
				+ "				table.deleteColumn(\"AlunosEmExcesso\");\r\n"
				+ "\r\n"
				+ "			const newColumn = {\r\n"
				+ "				title: \"Alunos em excesso\",\r\n"
				+ "				field: \"AlunosEmExcesso\",\r\n"
				+ "				headerFilter: \"input\",\r\n"
				+ "			};\r\n"
				+ "			table.addColumn(newColumn);\r\n"
				+ "\r\n"
				+ "			let students_total = 0;\r\n"
				+ "			for (let item of filteredData) {\r\n"
				+ "				let students = item.InscritosNoTurno - item.CapacidadeNormal;\r\n"
				+ "				students_total += students;\r\n"
				+ "				item.AlunosEmExcesso = students;\r\n"
				+ "			}\r\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número total de estudantes que estão a mais em aulas sobrelotadas: <span id='resultados_numero'>\" + students_total + \"</span>\";\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function customFilter3() {\r\n"
				+ "			filteredData = [];\r\n"
				+ "			joinedData.forEach(function (item) {\r\n"
				+ "				if (item.SalaAtribuidaAAula != \"\") {\r\n"
				+ "					let column = ColumnName(classroomsData, item.CaracteristicasDaSalaPedidaParaAAula);\r\n"
				+ "					if (column && item[column] !== \"X\") {\r\n"
				+ "						filteredData.push(item);\r\n"
				+ "					}\r\n"
				+ "				}\r\n"
				+ "			});\r\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número de aulas em salas que não têm a característica solicitada: <span id='resultados_numero'>\" + filteredData.length + \"</span>\";\r\n"
				+ "			showResultadosHeader();\r\n"
				+ "			table.setData(filteredData);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function customFilter4() {\r\n"
				+ "			formula += `item.SalaAtribuidaAAula == \"\"`;\r\n"
				+ "			parseCustomFilterString(formula);\r\n"
				+ "			filteredData = joinedData.filter(filterFunction);\r\n"
				+ "			console.log(\"Filtered:\", filteredData);\r\n"
				+ "\r\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número de aulas em que não foi atribuída sala: <span id='resultados_numero'>\" + filteredData.length + \"</span>\";\r\n"
				+ "			showResultadosHeader();\r\n"
				+ "			table.setData(filteredData);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function customFilter5() {\r\n"
				+ "			if (table.getColumn(\"CaracteristicasDesperdicadas\"))\r\n"
				+ "				table.deleteColumn(\"CaracteristicasDesperdicadas\");\r\n"
				+ "\r\n"
				+ "			const newColumn = {\r\n"
				+ "				title: \"Características desperdiçadas\",\r\n"
				+ "				field: \"CaracteristicasDesperdicadas\",\r\n"
				+ "				headerFilter: \"input\",\r\n"
				+ "			};\r\n"
				+ "			table.addColumn(newColumn);\r\n"
				+ "\r\n"
				+ "			filteredData = [...joinedData];\r\n"
				+ "			let wastedCharacteristics_total = 0;\r\n"
				+ "			filteredData = filteredData.filter(item => item.SalaAtribuidaAAula != \"\");\r\n"
				+ "			for (let item of filteredData) {\r\n"
				+ "				if (item.SalaAtribuidaAAula != \"\") {\r\n"
				+ "					let wastedCharacteristics = item.NumCaracteristicas - 1 || 0;\r\n"
				+ "					wastedCharacteristics_total += wastedCharacteristics;\r\n"
				+ "					item.CaracteristicasDesperdicadas = wastedCharacteristics;\r\n"
				+ "				}\r\n"
				+ "			}\r\n"
				+ "			console.log(\"Filtered:\", filteredData);\r\n"
				+ "\r\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número total de características desperdiçadas em salas atribu+idas às aulas: <span id='resultados_numero'>\" + wastedCharacteristics_total + \"</span>\";\r\n"
				+ "			showResultadosHeader();\r\n"
				+ "			table.setData(filteredData);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function SalasDisponiveis() {\r\n"
				+ "			if (table.getColumn(\"salasDisponiveis\"))\r\n"
				+ "				table.deleteColumn(\"salasDisponiveis\");\r\n"
				+ "\r\n"
				+ "			const newColumn = {\r\n"
				+ "				title: \"Salas Disponiveis\",\r\n"
				+ "				field: \"salasDisponiveis\",\r\n"
				+ "				headerFilter: \"input\",\r\n"
				+ "			};\r\n"
				+ "			table.addColumn(newColumn);\r\n"
				+ "			let capacidadeMi = capacidadeMin.value;\r\n"
				+ "			let capacidadeMa = capacidadeMax.value;\r\n"
				+ "			let carateristicaAula = carateristica.value;\r\n"
				+ "			let carateristicaSala = ColumnName(classroomsData, carateristicaAula);\r\n"
				+ "			let match;\r\n"
				+ "			let capacidadeNormal;\r\n"
				+ "			let conditionFunction;\r\n"
				+ "			let form;\r\n"
				+ "			let formC;\r\n"
				+ "			let filteredData = table.getData(true);\r\n"
				+ "\r\n"
				+ "			if (carateristicaAula)\r\n"
				+ "				formC = `&& caract === 'X' `;\r\n"
				+ "			else\r\n"
				+ "				formC = '';\r\n"
				+ "\r\n"
				+ "			if (capacidadeMax.value && capacidadeMin.value) {\r\n"
				+ "				form = `matchingSala && CapacidadeNormal >= capacidadeMin && CapacidadeNormal <= capacidadeMax ` + formC;\r\n"
				+ "				conditionFunction = parseForm(form);\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			else if (!capacidadeMax.value && capacidadeMin.value) {\r\n"
				+ "				form = `matchingSala && CapacidadeNormal >= capacidadeMin ` + formC;\r\n"
				+ "				conditionFunction = parseForm(form);\r\n"
				+ "				capacidadeMa = undefined;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			else if (capacidadeMax.value && !capacidadeMin.value) {\r\n"
				+ "				form = `matchingSala && CapacidadeNormal <= capacidadeMax ` + formC;\r\n"
				+ "				conditionFunction = parseForm(form);\r\n"
				+ "				capacidadeMi = undefined;\r\n"
				+ "			} else {\r\n"
				+ "				form = `matchingSala` + formC;\r\n"
				+ "				conditionFunction = parseForm(form);\r\n"
				+ "				capacidadeMi = undefined;\r\n"
				+ "				capacidadeMa = undefined;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			filteredData.forEach((row) => {\r\n"
				+ "				let salas = [];\r\n"
				+ "				const filteredSalas = [];\r\n"
				+ "				const startTime = row.HoraInicioDaAula;\r\n"
				+ "				const endTime = row.HoraFimDaAula;\r\n"
				+ "				const date = row.DataDaAula;\r\n"
				+ "\r\n"
				+ "				salas = getAvailableClassrooms(date, startTime, endTime);\r\n"
				+ "\r\n"
				+ "				salas.forEach((sala) => {\r\n"
				+ "					const matchingSala = classroomsData.find((classroom) => classroom.NomeSala === sala);\r\n"
				+ "					let match = matchingSala;\r\n"
				+ "					let capacidadeNormal = matchingSala.CapacidadeNormal;\r\n"
				+ "					let caract = matchingSala[carateristicaSala];\r\n"
				+ "\r\n"
				+ "					if (conditionFunction(match, capacidadeNormal, capacidadeMi, capacidadeMa, caract))\r\n"
				+ "						filteredSalas.push(sala);\r\n"
				+ "				});\r\n"
				+ "				row.salasDisponiveis = filteredSalas;\r\n"
				+ "			});\r\n"
				+ "\r\n"
				+ "			table.redraw();\r\n"
				+ "			table.setData(filteredData);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function getAvailableClassrooms(date, startTime, endTime) {\r\n"
				+ "			const classroomsInUse = [];\r\n"
				+ "			const allClassrooms = classroomsData.slice(1).map(classroom => classroom.NomeSala);\r\n"
				+ "			tabledata.forEach((row) => {\r\n"
				+ "				if (row.DataDaAula === date && row.HoraInicioDaAula == startTime && row.HoraFimDaAula == endTime)\r\n"
				+ "					classroomsInUse.push(row.SalaAtribuidaAAula);\r\n"
				+ "			});\r\n"
				+ "\r\n"
				+ "			const availableClassrooms = allClassrooms.filter(classroom => !classroomsInUse.includes(classroom));\r\n"
				+ "			return availableClassrooms;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function parseForm(form) {\r\n"
				+ "			return new Function('matchingSala', 'CapacidadeNormal', 'capacidadeMin', 'capacidadeMax', 'caract', `return ${form}`);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function saveMetric(formula) {\r\n"
				+ "			let metricName = prompt(\"Registe o nome associado a esta métrica que criou\");\r\n"
				+ "			if (metricName != null) {\r\n"
				+ "				const filterNr = \"filtro\" + (metricasPredefinidas.options.length);\r\n"
				+ "				const newOption = {value: filterNr, text: metricName, formula: formula};\r\n"
				+ "				userDefinedMetrics.push(newOption);\r\n"
				+ "\r\n"
				+ "				const newSelectOption = document.createElement(\"option\");\r\n"
				+ "				newSelectOption.value = newOption.value;\r\n"
				+ "				newSelectOption.text = newOption.text;\r\n"
				+ "				metricasPredefinidas.appendChild(newSelectOption);\r\n"
				+ "				const optionCount = metricasPredefinidas.options.length;\r\n"
				+ "			} else {\r\n"
				+ "				exit;\r\n"
				+ "			}\r\n"
				+ "		}");
		jsCode.append("const table = new Tabulator(\"#example-table\", {\n");
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

		jsCode.append("],\n");
		jsCode.append("rowClick: function (e, row) {\r\n"
				+ "				rowData = row.getData();\r\n"
				+ "				openPopup(rowData.salasDisponiveis);\r\n"
				+ "			},");
		jsCode.append("});\ndefaultColumns = [...table.getColumns()];\r\n"
				+ "\r\n"
				+ "		function openPopup(data) {\r\n"
				+ "			var popup = document.getElementById(\"popup\");\r\n"
				+ "			var popupContent = document.getElementById(\"popup-content\");\r\n"
				+ "			popupContent.innerHTML = \"<h3>Salas disponiveis para esta aula</h3><pre>\" + JSON.stringify(data, null, 2) + \"</pre>\";\r\n"
				+ "			popup.style.display = \"block\";\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		function closePopup() {\r\n"
				+ "			var popup = document.getElementById(\"popup\");\r\n"
				+ "			popup.style.display = \"none\";\r\n"
				+ "		}\r\n"
				+ "		console.clear();\n");
		jsCode.append("</script>");

		// Escrever o código JavaScript gerado para um ficheiro HTML
		try (FileWriter writer = new FileWriter("ScheduleApp.html")) {
			writer.write("<!DOCTYPE html>\n<html lang=\"en\">\n");
			writer.write("<head>\n" + "<meta charset=\"utf-8\" />\n"
					+ "		<link href=\"https://unpkg.com/tabulator-tables@4.8.4/dist/css/tabulator.min.css\" rel=\"stylesheet\">\n"
					+ "<link href=\"styles.css\" rel=\"stylesheet\">"
					+ "		<script type=\"text/javascript\" src=\"https://unpkg.com/tabulator-tables@4.8.4/dist/js/tabulator.min.js\"></script>\n"
					+ "\t <title>" + file.getName() + "</title>\n"
					+ "	</head>");
			writer.write("\n<body>\n" + "<H2>Métricas predefinidas</H2>\n" + "<div>\n"
					+ "		<select id=\"metricasPredefinidas\">\n" + ScheduleMetrics.metricOptions()
					+ "  	</select>\n" + "</div>\n\n"
					+ "<H2>Cálculo de métricas</H2>\n" + "<div>\n"
					+ "    <select id=\"select-campo\">\n" + ScheduleMetrics.columnOptions()
					+ "  	</select>\n\n"
					+ "  	<select id=\"select-operador\">\n" + ScheduleMetrics.operatorOptions()
					+ "  	</select>\n\n"
					+ "  	<input id=\"input-valor\" type=\"text\" placeholder=\"valor\">\n\n"
					+ "  	<button id=\"button-confirmar\" onclick=\"addToFormula()\">Adicionar</button>\n\n"
					+ "  	<button id=\"button-resultado\" onclick=\"showResults()\">Obter resultado</button>\n\n"
					+ "  	<button id=\"button-clear\" onclick=\"clearMetric()\">Clear Filter</button>\n"
					+ "	</div>\n\n"
					+ "	<div id=\"resultado-obtido\">\n"
					+ "  		<div id=\"filter-result\"></div>\n"
					+ " 		<p><br><br></p>\n"
					+ "	</div>\n\n"
					+ "<H2>Salas Disponiveis</H2>\r\n"
					+ "	<div>\r\n"
					+ "		<input id=\"input-capacidadeMin\" type=\"number\" placeholder=\"Capacidade min.\">\r\n"
					+ "		<input id=\"input-capacidadeMax\" type=\"number\" placeholder=\"Capacidade max.\">\r\n"
					+ "		<input id=\"input-caraterística\" type=\"text\" placeholder=\"Caraterística da Sala\">\r\n"
					+ "		<button id=\"button-SalasDisponiveis\" onclick=\"SalasDisponiveis()\">Salas Disponiveis</button>\r\n"
					+ "	</div>"
					+ "\t<header id=\"horarioHeader\">\r\n"
					+ "		<h1>Horário</h1>\r\n"
					+ "	</header>\r\n"
					+ "	\r\n"
					+ "	<header id=\"resultadosHeader\" class=\"hidden\">\r\n"
					+ "		<H1>Resultado</H1>\r\n"
					+ "		<p><h5>Está a visualizar o resultado da métrica aplicada, pressione \"Clear Filter\" para ver a tabela original</h5></p>\r\n"
					+ "		<span id=\"resultados\"></span>\r\n"
					+ "	</header>\n"
					+ "\n<div id=\"example-table\"></div>\n\n"
					+ "<div id=\"popup\">\r\n"
					+ "		<button onclick=\"closePopup()\">Close</button>\r\n"
					+ "		<div id=\"popup-content\"></div>\r\n"
					+ "	</div>\n");
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
	
//	private boolean hasHeader(boolean perfil) {
//		
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
