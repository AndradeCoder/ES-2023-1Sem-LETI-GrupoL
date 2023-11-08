package es_grupoL.AppGestaoHorarios;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFileChooser;
import java.awt.Desktop;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.*; 

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
				Desktop desk = Desktop.getDesktop();
				String filepath = System.getProperty("user.dir") + "/SalasDeAulaPorTiposDeSala.html";
				filepath = filepath.replace("\\","/");
				try {
					desk.browse(new java.net.URI("file://" + filepath));
				} catch (IOException | URISyntaxException e1) {e1.printStackTrace();}
				System.out.println("Working Directory = " + System.getProperty("user.dir"));
			}
		});

		add(fileButton);
		add(websiteButton);

		setTitle("JFrame");
		setSize(500, 150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

    public static void main(String[] args) {
        new Botoes();
    }
}
