package com.tsi.ui;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public abstract class Ajuda {
	private BorderPane root;
	private Stage primaryStage; 
	private Popup popUp;

	private static Ajuda ajuda;

	/**Define onde onde será mostrada a janela dentro do stage*/
	public static int POSICAO_INFERIOR = 1, POSICAO_SUPERIOR = 2;

	private Ajuda(Stage primaryStage) {
		try {
			popUp = new Popup();
			root = (BorderPane)FXMLLoader.load(getClass().getResource("fxml/ajuda.fxml"));
			this.primaryStage = primaryStage;
			popUp.getContent().add(root);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Ajuda getInstance(final Stage primaryStage){
		return ajuda == null ? new Ajuda(primaryStage){} : ajuda;
	}

	public void exibirAjuda(String texto, String titulo) {
		Label top = (Label) root.getTop(),
				center = (Label) root.getCenter();

		top.setText(titulo);
		center.setText(texto);

		definirPosicao();
		if(!popUp.isShowing()) popUp.show(primaryStage);

	}

	private void definirPosicao() {

		final Window window = popUp.getScene().getWindow();

		window.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
				window.setY((screenBounds.getHeight() - window.getHeight()) / 2);

			}
		});

	}

	public void esconderAjuda() {
		popUp.hide();
	}

	public static void alerta(String titulo, String texto, AlertType tipo) {
		Alert alert = new Alert(tipo);
		alert.setHeaderText(null);
		alert.setTitle(titulo);
		alert.setContentText(texto);
		alert.showAndWait();
	}




}
