let classroomInfo;
let joinedData = tabledata.map(function (item) {
    classroomInfo = classroomsData.find(function (classroom) {
        return classroom.NomeSala === item.SalaAtribuidaAAula;
    });

    if (classroomInfo) {
        for (let column in classroomInfo)
            // update do valor ou adicionar a propriedade ao objeto item
            item[column] = classroomInfo[column];
    }
    return item;
});

function SalasDisponiveis() {
    if (table.getColumn("salasDisponiveis"))
        table.deleteColumn("salasDisponiveis");

    const newColumn = {
        title: "Salas Disponiveis",
        field: "salasDisponiveis",
        headerFilter: "input",
        sorter: (a, b) => {
            // Converter os arrays para strings
            const aString = a.join(', ');
            const bString = b.join(', ');
    
            // Fazer o sort entre os arrays com localeCompare
            return aString.localeCompare(bString);
        },
    };
    table.addColumn(newColumn);

    filteredData = table.getData(true);
    let capacidadeMi = capacidadeMin.value;
    let capacidadeMa = capacidadeMax.value;
    let carateristicaPedidaAula = carateristica.value;
    let carateristicaSala = getColumnName(classroomsData, carateristicaPedidaAula);
    let conditionFunction;
    let form;
    let formC = carateristicaPedidaAula ? `&& caract === 'X' ` : '';

    if (capacidadeMax.value && capacidadeMin.value) {
        form = `matchingSala && CapacidadeNormal >= capacidadeMin && CapacidadeNormal <= capacidadeMax ` + formC;
        conditionFunction = parseForm(form);
    } else if (!capacidadeMax.value && capacidadeMin.value) {
        form = `matchingSala && CapacidadeNormal >= capacidadeMin ` + formC;
        conditionFunction = parseForm(form);
        capacidadeMa = undefined;
    } else if (capacidadeMax.value && !capacidadeMin.value) {
        form = `matchingSala && CapacidadeNormal <= capacidadeMax ` + formC;
        conditionFunction = parseForm(form);
        capacidadeMi = undefined;
    } else {
        form = `matchingSala` + formC;
        conditionFunction = parseForm(form);
        capacidadeMi = capacidadeMa = undefined;
    }

    filteredData.forEach((row) => {
        let salas = [];
        const filteredSalas = [];
        const startTime = row.HoraInicioDaAula;
        const endTime = row.HoraFimDaAula;
        const date = row.DataDaAula;

        salas = getAvailableClassrooms(date, startTime, endTime);
        salas.forEach((sala) => {
            const matchingSala = classroomsData.find((classroom) => classroom.NomeSala === sala);
            let match = matchingSala;
            let capacidadeNormal = matchingSala.CapacidadeNormal;
            let caract = matchingSala[carateristicaSala];

            if (conditionFunction(match, capacidadeNormal, capacidadeMi, capacidadeMa, caract))
                filteredSalas.push(sala);
        });
        row.salasDisponiveis = filteredSalas;
    });

    table.redraw();
    table.setData(filteredData);
}

function parseForm(form) {
    return new Function('matchingSala', 'CapacidadeNormal', 'capacidadeMin', 'capacidadeMax', 'caract', `return ${form}`);
}

function getAvailableClassrooms(date, startTime, endTime) {
    const classroomsInUse = [];
    const allClassrooms = classroomsData.slice(1).map(classroom => classroom.NomeSala);
    tabledata.forEach((row) => {
        if (row.DataDaAula === date && row.HoraInicioDaAula == startTime && row.HoraFimDaAula == endTime)
            classroomsInUse.push(row.SalaAtribuidaAAula);
    });

    const availableClassrooms = allClassrooms.filter(classroom => !classroomsInUse.includes(classroom));
    return availableClassrooms;
}

function getColumnName(data, targetValue) {
    let firstRow = data[0];
    for (let getcolumnName in firstRow)
        if (firstRow[getcolumnName] === targetValue)
            return getcolumnName;

    return null;
}