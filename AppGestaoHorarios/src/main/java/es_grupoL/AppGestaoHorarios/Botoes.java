package es_grupoL.AppGestaoHorarios;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFileChooser;

public class Botoes extends JFrame {

	public Botoes() {
		setLayout(new FlowLayout());

		JButton fileButton = new JButton("Carregar ficheiro");
		JButton websiteButton = new JButton("Abre Website");

		fileButton.setPreferredSize(new Dimension(200, 100));
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int f = fc.showOpenDialog(null);
				if (f == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					String filePath = selectedFile.getAbsolutePath();
					System.out.println(filePath);
					if (filePath.toLowerCase().endsWith(".csv")) {
						System.out.println("Ficheiro CSV selecionado: " + filePath);
					} else {
						System.out.println("Por favor selecione um ficheiro .csv");
					}
				}
			}
		});

		websiteButton.setPreferredSize(new Dimension(200, 100));
		websiteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("funciona");
			}
		});

		add(fileButton);
		add(websiteButton);

		setTitle("JFrame");
		setSize(500, 150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

//    public static void main(String[] args) {
//        new Botoes();
//    }
}
