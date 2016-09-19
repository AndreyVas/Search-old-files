package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application 
{
	
	private SearchEngine searchEngine;
	private DisplayItems displayItems;
	
	BorderPane mainPane;
	ScrollPane contentPane;
	
	private final double MAIN_DISPLAY_WIDTH = 845;
	private final double MAIN_DISPLAY_HEIGHT = 500;
	
	@Override
	public void start(Stage primaryStage) 
	{
		
		try 
		{
			
			primaryStage.getIcons().add(new Image("Images/FileIcon.png"));
			
			searchEngine = new SearchEngine();
			displayItems = new DisplayItems();

			
			mainPane = new BorderPane();
			
			Scene scene = new Scene(mainPane,this.MAIN_DISPLAY_WIDTH,this.MAIN_DISPLAY_HEIGHT);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();

			mainPane.setTop(displayItems.searchProperties(mainPane, searchEngine));
			mainPane.setCenter(new Label("Нет записей для отображения"));
		} 
		catch(Exception e) 
		{
			InfoWindow.showError("Ошибка :блин " + e);
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) 
	{
		launch(args);
	}
}
