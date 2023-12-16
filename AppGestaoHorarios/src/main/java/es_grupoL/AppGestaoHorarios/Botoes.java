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
 * The {@code Botoes} class represents the main application window for schedule
 * management. It provides options for loading schedule data from local or
 * remote sources, visualizing the schedule in a web browser, and applying
 * filters. Users can load schedule data from either a local CSV file or a
 * remote URL. The loaded schedule can be converted to HTML format for viewing
 * in a web browser. Filters can be applied to the schedule data, and quality
 * analysis criteria can be defined by the user.
 * 
 * @version 1.6
 */
public class Botoes extends JFrame {

	private static Botoes INSTANCE = null;
	private JTextField urlRemoto; // URL remoto a ser inserido pelo utilizador, para ficheiros remotos
	private String filePath; // Guardar o caminho do ficheiro para usar nas funções da classe FileToTable
	private List<String> mappedColumnsInOrder = new ArrayList<>(); // Lista ordenada dos campos mapeados. Ex: mappedColumnsInOrder.get(0) = coluna 1
	private FileToTable userFileToTable;	// Ficheiro do horário
	private Map<Integer, ArrayList<String>> userFileMap;
	private ConfigApp configuracao_aplicacao;
	private JComboBox<String> comboFormatoData; // JComboBox para o formato de data
	private JComboBox<String> comboFormatoHora; // JComboBox para o formato de hora
	private String[] formatoDataVisual = {"Dia/Mês/Ano", "Mês/Dia/Ano"};
	private String[] formatoDataReal = {"%d/%m/%Y", "%m/%d/%Y",};
	private String[] formatoHoraVisual = {"Horas(24H):Minutos:Segundos", "Horas(12H):Minutos:Segundos (PM ou AM)"};
	private String[] formatoHoraReal = {"%H:%M:%S","%I:%M:%S %p"};

	public static Botoes getInstance() {
		if (INSTANCE == null) {
			ConfigApp ca = new ConfigApp();
			INSTANCE = new Botoes(ca);
		}
		return INSTANCE;
	}

	/**
	 * Constructs the main application window, initializes components, and sets up
	 * event listeners.
	 */
	private Botoes(ConfigApp ca) {
		this.configuracao_aplicacao = ca;
		//System.out.println(classroomsFileMap.get(2));

		final JCheckBox checkBoxLocal = new JCheckBox("Ficheiro Local");
		final JCheckBox checkBoxRemoto = new JCheckBox("Ficheiro remoto");
		this.urlRemoto = new JTextField(20); 
		this.urlRemoto.setToolTipText("Inserir o url do ficheiro remoto");
		JButton fileButton = new JButton("Selecione o ficheiro local");
		JButton websiteButton = new JButton("Abre Website");
		JButton MappingButton = new JButton("Confirmar mapeamento");
		JButton CancellingButton = new JButton("Apagar ficheiro");
		ButtonGroup checkBoxes = new ButtonGroup();
		List<SelectButton> listOfSelects = SelectButton.listOfSelectButtons();

		// JComboBox para o formato de data/hora
		comboFormatoData = new JComboBox<>(formatoDataVisual);
		comboFormatoData.setSelectedIndex(0);
		comboFormatoHora = new JComboBox<>(formatoHoraVisual);
		comboFormatoHora.setSelectedIndex(0);

		urlRemoto.setVisible(false);

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
		checkBoxLocal.setVisible(false);
		checkBoxLocal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileButton.setText("Selecione o ficheiro local");
				urlRemoto.setEnabled(false);
			}
		});
		checkBoxRemoto.setVisible(false);
		checkBoxRemoto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileButton.setText("Download do ficheiro remoto");
				urlRemoto.setEnabled(true);
			}
		});

		// Ações do botão Definir formato Data/Hora
		comboFormatoData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Obter o formato selecionado do JComboBox
				int indiceSelecionado = comboFormatoData.getSelectedIndex();
				String formatoSelecionado = formatoDataReal[indiceSelecionado];
				configuracao_aplicacao.setFormatoData(formatoSelecionado);
				configuracao_aplicacao.salvarConfiguracao();
				comboFormatoHora.setVisible(true);
				System.out.println("Novo formato definido: " + formatoDataVisual[indiceSelecionado]);
			}
		});

		comboFormatoHora.setVisible(false);
		comboFormatoHora.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Obter o formato selecionado do JComboBox
				int indiceSelecionado = comboFormatoHora.getSelectedIndex();
				String formatoSelecionado = formatoHoraReal[indiceSelecionado];
				configuracao_aplicacao.setFormatoHora(formatoSelecionado);
				configuracao_aplicacao.salvarConfiguracao();
				checkBoxLocal.setVisible(true);
				checkBoxRemoto.setVisible(true);
				urlRemoto.setVisible(true);
				fileButton.setVisible(true);
				System.out.println("Novo formato definido: " + formatoHoraVisual[indiceSelecionado]);
			}
		});

		// Ações do botão abir/carregar ficheiro
		fileButton.setVisible(false);
		fileButton.setPreferredSize(new Dimension(200, 100));
		fileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkBoxLocal.isSelected()) {
					selectLocalFile();
				} else if (checkBoxRemoto.isSelected()) {
					selectRemoteFile();
				} else {
					String erroBoxes = "Tem de selecionar pelo menos uma das caixas";
					JOptionPane.showMessageDialog(Botoes.this, erroBoxes, "Erro", JOptionPane.INFORMATION_MESSAGE);

					System.out.println("Selecione uma das opções para carregar o ficheiro");
				}

				if (filePath != null && !filePath.isBlank()) {
					File file = new File(filePath);
					userFileToTable = new FileToTable(file, ca);
					userFileMap = userFileToTable.readCSV();
					CancellingButton.setVisible(true);

					if (userFileToTable.isColumnsMapped()) // Se o mapeamento é automático (ficheiro tem header) então o botão do website aparece
						websiteButton.setVisible(true);
					else {	
						if(configuracao_aplicacao.getFicheiroConf() != null) {


							int opcao = JOptionPane.showOptionDialog(null, "Deseja utilizar as definições usadas anteriormente?","Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Sim", "Não"}, "Sim");
							if(opcao == JOptionPane.NO_OPTION) {
								for (SelectButton selectButton : listOfSelects) {	// Caso contrário tem de fazer o mapeamento manual
									selectButton.setVisible(true);
									MappingButton.setVisible(true);
								}
							} else if(opcao == JOptionPane.YES_OPTION) {
								configuracao_aplicacao.carregarConfiguracao();
								websiteButton.setVisible(true);
							} else {
								System.out.println("Não clicou em nehuma das opções, tente de novo");
							}
						} else {
							for (SelectButton selectButton : listOfSelects) {	// Caso contrário tem de fazer o mapeamento manual
								selectButton.setVisible(true);
								MappingButton.setVisible(true);
							}
						}
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

		//Ações do botão de apagar ficheiro
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
				userFileToTable = null;
				mappedColumnsInOrder = null;
			}
		});

		// Acões do botão de confirmar mapeamento
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
					userFileToTable.setColumnsMapped(true);
					configuracao_aplicacao.setCamposMapeamento(mappedColumnsInOrder);
					configuracao_aplicacao.salvarConfiguracao();
					System.out.println(mappedColumnsInOrder);
				} else {
					String erroCampos = "Tem de selecionar todos campos";
					JOptionPane.showMessageDialog(Botoes.this, erroCampos, "Erro", JOptionPane.INFORMATION_MESSAGE);
					System.out.println("Tem de selecionar todos os campos");
				}
			}
		});

		setLayout(new FlowLayout());
		add(comboFormatoData);
		add(comboFormatoHora);
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
		this.userFileToTable.createHTML(this.userFileMap);
		Desktop desk = Desktop.getDesktop();
		String filePath = System.getProperty("user.dir") + File.separator + FileToTable.MAIN_FOLDER + "ScheduleApp.html";
		filePath = filePath.replace("\\", "/");
		System.out.println("Open website: "+filePath);
		
		try {
			desk.browse(new java.net.URI("file://" + filePath));
		} catch (IOException | URISyntaxException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Allows the user to select a local CSV file using a file chooser dialog.
	 */
	private void selectLocalFile() {
		JFileChooser fc = new JFileChooser();
		int f = fc.showOpenDialog(null);

		if (f == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();

			if (selectedFile.getAbsolutePath().toLowerCase().endsWith(".csv")) {
				filePath = selectedFile.getAbsolutePath();
				System.out.println("Ficheiro CSV selecionado: " + filePath);
			} else {
				String erroFicheiro = "Por favor selecione um ficheiro .csv";
				JOptionPane.showMessageDialog(Botoes.this, erroFicheiro, "Erro", JOptionPane.INFORMATION_MESSAGE);
				System.out.println("Este ficheiro remoto não é do tipo .csv");
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
			String erroFicheiro = "Por favor selecione um ficheiro .csv";
			JOptionPane.showMessageDialog(Botoes.this, erroFicheiro, "Erro", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Este ficheiro remoto não é do tipo .csv");
		}
		fi.deleteOnExit(); // Apagar ficheiro temporário
	}

	public Map<Integer, ArrayList<String>> getUserFileMap() {
		return this.userFileMap;
	}

	public List<String> getMappedColumnsInOrder() {
		return mappedColumnsInOrder;
	}

	public FileToTable getUserFileToTable() {
		return userFileToTable;
	}

	/**
	 * The main method that initializes and runs the application.
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ConfigApp ca = new ConfigApp();
				new Botoes(ca);
			}
		});
	}
}
