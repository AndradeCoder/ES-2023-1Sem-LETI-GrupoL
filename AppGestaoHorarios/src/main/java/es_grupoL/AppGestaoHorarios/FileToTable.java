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
 * @version 1.3
 */
public class FileToTable {

	private boolean columnsMapped = false;
	private File file;
	private List<String> mappedHeader = new ArrayList<>();
	//atributo não utilizado
	private ConfigApp configuracao_aplicacao;

	/**
	 * Constructs a {@code FileToTable} object for processing a given CSV file.
	 *
	 * @param file The CSV file to be processed.
	 * @param configuracao_aplicacao The saved configurations made in the previous application run
	 */
	public FileToTable(File file, ConfigApp configuracao_aplicacao) {
		this.file = file;
		this.configuracao_aplicacao = configuracao_aplicacao;
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
		return String.format(configuracao_aplicacao.getFormatoData(), valor);
	}

	private String formatarHora(String valor) {
		return String.format(configuracao_aplicacao.getFormatoHora(), valor);
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
		jsCode.append("let formula = \"\";\n\n"
				+ "		let filterFunction;\n\n"
				+ "		let campo = document.getElementById(\"select-campo\");\n\n"
				+ "		let operador = document.getElementById(\"select-operador\");\n\n"
				+ "		let valor = document.getElementById(\"input-valor\");\n\n"
				+ "		let typeResult = document.getElementById(\"button-resultado\");\n\n"
				+ "		let filterResultElement = document.getElementById(\"filter-result\");\n\n"
				+ "		let capacidadeMin = document.getElementById(\"input-capacidadeMin\");\n\n"
				+ "		let capacidadeMax = document.getElementById(\"input-capacidadeMax\");\n\n"
				+ "		let carateristica = document.getElementById(\"input-caraterística\");\n\n"
				+ "		const horarioHeader = document.getElementById('horarioHeader');\n\n"
				+ "		const resultadosHeader = document.getElementById('resultadosHeader');\n\n"
				+ "		let metricasPredefinidas = document.getElementById(\"metricasPredefinidas\");\n\n"
				+ "		let userDefinedMetrics = [];\n\n"
				+ "\n\n"
				+ "		function adicionarExpressoes() {\n\n"
				+ "			const campoValue = campo.options[campo.selectedIndex].value;\n\n"
				+ "			const operadorValue = operador.options[operador.selectedIndex].value;\n\n"
				+ "			let valorValue = valor.value;\n\n"
				+ "			const selectedOption = campo.options[campo.selectedIndex];\n\n"
				+ "\n\n"
				+ "			const allowedPattern = /^[a-zA-ZÀ-ÿ0-9\\s().+*\\/!'-]+$/;\n\n"
				+ "			if (!allowedPattern.test(valorValue) && valorValue) {\n\n"
				+ "				alert(`Invalid input string: ${valorValue}`);\n\n"
				+ "				throw new Error('Invalid input string.');\n\n"
				+ "			}\n\n"
				+ "			console.log(valorValue);\n\n"
				+ "\n\n"
				+ "			formula += campoValue ? `item.${campoValue} ` : '';\n\n"
				+ "			formula += `${operadorValue} `;\n\n"
				+ "			if ((campoValue && operadorValue && !isNaN(valorValue)) || (!campoValue && !operadorValue && !valorValue))\n\n"
				+ "				formula += valorValue ? `${Number(valorValue)} ` : `'' `;\n\n"
				+ "			else if (!campoValue && !operadorValue && !isNaN(valorValue))\n\n"
				+ "				formula += `${Number(valorValue)} `;\n\n"
				+ "			else\n\n"
				+ "				formula += valorValue ? `'${valorValue}' ` : '';\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		let classroomInfo;\n\n"
				+ "		let joinedData = tabledata.map(function (item) {\n\n"
				+ "			classroomInfo = classroomsData.find(function (classroom) {\n\n"
				+ "				return classroom.NomeSala === item.SalaAtribuidaAAula;\n\n"
				+ "			});\n\n"
				+ "\n\n"
				+ "			if (classroomInfo) {\n\n"
				+ "				for (let column in classroomInfo)\n\n"
				+ "					// update do valor ou adicionar a propriedade ao objeto item\n\n"
				+ "					item[column] = classroomInfo[column];\n\n"
				+ "			}\n\n"
				+ "			return item;\n\n"
				+ "		});\n\n"
				+ "\n\n"
				+ "		function ColumnName(data, targetValue) {\n\n"
				+ "			var firstRow = data[0];\n\n"
				+ "\n\n"
				+ "			for (var columnName in firstRow) {\n\n"
				+ "				if (firstRow[columnName] === targetValue) {\n\n"
				+ "					return columnName;\n\n"
				+ "				}\n\n"
				+ "			}\n\n"
				+ "\n\n"
				+ "			return null;\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function atualizarResultado() {\n\n"
				+ "			filterResultElement.textContent = \"Fórmula: \" + formula;\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function parseCustomFilterString(formula) {\n\n"
				+ "			filterFunction = new Function('item', `return ${formula};`);\n\n"
				+ "			console.log(filterFunction);\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function showHorarioHeader() {\n\n"
				+ "			horarioHeader.classList.remove('hidden');\n\n"
				+ "			resultadosHeader.classList.add('hidden');\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function showResultadosHeader() {\n\n"
				+ "			horarioHeader.classList.add('hidden');\n\n"
				+ "			resultadosHeader.classList.remove('hidden');\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function addToFormula() {\n\n"
				+ "			adicionarExpressoes();\n\n"
				+ "			atualizarResultado();\n\n"
				+ "			campo.value = \"\";\n\n"
				+ "			operador.value = \"\";\n\n"
				+ "			valor.value = \"\";\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		let filteredData;\n\n"
				+ "		function showResults() {\n\n"
				+ "			let metricChosen = metricasPredefinidas.options[metricasPredefinidas.selectedIndex].value;\n\n"
				+ "			typeResult.disabled = true;\n\n"
				+ "			if (metricChosen != \"\") {\n\n"
				+ "				resolverMetricas();\n\n"
				+ "			} else if (metricChosen == \"\" || metricasPredefinidas.value == \"\") {\n\n"
				+ "				parseCustomFilterString(formula);\n\n"
				+ "				filteredData = joinedData.filter(filterFunction);\n\n"
				+ "				console.log(\"Joined:\", joinedData);\n\n"
				+ "				console.log(\"Filtered:\", filteredData);\n\n"
				+ "\n\n"
				+ "				document.getElementById(\"resultados\").innerHTML = \"Número de aulas: <span id='resultados_numero'>\" + filteredData.length + \"</span><button id='saveMetricButton'>Guardar métrica</button>\";\n\n"
				+ "\n\n"
				+ "				document.getElementById(\"saveMetricButton\").addEventListener(\"click\", function () {\n\n"
				+ "					saveMetric(formula);\n\n"
				+ "				});\n\n"
				+ "				showResultadosHeader();\n\n"
				+ "				table.setData(filteredData);\n\n"
				+ "			}\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function clearMetric() {\n\n"
				+ "			metricasPredefinidas.value = \"\";\n\n"
				+ "			campo.value = \"\";\n\n"
				+ "			operador.value = \"\";\n\n"
				+ "			valor.value = \"\";\n\n"
				+ "			formula = \"\";\n\n"
				+ "			capacidadeMin.value = \"\";\n\n"
				+ "			capacidadeMax.value = \"\";\n\n"
				+ "			carateristica.value = \"\";\n\n"
				+ "			typeResult.disabled = false;\n\n"
				+ "			atualizarResultado();\n\n"
				+ "			showHorarioHeader();\n\n"
				+ "\n\n"
				+ "			table.getColumns().forEach(function (column) {\n\n"
				+ "				if (!defaultColumns.includes(column))\n\n"
				+ "					table.deleteColumn(column.getField());\n\n"
				+ "			});\n\n"
				+ "			table.setData(tabledata);\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function resolverMetricas() {\n\n"
				+ "			let metricChosen = metricasPredefinidas.options[metricasPredefinidas.selectedIndex].value;\n\n"
				+ "			switch (metricChosen) {\n\n"
				+ "				case \"filtro1\": customFilter1(); break;\n\n"
				+ "				case \"filtro2\": customFilter1(); customFilter2(); customFilter2(); break;\n\n"
				+ "				case \"filtro3\": customFilter3(); break;\n\n"
				+ "				case \"filtro4\": customFilter4(); break;\n\n"
				+ "				case \"filtro5\": customFilter5(); break;\n\n"
				+ "				default: metricasPredefinidas.value = \"\";\n\n"
				+ "					let metric = userDefinedMetrics.find(option => option.value == metricChosen);\n\n"
				+ "					if (metric)\n\n"
				+ "						formula = metric.formula;\n\n"
				+ "					showResults();\n\n"
				+ "			}\n\n"
				+ "			atualizarResultado();\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function customFilter1() {\n\n"
				+ "			formula += `item.InscritosNoTurno > item.CapacidadeNormal`;\n\n"
				+ "			parseCustomFilterString(formula);\n\n"
				+ "			filteredData = joinedData.filter(filterFunction);\n\n"
				+ "			console.log(\"Filtered:\", filteredData);\n\n"
				+ "\n\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número de aulas em sobrelotação: <span id='resultados_numero'>\" + filteredData.length + \"</span>\";\n\n"
				+ "			showResultadosHeader();\n\n"
				+ "			table.setData(filteredData);\n\n"
				+ "\n\n"
				+ "			return filteredData;\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function customFilter2() {\n\n"
				+ "			if (table.getColumn(\"AlunosEmExcesso\"))\n\n"
				+ "				table.deleteColumn(\"AlunosEmExcesso\");\n\n"
				+ "\n\n"
				+ "			const newColumn = {\n\n"
				+ "				title: \"Alunos em excesso\",\n\n"
				+ "				field: \"AlunosEmExcesso\",\n\n"
				+ "				headerFilter: \"input\",\n\n"
				+ "			};\n\n"
				+ "			table.addColumn(newColumn);\n\n"
				+ "\n\n"
				+ "			let students_total = 0;\n\n"
				+ "			for (let item of filteredData) {\n\n"
				+ "				let students = item.InscritosNoTurno - item.CapacidadeNormal;\n\n"
				+ "				students_total += students;\n\n"
				+ "				item.AlunosEmExcesso = students;\n\n"
				+ "			}\n\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número total de estudantes que estão a mais em aulas sobrelotadas: <span id='resultados_numero'>\" + students_total + \"</span>\";\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function customFilter3() {\n\n"
				+ "			filteredData = [];\n\n"
				+ "			joinedData.forEach(function (item) {\n\n"
				+ "				if (item.SalaAtribuidaAAula != \"\") {\n\n"
				+ "					let column = ColumnName(classroomsData, item.CaracteristicasDaSalaPedidaParaAAula);\n\n"
				+ "					if (column && item[column] !== \"X\") {\n\n"
				+ "						filteredData.push(item);\n\n"
				+ "					}\n\n"
				+ "				}\n\n"
				+ "			});\n\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número de aulas em salas que não têm a característica solicitada: <span id='resultados_numero'>\" + filteredData.length + \"</span>\";\n\n"
				+ "			showResultadosHeader();\n\n"
				+ "			table.setData(filteredData);\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function customFilter4() {\n\n"
				+ "			formula += `item.SalaAtribuidaAAula == \"\"`;\n\n"
				+ "			parseCustomFilterString(formula);\n\n"
				+ "			filteredData = joinedData.filter(filterFunction);\n\n"
				+ "			console.log(\"Filtered:\", filteredData);\n\n"
				+ "\n\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número de aulas em que não foi atribuída sala: <span id='resultados_numero'>\" + filteredData.length + \"</span>\";\n\n"
				+ "			showResultadosHeader();\n\n"
				+ "			table.setData(filteredData);\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function customFilter5() {\n\n"
				+ "			if (table.getColumn(\"CaracteristicasDesperdicadas\"))\n\n"
				+ "				table.deleteColumn(\"CaracteristicasDesperdicadas\");\n\n"
				+ "\n\n"
				+ "			const newColumn = {\n\n"
				+ "				title: \"Características desperdiçadas\",\n\n"
				+ "				field: \"CaracteristicasDesperdicadas\",\n\n"
				+ "				headerFilter: \"input\",\n\n"
				+ "			};\n\n"
				+ "			table.addColumn(newColumn);\n\n"
				+ "\n\n"
				+ "			filteredData = [...joinedData];\n\n"
				+ "			let wastedCharacteristics_total = 0;\n\n"
				+ "			filteredData = filteredData.filter(item => item.SalaAtribuidaAAula != \"\");\n\n"
				+ "			for (let item of filteredData) {\n\n"
				+ "				if (item.SalaAtribuidaAAula != \"\") {\n\n"
				+ "					let wastedCharacteristics = item.NumCaracteristicas - 1 || 0;\n\n"
				+ "					wastedCharacteristics_total += wastedCharacteristics;\n\n"
				+ "					item.CaracteristicasDesperdicadas = wastedCharacteristics;\n\n"
				+ "				}\n\n"
				+ "			}\n\n"
				+ "			console.log(\"Filtered:\", filteredData);\n\n"
				+ "\n\n"
				+ "			document.getElementById(\"resultados\").innerHTML = \"Número total de características desperdiçadas em salas atribu+idas às aulas: <span id='resultados_numero'>\" + wastedCharacteristics_total + \"</span>\";\n\n"
				+ "			showResultadosHeader();\n\n"
				+ "			table.setData(filteredData);\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function SalasDisponiveis() {\n\n"
				+ "			if (table.getColumn(\"salasDisponiveis\"))\n\n"
				+ "				table.deleteColumn(\"salasDisponiveis\");\n\n"
				+ "\n\n"
				+ "			const newColumn = {\n\n"
				+ "				title: \"Salas Disponiveis\",\n\n"
				+ "				field: \"salasDisponiveis\",\n\n"
				+ "				headerFilter: \"input\",\n\n"
				+ "			};\n\n"
				+ "			table.addColumn(newColumn);\n\n"
				+ "			let capacidadeMi = capacidadeMin.value;\n\n"
				+ "			let capacidadeMa = capacidadeMax.value;\n\n"
				+ "			let carateristicaAula = carateristica.value;\n\n"
				+ "			let carateristicaSala = ColumnName(classroomsData, carateristicaAula);\n\n"
				+ "			let match;\n\n"
				+ "			let capacidadeNormal;\n\n"
				+ "			let conditionFunction;\n\n"
				+ "			let form;\n\n"
				+ "			let formC;\n\n"
				+ "			let filteredData = table.getData(true);\n\n"
				+ "\n\n"
				+ "			if (carateristicaAula)\n\n"
				+ "				formC = `&& caract === 'X' `;\n\n"
				+ "			else\n\n"
				+ "				formC = '';\n\n"
				+ "\n\n"
				+ "			if (capacidadeMax.value && capacidadeMin.value) {\n\n"
				+ "				form = `matchingSala && CapacidadeNormal >= capacidadeMin && CapacidadeNormal <= capacidadeMax ` + formC;\n\n"
				+ "				conditionFunction = parseForm(form);\n\n"
				+ "			}\n\n"
				+ "\n\n"
				+ "			else if (!capacidadeMax.value && capacidadeMin.value) {\n\n"
				+ "				form = `matchingSala && CapacidadeNormal >= capacidadeMin ` + formC;\n\n"
				+ "				conditionFunction = parseForm(form);\n\n"
				+ "				capacidadeMa = undefined;\n\n"
				+ "			}\n\n"
				+ "\n\n"
				+ "			else if (capacidadeMax.value && !capacidadeMin.value) {\n\n"
				+ "				form = `matchingSala && CapacidadeNormal <= capacidadeMax ` + formC;\n\n"
				+ "				conditionFunction = parseForm(form);\n\n"
				+ "				capacidadeMi = undefined;\n\n"
				+ "			} else {\n\n"
				+ "				form = `matchingSala` + formC;\n\n"
				+ "				conditionFunction = parseForm(form);\n\n"
				+ "				capacidadeMi = undefined;\n\n"
				+ "				capacidadeMa = undefined;\n\n"
				+ "			}\n\n"
				+ "\n\n"
				+ "			filteredData.forEach((row) => {\n\n"
				+ "				let salas = [];\n\n"
				+ "				const filteredSalas = [];\n\n"
				+ "				const startTime = row.HoraInicioDaAula;\n\n"
				+ "				const endTime = row.HoraFimDaAula;\n\n"
				+ "				const date = row.DataDaAula;\n\n"
				+ "\n\n"
				+ "				salas = getAvailableClassrooms(date, startTime, endTime);\n\n"
				+ "\n\n"
				+ "				salas.forEach((sala) => {\n\n"
				+ "					const matchingSala = classroomsData.find((classroom) => classroom.NomeSala === sala);\n\n"
				+ "					let match = matchingSala;\n\n"
				+ "					let capacidadeNormal = matchingSala.CapacidadeNormal;\n\n"
				+ "					let caract = matchingSala[carateristicaSala];\n\n"
				+ "\n\n"
				+ "					if (conditionFunction(match, capacidadeNormal, capacidadeMi, capacidadeMa, caract))\n\n"
				+ "						filteredSalas.push(sala);\n\n"
				+ "				});\n\n"
				+ "				row.salasDisponiveis = filteredSalas;\n\n"
				+ "			});\n\n"
				+ "\n\n"
				+ "			table.redraw();\n\n"
				+ "			table.setData(filteredData);\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function getAvailableClassrooms(date, startTime, endTime) {\n\n"
				+ "			const classroomsInUse = [];\n\n"
				+ "			const allClassrooms = classroomsData.slice(1).map(classroom => classroom.NomeSala);\n\n"
				+ "			tabledata.forEach((row) => {\n\n"
				+ "				if (row.DataDaAula === date && row.HoraInicioDaAula == startTime && row.HoraFimDaAula == endTime)\n\n"
				+ "					classroomsInUse.push(row.SalaAtribuidaAAula);\n\n"
				+ "			});\n\n"
				+ "\n\n"
				+ "			const availableClassrooms = allClassrooms.filter(classroom => !classroomsInUse.includes(classroom));\n\n"
				+ "			return availableClassrooms;\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function parseForm(form) {\n\n"
				+ "			return new Function('matchingSala', 'CapacidadeNormal', 'capacidadeMin', 'capacidadeMax', 'caract', `return ${form}`);\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function saveMetric(formula) {\n\n"
				+ "			let metricName = prompt(\"Registe o nome associado a esta métrica que criou\");\n\n"
				+ "			if (metricName != null) {\n\n"
				+ "				const filterNr = \"filtro\" + (metricasPredefinidas.options.length);\n\n"
				+ "				const newOption = {value: filterNr, text: metricName, formula: formula};\n\n"
				+ "				userDefinedMetrics.push(newOption);\n\n"
				+ "\n\n"
				+ "				const newSelectOption = document.createElement(\"option\");\n\n"
				+ "				newSelectOption.value = newOption.value;\n\n"
				+ "				newSelectOption.text = newOption.text;\n\n"
				+ "				metricasPredefinidas.appendChild(newSelectOption);\n\n"
				+ "				const optionCount = metricasPredefinidas.options.length;\n\n"
				+ "			} else {\n\n"
				+ "				exit;\n\n"
				+ "			}\n\n"
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
		jsCode.append("rowClick: function (e, row) {\n\n"
				+ "				rowData = row.getData();\n\n"
				+ "				openPopup(rowData.salasDisponiveis);\n\n"
				+ "			},");
		jsCode.append("});\ndefaultColumns = [...table.getColumns()];\n\n"
				+ "\n\n"
				+ "		function openPopup(data) {\n\n"
				+ "			var popup = document.getElementById(\"popup\");\n\n"
				+ "			var popupContent = document.getElementById(\"popup-content\");\n\n"
				+ "			popupContent.innerHTML = \"<h3>Salas disponiveis para esta aula</h3><pre>\" + JSON.stringify(data, null, 2) + \"</pre>\";\n\n"
				+ "			popup.style.display = \"block\";\n\n"
				+ "		}\n\n"
				+ "\n\n"
				+ "		function closePopup() {\n\n"
				+ "			var popup = document.getElementById(\"popup\");\n\n"
				+ "			popup.style.display = \"none\";\n\n"
				+ "		}\n\n"
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
					+ "<H2>Salas Disponiveis</H2>\n\n"
					+ "	<div>\n\n"
					+ "		<input id=\"input-capacidadeMin\" type=\"number\" placeholder=\"Capacidade min.\">\n\n"
					+ "		<input id=\"input-capacidadeMax\" type=\"number\" placeholder=\"Capacidade max.\">\n\n"
					+ "		<input id=\"input-caraterística\" type=\"text\" placeholder=\"Caraterística da Sala\">\n\n"
					+ "		<button id=\"button-SalasDisponiveis\" onclick=\"SalasDisponiveis()\">Salas Disponiveis</button>\n\n"
					+ "	</div>"
					+ "\t<header id=\"horarioHeader\">\n\n"
					+ "		<h1>Horário</h1>\n\n"
					+ "	</header>\n\n"
					+ "	\n\n"
					+ "	<header id=\"resultadosHeader\" class=\"hidden\">\n\n"
					+ "		<H1>Resultado</H1>\n\n"
					+ "		<p><h5>Está a visualizar o resultado da métrica aplicada, pressione \"Clear Filter\" para ver a tabela original</h5></p>\n\n"
					+ "		<span id=\"resultados\"></span>\n\n"
					+ "	</header>\n"
					+ "\n<div id=\"example-table\"></div>\n\n"
					+ "<div id=\"popup\">\n\n"
					+ "		<button onclick=\"closePopup()\">Close</button>\n\n"
					+ "		<div id=\"popup-content\"></div>\n\n"
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
		ConfigApp ca = new ConfigApp();
		File file = new File("C:\\Users\\leoth\\Downloads\\HorarioDeExemplo.csv");
		FileToTable ftt = new FileToTable(file, ca);
		FileToTable a = new FileToTable(file, ca);
		FileToTable b = new FileToTable(file, ca);
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
