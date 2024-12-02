import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import Demo.FilePrx;
import Demo.Master;

public class FileGenerator implements Master {
    // Nombre de la carpeta donde se guardarán los archivos
    private final String folderPath = "soluciones";
    private String filePath;
    private long startTime;
    private long linesNumber;
    private long currentLines;

    public FileGenerator(){
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Folder 'soluciones' created successfully.");
            } else {
                System.err.println("Failed to create folder 'soluciones'.");
            }
        }
        
    }

    public void readFile(String ruta, List<String> subscribers, FilePrx file) {
        filePath = generateUniqueFilePath(folderPath);
        currentLines=0;
        startTime = System.currentTimeMillis();
        BufferedReader reader = null;
        try { 
            reader = new BufferedReader(new FileReader(ruta));
            String line;
            linesNumber = Files.lines(Paths.get(ruta)).count();

            // Dividir las líneas entre los suscriptores
            int numSubscribers = subscribers.size();
            int linesPerSubscriber = (int) Math.ceil((double) linesNumber / numSubscribers);

            int currentSubscriber = 0;
            int currentLine = 0;
            file.Share(subscribers.get(currentSubscriber));
            while ((line = reader.readLine()) != null) {
                file.Share(line);
                currentLine++;
                if (currentLine >= linesPerSubscriber) {
                    currentLine = 0;
                    currentSubscriber++;
                    if (currentSubscriber >= numSubscribers) {
                        currentSubscriber = 0;
                    }
                    file.Share(subscribers.get(currentSubscriber));
                }
            }
            file.Share("END_OF_DOCUMENTS");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el archivo: " + e.getMessage());
            }
        }
    }
    


    @Override
    public void receiveTask(String[] tasks, com.zeroc.Ice.Current current) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true))) {
            for (String task : tasks) {
                writer.write(task);
                writer.newLine();
                currentLines++;
                System.out.println(currentLines);
            }
            writer.flush();
            if (currentLines>=linesNumber) {
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                System.out.println("Tiempo de ejecucion: " + elapsedTime + " ms");
            }
        } catch (IOException e) {
            System.err.println("Error writing tasks to file: " + e.getMessage());
        }
    }

    /**
     * Genera un nombre de archivo único basado en un consecutivo en la carpeta especificada.
     */
    private String generateUniqueFilePath(String folderPath) {
        int fileIndex = 1;
        String filePath;

        do {
            filePath = folderPath + File.separator + "tasks_" + fileIndex + ".txt";
            fileIndex++;
        } while (new File(filePath).exists());

        return filePath;
    }
}
