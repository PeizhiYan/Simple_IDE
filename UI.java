package COSC1047.EditorPlus;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *  User Interface of EditorPlus
 **/
public class UI {

    private static File filePath = null;
    private static TextArea textArea = new TextArea(); // text area
    private static Label wordCountLable = new Label("word count: "); // show word counter
    private static Label lineCountLable = new Label("\tline count: "); // show word counter
    private static Label dateLable = new Label();
    private static HBox bottomBar = new HBox(); // used to hold bottom labels
    private static String fontName = "Times New Roman";
    private static Font font = new Font(fontName,20);// default font
    private static boolean italic = false;
    private static boolean bold = false;
    private static boolean dark = false;

    // FIXME: 2017-04-03
    UI(){
        textArea.setPrefRowCount(100);
        textArea.setFont(font);

        bottomBar.getChildren().addAll(wordCountLable,lineCountLable,dateLable);

        /** refresh time */
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            Date date = new Date();
            dateLable.setText("\t\t\tDate: "+date.toString());
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        /** keyboard event listeners */
        textArea.setOnKeyPressed(e -> {
            /** refresh counter content once user press a key */
            wordCountLable.setText("words: "+getWordCount());
            lineCountLable.setText("\tlines: "+getLineCount());
            /** apply shortcuts */
            // Apply-Shortcut shortcut
            if (e.getCode() == KeyCode.COMMAND){
                String code = textArea.getText();
                String tmp = applyShortcut(code);
                if (tmp != null){
                    textArea.setText(tmp);
                }
                System.out.println("ShortCut applied!");
            }
        });

    }// initialize essential

    // FIXME: 2017-04-03
    /**
     * initialize User Interface
     * return a VBox as main pane
     * */
    public static VBox UIInitializer(){

        /* menu and items */
        // file menu
        MenuItem newFile = new MenuItem("New");
        MenuItem saveAsFile = new MenuItem("Save As");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem openFile = new MenuItem("Open");
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newFile,saveFile,saveAsFile,openFile);
        // edit menu
        MenuItem bigger = new MenuItem("Bigger");
        MenuItem smaller = new MenuItem("Smaller");
        MenuItem darkMode = new MenuItem("Dark Mode");
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(bigger,smaller,darkMode);
        // about menu
        MenuItem authorItem = new MenuItem("Author");
        MenuItem sourceCode = new MenuItem("Source Code");
        MenuItem version = new MenuItem("Version");
        Menu aboutMenu = new Menu("About");
        aboutMenu.getItems().addAll(authorItem,sourceCode,version);
        // compiler menu
        MenuItem javaCompiler = new MenuItem("Run Java Code");
        MenuItem cCompiler = new MenuItem("Run C++ Code");
        Menu compilerMenu = new Menu("Run");
        compilerMenu.getItems().addAll(javaCompiler,cCompiler);

        /* menu bar */
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,editMenu,compilerMenu,aboutMenu);

        /* tool bar */
        HBox toolBar = new HBox();
        Button font_bigger = new Button("+");
        Button font_smaller = new Button("-");
        Button font_italic = new Button("Italic");
        Button font_bold = new Button("Bold");
        Button run_tensorflow = new Button("Run TensorFlow Program");
        toolBar.getChildren().addAll(font_bigger,font_smaller,font_italic,font_bold,run_tensorflow);

        /* main container */
        VBox container = new VBox();
        container.getChildren().addAll(menuBar,toolBar,textArea,bottomBar);

        /** menu handlers */
        saveAsFile.setOnAction(new SaveAsHandler());
        openFile.setOnAction(new OpenHandler());
        saveFile.setOnAction(new SaveHandler());
        bigger.setOnAction(e -> {
            Font tmp = new Font(fontName, font.getSize()+6);
            font = tmp;
            textArea.setFont(font);
        });
        smaller.setOnAction(e -> {
            if (font.getSize() > 10){
                Font tmp = new Font(fontName, font.getSize()-6);
                font = tmp;
                textArea.setFont(font);
            }
        });
        darkMode.setOnAction(e -> {
            if (dark == false){
                textArea.setStyle("-fx-text-fill: WHITE; -fx-control-inner-background: BLACK;");
                dark = true;
            }
            else {
                textArea.setBackground( new Background( new BackgroundFill( Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY ) ) );
                textArea.setStyle("-fx-text-fill: BLACK; -fx-control-inner-background: WHITE;");
                dark = false;
            }
        });
        javaCompiler.setOnAction(e -> {
            saveJavaFile();
            RunJava.compile(filePath);
        });
        cCompiler.setOnAction(e -> {
            saveCppFile();
            RunC.compile(filePath);
        });
        authorItem.setOnAction(e -> {
            author();
        });

        /** tool bar handlers */
        font_bigger.setOnMouseClicked(e -> {
            Font tmp = new Font(fontName, font.getSize()+1);
            font = tmp;
            textArea.setFont(font);
        });
        font_smaller.setOnMouseClicked(e -> {
            if (font.getSize() > 10){
                Font tmp = new Font(fontName, font.getSize()-1);
                font = tmp;
                textArea.setFont(font);
            }
        });
        font_bold.setOnMouseClicked(e -> {
            if (bold == false){
                Font tmp = font.font(font.getFamily(),FontWeight.BOLD,font.getSize());
                font = tmp;
                textArea.setFont(font);
                bold = true;
            }
            else {
                Font tmp = font.font(font.getFamily(),FontWeight.NORMAL,font.getSize());
                font = tmp;
                textArea.setFont(font);
                bold = false;
            }
        });
        font_italic.setOnMouseClicked(e -> {
            if (italic == false){
                Font tmp = font.font(font.getFamily(),FontPosture.ITALIC,font.getSize());
                font = tmp;
                textArea.setFont(font);
                italic = true;
            }
            else {
                Font tmp = font.font(font.getFamily(),FontPosture.REGULAR,font.getSize());
                font = tmp;
                textArea.setFont(font);
                italic = false;
            }
        });
        run_tensorflow.setOnAction(e -> {
            saveCppFile();
            RunTensorflowC.compile(filePath);
        });

        return container;
    }// end of UI initializer

    // FIXME: 2017-04-03
    /** get word count */
    private static int getWordCount(){
        String tmp = getText();
        String[] tmpArray = tmp.split("[ ,\n]");
        return tmpArray.length;
    }

    // FIXME: 2017-04-03
    /** get line count */
    private static int getLineCount(){
        String tmp = getText();
        String[] tmpArray = tmp.split("\n");
        return tmpArray.length;
    }

    // FIXME: 2017-04-03
    /** get text as a String */
    private static String getText(){
        return textArea.getText();
    }

    // FIXME: 2017-04-04
    /** save java file */
    private static void saveJavaFile(){
        if (filePath == null || filePath.exists() == false){
            FileChooser fileChooser = new FileChooser();
            Stage stage = new Stage();

            //Set extension filter
            FileChooser.ExtensionFilter _class = new FileChooser.ExtensionFilter("Java Class files (*.java)", "*.java");
            fileChooser.getExtensionFilters().addAll(_class);

            //Get path
            fileChooser.setTitle("Choose a location");
            filePath = fileChooser.showSaveDialog(stage);

            //write into file
            try(PrintWriter fileOut = new PrintWriter(filePath)) {
                fileOut.print(getText());
            }
            catch (FileNotFoundException ex){
                try(PrintWriter fileOut = new PrintWriter(filePath)) {
                    fileOut.print(getText());
                }
                catch (FileNotFoundException ex2){}
            }
        }
        else {
            //write into file
            try(PrintWriter fileOut = new PrintWriter(filePath)) {
                fileOut.print(getText());
            }
            catch (FileNotFoundException ex){
            }
        }
    }

    // FIXME: 2017-04-06
    /** save cpp file */
    // FIXME: 2017-04-04
    /** save java file */
    private static void saveCppFile(){
        if (filePath == null || filePath.exists() == false){
            FileChooser fileChooser = new FileChooser();
            Stage stage = new Stage();

            //Set extension filter
            FileChooser.ExtensionFilter _class = new FileChooser.ExtensionFilter("C++ files (*.cpp)", "*.cpp");
            fileChooser.getExtensionFilters().addAll(_class);

            //Get path
            fileChooser.setTitle("Choose a location");
            filePath = fileChooser.showSaveDialog(stage);

            //write into file
            try(PrintWriter fileOut = new PrintWriter(filePath)) {
                fileOut.print(getText());
            }
            catch (FileNotFoundException ex){
                try(PrintWriter fileOut = new PrintWriter(filePath)) {
                    fileOut.print(getText());
                }
                catch (FileNotFoundException ex2){}
            }
        }
        else {
            //write into file
            try(PrintWriter fileOut = new PrintWriter(filePath)) {
                fileOut.print(getText());
            }
            catch (FileNotFoundException ex){
            }
        }
    }

    // FIXME: 2017-04-03
    /** Save as handler */
    static class SaveAsHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e){
                FileChooser fileChooser = new FileChooser();
                Stage stage = new Stage();

                //Set extension filter
                FileChooser.ExtensionFilter _txt = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                FileChooser.ExtensionFilter _html = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
                FileChooser.ExtensionFilter _cpp = new FileChooser.ExtensionFilter("C++ files (*.cpp)", "*.cpp");
                FileChooser.ExtensionFilter _class = new FileChooser.ExtensionFilter("Java Class files (*.java)", "*.java");
                fileChooser.getExtensionFilters().addAll(_txt,_html,_class,_cpp);

                //Get path
                fileChooser.setTitle("Choose a location");
                filePath = fileChooser.showSaveDialog(stage);

                saveFile(filePath);
        }
        /** save file under specified path */
        private static void saveFile(File filePath){
            try(PrintWriter fileOut = new PrintWriter(filePath)) {

            }
            catch (FileNotFoundException ex){
                try(PrintWriter fileOut = new PrintWriter(filePath)) {
                    fileOut.print(getText());
                }
                catch (FileNotFoundException ex2){}
            }
        }
    }

    // FIXME: 2017-04-03
    /** Save handler */
    static class SaveHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e){
            if (filePath.exists()){
                saveFile(filePath);
            }
        }
        /** save file under specified path */
        private static void saveFile(File filePath){
            try(PrintWriter fileOut = new PrintWriter(filePath)) {
                fileOut.print(getText());
            }
            catch (FileNotFoundException ex){}
        }
    }

    // FIXME: 2017-04-03
    /** Open file handler */
    static class OpenHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e){
            /* save old one */
            //SaveAsHandler saveAsHandler = new SaveAsHandler();
            //saveAsHandler.handle(e);

            /* choose new file */
            Stage stage = new Stage();
            FileChooser fileChooser = new FileChooser();
            filePath = fileChooser.showOpenDialog(stage);

            /* open chosen file */
            openFile();
        }

        private void openFile(){
           /*
            String OS = System.getProperty("os.name").toLowerCase();// get Operating System information
            String nextLine = "\n";
            if (isWindows(OS)){
                nextLine = "\r\n";
            }
            */
            String tmp = "";
            try(Scanner fileIn = new Scanner(filePath)){
                while (fileIn.hasNext()){
                    tmp = tmp + fileIn.nextLine() + "\n";
                }
            }
            catch (FileNotFoundException ex){

            }
            textArea.setText(tmp);
        }

        /** detect Windows operating system */
        private boolean isWindows(String OS) {
            return (OS.indexOf("win") >= 0);
        }
    }

    // FIXME: 2017-04-03
    /** author information Stage */
    private static void author(){
        VBox contentBox = new VBox();
        Label line_1 = new Label("\n\n\tAuthor: Matthew Yan");
        Label line_2 = new Label("\tEmail: pyan@algomau.ca");
        Label line_3 = new Label("\t");
        contentBox.getChildren().addAll(line_1,line_2,line_3);
        Scene scene = new Scene(contentBox);
        Stage tmp = new Stage();
        tmp.setTitle("About Author");
        tmp.setMinHeight(200);
        tmp.setMinWidth(200);
        tmp.setMaxHeight(200);
        tmp.setMaxWidth(200);
        tmp.setScene(scene);
        tmp.show();
    }

    // FIXME: 2017-04-03
    /** New file handler */
    static class NewHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e){
            /* save old one */
            SaveAsHandler saveAsHandler = new SaveAsHandler();
            saveAsHandler.handle(e);

            /* create a new file */
            textArea.setText("");
        }
    }

    // FIXME: 2017-04-04
    /** Keyboard event handler */
    private static String applyShortcut(String code){
        return RunJava.shortcuts(code);
    }

}// end of UI class
