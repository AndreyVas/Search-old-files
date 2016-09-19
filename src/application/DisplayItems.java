package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.LinkedList;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DisplayItems 
{
	private LinkedList<FileItems> fileItems;
	
	private final String DOCUMENTS = "Документы";
	private final String DESKTOP = "Рабочий стол";
	private final String OTHER = "Другое";
	
	private final String MORE_THAN_1_YEAR = "Больше года";
	private final String MORE_THAN_2_YEAR = "Больше двух лет";
	private final String MORE_THAN_3_YEAR = "Больше трёх лет";
	private final String MORE_THAN_4_YEAR = "Большое четырёх лет";
	private final String MORE_THAN_5_YEAR = "Большое пяти лет";
	
	private final String prF = "C:\\Program Files";
	private final String pf86 = "C:\\Program Files (x86)";
	private final String pd = "C:\\ProgramData";
	private final String win = "C:\\Windows";
	
	private Path searchPath;
	private int searchTime;
	
	DisplayItems()
	{
		fileItems = new LinkedList<FileItems>();
		searchPath = Paths.get( System.getProperty("user.home")+"\\Documents\\");
		searchTime = 3;
	}
	
	public VBox searchProperties(BorderPane mainPane, SearchEngine searchEngine )
	{
		HBox container = new HBox();
		
		//-------------------------------------------------------

		Label gagLabel = new Label();
		gagLabel.getStyleClass().add("gag");
		gagLabel.setWrapText(true);

		//-------------------------------------------------------		
		
		VBox chooseDirectoryCont = new VBox();
		
		Label cdLabel = new Label("Выберите дирректорию");
		cdLabel.getStyleClass().add("chooseLabel");
		
		String[] chooiceItems = new String[]{ this.DOCUMENTS, this.DESKTOP, this.OTHER};
		
		ChoiceBox<String> cdChoice = new ChoiceBox<String>(FXCollections.observableArrayList(chooiceItems));
		cdChoice.getStyleClass().add("chooseBox");
		cdChoice.setValue(this.DOCUMENTS);
		
		cdChoice.getSelectionModel().selectedIndexProperty().addListener(
			(ObservableValue<? extends Number> ov,
				Number old_val, Number new_val) -> {
					
					if(chooiceItems[new_val.intValue()].equals(this.OTHER))
					{
						DirectoryChooser fileChooser = new DirectoryChooser();
						fileChooser.setTitle("Open Resource File");
				
						File file = fileChooser.showDialog(new Stage());
	
		                if (file != null) 
		                {
		                	gagLabel.setText("Дирректория поиска : " + file.toString());
		                	searchPath = file.toPath();
		                }
					}
					else if(chooiceItems[new_val.intValue()].equals(this.DOCUMENTS))
					{
						searchPath = Paths.get( System.getProperty("user.home")+"\\Documents\\");
						gagLabel.setText(chooiceItems[new_val.intValue()]);
					}
					else if(chooiceItems[new_val.intValue()].equals(this.DESKTOP))
					{
						searchPath = Paths.get( System.getProperty("user.home")+"\\Desktop\\");
					}
				});

		chooseDirectoryCont.getChildren().addAll(cdLabel, cdChoice);
		
		//-------------------------------------------------------
		
		VBox chooseTimeCont = new VBox();
		
		Label ctLabel = new Label("Время последнего изменения");
		ctLabel.getStyleClass().add("chooseLabel");
		
		ChoiceBox<String> ctChoice = new ChoiceBox<String>(FXCollections.observableArrayList(
			    this.MORE_THAN_1_YEAR, this.MORE_THAN_2_YEAR, this.MORE_THAN_3_YEAR, this.MORE_THAN_4_YEAR, this.MORE_THAN_5_YEAR)
			);
		
	
		
		ctChoice.getSelectionModel().selectedIndexProperty().addListener(
				(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) -> {
						searchTime = new_val.intValue() + 1;
					});
		
		ctChoice.getStyleClass().add("chooseBox");
		ctChoice.setValue(this.MORE_THAN_3_YEAR);
		
		chooseTimeCont.getChildren().addAll(ctLabel, ctChoice);
		
		//-------------------------------------------------------
		
		VBox startCont = new VBox();
		
		Button start = new Button("Поиск");
		start.getStyleClass().add("chooseButton");
		
		start.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				if(searchTime != -1 && searchPath != null )
				{
					if(searchPath.toString().equals(prF) || searchPath.toString().equals(pf86) 
							|| searchPath.toString().equals(pd) || searchPath.toString().equals(win))
					{
						InfoWindow.showInfo("Поиск запрещён в папках : " + "\n" + pf86 + "\n" + prF + "\n" + pd + "\n" + win);
					}
					else
					{
						cdChoice.setDisable(true);
						ctChoice.setDisable(true);
						start.setDisable(true);
						
						InfoWindow.showInfo("Запущен поиск, после его завершения будут отображены фаблы, удовлетворяющие критериям. \n"
								+ "Во время поиска программа будет неактивна.");
						
						fileItems = searchEngine.search(searchPath, searchTime);	
						mainPane.setCenter(showFoundedItems(fileItems));
						
						cdChoice.setDisable(false);
						ctChoice.setDisable(false);
						start.setDisable(false);
					}
				}
				else
				{
					InfoWindow.showError("Заполнены не все поля");
				}
			}
	
		});
		
		startCont.getChildren().addAll(start);

		//-------------------------------------------------------
		
		container.getChildren().addAll(chooseDirectoryCont, chooseTimeCont, gagLabel, startCont);

		VBox mainCont = new VBox();
		
		mainCont.getStyleClass().add("itemsHeadCont");
		Separator separator = new Separator();
		separator.setOrientation(Orientation.HORIZONTAL);
		
		mainCont.getChildren().addAll(container , separator, createItemsHead());
		
		return mainCont;
		
	}
	
	public ImageView getIcon(String ext)
	{
		ImageView im;
		
		ext = ext.toLowerCase();
		
		switch(ext)
		{

			case ".doc":
			case ".docx":
			case ".docm":
			case ".dotx":
			case ".dotm":
				im = new ImageView("Images\\Word.png");
				break;
				
			case ".xls":
			case ".xlsx":
			case ".xlsm":
			case ".xltx":
			case ".xltm":
			case ".xlsb":
			case ".xlam":
			case ".xla":
			case ".xlt":
				im = new ImageView("Images\\Excel.png");
				break;
				
			case ".pptx":
			case ".ppt":
			case ".pptm":
			case ".ppsx":
			case ".pps":
			case ".ppsm":
			case ".potx":
			case ".pot":
			case ".potm":
			case ".ppam":
			case ".ppa":
				im = new ImageView("Images\\PowerPoint.png");
				break;
				
			case ".mdb":
			case ".accdb":
				im = new ImageView("Images\\Access.png");
				break;
				
			case ".vsd":
			case ".vss":
			case ".vst":
			case ".vdx":
			case ".vsx":
			case ".vtx":
			case ".vsl":
			case ".vsdx":
			case ".vsdm":
				im = new ImageView("Images\\Visio.png");
				break;
	
			case ".pst":
			case ".ost":
				im = new ImageView("Images\\Outlook.png");
				break;

			case ".mpp":
				im = new ImageView("Images\\Project.png");
				break;

			case ".wav":
			case ".mid":
			case ".mp3":
				im = new ImageView("Images\\Audio.png");
				break;
				
			case ".mkv":
			case ".mov":
			case ".mp4":
			case ".mpeg":
			case ".mpg":
			case ".vcd":
				im = new ImageView("Images\\Video.png");
				break;
				
			case ".pdf":
				im = new ImageView("Images\\Pdf.png");
				break;
				
			case ".jpg":
			case ".jpeg":
			case ".png":
			case ".bmp":
			case ".gif":
				im = new ImageView("Images\\Image.png");
				break;
				
			default:
				im = new ImageView("Images\\File.png");
				
		}
		return im;
	}
	
	public HBox createDisplayItem(FileItems fileItem)
	{
		HBox item = new HBox();
		item.getStyleClass().add("item");
		
		Label icon = new Label();
		icon.setGraphic(getIcon(fileItem.getExt()));
		icon.getStyleClass().add("icon");
		
		Label name = new Label(fileItem.getName());
		name.getStyleClass().add("name");
		name.setTooltip(new Tooltip(fileItem.getName()));
		
		Label path = new Label(fileItem.getPath().toString());
		path.getStyleClass().add("path");
		path.setTooltip(new Tooltip(fileItem.getPath().toString()));
		
		/*
		Calendar cLAD = Calendar.getInstance();
		cLAD.setTimeInMillis(fi.getLastAccessDate());
		Label lastAccessDate = new Label(String.valueOf(cLAD.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(cLAD.get(Calendar.MONTH)) + "/" + String.valueOf(cLAD.get(Calendar.YEAR))); 
		lastAccessDate.getStyleClass().add("lastAccessDate");
		*/
		Calendar cLMD = Calendar.getInstance();
		cLMD.setTimeInMillis(fileItem.getLastModifiedDate());
		Label lastModifiedDate = new Label(String.valueOf(cLMD.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(cLMD.get(Calendar.MONTH) + 1) + "/" + String.valueOf(cLMD.get(Calendar.YEAR))); 
		lastModifiedDate.getStyleClass().add("lastModifiedDate");
		
		Calendar cCD = Calendar.getInstance();
		cCD.setTimeInMillis(fileItem.getCreationDate());
		Label creationDate = new Label(String.valueOf(cCD.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(cCD.get(Calendar.MONTH) + 1) + "/" + String.valueOf(cCD.get(Calendar.YEAR)));
		creationDate.getStyleClass().add("creationDate");

		Button showInFolder = new Button("Открыть папку");
		showInFolder.getStyleClass().add("showInFolder");
		
		showInFolder.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				Desktop desktop = null;
				if (Desktop.isDesktopSupported()) {
				    desktop = Desktop.getDesktop();
				}
				
				try 
				{
				    desktop.open(new File(fileItem.getPath().toString()));
				    
				} catch (IOException ioe) 
				{
				    InfoWindow.showError("Ошибка ввода\\вывода");
				}
			}
		});

		Button delete = new Button("Удалить");
		delete.getStyleClass().add("delete");
		
		delete.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				try 
				{
				    Files.delete(Paths.get(fileItem.getPath().toString() + "\\" + fileItem.getName()));
				    item.setDisable(true);
				} 
				catch (NoSuchFileException x) 
				{
					InfoWindow.showError("Удаляемый файл не существует : " + path.toString());
				} 
				catch (DirectoryNotEmptyException x) 
				{
					InfoWindow.showError("Дирректория не пуста : " + path.toString());
				}
				catch (IOException x) 
				{
					InfoWindow.showError(x.toString());
				}
			}
		});
		
		item.getChildren().addAll(icon, name, path, lastModifiedDate, creationDate, showInFolder, delete);
		
		return item;
	}
	
	public HBox createItemsHead()
	{
		HBox head = new HBox();
		
		Label lIcon = new Label();
		lIcon.getStyleClass().add("icon");
		Label lName = new Label("Имя файла");
		lName.getStyleClass().add("name");
		Label lPath = new Label("Путь до файла");
		lPath.getStyleClass().add("path");
		Label lLastModifiedDate = new Label("Изменен");
		lLastModifiedDate.getStyleClass().add("lastModifiedDate");
		Label lCreationDate = new Label("Cоздан");
		lCreationDate.getStyleClass().add("creationDate");
		Label lShowInFolder = new Label();
		lShowInFolder.getStyleClass().add("showInFolderLabel");
		Label lDelete = new Label();
		lDelete.getStyleClass().add("deleteLabel");
		
		head.getChildren().addAll(lIcon, lName, lPath, lLastModifiedDate, lCreationDate, lShowInFolder, lDelete);
		
		return head;
	}
	
	public ScrollPane showFoundedItems(LinkedList<FileItems> fileItems)
	{
		ScrollPane contentPane = new ScrollPane();
		contentPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		contentPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		contentPane.getStyleClass().add("scrollPane");
		
		VBox items = new VBox();

		for(FileItems fi : fileItems)
		{
			HBox item = createDisplayItem(fi);
			items.getChildren().add(item);
		}
		
		contentPane.setContent(items);

		return contentPane;
	}
}
