/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedList;

/**
 *
 * @author Xiaoxing
 */
public class FileLib {

    private File getConfigFile() {
        String pathToFile = DataParser.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            pathToFile = URLDecoder.decode(pathToFile, "UTF-8");
            pathToFile = (new File(pathToFile)).getParentFile().getCanonicalPath() + "\\" + "serialJ.ini";
//            System.out.println(pathToFile);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        File f = new File(pathToFile);
        if (f.exists()) {
            return f;
        }
        return null;
    }

    public String readPath(String format) {
        String s;
        String path;
        File f = getConfigFile();
        if (f != null) {
            try (BufferedReader br = new BufferedReader((new FileReader(f)))) {
                while ((s = br.readLine()) != null) {
                    if (s.startsWith(format)) {
                        path = br.readLine();
                        if (path.endsWith("\\")) {
                            return path;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        return null;
    }

    public String[] getExperimentConditions() {
        String s;
        LinkedList<String> exps = new LinkedList<>();
        File f = getConfigFile();

        if (f != null) {
            try (BufferedReader br = new BufferedReader((new FileReader(f)))) {
                while ((s = br.readLine()) != null && !s.startsWith("[Experiments]")) {
                };

                while ((s = br.readLine()) != null && !s.startsWith("[")) {
                    if (s.length() > 2) {
                        exps.add(s);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }

        return exps.toArray(new String[exps.size()]);
    }
}
