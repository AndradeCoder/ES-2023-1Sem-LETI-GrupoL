package es_grupoL.AppGestaoHorarios;

import java.util.ArrayList;
import java.util.List;

public enum ColunasHorario {
	Curso("Curso"), UnidadeCurricular("Unidade Curricular"), Turno("Turno"), Turma("Turma"),
	InscritosNoTurno("Inscritos no turno"), DiaDaSemana("Dia da semana"), HoraInicioDaAula("Hora início da aula"),
	HoraFimDaAula("Hora fim da aula"), DataDaAula("Data da aula"),
	CaracteristicasDaSalaPedidaParaAAula("Características da sala pedida para a aula"),
	SalaAtribuídaÀAula("Sala atribuída à aula");

	private final String columnName; // Não faz diferença ser final aqui, apenas serve para indicar que não se deve
										// mudar

	ColunasHorario(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public static List<ColunasHorario> constantsList(){
		List<ColunasHorario> columnsList = new ArrayList<>();
		ColunasHorario[] columnsArray = ColunasHorario.values();

		for (int i = 0; i < columnsArray.length; i++) {
			columnsList.add(columnsArray[i]);
		}
		return columnsList;
	}

	public static List<String> valuesList() {
		List<String> columnsList = new ArrayList<>();
		ColunasHorario[] columnsArray = ColunasHorario.values();

		for (int i = 0; i < columnsArray.length; i++) {
			columnsList.add(columnsArray[i].getColumnName());
		}
		return columnsList;
	}

}
