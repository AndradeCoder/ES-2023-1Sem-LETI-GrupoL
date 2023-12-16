function chooseSavedMetric() {
	let metricChosen = metricasPredefinidas.options[metricasPredefinidas.selectedIndex].value;
	switch (metricChosen) {
		case "metrica1": customFilter1(); break;
		case "metrica2": customFilter1(); customFilter2(); customFilter2(); break;
		case "metrica3": customFilter3(); break;
		case "metrica4": customFilter4(); break;
		case "metrica5": customFilter5(); break;
		default: metricasPredefinidas.value = "";
			let metric = userDefinedMetrics.find(option => option.value == metricChosen);
			if (metric)
				formula = metric.formula;
			showResults();	// Entra no else-if statement do showResults
	}
	atualizarResultado();
}

function customFilter1() {
	formula += `item.InscritosNoTurno > item.CapacidadeNormal`;
	parseCustomFilterString(formula);
	filteredData = joinedData.filter(filterFunction);
	console.log("Filtered:", filteredData);

	document.getElementById("resultados").innerHTML = "Número de aulas em sobrelotação: <span id='resultados_numero'>" + filteredData.length + "</span>";
	showResultadosHeader();
	table.setData(filteredData);

	return filteredData;
}

function customFilter2() {
	if (table.getColumn("AlunosEmExcesso"))
		table.deleteColumn("AlunosEmExcesso");

	const newColumn = {
		title: "Alunos em excesso",
		field: "AlunosEmExcesso",
		headerFilter: "input",
	};
	table.addColumn(newColumn);

	let students_total = 0;
	for (let item of filteredData) {
		let students = item.InscritosNoTurno - item.CapacidadeNormal;
		students_total += students;
		item.AlunosEmExcesso = students;
	}
	document.getElementById("resultados").innerHTML = "Número total de estudantes que estão a mais em aulas sobrelotadas: <span id='resultados_numero'>" + students_total + "</span>";
}

function customFilter3() {
	filteredData = [];
	joinedData.forEach(function(item) {
		if (item.SalaAtribuidaAAula != "") {
			let column = getColumnName(classroomsData, item.CaracteristicasDaSalaPedidaParaAAula);
			if (column && item[column] !== "X")
				filteredData.push(item);
		}
	});
	document.getElementById("resultados").innerHTML = "Número de aulas em salas que não têm a característica solicitada: <span id='resultados_numero'>" + filteredData.length + "</span>";
	showResultadosHeader();
	table.setData(filteredData);
}

function customFilter4() {
	formula += `item.SalaAtribuidaAAula == ""`;
	parseCustomFilterString(formula);
	filteredData = joinedData.filter(filterFunction);
	console.log("Filtered:", filteredData);

	document.getElementById("resultados").innerHTML = "Número de aulas em que não foi atribuída sala: <span id='resultados_numero'>" + filteredData.length + "</span>";
	showResultadosHeader();
	table.setData(filteredData);
}

function customFilter5() {
	if (table.getColumn("CaracteristicasDesperdicadas"))
		table.deleteColumn("CaracteristicasDesperdicadas");

	const newColumn = {
		title: "Características desperdiçadas",
		field: "CaracteristicasDesperdicadas",
		headerFilter: "input",
	};
	table.addColumn(newColumn);

	filteredData = [...joinedData];
	let wastedCharacteristics_total = 0;
	filteredData = filteredData.filter(item => item.SalaAtribuidaAAula != "");
	for (let item of filteredData) {
		if (item.SalaAtribuidaAAula != "") {
			let wastedCharacteristics = item.NumCaracteristicas - 1 || 0;
			wastedCharacteristics_total += wastedCharacteristics;
			item.CaracteristicasDesperdicadas = wastedCharacteristics;
		}
	}
	console.log("Filtered:", filteredData);

	document.getElementById("resultados").innerHTML = "Número total de características desperdiçadas em salas atribu+idas às aulas: <span id='resultados_numero'>" + wastedCharacteristics_total + "</span>";
	showResultadosHeader();
	table.setData(filteredData);
}

function saveCustomMetric(formula) {
	let metricName = prompt("Registe o nome associado a esta métrica que criou");
	if (metricName != null) {
		const metricCount = metricasPredefinidas.options.length;
		const filterNr = "metrica" + (metricCount);
		const newOption = { value: filterNr, text: metricName, formula: formula };
		userDefinedMetrics.push(newOption);

		const userOptgroup = document.getElementById("MetricasCustomizadas");
		userOptgroup.removeAttribute('disabled');
		const newSelectOption = document.createElement("option");
		newSelectOption.value = newOption.value;
		newSelectOption.textContent = newOption.text;

		metricasPredefinidas.appendChild(newSelectOption);

		localStorage.clear();   // Elimina o array de métricas desatualizado (userDefinedMetrics)
		localStorage.setItem("userDefinedMetrics", JSON.stringify(userDefinedMetrics));    // Reintroduz no storage o novo array atualizado
	}
}

if (localStorage.length == 1) {
	document.addEventListener('DOMContentLoaded', function() {
		userDefinedMetrics = JSON.parse(localStorage.getItem("userDefinedMetrics"));
		for (let index = 0; index < userDefinedMetrics.length; index++) {
			const savedOption = userDefinedMetrics[index];
			const newOption = document.createElement('option');
			newOption.value = savedOption.value;
			newOption.textContent = savedOption.text;
			metricasPredefinidas.appendChild(newOption);
			console.log("LocalStorage contents: ", newOption);
		}
	});
}
console.clear();