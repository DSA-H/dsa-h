package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sepm.dsa.model.Region;
import sepm.dsa.service.RegionService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
	    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	    RegionService regionService = (RegionService) ctx.getBean("regionService");

	    System.out.println("Region ID 1:" + regionService.get(1));

	    Region r = new Region();
	    r.setName("Region 1");
	    r.setColor("424242");
	    int id = regionService.add(r);
	    System.out.println("Created: "+id);
	    System.out.println(r.getName());

	    System.out.println("From DB:" + regionService.get(r.getId()));
    }
}
