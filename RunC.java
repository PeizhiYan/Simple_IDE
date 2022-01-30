package COSC1047.EditorPlus;

import java.io.File;
import java.io.IOException;

public class RunC {
    RunC(){}

    // FIXME: 2017-04-04
    /** connect java compiler
     *  only on MacOS
     * */
    public static void compile(File path){

        Runtime runtime = Runtime.getRuntime();
        String cmd1 = "gcc -o "+path.getName().substring(0,path.getName().length()-4)+" "+path.getName();

        String cmd2 = "cd "+path.toString().substring(0,path.toString().length()-path.getName().length());
        String cmd3 = "./"+path.getName().substring(0,path.getName().length()-4);
        String cmd = cmd2+"\n"+cmd1+"\n"+cmd3;

        String applescriptCommand =  "tell application \"Terminal\"\n" +
                "do script \""+cmd+"\"" +
                "activate\n" +
                "end tell";

        String[] args = { "osascript", "-e", applescriptCommand };
        try {
            Process process = runtime.exec(args);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * both java and cpp!!
     * */
    public static String shortcuts(String code){
        String tmp = shortcutsJava(code);
        if (tmp == null){
            return shortcutsCpp(code);
        }
        return tmp;
    }


    // FIXME: 2017-04-04
    /**
     * shortcuts : to complete code
     * */
    public static String shortcutsJava(String code){
        String[] lexemes = code.split("[ ,\n]");
        for (String tmp: lexemes){
            if (tmp.equals("#java")){
                code = "public class ClassName{\n"+
                        "\tpublic static void main(String[] args){\n"+
                        "\t\n"+
                        "\t}\n"+
                        "}";
                return code;
            }
            else if (tmp.matches("(.*)#prt(.*)")){
                code = code.replaceAll("#prt","System.out.printf();");
                return code;
            }
            else if (tmp.matches("(.*)#str(.*)")){
                code = code.replaceAll("#str","String");
                return code;
            }
            else if (tmp.matches("(.*)#scn(.*)")){
                code = code.replaceAll("#scn","Scanner input = new Scanner(System.in);");
                return code;
            }
            else if (tmp.matches("(.*)#impScanner(.*)")){
                code = "import java.util.Scanner;\n"+code;
                code = code.replaceAll("#impScanner","");
                return code;
            }
            else if (tmp.matches("(.*)#impUtil(.*)")){
                code = "import java.util.*;\n"+code;
                code = code.replaceAll("#impUtil","");
                return code;
            }
            else if (tmp.matches("(.*)#for(.*)")){
                code = code.replaceAll("#for","for(int i=0; i<n; i++){\n\t\n}");
                return code;
            }
        }
        return null;
    }

    // FIXME: 2017-04-04 
    /** shortcuts for cpp */
    public static String shortcutsCpp(String code){
        String[] lexemes = code.split("[ ,\n]");
        for (String tmp: lexemes){
            if (tmp.equals("@cpp")){
                code = "#include<iostream>\nusing namespace std;\n"+
                        "int main(){\n"+
                        "\treturn 0;\n"+
                        "}\n";
                return code;
            }
            else if (tmp.matches("(.*)@cout(.*)")){
                code = code.replaceAll("@cout","cout<< <<endl;");
                return code;
            }
            else if (tmp.matches("(.*)@cin(.*)")){
                code = code.replaceAll("#cin","cin>> ;");
                return code;
            }
            else if (tmp.matches("(.*)#for(.*)")){
                code = code.replaceAll("#for","for(int i=0; i<n; i++){\n\t\n}");
                return code;
            }
        }
        return null;
    }

}
