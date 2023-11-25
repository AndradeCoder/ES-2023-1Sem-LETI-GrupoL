package es_grupoL.AppGestaoHorarios;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SelectButton extends JButton {
	private List<JMenuItem> listOfOptions = new ArrayList<>();
	private JPopupMenu popupMenu = new JPopupMenu();
	private boolean selectedAtLeastOnce;


	public SelectButton(String text, List<String> options) {
		super(text);
		init(options);
		addActionListener(createActionListener());
	}

	public boolean isSelectedAtLeastOnce() {
		return selectedAtLeastOnce;
	}

	public void setSelectedAtLeastOnce(boolean selectedAtLeastOnce) {
		this.selectedAtLeastOnce = selectedAtLeastOnce;
	}

	public static boolean checkAllButtonsSelectedAtLeastOnce(List<SelectButton> list) {
		for(SelectButton sb : list)
			if(!sb.isSelectedAtLeastOnce())
				return false;
		return true;
	}

	public static List<SelectButton> listOfSelectButtons(){
		List<SelectButton> list = new ArrayList<>();
		List<String> columnOptions = ColunasHorario.valuesList();

		for (int i = 0; i < columnOptions.size(); i++) {
			SelectButton sb = new SelectButton((i+1)+ "ª Coluna", columnOptions);
			list.add(i,sb);
		}
		return list;
	}

	private void init(List<String> options) {
		for (int i = 0; i < options.size(); i++) {
			JMenuItem option = new JMenuItem(options.get(i));
			listOfOptions.add(option);
			popupMenu.add(option);
		}
	}

	protected ActionListener createActionListener() { // protected ou public?? Descobrir depois
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popupMenu.show(SelectButton.this, 0, SelectButton.this.getHeight());
			}
		};
	}

	public void addOptionClickListener(ActionListener listener, int index) {
		if (index >= 0 && index < listOfOptions.size()) {
			listOfOptions.get(index).addActionListener(listener);
		}
	}

	
}


