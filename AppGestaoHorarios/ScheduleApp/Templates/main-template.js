let formula = "";
let filterFunction;
let campo = document.getElementById("select-campo");
let operador = document.getElementById("select-operador");
let valor = document.getElementById("input-valor");
let typeResult = document.getElementById("button-resultado");
let filterResultElement = document.getElementById("filter-result");
let capacidadeMin = document.getElementById("input-capacidadeMin");
let capacidadeMax = document.getElementById("input-capacidadeMax");
let carateristica = document.getElementById("input-caraterística");
const horarioHeader = document.getElementById('horarioHeader');
const resultadosHeader = document.getElementById('resultadosHeader');
let metricasPredefinidas = document.getElementById("metricasPredefinidas");
let userDefinedMetrics = [];

filterResultElement.textContent = "Fórmula: ";  // Inicializar o texto
function atualizarResultado() {
    filterResultElement.textContent = "Fórmula: " + formula;
}

function showHorarioHeader() {
    horarioHeader.classList.remove('hidden');
    resultadosHeader.classList.add('hidden');
}

function showResultadosHeader() {
    horarioHeader.classList.add('hidden');
    resultadosHeader.classList.remove('hidden');
}

function adicionarExpressoes() {
    const campoValue = campo.options[campo.selectedIndex].value;
    const operadorValue = operador.options[operador.selectedIndex].value;
    let valorValue = valor.value;
    const selectedOption = campo.options[campo.selectedIndex];

    const allowedPattern = /^[a-zA-ZÀ-ÿ0-9\s().+*\/!'-]+$/;
    if (valorValue && !allowedPattern.test(valorValue)) {
        alert(`Invalid input string: ${valorValue}`);
        throw new Error('Invalid input string.');
    }
    console.log(valorValue);

    formula += campoValue ? `item.${campoValue} ` : '';
    formula += `${operadorValue} `;
    if ((campoValue && operadorValue && !isNaN(valorValue)) || (!campoValue && !operadorValue && !valorValue))
        formula += valorValue ? `${Number(valorValue)} ` : `'' `;
    else if (!campoValue && !operadorValue && !isNaN(valorValue))
        formula += `${Number(valorValue)} `;
    else
        formula += valorValue ? `'${valorValue}' ` : '';
}

function addToFormula() {
    adicionarExpressoes();
    atualizarResultado();
    campo.value = "";
    operador.value = "";
    valor.value = "";
}

function parseCustomFilterString(formula) {
    filterFunction = new Function('item', `return ${formula};`);
    console.log(filterFunction);
}

let filteredData;
function showResults() {
    let metricChosen = metricasPredefinidas.options[metricasPredefinidas.selectedIndex].value;
    typeResult.disabled = true;

    if (metricChosen != "") {
        chooseSavedMetric();
    } else if (metricChosen == "" || metricasPredefinidas.value == "") {
        parseCustomFilterString(formula);
        filteredData = joinedData.filter(filterFunction);
        console.log("Joined:", joinedData);
        console.log("Filtered:", filteredData);

        // Se for uma métrica costumizada apenas mostra o resultado, caso contrário dá a opção de também gravar a métrica criada
        if (metricasPredefinidas.value == "") {
            document.getElementById("resultados").innerHTML = "Número de aulas: <span id='resultados_numero'>" + filteredData.length + "</span>";
        } else {
            document.getElementById("resultados").innerHTML = "Número de aulas: <span id='resultados_numero'>" + filteredData.length + 
            "</span><button id='saveMetricButton'>Guardar métrica</button>";

            document.getElementById("saveMetricButton").addEventListener("click", function () {
                saveCustomMetric(formula);
            });
        }
        showResultadosHeader();
        table.setData(filteredData);
    }
}

function clearMetric() {
    metricasPredefinidas.value = "";
    campo.value = "";
    operador.value = "";
    valor.value = "";
    formula = "";
    capacidadeMin.value = "";
    capacidadeMax.value = "";
    carateristica.value = "";
    typeResult.disabled = false;
    atualizarResultado();
    showHorarioHeader();

    table.getColumns().forEach(function (column) {
        if (!defaultColumns.includes(column))
            table.deleteColumn(column.getField());
    });
    table.setData(tabledata);
}

const table = new Tabulator("#example-table", {
    data: tabledata,
    layout: "fitDatafill",
    pagination: "local",
    paginationSize: 10,
    paginationSizeSelector: [5, 10, 20, 40],
    movableColumns: true,
    paginationCounter: "rows",
    initialSort: [{column: "building", dir: "asc"},],
    columns: [
		//Insert here
	],
    rowClick: function (e, row) {
        rowData = row.getData();
        openPopup(rowData.salasDisponiveis);
    },
});
defaultColumns = [...table.getColumns()];

function openPopup(data) {
    const checkColumn = table.getColumn("salasDisponiveis");
    if (checkColumn) {
        const popup = document.getElementById("popup");
        const popupContent = document.getElementById("popup-content");
        popupContent.innerHTML = "<h3>Salas disponiveis para esta aula</h3><pre>" + JSON.stringify(data, null, 2) + "</pre>";
        popup.style.display = "block";
    }
}

function closePopup() {
    const popup = document.getElementById("popup");
    popup.style.display = "none";
}