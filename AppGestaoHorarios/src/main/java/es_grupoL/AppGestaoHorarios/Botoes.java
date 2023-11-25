package es_grupoL.AppGestaoHorarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * The `Botoes` class represents the main application window for schedule
 * management. It provides options for loading schedule data from local or
 * remote sources, visualizing the schedule in a web browser, and applying
 * filters. Users can load schedule data from either a local CSV file or a
 * remote URL. The loaded schedule can be converted to HTML format for viewing
 * in a web browser. Filters can be applied to the schedule data, and quality
 * analysis criteria can be defined by the user.
 */
public class Botoes extends JFrame {
	private JTextField urlRemoto; // URL remoto a ser inserido pelo utilizador, para ficheiros remotos
	private String filePath; // Guardar o caminho do ficheiro para usar nas funções da classe FileToTable
	private List<String> mappedColumnsInOrder = new ArrayList<>(); // Lista ordenada dos campos mapeados. Ex: mappedColumnsInOrder.get(0) = coluna 1
	private FileToTable userFileToTable;	// Ficheiro do horário
	private Map<Integer, ArrayList<String>> userFileMap;

	/**
	 * Constructs the main application window, initializes components, and sets up
	 * event listeners.
	 */
	public Botoes() {
		//System.out.println(classroomsFileMap.get(2));
		
		final JCheckBox checkBoxLocal = new JCheckBox("Ficheiro Local");
		final JCheckBox checkBoxRemoto = new JCheckBox("Ficheiro remoto");
		this.urlRemoto = new JTextField(20); 
		this.urlRemoto.setToolTipText("Inserir o url do ficheiro remoto");
		JButton fileButton = new JButton("Selecione o ficheiro local");
		JButton websiteButton = new JButton("Abre Website");
		JButton MappingButton = new JButton("Confirmar mapeamento");
		JButton CancellingButton = new JButton("Cancelar mapeamento");
		ButtonGroup checkBoxes = new ButtonGroup();
		List<SelectButton> listOfSelects = SelectButton.listOfSelectButtons();

		JPanel panel = new JPanel();
		for (SelectButton selectButton : listOfSelects) {
			panel.add(selectButton);
			selectButton.setVisible(false);	// Só vão aparecer se for preciso fazer mapeamento manual 
		}
		
		// Ações dos selects das colunas
		for (SelectButton selectButton : listOfSelects) {
			for (int i = 0; i < ColunasHorario.valuesList().size(); i++) {
				int index = i;
				selectButton.addOptionClickListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectButton.setText(ColunasHorario.valuesList().get(index));	// Nome da coluna selecionada
						selectButton.setSelectedAtLeastOnce(true);	
					}
				}, index);
			}
		}

		// Ações das checkboxes
		checkBoxes.add(checkBoxLocal);
		checkBoxes.add(checkBoxRemoto);
		checkBoxLocal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileButton.setText("Selecione o ficheiro local");
				urlRemoto.setEnabled(false);
			}
		});
		checkBoxRemoto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileButton.setText("Download do ficheiro remoto");
				urlRemoto.setEnabled(true);
			}
		});

		// Ações do botão abir/carregar ficheiro
		fileButton.setPreferredSize(new Dimension(200, 100));
		fileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkBoxLocal.isSelected()) {
					selectLocalFile();
				} else if (checkBoxRemoto.isSelected()) {
					selectRemoteFile();
				} else {
					System.out.println("Selecione uma das opções para carregar o ficheiro");
				}

				if (filePath != null && !filePath.isBlank()) {
					File file = new File(filePath);
					userFileToTable = new FileToTable(file);
					userFileMap = userFileToTable.readCSV();

					if (userFileToTable.isColumnsMapped()) // Se o mapeamento é automático (ficheiro tem header) então o botão do website aparece
						websiteButton.setVisible(true);
					else {	
						for (SelectButton selectButton : listOfSelects) {	// Caso contrário tem de fazer o mapeamento manual
							selectButton.setVisible(true);
						}
						MappingButton.setVisible(true);
						CancellingButton.setVisible(true);
					}
				}
			}
		});

		// Ações do botão de abrir o website
		websiteButton.setVisible(false); // O botão só ficará visível quando o mapeamento estiver tratado
		websiteButton.setPreferredSize(new Dimension(200, 100));
		websiteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openWebsite();
			}
		});
		
		//Ações do botão de cancelar mapeamento
		CancellingButton.setVisible(false);
		CancellingButton.setBackground(Color.RED);
		CancellingButton.setPreferredSize(new Dimension(CancellingButton.getPreferredSize().width, 50));
		CancellingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CancellingButton.setVisible(false);
				MappingButton.setVisible(false);
				websiteButton.setVisible(false);
				for (int i = 0; i < listOfSelects.size(); i++) {
				    SelectButton selectButton = listOfSelects.get(i);
				    selectButton.setText((i + 1) + "ª Coluna");
				    selectButton.setVisible(false);
				    selectButton.setEnabled(true);
				}
				filePath = "";
			}
		});
		
		// Acões do butão de confirmar mapeamento
		MappingButton.setVisible(false);
		MappingButton.setBackground(Color.GREEN);
		MappingButton.setPreferredSize(new Dimension(MappingButton.getPreferredSize().width, 50));
		MappingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(SelectButton.checkAllButtonsSelectedAtLeastOnce(listOfSelects)) {
					MappingButton.setVisible(false);
					websiteButton.setVisible(true);
					for(SelectButton sb : listOfSelects)
						sb.setEnabled(false);
					
					for (SelectButton selectButton : listOfSelects) {
						mappedColumnsInOrder.add(selectButton.getText());
					}
					userFileToTable.setMappedHeader(mappedColumnsInOrder);
					System.out.println(mappedColumnsInOrder);
				} else {
					System.out.println("Tem de selecionar todos os campos");
				}
			}
		});

		setLayout(new FlowLayout());
		add(checkBoxLocal);
		add(checkBoxRemoto);
		add(this.urlRemoto);
		add(fileButton);
		add(websiteButton);
		add(panel);
		add(MappingButton);
		add(CancellingButton);

		setTitle("JFrame");
		setSize(15000, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Opens the schedule visualization in a web browser using the generated HTML
	 * file.
	 */
	private void openWebsite() {	
		this.userFileToTable.createHTML(userFileMap);
		Desktop desk = Desktop.getDesktop();
		String filepath = System.getProperty("user.dir") + "/ScheduleApp.html";
		filepath = filepath.replace("\\", "/");
		
		try {
			desk.browse(new java.net.URI("file://" + filepath));
		} catch (IOException | URISyntaxException e1) {
			e1.printStackTrace();
		}
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
	}

	/**
	 * Allows the user to select a local CSV file using a file chooser dialog.
	 */
	private void selectLocalFile() {
		JFileChooser fc = new JFileChooser();
		int f = fc.showOpenDialog(null);

		if (f == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();
			filePath = selectedFile.getAbsolutePath();
			if (filePath.toLowerCase().endsWith(".csv")) {
				System.out.println("Ficheiro CSV selecionado: " + filePath);
			} else {
				System.out.println("Por favor selecione um ficheiro .csv");
			}
		}
	}

	/**
	 * Allows the user to select a remote CSV file by providing a URL. If the URL is
	 * from GitHub, it is modified to access the raw content.
	 */
	private void selectRemoteFile() {
		String url = this.urlRemoto.getText();
		String urlOriginal = url;
		File fi = new File("RemoteFile.csv"); // Criar ficheiro temporário para guardar o seu file path em filePath

		if (url.endsWith(".csv")) {
			try {
				if (url.startsWith("https://github.com/") && url.contains("/blob/")) {
					url = url.replace("https://github.com/", "https://raw.githubusercontent.com/");
					url = url.replace("/blob/", "/");
				} else if (url.startsWith("https://github.com/")) {
					url = url.replace("https://github.com/", "https://raw.githubusercontent.com/");
				}

				FileUtils.copyURLToFile(new URL(url), fi);
				filePath = fi.getAbsolutePath();
				System.out.println("Download completo do ficheiro remoto: " + urlOriginal);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Este ficheiro remoto não é do tipo .csv");
		}
		fi.deleteOnExit(); // Apagar ficheiro temporário
	}

	/**
	 * The main method that initializes and runs the application.
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Botoes();
			}
		});
	}
}
