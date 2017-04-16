package parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class MyFileReader {
    String getFile(String fileName) {

        StringBuilder result = new StringBuilder("");
        String line;
        try {
            FileReader fileReader =
                    new FileReader(fileName);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return result.toString();

    }
}
