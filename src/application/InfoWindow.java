package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InfoWindow 
{
	public static void showInfo(String body)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		
		alert.setTitle("Информация");
		alert.setHeaderText(null);
		alert.setContentText(body);

		alert.showAndWait();
	}
	
	public static void showError(String body)
	{
		Alert alert = new Alert(AlertType.ERROR);
		
		alert.setTitle("Ошибка");
		alert.setHeaderText(null);
		alert.setContentText(body);

		alert.show();
		
	}
}